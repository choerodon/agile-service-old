package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.ReportIssueDTO;
import io.choerodon.agile.app.service.ReportService;
import io.choerodon.agile.domain.agile.converter.SprintConverter;
import io.choerodon.agile.domain.agile.entity.*;
import io.choerodon.agile.infra.dataobject.SprintDO;
import io.choerodon.agile.infra.mapper.ReportMapper;
import io.choerodon.agile.infra.mapper.SprintMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/19
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ReportServiceImpl implements ReportService {

    @Autowired
    private SprintMapper sprintMapper;
    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private SprintConverter sprintConverter;

    private static final String STORY_POINTS = "storyPoints";
    private static final String REMAINING_ESTIMATED_TIME = "remainingEstimatedTime";
    private static final String ISSUE_COUNT = "issueCount";
    private static final String SPRINT_PLANNING_CODE = "sprint_planning";
    private static final String REPORT_SPRINT_ERROR = "error.report.sprintError";
    private static final String FILED_TIMEESTIMATE = "timeestimate";
    private static final String FILED_STORY_POINTS = "Story Points";
    private static final String SPRINT_CLOSED = "closed";


    @Override
    public List<ReportIssueDTO> queryBurnDownReport(Long projectId, Long sprintId, String type) {
        List<ReportIssueE> reportIssueEList = new ArrayList<>();
        SprintDO sprintDO = new SprintDO();
        sprintDO.setSprintId(sprintId);
        sprintDO.setProjectId(projectId);
        SprintE sprintE = sprintConverter.doToEntity(sprintMapper.selectOne(sprintDO));
        if (sprintE != null && !sprintE.getStatusCode().equals(SPRINT_PLANNING_CODE)) {
            sprintE.initStartAndEndTime();
            switch (type) {
                case STORY_POINTS:
                    queryStoryPointsOrRemainingEstimatedTime(sprintE, reportIssueEList, FILED_STORY_POINTS);
                    break;
                case REMAINING_ESTIMATED_TIME:
                    queryStoryPointsOrRemainingEstimatedTime(sprintE, reportIssueEList, FILED_TIMEESTIMATE);
                    break;
                case ISSUE_COUNT:
                    queryIssueCount(sprintE, reportIssueEList);
                    break;
                default:
                    queryStoryPointsOrRemainingEstimatedTime(sprintE, reportIssueEList, FILED_STORY_POINTS);
                    break;
            }
        } else {
            throw new CommonException(REPORT_SPRINT_ERROR);
        }
        return ConvertHelper.convertList(reportIssueEList.stream().
                sorted(Comparator.comparing(ReportIssueE::getDate)).collect(Collectors.toList()), ReportIssueDTO.class);
    }

    private void queryIssueCount(SprintE sprintE, List<ReportIssueE> reportIssueEList) {
        SprintDO sprintDO = sprintConverter.entityToDo(sprintE);
        //==========================================不考虑状态拖动的情况(包含子任务)
        //获取冲刺开启前的issue
        List<Long> issueIdBeforeSprintList = reportMapper.queryIssueIdsBeforeSprintStart(sprintDO);
        //获取冲刺开启前的issue统计
        handleIssueCountBeforeSprint(sprintDO, reportIssueEList, issueIdBeforeSprintList);
        //获取当前冲刺期间加入的issue
        List<Long> issueIdAddList = !issueIdBeforeSprintList.isEmpty() ? reportMapper.queryAddIssueIdsDuringSprint(sprintDO, issueIdBeforeSprintList) : reportMapper.queryAddIssueIdsDuringSprintNoBefore(sprintDO);
        //获取当前冲刺期间加入的issue(包含加入时间)
        handleAddIssueCountDuringSprint(sprintDO, reportIssueEList, issueIdAddList);
        //获取当前冲刺期间移除的issue
        List<Long> issueIdRemoveList = queryRemoveIssueIdsDuringSprint(sprintDO.getSprintId(), issueIdBeforeSprintList);
        //获取当前冲刺期间移除的issue(包含移除时间)
        handleRemoveCountDuringSprint(sprintDO, reportIssueEList, issueIdRemoveList);
        //获取冲刺结束时的issue(结束前状态为done的issue不计入统计字段设为false)(包含子任务)
        handleIssueCountAfterSprint(sprintDO, reportIssueEList);
        //==========================================考虑状态拖动的情况(包含子任务)
        //获取冲刺期间所有操作到的issue
        List<Long> issueAllList = getAllIssueDuringSprint(issueIdBeforeSprintList, issueIdAddList, issueIdRemoveList);
        //获取当前冲刺期间移动到done状态的issue(包含移动到done的时间)(包含子任务)
        handleAddDoneIssueCountDuringSprint(sprintDO, reportIssueEList, issueAllList);
        //获取当前冲刺期间done移动到非done状态的issue(包含移动到非done的时间)(包含子任务)
        handleRemoveDoneIssueCountDuringSprint(sprintDO, reportIssueEList, issueAllList);
    }

    private void queryStoryPointsOrRemainingEstimatedTime(SprintE sprintE, List<ReportIssueE> reportIssueEList, String field) {
        SprintDO sprintDO = sprintConverter.entityToDo(sprintE);
        //==========================================不考虑状态拖动的情况(包含子任务)
        //获取冲刺开启前的issue
        List<Long> issueIdBeforeSprintList = reportMapper.queryIssueIdsBeforeSprintStart(sprintDO);
        //获取当前冲刺期间加入的issue
        List<Long> issueIdAddList = !issueIdBeforeSprintList.isEmpty() ? reportMapper.queryAddIssueIdsDuringSprint(sprintDO, issueIdBeforeSprintList) : reportMapper.queryAddIssueIdsDuringSprintNoBefore(sprintDO);
        //获取当前冲刺期间移除的issue
        List<Long> issueIdRemoveList = queryRemoveIssueIdsDuringSprint(sprintDO.getSprintId(), issueIdBeforeSprintList);
        //获取当前冲刺开启前的issue的变更字段值(包含子任务)
        handleIssueValueBeforeSprint(sprintDO, reportIssueEList, issueIdBeforeSprintList, field);
        //获取当前冲刺期间加入的issue(包含加入时间、加入时的字段值)(包含子任务)
        handleAddIssueValueDuringSprint(sprintDO, reportIssueEList, issueIdAddList, field);
        //获取当前冲刺期间移除的issue(包含移除时间、移除时的字段值)(包含子任务)
        handleRemoveIssueValueDuringSprint(sprintDO, reportIssueEList, issueIdRemoveList, field);
        //==========================================考虑冲刺期间所有issue的值更改情况
        //获取冲刺期间所有操作到的issue
        List<Long> issueAllList = getAllIssueDuringSprint(issueIdBeforeSprintList, issueIdAddList, issueIdRemoveList);
        handleChangeIssueValueDuringSprint(sprintDO, reportIssueEList, issueAllList, field);
        //==========================================考虑状态拖动的情况
        //获取当前冲刺期间移动到done状态的issue
        handleAddDoneIssueValueDuringSprint(sprintDO, reportIssueEList, field, issueAllList);
        //获取当前冲刺期间done移动到非done状态的issue
        handleRemoveDoneIssueValueDuringSprint(sprintDO, reportIssueEList, field, issueAllList);
        //获取冲刺结束时的issue(结束前状态为done的issue计入统计字段设为false)
        handleIssueValueAfterSprint(sprintDO, reportIssueEList, field);
    }

    private List<Long> getAllIssueDuringSprint(List<Long> issueIdList, List<Long> issueIdAddList, List<Long> issueIdRemoveList) {
        List<Long> issueAllList = new ArrayList<>();
        if (issueIdList != null && !issueIdList.isEmpty()) {
            issueAllList.addAll(issueIdList);
        }
        if (issueIdAddList != null && !issueIdAddList.isEmpty()) {
            issueAllList.addAll(issueIdAddList);
        }
        if (issueIdRemoveList != null && !issueIdRemoveList.isEmpty()) {
            issueAllList.addAll(issueIdRemoveList);
        }
        return issueAllList.stream().distinct().collect(Collectors.toList());
    }


    private void handleIssueCountAfterSprint(SprintDO sprintDO, List<ReportIssueE> reportIssueEList) {
        if (sprintDO.getStatusCode().equals(SPRINT_CLOSED)) {
            List<ReportIssueE> issueAfterSprintList = ConvertHelper.convertList(reportMapper.queryIssueCountAfterSprint(sprintDO), ReportIssueE.class);
            if (issueAfterSprintList != null && !issueAfterSprintList.isEmpty()) {
                reportIssueEList.addAll(issueAfterSprintList);
            }
        }
    }

    private void handleRemoveDoneIssueCountDuringSprint(SprintDO sprintDO, List<ReportIssueE> reportIssueEList, List<Long> issueAllList) {
        // 获取当前冲刺期间移除done状态的issue
        List<Long> issueIdRemoveDoneList = issueAllList != null && !issueAllList.isEmpty() ? reportMapper.queryRemoveDoneIssueIdsDuringSprint(sprintDO, issueAllList) : null;
        List<ReportIssueE> issueRemoveDoneList = issueIdRemoveDoneList != null && !issueIdRemoveDoneList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryRemoveIssueDoneDetailDurationSprint(issueIdRemoveDoneList, sprintDO), ReportIssueE.class) : null;
        if (issueRemoveDoneList != null && !issueRemoveDoneList.isEmpty()) {
            reportIssueEList.addAll(issueRemoveDoneList);
        }
    }

    private void handleAddDoneIssueCountDuringSprint(SprintDO sprintDO, List<ReportIssueE> reportIssueEList, List<Long> issueAllList) {
        // 获取当前冲刺期间移动到done状态的issue
        List<Long> issueIdAddDoneList = issueAllList != null && !issueAllList.isEmpty() ? reportMapper.queryAddDoneIssueIdsDuringSprint(sprintDO, issueAllList) : null;
        List<ReportIssueE> issueAddDoneList = issueIdAddDoneList != null && !issueIdAddDoneList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryAddIssueDoneDetailDuringSprint(issueIdAddDoneList, sprintDO), ReportIssueE.class) : null;
        if (issueAddDoneList != null && !issueAddDoneList.isEmpty()) {
            reportIssueEList.addAll(issueAddDoneList);
        }
    }

    private void handleRemoveCountDuringSprint(SprintDO sprintDO, List<ReportIssueE> reportIssueEList, List<Long> issueIdRemoveList) {
        List<ReportIssueE> issueRemoveList = issueIdRemoveList != null && !issueIdRemoveList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryRemoveIssueDuringSprint(issueIdRemoveList, sprintDO), ReportIssueE.class) : null;
        if (issueRemoveList != null && !issueRemoveList.isEmpty()) {
            //移除时，状态为done的不计入统计
            issueRemoveList.parallelStream().forEach(this::handleDoneStatusIssue);
            reportIssueEList.addAll(issueRemoveList);
        }
    }

    private void handleAddIssueCountDuringSprint(SprintDO sprintDO, List<ReportIssueE> reportIssueEList, List<Long> issueIdAddList) {
        List<ReportIssueE> issueAddList = issueIdAddList != null && !issueIdAddList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryAddIssueDuringSprint(issueIdAddList, sprintDO), ReportIssueE.class) : null;
        if (issueAddList != null && !issueAddList.isEmpty()) {
            //在冲刺开始期间创建的，没有生成加入冲刺日志时间，要处理
            issueAddList.stream().filter(reportIssueE -> reportIssueE.getDate() == null).forEach(reportIssueE -> reportIssueE.setDate(reportMapper.queryAddIssueDuringSprintNoData(reportIssueE.getIssueId(), sprintDO.getSprintId())));
            //添加时，状态为done的不计入统计
            issueAddList.parallelStream().forEach(this::handleDoneStatusIssue);
            reportIssueEList.addAll(issueAddList);
        }
    }

    private void handleIssueCountBeforeSprint(SprintDO sprintDO, List<ReportIssueE> reportIssueEList, List<Long> issueIdBeforeSprintList) {
        //获取冲刺开启前状态为done的issue
        List<Long> doneBeforeIssue = !issueIdBeforeSprintList.isEmpty() ? reportMapper.queryDoneIssueIdsBeforeSprintStart(issueIdBeforeSprintList, sprintDO) : null;
        List<ReportIssueE> issueBeforeSprintList = !issueIdBeforeSprintList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryAddIssueBeforeDuringSprint(issueIdBeforeSprintList, sprintDO), ReportIssueE.class) : null;
        // 过滤开启冲刺前状态为done的issue，统计字段设为false（表示跳过统计）
        if (issueBeforeSprintList != null && !issueBeforeSprintList.isEmpty()) {
            if (doneBeforeIssue != null && !doneBeforeIssue.isEmpty()) {
                issueBeforeSprintList.stream().filter(reportIssueE ->
                        doneBeforeIssue.contains(reportIssueE.getIssueId()))
                        .forEach(reportIssueE -> reportIssueE.setStatistical(false));
            }
            reportIssueEList.addAll(issueBeforeSprintList);
        }
    }

    private void handleIssueValueAfterSprint(SprintDO sprintDO, List<ReportIssueE> reportIssueEList, String field) {
        if (sprintDO.getStatusCode().equals(SPRINT_CLOSED)) {
            List<ReportIssueE> issueAfterSprintList = ConvertHelper.convertList(reportMapper.queryIssueValueAfterSprint(sprintDO, field), ReportIssueE.class);
            if (issueAfterSprintList != null && !issueAfterSprintList.isEmpty()) {
                reportIssueEList.addAll(issueAfterSprintList);
            }
        }
    }

    private void handleRemoveDoneIssueValueDuringSprint(SprintDO sprintDO, List<ReportIssueE> reportIssueEList, String field, List<Long> issueAllList) {
        // 获取当前冲刺期间移除done状态的issue
        List<Long> issueIdRemoveDoneList = issueAllList != null && !issueAllList.isEmpty() ? reportMapper.queryRemoveDoneIssueIdsDuringSprint(sprintDO, issueAllList) : null;
        List<ReportIssueE> issueRemoveDoneList = issueIdRemoveDoneList != null && !issueIdRemoveDoneList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryRemoveIssueDoneValueDurationSprint(issueIdRemoveDoneList, sprintDO, field), ReportIssueE.class) : null;
        if (issueRemoveDoneList != null) {
            reportIssueEList.addAll(issueRemoveDoneList);
        }
    }

    private void handleAddDoneIssueValueDuringSprint(SprintDO sprintDO, List<ReportIssueE> reportIssueEList, String field, List<Long> issueAllList) {
        // 获取当前冲刺期间移动到done状态的issue
        List<Long> issueIdAddDoneList = issueAllList != null && !issueAllList.isEmpty() ? reportMapper.queryAddDoneIssueIdsDuringSprint(sprintDO, issueAllList) : null;
        List<ReportIssueE> issueAddDoneList = issueIdAddDoneList != null && !issueIdAddDoneList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryAddIssueDoneValueDuringSprint(issueIdAddDoneList, sprintDO, field), ReportIssueE.class) : null;
        if (issueAddDoneList != null && !issueIdAddDoneList.isEmpty()) {
            reportIssueEList.addAll(issueAddDoneList);
        }
    }

    private void handleChangeIssueValueDuringSprint(SprintDO sprintDO, List<ReportIssueE> reportIssueEList, List<Long> issueAllList, String field) {
        //获取冲刺期间所有的当前值的变更
        List<ReportIssueE> issueChangeList = issueAllList != null && !issueAllList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryIssueChangeValueDurationSprint(issueAllList, sprintDO, field), ReportIssueE.class) : null;
        if (issueChangeList != null && !issueChangeList.isEmpty()) {
            issueChangeList.parallelStream().forEach(reportIssueE -> {
                //(如果变更时间是在done状态或者是移出冲刺期间，计入统计字段设为false)
                if (!reportMapper.checkIssueValueIsStatisticalDurationSprint(sprintDO.getSprintId(), reportIssueE.getIssueId(), reportIssueE.getDate())) {
                    reportIssueE.setStatistical(false);
                }
            });
        }
        if (issueChangeList != null) {
            reportIssueEList.addAll(issueChangeList);
        }
    }

    private void handleRemoveIssueValueDuringSprint(SprintDO sprintDO, List<ReportIssueE> reportIssueEList, List<Long> issueIdRemoveList, String field) {
        List<ReportIssueE> issueRemoveList = issueIdRemoveList != null && !issueIdRemoveList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryRemoveIssueValueDurationSprint(issueIdRemoveList, sprintDO, field), ReportIssueE.class) : null;
        if (issueRemoveList != null && !issueRemoveList.isEmpty()) {
            //移除时，状态为done的不计入统计
            issueRemoveList.parallelStream().forEach(this::handleDoneStatusIssue);
            reportIssueEList.addAll(issueRemoveList);
        }

    }

    private void handleDoneStatusIssue(ReportIssueE reportIssueE) {
        if (reportMapper.checkIssueDoneStatus(reportIssueE.getIssueId(), reportIssueE.getDate())) {
            reportIssueE.setStatistical(false);
        }
    }

    private void handleAddIssueValueDuringSprint(SprintDO sprintDO, List<ReportIssueE> reportIssueEList, List<Long> issueIdAddList, String field) {
        List<ReportIssueE> issueAddList = issueIdAddList != null && !issueIdAddList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryAddIssueValueDuringSprint(issueIdAddList, sprintDO, field), ReportIssueE.class) : null;
        if (issueAddList != null && !issueAddList.isEmpty()) {
            //在冲刺开始期间创建的，没有生成加入冲刺日志时间，要处理
            issueAddList.stream().filter(reportIssueE -> reportIssueE.getDate() == null).forEach(reportIssueE -> {
                Date date = reportMapper.queryAddIssueDuringSprintNoData(reportIssueE.getIssueId(), sprintDO.getSprintId());
                reportIssueE.setDate(date);
                Integer value = reportMapper.queryAddIssueValueDuringSprintNoData(reportIssueE.getIssueId(), date, field);
                reportIssueE.setNewValue(value == null ? 0 : value);
            });
            //添加时，状态为done的不计入统计
            issueAddList.parallelStream().forEach(this::handleDoneStatusIssue);
            reportIssueEList.addAll(issueAddList);
        }
    }

    private void handleIssueValueBeforeSprint(SprintDO sprintDO, List<ReportIssueE> reportIssueEList, List<Long> issueIdBeforeSprintList, String field) {
        List<ReportIssueE> issueBeforeList = !issueIdBeforeSprintList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryValueBeforeSprintStart(issueIdBeforeSprintList, sprintDO, field), ReportIssueE.class) : null;
        // 获取冲刺开启前状态为done的issue
        List<Long> doneIssueBeforeSprint = !issueIdBeforeSprintList.isEmpty() ? reportMapper.queryDoneIssueIdsBeforeSprintStart(issueIdBeforeSprintList, sprintDO) : null;
        // 过滤开启冲刺前状态为done的issue，统计字段设为false（表示跳过统计）
        if (issueBeforeList != null) {
            if (doneIssueBeforeSprint != null && !doneIssueBeforeSprint.isEmpty()) {
                issueBeforeList.stream().filter(reportIssueE -> doneIssueBeforeSprint.contains(reportIssueE.getIssueId())).
                        forEach(reportIssueE -> reportIssueE.setStatistical(false));
            }
            reportIssueEList.addAll(issueBeforeList);
        }
    }

    private List<Long> queryRemoveIssueIdsDuringSprint(Long sprintId, List<Long> issueIdBeforeSprintList) {
        List<Long> issueId = reportMapper.queryIssueIdsBySprintId(sprintId);
        return issueId != null && !issueId.isEmpty() ? issueIdBeforeSprintList.stream().filter(id -> !issueId.contains(id)).collect(Collectors.toList()) : null;
    }

}

