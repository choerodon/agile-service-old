package io.choerodon.agile.app.service.impl;

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
import io.choerodon.agile.infra.mapper.BoardColumnMapper;
import io.choerodon.agile.infra.mapper.ColumnStatusRelMapper;
import io.choerodon.agile.infra.mapper.IssueMapper;
import io.choerodon.agile.infra.mapper.IssueStatusMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/16.
 * Email: fuqianghuang01@gmail.com
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class IssueStatusServiceImpl implements IssueStatusService {

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

    @Override
    public IssueStatusDTO create(Long projectId, IssueStatusDTO issueStatusDTO) {
        IssueStatusValidator.checkCreateStatus(projectId, issueStatusDTO);
        issueStatusDTO.setCompleted(false);
        IssueStatusE issueStatusE = ConvertHelper.convert(issueStatusDTO, IssueStatusE.class);
        return ConvertHelper.convert(issueStatusRepository.create(issueStatusE), IssueStatusDTO.class);
    }

    public IssueStatusE updateStatus(Long projectId, Long id, StatusMoveDTO statusMoveDTO) {
        BoardColumnDO boardColumnDO = boardColumnMapper.selectByPrimaryKey(statusMoveDTO.getColumnId());
        IssueStatusE issueStatusE = new IssueStatusE();
        issueStatusE.setId(id);
        issueStatusE.setProjectId(projectId);
        issueStatusE.setCategoryCode(boardColumnDO.getCategoryCode());
        issueStatusE.setObjectVersionNumber(statusMoveDTO.getStatusObjectVersionNumber());
        return issueStatusRepository.update(issueStatusE);
    }

    public Boolean checkColumnStatusRelExist(Long projectId, Long id, Long originColumnId) {
        ColumnStatusRelDO columnStatusRelDO = new ColumnStatusRelDO();
        columnStatusRelDO.setStatusId(id);
        columnStatusRelDO.setColumnId(originColumnId);
        columnStatusRelDO.setProjectId(projectId);
        ColumnStatusRelDO rel = columnStatusRelMapper.selectOne(columnStatusRelDO);
        return rel == null;
    }

    public void deleteColumnStatusRel(Long projectId, Long id, Long originColumnId) {
        ColumnStatusRelE columnStatusRelE = new ColumnStatusRelE();
        columnStatusRelE.setStatusId(id);
        columnStatusRelE.setColumnId(originColumnId);
        columnStatusRelE.setProjectId(projectId);
        columnStatusRelRepository.delete(columnStatusRelE);
    }

    public void createColumnStatusRel(Long projectId, Long id, StatusMoveDTO statusMoveDTO) {
        ColumnStatusRelDO columnStatusRelDO = new ColumnStatusRelDO();
        columnStatusRelDO.setStatusId(id);
        columnStatusRelDO.setColumnId(statusMoveDTO.getColumnId());
        if (columnStatusRelMapper.select(columnStatusRelDO).isEmpty()) {
            ColumnStatusRelE columnStatusRelE = new ColumnStatusRelE();
            columnStatusRelE.setColumnId(statusMoveDTO.getColumnId());
            columnStatusRelE.setPosition(statusMoveDTO.getPosition());
            columnStatusRelE.setStatusId(id);
            columnStatusRelE.setProjectId(projectId);
            columnStatusRelRepository.create(columnStatusRelE);
        }
    }

    @Override
    public IssueStatusDTO moveStatusToColumn(Long projectId, Long id, StatusMoveDTO statusMoveDTO) {
        if (!checkColumnStatusRelExist(projectId, id, statusMoveDTO.getOriginColumnId())) {
            deleteColumnStatusRel(projectId, id, statusMoveDTO.getOriginColumnId());
        }
        createColumnStatusRel(projectId, id, statusMoveDTO);
        return ConvertHelper.convert(issueStatusMapper.selectByPrimaryKey(id), IssueStatusDTO.class);
    }

    @Override
    public IssueStatusDTO moveStatusToUnCorrespond(Long projectId, Long id, StatusMoveDTO statusMoveDTO) {
        ColumnStatusRelE columnStatusRelE = new ColumnStatusRelE();
        columnStatusRelE.setStatusId(id);
        columnStatusRelE.setColumnId(statusMoveDTO.getColumnId());
        columnStatusRelE.setProjectId(projectId);
        columnStatusRelRepository.delete(columnStatusRelE);
        return ConvertHelper.convert(issueStatusMapper.selectByPrimaryKey(id), IssueStatusDTO.class);
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
        List<StatusAndIssuesDO> statusAndIssuesDOList = issueStatusMapper.queryUnCorrespondStatus(projectId, boardId);
        if (statusAndIssuesDOList != null && !statusAndIssuesDOList.isEmpty()) {
            List<Long> ids = new ArrayList<>();
            for (StatusAndIssuesDO statusAndIssuesDO : statusAndIssuesDOList) {
                ids.add(statusAndIssuesDO.getStatusId());
            }
            Map<Long, Status> map =  stateMachineFeignClient.batchStatusGet(ids).getBody();
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

    @Override
    public void deleteStatus(Long projectId, Long id) {
        checkIssueNumOfStatus(projectId, id);
        ColumnStatusRelE columnStatusRelE = new ColumnStatusRelE();
        columnStatusRelE.setProjectId(projectId);
        columnStatusRelE.setStatusId(id);
        columnStatusRelRepository.delete(columnStatusRelE);
        IssueStatusE issueStatusE = new IssueStatusE();
        issueStatusE.setProjectId(projectId);
        issueStatusE.setId(id);
        issueStatusRepository.delete(issueStatusE);
    }

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

    @Override
    public Page<StatusDTO> listByProjectId(Long projectId, PageRequest pageRequest) {
        Page<StatusDO> statusDOPage = PageHelper.doPageAndSort(pageRequest, () -> issueStatusMapper.listByProjectId(projectId));
        Page<StatusDTO> statusDTOPage = new Page<>();
        statusDTOPage.setTotalPages(statusDOPage.getTotalPages());
        statusDTOPage.setNumber(statusDOPage.getNumber());
        statusDTOPage.setNumberOfElements(statusDOPage.getNumberOfElements());
        statusDTOPage.setTotalElements(statusDOPage.getTotalElements());
        statusDTOPage.setSize(statusDOPage.getSize());
        statusDTOPage.setContent(ConvertHelper.convertList(statusDOPage.getContent(), StatusDTO.class));
        return statusDTOPage;
    }

    @Override
    public void moveStatus(Long projectId) {
        List<StatusForMoveDataDO> result = new ArrayList<>();
        List<IssueStatusDO> statuses = issueStatusMapper.selectAll();
        Collections.sort(statuses, Comparator.comparing(IssueStatusDO::getId));
        List<Long> organizationIds = new ArrayList<>();
        Map<Long, Long> proWithOrg = new HashMap<>();
        for (IssueStatusDO issueStatusDO : statuses) {
            StatusForMoveDataDO statusForMoveDataDO = new StatusForMoveDataDO();
            statusForMoveDataDO.setId(issueStatusDO.getId());
            statusForMoveDataDO.setProjectId(issueStatusDO.getProjectId());
            statusForMoveDataDO.setCategoryCode(issueStatusDO.getCategoryCode());
            statusForMoveDataDO.setName(issueStatusDO.getName());
            ProjectDTO projectDTO = userRepository.queryProject(issueStatusDO.getProjectId());
            statusForMoveDataDO.setOrganizationId(projectDTO.getOrganizationId());
            if (projectDTO.getOrganizationId()!= null && projectDTO.getId() != null) {
                proWithOrg.put(projectDTO.getId(), projectDTO.getOrganizationId());
            }
            if (!organizationIds.contains(projectDTO.getOrganizationId()) && projectDTO.getOrganizationId() != null) {
                organizationIds.add(projectDTO.getOrganizationId());
            }
            result.add(statusForMoveDataDO);
        }

        // 迁移状态
        Map<Long, List<Status>> returnStatus = stateMachineFeignClient.initStatus(result).getBody();
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
        Map<Long, Map<String, Long>> prioritys = issueFeignClient.initProrityByOrganization(organizationIds).getBody();
        List<IssueDO> issueDOList = issueMapper.selectAllPriority();
        for (IssueDO issueDO : issueDOList) {
            if (proWithOrg.get(issueDO.getProjectId()) != null) {
                Map<String, Long> ps = prioritys.get(proWithOrg.get(issueDO.getProjectId()));
                issueDO.setPriorityId(ps.get(issueDO.getPriorityCode()));
            }

        }
        issueMapper.batchUpdatePriority(issueDOList);

        // 迁移问题类型

    }
}
