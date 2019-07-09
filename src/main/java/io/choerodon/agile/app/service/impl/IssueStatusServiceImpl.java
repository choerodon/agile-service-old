package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.api.validator.IssueStatusValidator;
import io.choerodon.agile.api.vo.event.AddStatusWithProject;
import io.choerodon.agile.app.service.ColumnStatusRelService;
import io.choerodon.agile.app.service.IssueStatusService;
import io.choerodon.agile.api.vo.event.StatusPayload;
import io.choerodon.agile.infra.common.annotation.DataLog;
import io.choerodon.agile.infra.common.aspect.DataLogRedisUtil;
import io.choerodon.agile.infra.common.utils.RedisUtil;
import io.choerodon.agile.infra.repository.ColumnStatusRelRepository;
import io.choerodon.agile.infra.repository.IssueStatusRepository;
import io.choerodon.agile.app.service.UserService;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.feign.StateMachineFeignClient;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.core.convertor.ConvertHelper;
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
    private UserService userService;

    @Autowired
    private StateMachineFeignClient stateMachineFeignClient;

    @Autowired
    private IssueFeignClient issueFeignClient;

    @Autowired
    private QuickFilterMapper quickFilterMapper;

    @Autowired
    private ColumnStatusRelService columnStatusRelService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private DataLogRedisUtil dataLogRedisUtil;

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

    public void createColumnStatusRel(Long projectId, Long statusId, StatusMoveDTO statusMoveDTO) {
        ColumnStatusRelDTO columnStatusRelDTO = new ColumnStatusRelDTO();
        columnStatusRelDTO.setStatusId(statusId);
        columnStatusRelDTO.setProjectId(projectId);
        columnStatusRelDTO.setColumnId(statusMoveDTO.getColumnId());
        if (columnStatusRelMapper.select(columnStatusRelDTO).isEmpty()) {
            ColumnStatusRelDTO columnStatusRel = new ColumnStatusRelDTO();
            columnStatusRel.setColumnId(statusMoveDTO.getColumnId());
            columnStatusRel.setPosition(statusMoveDTO.getPosition());
            columnStatusRel.setStatusId(statusId);
            columnStatusRel.setProjectId(projectId);
            columnStatusRelService.create(columnStatusRel);
        }
    }

    @Override
    public IssueStatusVO moveStatusToColumn(Long projectId, Long statusId, StatusMoveDTO statusMoveDTO) {
        if (!checkColumnStatusRelExist(projectId, statusId, statusMoveDTO.getOriginColumnId())) {
            deleteColumnStatusRel(projectId, statusId, statusMoveDTO.getOriginColumnId());
        }
        createColumnStatusRel(projectId, statusId, statusMoveDTO);
        return ConvertHelper.convert(issueStatusMapper.selectByStatusId(projectId, statusId), IssueStatusVO.class);
    }

    @Override
    public IssueStatusVO moveStatusToUnCorrespond(Long projectId, Long statusId, StatusMoveDTO statusMoveDTO) {
        ColumnStatusRelDTO columnStatusRelDTO = new ColumnStatusRelDTO();
        columnStatusRelDTO.setStatusId(statusId);
        columnStatusRelDTO.setColumnId(statusMoveDTO.getColumnId());
        columnStatusRelDTO.setProjectId(projectId);
        columnStatusRelService.delete(columnStatusRelDTO);
        return modelMapper.map(issueStatusMapper.selectByStatusId(projectId, statusId), IssueStatusVO.class);
    }

    @Override
    public List<StatusAndIssuesDTO> queryUnCorrespondStatus(Long projectId, Long boardId, String applyType) {
        List<StatusMapVO> statusMapVOList = issueFeignClient.queryStatusByProjectId(projectId, applyType).getBody();
        List<Long> realStatusIds = new ArrayList<>();
        for (StatusMapVO statusMapVO : statusMapVOList) {
            realStatusIds.add(statusMapVO.getId());
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
        return modelMapper.map(update(issueStatusDTO), IssueStatusVO.class);
    }


    @Override
    public IssueStatusDTO insertIssueStatus(IssueStatusDTO issueStatusDTO) {
        if (issueStatusMapper.insert(issueStatusDTO) != 1) {
            throw new CommonException("error.IssueStatus.insert");
        }
        redisUtil.deleteRedisCache(new String[]{PIECHART + issueStatusDTO.getProjectId() + ':' + STATUS + "*"});
        return modelMapper.map(issueStatusMapper.selectByStatusId(issueStatusDTO.getProjectId(), issueStatusDTO.getStatusId()), IssueStatusDTO.class);
    }

    @Override
    @DataLog(type = "batchUpdateIssueStatus", single = false)
    public IssueStatusDTO update(IssueStatusDTO issueStatusDTO) {
        if (issueStatusMapper.updateByPrimaryKeySelective(issueStatusDTO) != 1) {
            throw new CommonException("error.status.update");
        }
        dataLogRedisUtil.deleteByUpdateIssueStatus(issueStatusDTO);
        return modelMapper.map(issueStatusMapper.selectByStatusId(issueStatusDTO.getProjectId(), issueStatusDTO.getStatusId()), IssueStatusDTO.class);
    }

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
