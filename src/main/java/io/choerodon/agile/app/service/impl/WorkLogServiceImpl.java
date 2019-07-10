package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.WorkLogVO;
import io.choerodon.agile.api.validator.WorkLogValidator;
import io.choerodon.agile.app.service.WorkLogService;
import io.choerodon.agile.infra.common.annotation.DataLog;
import io.choerodon.agile.infra.dataobject.IssueConvertDTO;
import io.choerodon.agile.infra.dataobject.WorkLogDTO;
import io.choerodon.agile.infra.repository.IssueRepository;
import io.choerodon.agile.app.service.UserService;
import io.choerodon.agile.infra.dataobject.IssueDTO;
import io.choerodon.agile.infra.dataobject.UserMessageDO;
import io.choerodon.agile.infra.mapper.IssueMapper;
import io.choerodon.agile.infra.mapper.WorkLogMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/18.
 * Email: fuqianghuang01@gmail.com
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WorkLogServiceImpl implements WorkLogService {

    private static final String SELF_ADJUSTMENT = "self_adjustment";
    private static final String NO_SET_PREDICTION_TIME = "no_set_prediction_time";
    private static final String SET_TO = "set_to";
    private static final String REDUCE = "reduce";
    private static final String REMAINING_TIME_FIELD = "remainingTime";

//    @Autowired
//    private WorkLogRepository workLogRepository;

    @Autowired
    private WorkLogMapper workLogMapper;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private IssueMapper issueMapper;

    @Autowired
    private UserService userService;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    private void setTo(Long issueId, BigDecimal predictionTime) {
        IssueConvertDTO issueConvertDTO = ConvertHelper.convert(issueMapper.selectByPrimaryKey(issueId), IssueConvertDTO.class);
        issueConvertDTO.setRemainingTime(predictionTime);
        issueRepository.update(issueConvertDTO, new String[]{REMAINING_TIME_FIELD});
    }

    private BigDecimal getRemainTime(IssueConvertDTO issueConvertDTO, BigDecimal theTime) {
        BigDecimal zero = new BigDecimal(0);
        return issueConvertDTO.getRemainingTime().subtract(theTime).compareTo(zero) < 0 ? zero : issueConvertDTO.getRemainingTime().subtract(theTime);
    }

    private void reducePrediction(Long issueId, BigDecimal predictionTime) {
        IssueConvertDTO issueConvertDTO = ConvertHelper.convert(issueMapper.selectByPrimaryKey(issueId), IssueConvertDTO.class);
        if (issueConvertDTO.getRemainingTime() != null) {
            issueConvertDTO.setRemainingTime(getRemainTime(issueConvertDTO, predictionTime));
            issueRepository.update(issueConvertDTO, new String[]{REMAINING_TIME_FIELD});
        }
    }

    private void selfAdjustment(Long issueId, BigDecimal workTime) {
        IssueConvertDTO issueConvertDTO = ConvertHelper.convert(issueMapper.selectByPrimaryKey(issueId), IssueConvertDTO.class);
        if (issueConvertDTO.getRemainingTime() != null) {
            issueConvertDTO.setRemainingTime(getRemainTime(issueConvertDTO, workTime));
            issueRepository.update(issueConvertDTO, new String[]{REMAINING_TIME_FIELD});
        }
    }

    @Override
    public WorkLogVO createWorkLog(Long projectId, WorkLogVO workLogVO) {
        IssueDTO issueDTO = issueMapper.selectByPrimaryKey(workLogVO.getIssueId());
        WorkLogValidator.checkCreateWorkLog(projectId, workLogVO, issueDTO);
        if (workLogVO.getResidualPrediction() != null) {
            switch (workLogVO.getResidualPrediction()) {
                case SELF_ADJUSTMENT:
                    selfAdjustment(workLogVO.getIssueId(), workLogVO.getWorkTime());
                    break;
                case NO_SET_PREDICTION_TIME:
                    break;
                case SET_TO:
                    setTo(workLogVO.getIssueId(), workLogVO.getPredictionTime());
                    break;
                case REDUCE:
                    reducePrediction(workLogVO.getIssueId(), workLogVO.getPredictionTime());
                    break;
                default:
                    break;
            }
        }
//        workLogE = workLogRepository.create(workLogE);
        WorkLogDTO res = create(modelMapper.map(workLogVO, WorkLogDTO.class));
        return queryWorkLogById(res.getProjectId(), res.getLogId());
    }

    @Override
    public WorkLogVO updateWorkLog(Long projectId, Long logId, WorkLogVO workLogVO) {
        WorkLogValidator.checkUpdateWorkLog(workLogVO);
        workLogVO.setProjectId(projectId);
        WorkLogDTO res = update(modelMapper.map(workLogVO, WorkLogDTO.class));
        return queryWorkLogById(res.getProjectId(), res.getLogId());
    }

    @Override
    public void deleteWorkLog(Long projectId, Long logId) {
        delete(projectId, logId);
    }

    @Override
    public WorkLogVO queryWorkLogById(Long projectId, Long logId) {
        WorkLogDTO workLogDTO = new WorkLogDTO();
        workLogDTO.setProjectId(projectId);
        workLogDTO.setLogId(logId);
        WorkLogVO workLogVO = ConvertHelper.convert(workLogMapper.selectOne(workLogDTO), WorkLogVO.class);
        workLogVO.setUserName(userService.queryUserNameByOption(workLogVO.getUserId(), true).getRealName());
        return workLogVO;
    }

    @Override
    public List<WorkLogVO> queryWorkLogListByIssueId(Long projectId, Long issueId) {
        List<WorkLogVO> workLogVOList = ConvertHelper.convertList(workLogMapper.queryByIssueId(issueId, projectId), WorkLogVO.class);
        List<Long> assigneeIds = workLogVOList.stream().filter(workLogDTO -> workLogDTO.getUserId() != null && !Objects.equals(workLogDTO.getUserId(), 0L)).map(WorkLogVO::getUserId).distinct().collect(Collectors.toList());
        Map<Long, UserMessageDO> usersMap = userService.queryUsersMap(assigneeIds, true);
        workLogVOList.forEach(workLogDTO -> {
            String assigneeName = usersMap.get(workLogDTO.getUserId()) != null ? usersMap.get(workLogDTO.getUserId()).getName() : null;
            workLogDTO.setUserName(assigneeName);
        });
        return workLogVOList;
    }

    @Override
    @DataLog(type = "createWorkLog")
    public WorkLogDTO create(WorkLogDTO workLogDTO) {
        if (workLogMapper.insert(workLogDTO) != 1) {
            throw new CommonException("error.workLog.insert");
        }
        return workLogMapper.selectByPrimaryKey(workLogDTO.getLogId());
    }

    @Override
    public WorkLogDTO update(WorkLogDTO workLogDTO) {
        if (workLogMapper.updateByPrimaryKeySelective(workLogDTO) != 1) {
            throw new CommonException("error.workLog.update");
        }
        return workLogMapper.selectByPrimaryKey(workLogDTO.getLogId());
    }

    @Override
    @DataLog(type = "deleteWorkLog")
    public void delete(Long projectId,Long logId) {
        WorkLogDTO query = new WorkLogDTO();
        query.setProjectId(projectId);
        query.setLogId(logId);
        WorkLogDTO workLogDTO = workLogMapper.selectOne(query);
        if (workLogDTO == null) {
            throw new CommonException("error.workLog.get");
        }
        if (workLogMapper.delete(workLogDTO) != 1) {
            throw new CommonException("error.workLog.delete");
        }
    }
}
