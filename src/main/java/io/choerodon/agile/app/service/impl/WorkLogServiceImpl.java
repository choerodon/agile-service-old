package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.WorkLogDTO;
import io.choerodon.agile.app.service.WorkLogService;
import io.choerodon.agile.domain.agile.entity.DataLogE;
import io.choerodon.agile.domain.agile.entity.IssueE;
import io.choerodon.agile.domain.agile.entity.WorkLogE;
import io.choerodon.agile.domain.agile.repository.DataLogRepository;
import io.choerodon.agile.domain.agile.repository.IssueRepository;
import io.choerodon.agile.domain.agile.repository.UserRepository;
import io.choerodon.agile.domain.agile.repository.WorkLogRepository;
import io.choerodon.agile.infra.dataobject.DataLogDO;
import io.choerodon.agile.infra.dataobject.IssueDO;
import io.choerodon.agile.infra.dataobject.UserMessageDO;
import io.choerodon.agile.infra.dataobject.WorkLogDO;
import io.choerodon.agile.infra.mapper.DataLogMapper;
import io.choerodon.agile.infra.mapper.IssueMapper;
import io.choerodon.agile.infra.mapper.WorkLogMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/18.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class WorkLogServiceImpl implements WorkLogService {

    private static final String SELF_ADJUSTMENT = "self_adjustment";
    private static final String NO_SET_PREDICTION_TIME = "no_set_prediction_time";
    private static final String SET_TO = "set_to";
    private static final String REDUCE = "reduce";
    private static final String FILED_TIMEESTIMATE = "timeestimate";
    private static final String FILED_TIMESPENT = "timespent";
    private static final String FILED_WORKLOGID = "WorklogId";

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

    @Autowired
    private DataLogRepository dataLogRepository;

    @Autowired
    private DataLogMapper dataLogMapper;


    private void setTo(Long issueId, BigDecimal predictionTime) {
        IssueE issueE = ConvertHelper.convert(issueMapper.selectByPrimaryKey(issueId), IssueE.class);
        issueE.setRemainingTime(predictionTime);
        issueRepository.updateSelective(issueE);
    }

    private BigDecimal getRemainTime(IssueE issueE, BigDecimal theTime) {
        BigDecimal zero = new BigDecimal(0);
        return issueE.getRemainingTime().subtract(theTime).compareTo(zero) < 0 ? zero : issueE.getRemainingTime().subtract(theTime);
    }

    private void reducePrediction(Long issueId, BigDecimal predictionTime) {
        IssueE issueE = ConvertHelper.convert(issueMapper.selectByPrimaryKey(issueId), IssueE.class);
        if (issueE.getRemainingTime() != null) {
            issueE.setRemainingTime(getRemainTime(issueE, predictionTime));
            issueRepository.updateSelective(issueE);
        }
    }

    private void selfAdjustment(Long issueId, BigDecimal workTime) {
        IssueE issueE = ConvertHelper.convert(issueMapper.selectByPrimaryKey(issueId), IssueE.class);
        if (issueE.getRemainingTime() != null) {
            issueE.setRemainingTime(getRemainTime(issueE, workTime));
            issueRepository.updateSelective(issueE);
        }
    }

    private void dataLogWorkLog(Long projectId, WorkLogDTO workLogDTO, IssueDO originIssueDO, IssueDO issueDO, WorkLogE workLogE) {
        DataLogE timeestimateLog = new DataLogE();
        timeestimateLog.setProjectId(projectId);
        timeestimateLog.setIssueId(workLogDTO.getIssueId());
        timeestimateLog.setFiled(FILED_TIMEESTIMATE);
        BigDecimal zero = new BigDecimal(0);
        if (originIssueDO.getRemainingTime() != null && originIssueDO.getRemainingTime().compareTo(zero) > 0) {
            timeestimateLog.setOldValue(originIssueDO.getRemainingTime().toString());
            timeestimateLog.setOldString(originIssueDO.getRemainingTime().toString());
            timeestimateLog.setNewValue(issueDO.getRemainingTime().toString());
            timeestimateLog.setNewString(issueDO.getRemainingTime().toString());
            dataLogRepository.create(timeestimateLog);
        } else {
            timeestimateLog.setNewValue(zero.toString());
            timeestimateLog.setNewString(zero.toString());
            List<DataLogDO> data = dataLogMapper.select(ConvertHelper.convert(timeestimateLog, DataLogDO.class));
            if (data == null || data.isEmpty()) {
                dataLogRepository.create(timeestimateLog);
            }
        }

        DataLogDO dataLogDO = dataLogMapper.selectLastWorkLogById(projectId, workLogDTO.getIssueId(), FILED_TIMESPENT);
        System.out.println("============= " + dataLogDO.getIssueId() + " , == issueId = "+workLogDTO.getIssueId());
        DataLogE timeSpentLog = new DataLogE();
        if (dataLogDO != null) {
            timeSpentLog.setProjectId(projectId);
            timeSpentLog.setIssueId(workLogDTO.getIssueId());
            timeSpentLog.setFiled(FILED_TIMESPENT);
            timeSpentLog.setOldValue(dataLogDO.getNewValue());
            timeSpentLog.setOldString(dataLogDO.getNewString());
            BigDecimal newValue = new BigDecimal(dataLogDO.getNewValue());
            timeSpentLog.setNewValue(newValue.add(workLogDTO.getWorkTime()).toString());
            timeSpentLog.setNewString(newValue.add(workLogDTO.getWorkTime()).toString());
        } else {
            timeSpentLog.setProjectId(projectId);
            timeSpentLog.setIssueId(workLogDTO.getIssueId());
            timeSpentLog.setFiled(FILED_TIMESPENT);
            timeSpentLog.setNewValue(workLogDTO.getWorkTime().toString());
            timeSpentLog.setNewString(workLogDTO.getWorkTime().toString());
        }
        dataLogRepository.create(timeSpentLog);

        DataLogE workLogIdE = new DataLogE();
        workLogIdE.setProjectId(projectId);
        workLogIdE.setFiled(FILED_WORKLOGID);
        workLogIdE.setIssueId(workLogDTO.getIssueId());
        workLogIdE.setOldValue(workLogE.getLogId().toString());
        workLogIdE.setOldString(workLogE.getLogId().toString());
        dataLogRepository.create(workLogIdE);
    }

    @Override
    public WorkLogDTO create(Long projectId, WorkLogDTO workLogDTO) {
        if (!projectId.equals(workLogDTO.getProjectId())) {
            throw new CommonException("error.projectId.notEqual");
        }
        IssueDO originIssueDO = issueMapper.selectByPrimaryKey(workLogDTO.getIssueId());
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
        dataLogWorkLog(projectId, workLogDTO, originIssueDO, issueMapper.selectByPrimaryKey(workLogDTO.getIssueId()), workLogE);
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
        workLogDTO.setUserName(userRepository.queryUserNameByOption(workLogDTO.getUserId(), true));
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
