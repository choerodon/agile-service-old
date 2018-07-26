package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.WorkLogDTO;
import io.choerodon.agile.app.service.WorkLogService;
import io.choerodon.agile.domain.agile.entity.IssueE;
import io.choerodon.agile.domain.agile.entity.WorkLogE;
import io.choerodon.agile.domain.agile.repository.IssueRepository;
import io.choerodon.agile.domain.agile.repository.UserRepository;
import io.choerodon.agile.domain.agile.repository.WorkLogRepository;
import io.choerodon.agile.infra.dataobject.UserMessageDO;
import io.choerodon.agile.infra.dataobject.WorkLogDO;
import io.choerodon.agile.infra.mapper.IssueMapper;
import io.choerodon.agile.infra.mapper.WorkLogMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private WorkLogRepository workLogRepository;

    @Autowired
    private WorkLogMapper workLogMapper;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private IssueMapper issueMapper;

    @Autowired
    private UserRepository userRepository;

    private void setTo(Long issueId, BigDecimal predictionTime) {
        IssueE issueE = ConvertHelper.convert(issueMapper.selectByPrimaryKey(issueId), IssueE.class);
        issueE.setRemainingTime(predictionTime);
        issueRepository.update(issueE,new String[]{REMAINING_TIME_FIELD});
    }

    private BigDecimal getRemainTime(IssueE issueE, BigDecimal theTime) {
        BigDecimal zero = new BigDecimal(0);
        return issueE.getRemainingTime().subtract(theTime).compareTo(zero) < 0 ? zero : issueE.getRemainingTime().subtract(theTime);
    }

    private void reducePrediction(Long issueId, BigDecimal predictionTime) {
        IssueE issueE = ConvertHelper.convert(issueMapper.selectByPrimaryKey(issueId), IssueE.class);
        if (issueE.getRemainingTime() != null) {
            issueE.setRemainingTime(getRemainTime(issueE, predictionTime));
            issueRepository.update(issueE,new String[]{REMAINING_TIME_FIELD});
        }
    }

    private void selfAdjustment(Long issueId, BigDecimal workTime) {
        IssueE issueE = ConvertHelper.convert(issueMapper.selectByPrimaryKey(issueId), IssueE.class);
        if (issueE.getRemainingTime() != null) {
            issueE.setRemainingTime(getRemainTime(issueE, workTime));
            issueRepository.update(issueE,new String[]{REMAINING_TIME_FIELD});
        }
    }

    @Override
    public WorkLogDTO create(Long projectId, WorkLogDTO workLogDTO) {
        if (!projectId.equals(workLogDTO.getProjectId())) {
            throw new CommonException("error.projectId.notEqual");
        }
        if (workLogDTO.getResidualPrediction() != null) {
            switch (workLogDTO.getResidualPrediction()) {
                case SELF_ADJUSTMENT:
                    selfAdjustment(workLogDTO.getIssueId(), workLogDTO.getWorkTime());
                    break;
                case NO_SET_PREDICTION_TIME:
                    break;
                case SET_TO:
                    setTo(workLogDTO.getIssueId(), workLogDTO.getPredictionTime());
                    break;
                case REDUCE:
                    reducePrediction(workLogDTO.getIssueId(), workLogDTO.getPredictionTime());
                    break;
                default:
                    break;
            }
        }
        WorkLogE workLogE = ConvertHelper.convert(workLogDTO, WorkLogE.class);
        workLogE = workLogRepository.create(workLogE);
        return queryWorkLogById(workLogE.getProjectId(), workLogE.getLogId());
    }

    @Override
    public WorkLogDTO update(Long projectId, Long logId, WorkLogDTO workLogDTO) {
        workLogDTO.setProjectId(projectId);
        WorkLogE workLogE = ConvertHelper.convert(workLogDTO, WorkLogE.class);
        workLogE = workLogRepository.update(workLogE);
        return queryWorkLogById(workLogE.getProjectId(), workLogE.getLogId());
    }

    @Override
    public void delete(Long projectId, Long logId) {
        workLogRepository.delete(logId);
    }

    @Override
    public WorkLogDTO queryWorkLogById(Long projectId, Long logId) {
        WorkLogDO workLogDO = new WorkLogDO();
        workLogDO.setProjectId(projectId);
        workLogDO.setLogId(logId);
        WorkLogDTO workLogDTO = ConvertHelper.convert(workLogMapper.selectOne(workLogDO), WorkLogDTO.class);
        workLogDTO.setUserName(userRepository.queryUserNameByOption(workLogDTO.getUserId(), true).getRealName());
        return workLogDTO;
    }

    @Override
    public List<WorkLogDTO> queryWorkLogListByIssueId(Long projectId, Long issueId) {
        WorkLogDO workLogDO = new WorkLogDO();
        workLogDO.setProjectId(projectId);
        workLogDO.setIssueId(issueId);
        List<WorkLogDTO> workLogDTOList = ConvertHelper.convertList(workLogMapper.select(workLogDO), WorkLogDTO.class);
        List<Long> assigneeIds = workLogDTOList.stream().filter(workLogDTO -> workLogDTO.getUserId() != null && !Objects.equals(workLogDTO.getUserId(), 0L)).map(WorkLogDTO::getUserId).distinct().collect(Collectors.toList());
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(assigneeIds, true);
        workLogDTOList.forEach(workLogDTO -> {
            String assigneeName = usersMap.get(workLogDTO.getUserId()) != null ? usersMap.get(workLogDTO.getUserId()).getName() : null;
            workLogDTO.setUserName(assigneeName);
        });
        return workLogDTOList;
    }
}
