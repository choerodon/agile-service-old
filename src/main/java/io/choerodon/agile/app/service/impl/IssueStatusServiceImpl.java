package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.api.validator.IssueStatusValidator;
import io.choerodon.agile.app.service.IssueStatusService;
import io.choerodon.agile.domain.agile.entity.ColumnStatusRelE;
import io.choerodon.agile.domain.agile.entity.IssueStatusE;
import io.choerodon.agile.domain.agile.repository.ColumnStatusRelRepository;
import io.choerodon.agile.domain.agile.repository.IssueStatusRepository;
import io.choerodon.agile.domain.agile.repository.UserRepository;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.feign.StateMachineFeignClient;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/16.
 * Email: fuqianghuang01@gmail.com
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class IssueStatusServiceImpl implements IssueStatusService {

    private static final Logger logger = LoggerFactory.getLogger(IssueStatusServiceImpl.class);

    @Autowired
    private IssueStatusRepository issueStatusRepository;

    @Autowired
    private ColumnStatusRelRepository columnStatusRelRepository;

    @Autowired
    private IssueStatusMapper issueStatusMapper;

    @Autowired
    private ColumnStatusRelMapper columnStatusRelMapper;

    @Autowired
    private BoardColumnMapper boardColumnMapper;

    @Autowired
    private IssueMapper issueMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StateMachineFeignClient stateMachineFeignClient;

    @Autowired
    private IssueFeignClient issueFeignClient;

    @Autowired
    private QuickFilterMapper quickFilterMapper;

    @Override
    public IssueStatusDTO create(Long projectId, IssueStatusDTO issueStatusDTO) {
        IssueStatusValidator.checkCreateStatus(projectId, issueStatusDTO);
        StatusInfoDTO statusInfoDTO = new StatusInfoDTO();
        statusInfoDTO.setType(issueStatusDTO.getCategoryCode());
        statusInfoDTO.setName(issueStatusDTO.getName());
        ResponseEntity<StatusInfoDTO> responseEntity = issueFeignClient.createStatusForAgile(projectId, statusInfoDTO);
        if (responseEntity.getStatusCode().value() == 200 && responseEntity.getBody() != null && responseEntity.getBody().getId() != null) {
            Long statusId = responseEntity.getBody().getId();
            if (issueStatusMapper.selectByStatusId(projectId, statusId) != null) {
                throw new CommonException("error.status.exist");
            }
            issueStatusDTO.setCompleted(false);
            issueStatusDTO.setStatusId(statusId);
            IssueStatusE issueStatusE = ConvertHelper.convert(issueStatusDTO, IssueStatusE.class);
            return ConvertHelper.convert(issueStatusRepository.create(issueStatusE), IssueStatusDTO.class);
        } else {
            throw new CommonException("error.status.create");
        }
    }

    @Override
    public IssueStatusDTO createStatusByStateMachine(Long projectId, IssueStatusDTO issueStatusDTO) {
        IssueStatusDO issueStatusDO = issueStatusMapper.selectByStatusId(projectId, issueStatusDTO.getStatusId());
        if (issueStatusDO == null) {
            issueStatusDTO.setCompleted(false);
            issueStatusDTO.setEnable(false);
            return ConvertHelper.convert(issueStatusRepository.create(ConvertHelper.convert(issueStatusDTO, IssueStatusE.class)), IssueStatusDTO.class);
        }
        return ConvertHelper.convert(issueStatusDO, IssueStatusDTO.class);
    }

//    public IssueStatusE updateStatus(Long projectId, Long id, StatusMoveDTO statusMoveDTO) {
//        BoardColumnDO boardColumnDO = boardColumnMapper.selectByPrimaryKey(statusMoveDTO.getColumnId());
//        IssueStatusE issueStatusE = new IssueStatusE();
//        issueStatusE.setId(id);
//        issueStatusE.setProjectId(projectId);
//        issueStatusE.setCategoryCode(boardColumnDO.getCategoryCode());
//        issueStatusE.setObjectVersionNumber(statusMoveDTO.getStatusObjectVersionNumber());
//        return issueStatusRepository.update(issueStatusE);
//    }

    public Boolean checkColumnStatusRelExist(Long projectId, Long statusId, Long originColumnId) {
        ColumnStatusRelDO columnStatusRelDO = new ColumnStatusRelDO();
        columnStatusRelDO.setStatusId(statusId);
        columnStatusRelDO.setColumnId(originColumnId);
        columnStatusRelDO.setProjectId(projectId);
        ColumnStatusRelDO rel = columnStatusRelMapper.selectOne(columnStatusRelDO);
        return rel == null;
    }

    public void deleteColumnStatusRel(Long projectId, Long statusId, Long originColumnId) {
        ColumnStatusRelE columnStatusRelE = new ColumnStatusRelE();
        columnStatusRelE.setStatusId(statusId);
        columnStatusRelE.setColumnId(originColumnId);
        columnStatusRelE.setProjectId(projectId);
        columnStatusRelRepository.delete(columnStatusRelE);
    }

    public void createColumnStatusRel(Long projectId, Long statusId, StatusMoveDTO statusMoveDTO) {
        ColumnStatusRelDO columnStatusRelDO = new ColumnStatusRelDO();
        columnStatusRelDO.setStatusId(statusId);
        columnStatusRelDO.setProjectId(projectId);
        columnStatusRelDO.setColumnId(statusMoveDTO.getColumnId());
        if (columnStatusRelMapper.select(columnStatusRelDO).isEmpty()) {
            ColumnStatusRelE columnStatusRelE = new ColumnStatusRelE();
            columnStatusRelE.setColumnId(statusMoveDTO.getColumnId());
            columnStatusRelE.setPosition(statusMoveDTO.getPosition());
            columnStatusRelE.setStatusId(statusId);
            columnStatusRelE.setProjectId(projectId);
            columnStatusRelRepository.create(columnStatusRelE);
        }
    }

    @Override
    public IssueStatusDTO moveStatusToColumn(Long projectId, Long statusId, StatusMoveDTO statusMoveDTO) {
        if (!checkColumnStatusRelExist(projectId, statusId, statusMoveDTO.getOriginColumnId())) {
            deleteColumnStatusRel(projectId, statusId, statusMoveDTO.getOriginColumnId());
        }
        createColumnStatusRel(projectId, statusId, statusMoveDTO);
        return ConvertHelper.convert(issueStatusMapper.selectByStatusId(projectId, statusId), IssueStatusDTO.class);
    }

    @Override
    public IssueStatusDTO moveStatusToUnCorrespond(Long projectId, Long statusId, StatusMoveDTO statusMoveDTO) {
        ColumnStatusRelE columnStatusRelE = new ColumnStatusRelE();
        columnStatusRelE.setStatusId(statusId);
        columnStatusRelE.setColumnId(statusMoveDTO.getColumnId());
        columnStatusRelE.setProjectId(projectId);
        columnStatusRelRepository.delete(columnStatusRelE);
        return ConvertHelper.convert(issueStatusMapper.selectByStatusId(projectId, statusId), IssueStatusDTO.class);
    }

//    @Override
//    public List<StatusAndIssuesDTO> queryUnCorrespondStatus(Long projectId, Long boardId) {
//        List<StatusAndIssuesDO> statusAndIssuesDOList = issueStatusMapper.queryUnCorrespondStatus(projectId, boardId);
//        List<StatusAndIssuesDTO> statusAndIssuesDTOList = new ArrayList<>();
//        if (statusAndIssuesDOList != null) {
//            statusAndIssuesDTOList = ConvertHelper.convertList(statusAndIssuesDOList, StatusAndIssuesDTO.class);
//        }
//        return statusAndIssuesDTOList;
//    }

    @Override
    public List<StatusAndIssuesDTO> queryUnCorrespondStatus(Long projectId, Long boardId) {
        List<StatusMapDTO> statusMapDTOList = issueFeignClient.queryStatusByProjectId(projectId, "agile").getBody();
        List<Long> realStatusIds = new ArrayList<>();
        for (StatusMapDTO statusMapDTO : statusMapDTOList)  {
            realStatusIds.add(statusMapDTO.getId());
        }
        List<StatusAndIssuesDO> statusAndIssuesDOList = issueStatusMapper.queryUnCorrespondStatus(projectId, boardId, realStatusIds);
        if (statusAndIssuesDOList != null && !statusAndIssuesDOList.isEmpty()) {
            List<Long> ids = new ArrayList<>();
            for (StatusAndIssuesDO statusAndIssuesDO : statusAndIssuesDOList) {
                ids.add(statusAndIssuesDO.getStatusId());
            }
            Map<Long, Status> map = stateMachineFeignClient.batchStatusGet(ids).getBody();
            for (StatusAndIssuesDO statusAndIssuesDO : statusAndIssuesDOList) {
                Status status = map.get(statusAndIssuesDO.getStatusId());
                statusAndIssuesDO.setCategoryCode(status.getType());
                statusAndIssuesDO.setName(status.getName());
            }
        }
        List<StatusAndIssuesDTO> statusAndIssuesDTOList = new ArrayList<>();
        if (statusAndIssuesDOList != null) {
            statusAndIssuesDTOList = ConvertHelper.convertList(statusAndIssuesDOList, StatusAndIssuesDTO.class);
        }
        return statusAndIssuesDTOList;
    }

    private void checkIssueNumOfStatus(Long projectId, Long statusId) {
        IssueDO issueDO = new IssueDO();
        issueDO.setStatusId(statusId);
        issueDO.setProjectId(projectId);
        List<IssueDO> issueDOList = issueMapper.select(issueDO);
        if (!issueDOList.isEmpty()) {
            throw new CommonException("error.statusHasIssues.delete");
        }
    }

//    @Override
//    public void deleteStatus(Long projectId, Long id) {
//        checkIssueNumOfStatus(projectId, id);
//        ColumnStatusRelE columnStatusRelE = new ColumnStatusRelE();
//        columnStatusRelE.setProjectId(projectId);
//        columnStatusRelE.setStatusId(id);
//        columnStatusRelRepository.delete(columnStatusRelE);
//        IssueStatusE issueStatusE = new IssueStatusE();
//        issueStatusE.setProjectId(projectId);
//        issueStatusE.setId(id);
//        issueStatusRepository.delete(issueStatusE);
//    }

    @Override
    public List<IssueStatusDTO> queryIssueStatusList(Long projectId) {
        IssueStatusDO issueStatusDO = new IssueStatusDO();
        issueStatusDO.setProjectId(projectId);
        return ConvertHelper.convertList(issueStatusMapper.select(issueStatusDO), IssueStatusDTO.class);
    }

    @Override
    public IssueStatusDTO updateStatus(Long projectId, IssueStatusDTO issueStatusDTO) {
        IssueStatusValidator.checkUpdateStatus(projectId, issueStatusDTO);
        IssueStatusE issueStatusE = ConvertHelper.convert(issueStatusDTO, IssueStatusE.class);
        return ConvertHelper.convert(issueStatusRepository.update(issueStatusE), IssueStatusDTO.class);
    }

//    @Override
//    public Page<StatusDTO> listByProjectId(Long projectId, PageRequest pageRequest) {
//        Page<StatusDO> statusDOPage = PageHelper.doPageAndSort(pageRequest, () -> issueStatusMapper.listByProjectId(projectId));
//        Page<StatusDTO> statusDTOPage = new Page<>();
//        statusDTOPage.setTotalPages(statusDOPage.getTotalPages());
//        statusDTOPage.setNumber(statusDOPage.getNumber());
//        statusDTOPage.setNumberOfElements(statusDOPage.getNumberOfElements());
//        statusDTOPage.setTotalElements(statusDOPage.getTotalElements());
//        statusDTOPage.setSize(statusDOPage.getSize());
//        statusDTOPage.setContent(ConvertHelper.convertList(statusDOPage.getContent(), StatusDTO.class));
//        return statusDTOPage;
//    }

    @Override
    public void moveStatus(Boolean isFixStatus) {
        logger.info("步骤1开始执行");
        List<StatusForMoveDataDO> result = new ArrayList<>();
        List<IssueStatusDO> statuses = issueStatusMapper.selectAll();
        Collections.sort(statuses, Comparator.comparing(IssueStatusDO::getId));
        List<Long> organizationIds = new ArrayList<>();
        Map<Long, ProjectDTO> projectDTOMap = new HashMap<>();
        for (IssueStatusDO issueStatusDO : statuses) {
            StatusForMoveDataDO statusForMoveDataDO = new StatusForMoveDataDO();
            statusForMoveDataDO.setId(issueStatusDO.getId());
            statusForMoveDataDO.setProjectId(issueStatusDO.getProjectId());
            statusForMoveDataDO.setCategoryCode(issueStatusDO.getCategoryCode());
            statusForMoveDataDO.setName(issueStatusDO.getName());
            ProjectDTO projectDTO = null;
            if (projectDTOMap.get(issueStatusDO.getProjectId()) != null) {
                projectDTO = projectDTOMap.get(issueStatusDO.getProjectId());
            } else {
                projectDTO = userRepository.queryProject(issueStatusDO.getProjectId());
                projectDTOMap.put(issueStatusDO.getProjectId(), projectDTO);
            }
            if (projectDTO != null) {
                statusForMoveDataDO.setOrganizationId(projectDTO.getOrganizationId());
                if (!organizationIds.contains(projectDTO.getOrganizationId()) && projectDTO.getOrganizationId() != null) {
                    organizationIds.add(projectDTO.getOrganizationId());
                }
                if (statusForMoveDataDO.getOrganizationId() != null) {
                    result.add(statusForMoveDataDO);
                }
            }
        }

        issueFeignClient.fixStateMachineScheme(result, isFixStatus);
        logger.info("步骤1执行完成");
    }


    @Override
    public void updateAllData() {
        logger.info("步骤2开始执行");
        List<IssueStatusDO> statuses = issueStatusMapper.selectAll();
        Collections.sort(statuses, Comparator.comparing(IssueStatusDO::getId));
        Map<Long, Long> proWithOrg = new HashMap<>();
        Map<Long, ProjectDTO> projectDTOMap = new HashMap<>();
        for (IssueStatusDO issueStatusDO : statuses) {
            ProjectDTO projectDTO = null;
            if (projectDTOMap.get(issueStatusDO.getProjectId()) != null) {
                projectDTO = projectDTOMap.get(issueStatusDO.getProjectId());
            } else {
                projectDTO = userRepository.queryProject(issueStatusDO.getProjectId());
                projectDTOMap.put(issueStatusDO.getProjectId(), projectDTO);
            }
            if (projectDTO != null) {
                if (projectDTO.getOrganizationId() != null && projectDTO.getId() != null) {
                    proWithOrg.put(projectDTO.getId(), projectDTO.getOrganizationId());
                }
            }
        }

        // 迁移状态
        Map<Long, List<Status>> returnStatus = stateMachineFeignClient.queryAllStatus().getBody();
        for (IssueStatusDO issueStatusDO : statuses) {
            List<Status> partStatus = returnStatus.get(proWithOrg.get(issueStatusDO.getProjectId()));
            if (partStatus != null) {
                for (Status status : partStatus) {
                    if (status.getName().equals(issueStatusDO.getName())) {
                        issueStatusDO.setStatusId(status.getId());
                        break;
                    }
                }
            }
        }
        issueStatusMapper.batchUpdateStatus(statuses);
        issueStatusMapper.updateAllStatusId();
        issueStatusMapper.updateAllColumnStatusId();
        issueStatusMapper.updateDataLogStatusId();

        // 迁移优先级
        Map<Long, Map<String, Long>> prioritys = issueFeignClient.queryPriorities().getBody();
        List<IssueDO> issueDOList = issueMapper.selectAllPriority();
        for (IssueDO issueDO : issueDOList) {
            if (proWithOrg.get(issueDO.getProjectId()) != null) {
                Map<String, Long> ps = prioritys.get(proWithOrg.get(issueDO.getProjectId()));
                issueDO.setPriorityId(ps.get(issueDO.getPriorityCode()));
            }
        }
        issueMapper.batchUpdatePriority(issueDOList);

        // 迁移问题类型
        Map<Long, Map<String, Long>> issueTypes = issueFeignClient.queryIssueTypes().getBody();
        List<IssueDO> issueDOForTypeList = issueMapper.selectAllType();
        for (IssueDO issueDO : issueDOForTypeList) {
            if (proWithOrg.get(issueDO.getProjectId()) != null) {
                Map<String, Long> iTypes = issueTypes.get(proWithOrg.get(issueDO.getProjectId()));
                issueDO.setIssueTypeId(iTypes.get(issueDO.getTypeCode()));
            }
        }
        issueMapper.batchUpdateIssueType(issueDOForTypeList);

        // 修复快速搜索数据,状态
        List<QuickFilterDO> quickFilterDOList = quickFilterMapper.selectAll();
        List<QuickFilterDO> updateDate = new ArrayList<>();
        for (QuickFilterDO quick : quickFilterDOList) {
            String sqlQuery = quick.getSqlQuery();
            if (sqlQuery.contains("status_id")) {
                String[] sqls = sqlQuery.split("and ");
                String result = "";
                int ind = 0;
                for (String s : sqls) {
                    if (s.contains("status_id")) {
                        String[] sqls2 = s.split("or ");
                        String filter2 = "";
                        int index = 0;
                        for (String s2 : sqls2) {
                            if (s2.contains("status_id")) {
                                String statusIdStr = getStatusNumber(s2);
                                String requirement = "";
                                String[] lists = statusIdStr.split(",");
                                int w = 0;
                                for (String ll : lists) {
                                    Long sId = issueStatusMapper.selectByPrimaryKey(Long.parseLong(ll)).getStatusId();
                                    if (w == 0) {
                                        requirement += sId;
                                    } else {
                                        requirement += "," + sId;
                                    }
                                    w++;
                                }
                                s2 = s2.replace(statusIdStr, requirement);
                            }
                            if (index == 0) {
                                filter2 += s2;
                            } else {
                                filter2 += " or " + s2;
                            }
                            index++;
                        }
                        if (ind == 0) {
                            result += filter2;
                        } else {
                            result += " and " + filter2;
                        }
                        ind++;
                    } else {
                        if (ind == 0) {
                            result += s;
                        } else {
                            result += " and " + s;
                        }
                        ind++;
                    }
                }

                // 处理description
                String description = quick.getDescription();
                String[] desStrs = description.split("\\+\\+\\+");
                JSONObject jsonObject = JSONObject.parseObject(desStrs[1]);
                List<JSONObject> arrs = JSONObject.parseArray(jsonObject.get("arr").toString(), JSONObject.class);
                for (JSONObject object : arrs) {
                    if ("status".equals(object.get("fieldCode"))) {
                        String finalResult = object.get("value").toString();
                        String val = getStatusNumber(object.get("value").toString());
                        String[] valSplit = val.split(",");
                        String valReal = "";
                        int vw = 0;
                        for (String v : valSplit) {
                            Long vId = issueStatusMapper.selectByPrimaryKey(Long.parseLong(v)).getStatusId();
                            if (vw == 0) {
                                valReal += vId;
                            } else {
                                valReal += "," + vId;
                            }
                            vw++;
                        }
                        finalResult = finalResult.replace(val, valReal);
                        object.put("value", finalResult);
                    }
                }

                QuickFilterDO q = new QuickFilterDO();
                q.setFilterId(quick.getFilterId());
                q.setObjectVersionNumber(quick.getObjectVersionNumber());
                q.setSqlQuery(result);
                jsonObject.put("arr", arrs);
                q.setDescription(desStrs[0] + "+++" + jsonObject.toString());
                updateDate.add(q);
            }
        }
        for (QuickFilterDO quick : updateDate) {
            if (quickFilterMapper.updateByPrimaryKeySelective(quick) != 1) {
                throw new CommonException("error.quickFilter.update");
            }
        }

        // 修复快速搜索数据,优先级
        List<QuickFilterDO> quickFilterPrioritys = quickFilterMapper.selectAll();
        List<QuickFilterDO> priorityResult = new ArrayList<>();
        for (QuickFilterDO qf : quickFilterPrioritys) {
            String qfStr = qf.getSqlQuery();
            if (qfStr.contains("priority_code")) {
                String[] splits = qfStr.split("and ");
                int b = 0;
                String res = "";
                for (String sp : splits) {
                    if (sp.contains("priority_code")) {
                        String[] splits2 = sp.split("or ");
                        String reStr = "";
                        int a = 0;
                        for (String sp2 : splits2) {
                            if (sp2.contains("priority_code")) {
                                if (sp2.contains("low")) {
                                    sp2 = sp2.replaceAll("'low'", getPriorityId(prioritys, proWithOrg, qf, "low").toString());
                                }
                                if (sp2.contains("medium")) {
                                    sp2 = sp2.replaceAll("'medium'", getPriorityId(prioritys, proWithOrg, qf, "medium").toString());
                                }
                                if (sp2.contains("high")) {
                                    sp2 = sp2.replaceAll("'high'", getPriorityId(prioritys, proWithOrg, qf, "high").toString());
                                }
                                sp2 = sp2.replaceAll("priority_code", "priority_id");
                                if (a == 0) {
                                    reStr += sp2;
                                } else {
                                    reStr += " or " + sp2;
                                }
                                a++;
                            }
                        }
                        if (b == 0) {
                            res += reStr;
                        } else {
                            res += " and " + reStr;
                        }
                        b++;
                    } else {
                        if (b == 0) {
                            res += sp;
                        } else {
                            res += " and " + sp;
                        }
                        b++;
                    }
                }
                // 处理description
                String description = qf.getDescription();
                String[] desStrs = description.split("\\+\\+\\+");
                JSONObject jsonObject = JSONObject.parseObject(desStrs[1]);
                List<JSONObject> arrs = JSONObject.parseArray(jsonObject.get("arr").toString(), JSONObject.class);
                for (JSONObject object : arrs) {
                    if ("priority".equals(object.get("fieldCode"))) {
                        String val = object.get("value").toString();
                        if (val.contains("high")) {
                            val = val.replaceAll("'high'", getPriorityId(prioritys, proWithOrg, qf, "high").toString());
                        }
                        if (val.contains("medium")) {
                            val = val.replaceAll("'medium'", getPriorityId(prioritys, proWithOrg, qf, "medium").toString());
                        }
                        if (val.contains("low")) {
                            val = val.replaceAll("'low'", getPriorityId(prioritys, proWithOrg, qf, "low").toString());
                        }
                        object.put("value", val);
                    }
                }

                QuickFilterDO updatePriority = new QuickFilterDO();
                updatePriority.setFilterId(qf.getFilterId());
                updatePriority.setObjectVersionNumber(qf.getObjectVersionNumber());
                updatePriority.setSqlQuery(res);
                jsonObject.put("arr", arrs);
                updatePriority.setDescription(desStrs[0] + "+++" + jsonObject.toString());
                priorityResult.add(updatePriority);
            }
        }
        for (QuickFilterDO qq : priorityResult) {
            if (quickFilterMapper.updateByPrimaryKeySelective(qq) != 1) {
                throw new CommonException("error.quickFilterPriority.update");
            }
        }

        logger.info("步骤2执行完成");
    }

    private Long getPriorityId(Map<Long, Map<String, Long>> prioritys, Map<Long, Long> proWithOrg, QuickFilterDO quickFilterDO, String priorityStr) {
        Map<String, Long> ps = prioritys.get(proWithOrg.get(quickFilterDO.getProjectId()));
        return ps.get(priorityStr);
    }

    private String getStatusNumber(String str) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll(" ").trim().replaceAll(" ", ",");
    }

}
