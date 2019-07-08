package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.api.validator.IssueStatusValidator;
import io.choerodon.agile.app.service.IssueStatusService;
import io.choerodon.agile.domain.agile.entity.ColumnStatusRelE;
import io.choerodon.agile.domain.agile.entity.IssueStatusE;
import io.choerodon.agile.api.vo.event.StatusPayload;
import io.choerodon.agile.infra.repository.ColumnStatusRelRepository;
import io.choerodon.agile.infra.repository.IssueStatusRepository;
import io.choerodon.agile.infra.repository.UserRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    public IssueStatusDTO create(Long projectId, String applyType, IssueStatusDTO issueStatusDTO) {
        IssueStatusValidator.checkCreateStatus(projectId, issueStatusDTO);
        StatusInfoDTO statusInfoDTO = new StatusInfoDTO();
        statusInfoDTO.setType(issueStatusDTO.getCategoryCode());
        statusInfoDTO.setName(issueStatusDTO.getName());
        ResponseEntity<StatusInfoDTO> responseEntity = issueFeignClient.createStatusForAgile(projectId, applyType, statusInfoDTO);
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

    @Override
    public List<StatusAndIssuesDTO> queryUnCorrespondStatus(Long projectId, Long boardId, String applyType) {
        List<StatusMapDTO> statusMapDTOList = issueFeignClient.queryStatusByProjectId(projectId, applyType).getBody();
        List<Long> realStatusIds = new ArrayList<>();
        for (StatusMapDTO statusMapDTO : statusMapDTOList) {
            realStatusIds.add(statusMapDTO.getId());
        }
        if (realStatusIds.isEmpty()) {
            return new ArrayList<>();
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
        if (issueDOList != null && !issueDOList.isEmpty()) {
            throw new CommonException("error.statusHasIssues.delete");
        }
    }

    private void checkStatusExist(Long projectId, Long statusId) {
        IssueStatusDO issueStatusDO = new IssueStatusDO();
        issueStatusDO.setProjectId(projectId);
        issueStatusDO.setStatusId(statusId);
        IssueStatusDO res = issueStatusMapper.selectOne(issueStatusDO);
        if (res == null) {
            throw new CommonException("error.checkStatusExist.get");
        }
    }

    @Override
    public void deleteStatus(Long projectId, Long statusId, String applyType) {
        checkIssueNumOfStatus(projectId, statusId);
        checkStatusExist(projectId, statusId);
        try {
            issueFeignClient.removeStatusForAgile(projectId, statusId, applyType);
        } catch (Exception e) {
            throw new CommonException("error.status.delete");
        }
    }

    @Override
    public void consumDeleteStatus(StatusPayload statusPayload) {
        Long projectId = statusPayload.getProjectId();
        Long statusId = statusPayload.getStatusId();
        checkStatusExist(projectId, statusId);
        IssueStatusE issueStatusE = new IssueStatusE();
        issueStatusE.setProjectId(projectId);
        issueStatusE.setStatusId(statusId);
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
