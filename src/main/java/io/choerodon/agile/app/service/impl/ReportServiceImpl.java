package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.ReportIssueDTO;
import io.choerodon.agile.app.service.ReportService;
import io.choerodon.agile.domain.agile.converter.SprintConverter;
import io.choerodon.agile.domain.agile.entity.*;
import io.choerodon.agile.infra.common.utils.DateUtil;
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
    @Autowired
    private DateUtil dateUtil;

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
        //==========================================不考虑状态拖动的情况
        //获取冲刺开启前的issue
        List<Long> issueIdList = reportMapper.queryIssueIdsBeforeSprintStart(sprintDO);
        //获取当前冲刺期间加入的issue
        List<Long> issueIdAddList = reportMapper.queryAddIssueIdsDuringSprint(sprintDO);
        //获取当前冲刺期间移除的issue
        List<Long> issueIdRemoveList = reportMapper.queryRemoveIssueIdsDuringSprint(sprintDO);
        // 获取冲刺开启前状态为done的issue
        List<Long> doneBeforeIssue = !issueIdList.isEmpty() ? reportMapper.queryDoneIssueIdsBeforeSprintStart(issueIdList, sprintDO) : null;
        //获取冲刺开启前的issue统计
        List<ReportIssueE> issueBeforeSprintList = !issueIdList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryAddIssueBeforeDuringSprint(issueIdList, sprintDO), ReportIssueE.class) : null;
        //获取当前冲刺期间加入的issue(包含加入时间)
        List<ReportIssueE> issueAddList = !issueIdAddList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryAddIssueDuringSprint(issueIdAddList, sprintDO), ReportIssueE.class) : null;
        //获取当前冲刺期间移除的issue(包含移除时间)
        List<ReportIssueE> issueRemoveList = !issueIdRemoveList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryRemoveIssueDuringSprint(issueIdRemoveList, sprintDO), ReportIssueE.class) : null;
        //==========================================考虑状态拖动的情况
        // 获取当前冲刺期间移动到done状态的issue
        List<Long> issueIdAddDoneList = reportMapper.queryAddDoneIssueIdsDuringSprint(sprintDO);
        // 获取当前冲刺期间移除done状态的issue
        List<Long> issueIdRemoveDoneList = reportMapper.queryRemoveDoneIssueIdsDuringSprint(sprintDO);
        //获取当前冲刺期间移动到done状态的issue(包含移动到done的时间)
        List<ReportIssueE> issueAddDoneList = !issueIdAddDoneList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryAddIssueDoneDetailDuringSprint(issueIdAddDoneList, sprintDO), ReportIssueE.class) : null;
        //获取当前冲刺期间done移动到非done状态的issue(包含移动到非done的时间)
        List<ReportIssueE> issueRemoveDoneList = !issueIdRemoveDoneList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryRemoveIssueDoneDetailDurationSprint(issueIdRemoveDoneList, sprintDO), ReportIssueE.class) : null;
        // 过滤开启冲刺前状态为done的issue，统计字段设为false（表示跳过统计）
        if (issueBeforeSprintList != null && !issueBeforeSprintList.isEmpty()) {
            if (doneBeforeIssue != null && !doneBeforeIssue.isEmpty()) {
                issueBeforeSprintList.stream().filter(reportIssueE ->
                        doneBeforeIssue.contains(reportIssueE.getIssueId()))
                        .forEach(reportIssueE -> reportIssueE.setStatistical(false));
            }
            reportIssueEList.addAll(issueBeforeSprintList);
        }
        if (issueAddList != null && !issueAddList.isEmpty()) {
            reportIssueEList.addAll(issueAddList);
        }
        if (issueRemoveList != null && !issueRemoveList.isEmpty()) {
            reportIssueEList.addAll(issueRemoveList);
        }
        if (issueAddDoneList != null && !issueAddDoneList.isEmpty()) {
            reportIssueEList.addAll(issueAddDoneList);
        }
        if (issueRemoveDoneList != null && !issueRemoveDoneList.isEmpty()) {
            reportIssueEList.addAll(issueRemoveDoneList);
        }
        //获取冲刺结束时的issue(结束前状态为done的issue不计入统计字段设为false)
        if (sprintDO.getStatusCode().equals(SPRINT_CLOSED)) {
            List<ReportIssueE> issueAfterSprintList = ConvertHelper.convertList(reportMapper.queryIssueAfterSprint(sprintDO), ReportIssueE.class);
            if (issueAfterSprintList != null && !issueAfterSprintList.isEmpty()) {
                reportIssueEList.addAll(issueAfterSprintList);
            }
        }
    }

    private void queryStoryPointsOrRemainingEstimatedTime(SprintE sprintE, List<ReportIssueE> reportIssueEList, String field) {
        SprintDO sprintDO = sprintConverter.entityToDo(sprintE);
        //==========================================不考虑状态拖动的情况
        //获取冲刺开启前的issue
        List<Long> issueIdList = reportMapper.queryIssueIdsBeforeSprintStart(sprintDO);
        //获取当前冲刺期间加入的issue
        List<Long> issueIdAddList = reportMapper.queryAddIssueIdsDuringSprint(sprintDO);
        //获取当前冲刺期间移除的issue
        List<Long> issueIdRemoveList = reportMapper.queryRemoveIssueIdsDuringSprint(sprintDO);
        //获取当前冲刺开启前的issue的变更字段值
        List<ReportIssueE> issueBeforeList = !issueIdList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryValueBeforeSprintStart(issueIdList, sprintDO, field), ReportIssueE.class) : null;
        //获取当前冲刺期间加入的issue(包含加入时间、加入时的字段值)
        List<ReportIssueE> issueAddList = !issueIdAddList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryAddIssueValueDuringSprint(issueIdAddList, sprintDO, field), ReportIssueE.class) : null;
        //获取当前冲刺期间移除的issue(包含移除时间、移除时的字段值)
        List<ReportIssueE> issueRemoveList = !issueIdRemoveList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryRemoveIssueValueDurationSprint(issueIdRemoveList, sprintDO, field), ReportIssueE.class) : null;
        //==========================================考虑冲刺期间所有issue的值更改情况
        List<Long> issueAllList = new ArrayList<>();
        if (!issueIdList.isEmpty()) {
            issueAllList.addAll(issueIdList);
        }
        if (!issueIdAddList.isEmpty()) {
            issueAllList.addAll(issueIdAddList);
        }
        if (!issueIdRemoveList.isEmpty()) {
            issueAllList.addAll(issueIdRemoveList);
        }
        issueAllList = issueAllList.stream().distinct().collect(Collectors.toList());
        //获取冲刺期间所有的当前值的变更(如果变更时间是在done状态或者是移出冲刺期间，计入统计字段设为false)
        List<ReportIssueE> issueChangeList = issueAllList != null && !issueAllList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryIssueValueDurationSprint(issueAllList, sprintDO, field), ReportIssueE.class) : null;
        if (issueChangeList != null && !issueChangeList.isEmpty()) {
            issueChangeList.parallelStream().forEach(reportIssueE -> {
                if (!reportMapper.checkIssueValueIsStatisticalDurationSprint(sprintDO.getSprintId(), reportIssueE.getIssueId(), reportIssueE.getDate())) {
                    reportIssueE.setStatistical(false);
                }
            });
        }
        //==========================================考虑状态拖动的情况
        // 获取当前冲刺期间移动到done状态的issue
        List<Long> issueIdAddDoneList = reportMapper.queryAddDoneIssueIdsDuringSprint(sprintDO);
        // 获取当前冲刺期间移除done状态的issue
        List<Long> issueIdRemoveDoneList = reportMapper.queryRemoveDoneIssueIdsDuringSprint(sprintDO);
        //获取当前冲刺期间移动到done状态的issue
        List<ReportIssueE> issueAddDoneList = !issueIdAddDoneList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryAddIssueDoneValueDuringSprint(issueIdAddDoneList, sprintDO, field), ReportIssueE.class) : null;
        //获取当前冲刺期间done移动到非done状态的issue
        List<ReportIssueE> issueRemoveDoneList = !issueIdRemoveDoneList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryRemoveIssueDoneValueDurationSprint(issueIdRemoveDoneList, sprintDO, field), ReportIssueE.class) : null;
        // 获取冲刺开启前状态为done的issue的变更字段总和
        List<Long> doneIssueBeforeSprint = !issueIdList.isEmpty() ? reportMapper.queryDoneIssueIdsBeforeSprintStart(issueIdList, sprintDO) : null;
        // 过滤开启冲刺前状态为done的issue，统计字段设为false（表示跳过统计）
        if (issueBeforeList != null) {
            if (doneIssueBeforeSprint != null && !doneIssueBeforeSprint.isEmpty()) {
                issueBeforeList.stream().filter(reportIssueE -> doneIssueBeforeSprint.contains(reportIssueE.getIssueId())).
                        forEach(reportIssueE -> reportIssueE.setStatistical(false));
            }
            reportIssueEList.addAll(issueBeforeList);
        }
        if (issueAddList != null) {
            reportIssueEList.addAll(issueAddList);
        }
        if (issueRemoveList != null) {
            reportIssueEList.addAll(issueRemoveList);
        }
        if (issueChangeList != null) {
            reportIssueEList.addAll(issueChangeList);
        }
        if (issueAddDoneList != null) {
            reportIssueEList.addAll(issueAddDoneList);
        }
        if (issueRemoveDoneList != null) {
            reportIssueEList.addAll(issueRemoveDoneList);
        }
        //获取冲刺结束时的issue(结束前状态为done的issue计入统计字段设为false)
        if (sprintDO.getStatusCode().equals(SPRINT_CLOSED)) {
            List<ReportIssueE> issueAfterSprintList = ConvertHelper.convertList(reportMapper.queryIssueValueAfterSprint(sprintDO, field), ReportIssueE.class);
            if (issueAfterSprintList != null && !issueAfterSprintList.isEmpty()) {
                reportIssueEList.addAll(issueAfterSprintList);
            }
        }
    }

}

