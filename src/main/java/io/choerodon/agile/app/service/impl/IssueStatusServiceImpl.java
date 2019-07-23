package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.api.validator.IssueStatusValidator;
import io.choerodon.agile.api.vo.event.AddStatusWithProject;
import io.choerodon.agile.app.service.ColumnStatusRelService;
import io.choerodon.agile.app.service.IIssueStatusService;
import io.choerodon.agile.app.service.IssueStatusService;
import io.choerodon.agile.api.vo.event.StatusPayload;
import io.choerodon.agile.infra.aspect.DataLogRedisUtil;
import io.choerodon.agile.infra.utils.RedisUtil;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.core.exception.CommonException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/16.
 * Email: fuqianghuang01@gmail.com
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class IssueStatusServiceImpl implements IssueStatusService {

    private static final Logger logger = LoggerFactory.getLogger(IssueStatusServiceImpl.class);
    private static final String AGILE = "Agile:";
    private static final String PIECHART = AGILE + "PieChart";
    private static final String STATUS = "status";

    @Autowired
    private IssueStatusMapper issueStatusMapper;

    @Autowired
    private ColumnStatusRelMapper columnStatusRelMapper;

    @Autowired
    private IssueMapper issueMapper;

    @Autowired
    private IssueFeignClient issueFeignClient;

    @Autowired
    private ColumnStatusRelService columnStatusRelService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private DataLogRedisUtil dataLogRedisUtil;

    @Autowired
    private IIssueStatusService iIssueStatusService;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public IssueStatusVO create(Long projectId, String applyType, IssueStatusVO issueStatusVO) {
        IssueStatusValidator.checkCreateStatus(projectId, issueStatusVO);
        StatusInfoVO statusInfoVO = new StatusInfoVO();
        statusInfoVO.setType(issueStatusVO.getCategoryCode());
        statusInfoVO.setName(issueStatusVO.getName());
        ResponseEntity<StatusInfoVO> responseEntity = issueFeignClient.createStatusForAgile(projectId, applyType, statusInfoVO);
        if (responseEntity.getStatusCode().value() == 200 && responseEntity.getBody() != null && responseEntity.getBody().getId() != null) {
            Long statusId = responseEntity.getBody().getId();
            if (issueStatusMapper.selectByStatusId(projectId, statusId) != null) {
                throw new CommonException("error.status.exist");
            }
            issueStatusVO.setCompleted(false);
            issueStatusVO.setStatusId(statusId);
            IssueStatusDTO issueStatusDTO = modelMapper.map(issueStatusVO, IssueStatusDTO.class);
            return modelMapper.map(insertIssueStatus(issueStatusDTO), IssueStatusVO.class);
        } else {
            throw new CommonException("error.status.create");
        }
    }

    @Override
    public IssueStatusVO createStatusByStateMachine(Long projectId, IssueStatusVO issueStatusVO) {
        IssueStatusDTO issueStatusDTO = issueStatusMapper.selectByStatusId(projectId, issueStatusVO.getStatusId());
        if (issueStatusDTO == null) {
            issueStatusVO.setCompleted(false);
            issueStatusVO.setEnable(false);
            return modelMapper.map(insertIssueStatus(modelMapper.map(issueStatusVO, IssueStatusDTO.class)), IssueStatusVO.class);
        }
        return modelMapper.map(issueStatusDTO, IssueStatusVO.class);
    }

    public Boolean checkColumnStatusRelExist(Long projectId, Long statusId, Long originColumnId) {
        ColumnStatusRelDTO columnStatusRelDTO = new ColumnStatusRelDTO();
        columnStatusRelDTO.setStatusId(statusId);
        columnStatusRelDTO.setColumnId(originColumnId);
        columnStatusRelDTO.setProjectId(projectId);
        ColumnStatusRelDTO rel = columnStatusRelMapper.selectOne(columnStatusRelDTO);
        return rel == null;
    }

    public void deleteColumnStatusRel(Long projectId, Long statusId, Long originColumnId) {
        ColumnStatusRelDTO columnStatusRelDTO = new ColumnStatusRelDTO();
        columnStatusRelDTO.setStatusId(statusId);
        columnStatusRelDTO.setColumnId(originColumnId);
        columnStatusRelDTO.setProjectId(projectId);
        columnStatusRelService.delete(columnStatusRelDTO);
    }

    public void createColumnStatusRel(Long projectId, Long statusId, StatusMoveVO statusMoveVO) {
        ColumnStatusRelDTO columnStatusRelDTO = new ColumnStatusRelDTO();
        columnStatusRelDTO.setStatusId(statusId);
        columnStatusRelDTO.setProjectId(projectId);
        columnStatusRelDTO.setColumnId(statusMoveVO.getColumnId());
        if (columnStatusRelMapper.select(columnStatusRelDTO).isEmpty()) {
            ColumnStatusRelDTO columnStatusRel = new ColumnStatusRelDTO();
            columnStatusRel.setColumnId(statusMoveVO.getColumnId());
            columnStatusRel.setPosition(statusMoveVO.getPosition());
            columnStatusRel.setStatusId(statusId);
            columnStatusRel.setProjectId(projectId);
            columnStatusRelService.create(columnStatusRel);
        }
    }

    @Override
    public IssueStatusVO moveStatusToColumn(Long projectId, Long statusId, StatusMoveVO statusMoveVO) {
        if (!checkColumnStatusRelExist(projectId, statusId, statusMoveVO.getOriginColumnId())) {
            deleteColumnStatusRel(projectId, statusId, statusMoveVO.getOriginColumnId());
        }
        createColumnStatusRel(projectId, statusId, statusMoveVO);
        return modelMapper.map(issueStatusMapper.selectByStatusId(projectId, statusId), IssueStatusVO.class);
    }

    @Override
    public IssueStatusVO moveStatusToUnCorrespond(Long projectId, Long statusId, StatusMoveVO statusMoveVO) {
        ColumnStatusRelDTO columnStatusRelDTO = new ColumnStatusRelDTO();
        columnStatusRelDTO.setStatusId(statusId);
        columnStatusRelDTO.setColumnId(statusMoveVO.getColumnId());
        columnStatusRelDTO.setProjectId(projectId);
        columnStatusRelService.delete(columnStatusRelDTO);
        return modelMapper.map(issueStatusMapper.selectByStatusId(projectId, statusId), IssueStatusVO.class);
    }

    @Override
    public List<StatusAndIssuesVO> queryUnCorrespondStatus(Long projectId, Long boardId, String applyType) {
        List<StatusMapVO> statusMapVOList = issueFeignClient.queryStatusByProjectId(projectId, applyType).getBody();
        List<Long> realStatusIds = new ArrayList<>();
        for (StatusMapVO statusMapVO : statusMapVOList) {
            realStatusIds.add(statusMapVO.getId());
        }
        if (realStatusIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<StatusAndIssuesDTO> statusAndIssuesDTOList = issueStatusMapper.queryUnCorrespondStatus(projectId, boardId, realStatusIds);
        if (statusAndIssuesDTOList != null && !statusAndIssuesDTOList.isEmpty()) {
            List<Long> ids = new ArrayList<>();
            for (StatusAndIssuesDTO statusAndIssuesDTO : statusAndIssuesDTOList) {
                ids.add(statusAndIssuesDTO.getStatusId());
            }
            Map<Long, Status> map = issueFeignClient.batchStatusGet(ids).getBody();
            for (StatusAndIssuesDTO statusAndIssuesDTO : statusAndIssuesDTOList) {
                Status status = map.get(statusAndIssuesDTO.getStatusId());
                statusAndIssuesDTO.setCategoryCode(status.getType());
                statusAndIssuesDTO.setName(status.getName());
            }
        }
        List<StatusAndIssuesVO> statusAndIssuesVOList = new ArrayList<>();
        if (statusAndIssuesDTOList != null) {
            statusAndIssuesVOList = modelMapper.map(statusAndIssuesDTOList, new TypeToken<List<StatusAndIssuesVO>>(){}.getType());
        }
        return statusAndIssuesVOList;
    }

    private void checkIssueNumOfStatus(Long projectId, Long statusId) {
        IssueDTO issueDTO = new IssueDTO();
        issueDTO.setStatusId(statusId);
        issueDTO.setProjectId(projectId);
        List<IssueDTO> issueDTOList = issueMapper.select(issueDTO);
        if (issueDTOList != null && !issueDTOList.isEmpty()) {
            throw new CommonException("error.statusHasIssues.delete");
        }
    }

    private void checkStatusExist(Long projectId, Long statusId) {
        IssueStatusDTO issueStatusDTO = new IssueStatusDTO();
        issueStatusDTO.setProjectId(projectId);
        issueStatusDTO.setStatusId(statusId);
        IssueStatusDTO res = issueStatusMapper.selectOne(issueStatusDTO);
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
        IssueStatusDTO issueStatusDTO = new IssueStatusDTO();
        issueStatusDTO.setProjectId(projectId);
        issueStatusDTO.setStatusId(statusId);
        delete(issueStatusDTO);
    }

    @Override
    public List<IssueStatusVO> queryIssueStatusList(Long projectId) {
        IssueStatusDTO issueStatusDTO = new IssueStatusDTO();
        issueStatusDTO.setProjectId(projectId);
        return modelMapper.map(issueStatusMapper.select(issueStatusDTO), new TypeToken<List<IssueStatusVO>>(){}.getType());
    }

    @Override
    public IssueStatusVO updateStatus(Long projectId, IssueStatusVO issueStatusVO) {
        IssueStatusValidator.checkUpdateStatus(projectId, issueStatusVO);
        IssueStatusDTO issueStatusDTO = modelMapper.map(issueStatusVO, IssueStatusDTO.class);
        return modelMapper.map(iIssueStatusService.update(issueStatusDTO), IssueStatusVO.class);
    }


    @Override
    public IssueStatusDTO insertIssueStatus(IssueStatusDTO issueStatusDTO) {
        if (issueStatusMapper.insert(issueStatusDTO) != 1) {
            throw new CommonException("error.IssueStatus.insert");
        }
        redisUtil.deleteRedisCache(new String[]{PIECHART + issueStatusDTO.getProjectId() + ':' + STATUS + "*"});
        return modelMapper.map(issueStatusMapper.selectByStatusId(issueStatusDTO.getProjectId(), issueStatusDTO.getStatusId()), IssueStatusDTO.class);
    }

//    @Override
//    @DataLog(type = "batchUpdateIssueStatus", single = false)
//    public IssueStatusDTO update(IssueStatusDTO issueStatusDTO) {
//        if (issueStatusMapper.updateByPrimaryKeySelective(issueStatusDTO) != 1) {
//            throw new CommonException("error.status.update");
//        }
//        dataLogRedisUtil.deleteByUpdateIssueStatus(issueStatusDTO);
//        return modelMapper.map(issueStatusMapper.selectByStatusId(issueStatusDTO.getProjectId(), issueStatusDTO.getStatusId()), IssueStatusDTO.class);
//    }

    @Override
    public void delete(IssueStatusDTO issueStatusDTO) {
        if (issueStatusMapper.delete(issueStatusDTO) != 1) {
            throw new CommonException("error.status.delete");
        }
        dataLogRedisUtil.deleteByUpdateIssueStatus(issueStatusDTO);
    }

    @Override
    public void batchCreateStatusByProjectIds(List<AddStatusWithProject> addStatusWithProjects, Long userId) {
        issueStatusMapper.batchCreateStatusByProjectIds(addStatusWithProjects, userId);
    }

}
