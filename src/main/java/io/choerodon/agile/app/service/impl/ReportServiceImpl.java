package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.BurnDownChangeDTO;
import io.choerodon.agile.api.dto.CoordinateDTO;
import io.choerodon.agile.app.assembler.ReportAssembler;
import io.choerodon.agile.app.service.ReportService;
import io.choerodon.agile.domain.agile.entity.*;
import io.choerodon.agile.infra.dataobject.SprintDO;
import io.choerodon.agile.infra.mapper.ReportMapper;
import io.choerodon.agile.infra.mapper.SprintMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/19
 */
@Service
@Transactional(rollbackFor = CommonException.class)
public class ReportServiceImpl implements ReportService {

    @Autowired
    private SprintMapper sprintMapper;
    @Autowired
    private ReportAssembler reportAssembler;
    @Autowired
    private ReportMapper reportMapper;

    private static final String STORY_POINTS = "storyPoints";
    private static final String REMAINING_ESTIMATED_TIME = "remainingEstimatedTime";
    private static final String ORIGINAL_ESTIMATED_TIME = "originalEstimatedTime";
    private static final String ISSUE_COUNT = "issueCount";
    private static final String SPRINT_PLANNING_CODE = "sprint_planning";
    private static final String REPORT_SPRINT_ERROR = "error.report.sprintError";


    @Override
    public CoordinateDTO queryBurnDownCoordinate(Long projectId, Long sprintId, String type) {
        CoordinateE coordinateE = new CoordinateE();
        SprintDO sprintDO = new SprintDO();
        sprintDO.setSprintId(sprintId);
        sprintDO.setProjectId(projectId);
        SprintE sprintE = ConvertHelper.convert(sprintMapper.selectOne(sprintDO), SprintE.class);
        if (sprintE != null && !sprintE.getStatusCode().equals(SPRINT_PLANNING_CODE)) {
            coordinateE.initXAxis(sprintE);
            switch (type) {
                case STORY_POINTS:
                    queryStoryPoints(sprintE, coordinateE);
                    break;
                case REMAINING_ESTIMATED_TIME:
                    queryRemainingEstimatedTime(sprintE, coordinateE);
                    break;
                case ORIGINAL_ESTIMATED_TIME:
                    queryOriginalEstimatedTime(sprintE, coordinateE);
                    break;
                case ISSUE_COUNT:
                    queryIssueCount(sprintE, coordinateE);
                    break;
                default:
                    queryStoryPoints(sprintE, coordinateE);
                    break;
            }
        } else {
            throw new CommonException(REPORT_SPRINT_ERROR);
        }
        return reportAssembler.coordinateEToDto(coordinateE);
    }

    @Override
    public BurnDownChangeDTO queryBurnDown(Long projectId, Long sprintId, String type) {
        BurnDownChangeE burnDownChangeE = new BurnDownChangeE();
        SprintDO sprintDO = new SprintDO();
        sprintDO.setSprintId(sprintId);
        sprintDO.setProjectId(projectId);
        SprintE sprintE = ConvertHelper.convert(sprintMapper.selectOne(sprintDO), SprintE.class);
        if (sprintE != null && !sprintE.getStatusCode().equals(SPRINT_PLANNING_CODE)) {
            switch (type) {
                case STORY_POINTS:
                    queryStoryPointsReport(sprintE, burnDownChangeE);
                    break;
                case REMAINING_ESTIMATED_TIME:
                    queryRemainingEstimatedTimeReport(sprintE, burnDownChangeE);
                    break;
                case ORIGINAL_ESTIMATED_TIME:
                    queryOriginalEstimatedTimeReport(sprintE, burnDownChangeE);
                    break;
                case ISSUE_COUNT:
                    queryIssueCountReport(sprintE, burnDownChangeE);
                    break;
                default:
                    queryStoryPointsReport(sprintE, burnDownChangeE);
                    break;
            }
        } else {
            throw new CommonException(REPORT_SPRINT_ERROR);
        }
        return reportAssembler.burndownChangeEToDto(burnDownChangeE);
    }

    private void queryIssueCountReport(SprintE sprintE, BurnDownChangeE burnDownChangeE) {
        //todo 根据Issue计数返回燃尽图报告信息
    }

    private void queryOriginalEstimatedTimeReport(SprintE sprintE, BurnDownChangeE burnDownChangeE) {
        //todo 根据原始估计时间返回燃尽图报告信息
    }

    private void queryRemainingEstimatedTimeReport(SprintE sprintE, BurnDownChangeE burnDownChangeE) {
        //todo 根据剩余预估时间返回燃尽图报告信息
        burnDownChangeE.getSprintStartDataE().setStartDate(sprintE.getStartDate());
        //todo 查询开启冲刺时候的issue的剩余估计时间
//        burnDownChangeE.getSprintStartDataE().setIssueChangeDataEList(reportMapper.queryIssueChangeBeforeSprint(sprintE));
        if (sprintE.getEndDate() != null) {
            burnDownChangeE.getSprintEndDataE().setEndDate(sprintE.getEndDate());
//            burnDownChangeE.getSprintEndDataE().setIssueChangeDataEList(reportMapper.queryIssueChangeAfterSprint(sprintE));
        }
        //todo 获取当前冲刺期间加入的issue(包含加入时间、加入时的剩余预估时间)
//        burnDownChangeE.setIssueChangeDataEList(reportMapper.queryIssueRemoveChangeBySprint(sprintE));
        //todo 获取当前冲刺期间移除的issue(包含移除时间、移除时的剩余预估时间)
//        burnDownChangeE.setIssueChangeDataEList(reportMapper.queryIssueAddChangeBySprint(sprintE));
        burnDownChangeE.getIssueChangeDataEList().stream().sorted(Comparator.comparing(BurnDownChangeE.IssueChangeDataE::getChangeDate));
    }

    private void queryStoryPointsReport(SprintE sprintE, BurnDownChangeE burnDownChangeE) {
        //todo 根据故事点返回燃尽图报告信息
    }

    private void queryIssueCount(SprintE sprintE, CoordinateE coordinateE) {
        List<Integer> yAxis = new ArrayList<>();
        //todo 获取当前冲刺开启前的issue的Issue计数总和
        Integer issueTotalCount = reportMapper.queryIssueTotalCountBeforeSprintStart(sprintE);
        //todo 获取当前冲刺期间加入的issue(包含加入时间)
        List<ReportIssueE> issueAddList = reportMapper.queryAddIssueDurationSprint(sprintE);
        //todo 获取当前冲刺期间移除的issue(包含移除时间)
        List<ReportIssueE> issueRemoveList = reportMapper.queryRemoveIssueDurationSprint(sprintE);
        for (Date date : coordinateE.getxAxis()) {
            List<ReportIssueE> reportIssueAddList = issueAddList.stream().filter(reportIssueE -> reportIssueE.getDate().before(date))
                    .collect(Collectors.toList());
            Integer issueCount = reportIssueAddList.size();
            List<ReportIssueE> reportIssueRemoveList = issueRemoveList.stream().filter(reportIssueE -> reportIssueE.getDate().before(date))
                    .collect(Collectors.toList());
            issueCount -= reportIssueRemoveList.size();
            yAxis.add(issueTotalCount + issueCount);
        }
        coordinateE.setyAxis(yAxis);
    }

    private void queryOriginalEstimatedTime(SprintE sprintE, CoordinateE coordinateE) {
        //todo 根据原始估计时间返回坐标信息
    }

    private void queryRemainingEstimatedTime(SprintE sprintE, CoordinateE coordinateE) {
        List<Integer> yAxis = new ArrayList<>();
        //todo 获取当前冲刺开启前的issue的剩余预估时间总和
        Integer remainingTimeCount = reportMapper.queryRemainingTimesBeforeSprintStart(sprintE);
        //todo 获取当前冲刺期间加入的issue(包含加入时间、加入时的剩余预估时间)
        List<ReportIssueE> issueAddList = reportMapper.queryAddIssueDurationSprint(sprintE);
        //todo 获取当前冲刺期间移除的issue(包含移除时间、移除时的剩余预估时间)
        List<ReportIssueE> issueRemoveList = reportMapper.queryRemoveIssueDurationSprint(sprintE);
        for (Date date : coordinateE.getxAxis()) {
            Integer remainingTime = 0;
            List<ReportIssueE> reportIssueAddList = issueAddList.stream().filter(reportIssueE -> reportIssueE.getDate().before(date))
                    .collect(Collectors.toList());
            for (ReportIssueE reportIssueE : reportIssueAddList) {
                remainingTime += reportIssueE.getRemainingTime();
            }
            List<ReportIssueE> reportIssueRemoveList = issueRemoveList.stream().filter(reportIssueE -> reportIssueE.getDate().before(date))
                    .collect(Collectors.toList());
            for (ReportIssueE reportIssueE : reportIssueRemoveList) {
                remainingTime -= reportIssueE.getRemainingTime();
            }
            yAxis.add(remainingTimeCount + remainingTime);
        }
        coordinateE.setyAxis(yAxis);
    }

    private void queryStoryPoints(SprintE sprintE, CoordinateE coordinateE) {
        List<Integer> yAxis = new ArrayList<>();
        //todo 获取当前冲刺开启前的issue的故事点总和
        Integer storyPointCount = reportMapper.queryStoryPointsBeforeSprintStart(sprintE);
        //todo 获取当前冲刺期间加入的issue(包含加入时间、加入时的故事点)
        List<ReportIssueE> issueAddList = reportMapper.queryAddIssueDurationSprint(sprintE);
        //todo 获取当前冲刺期间移除的issue(包含移除时间、移除时的故事点)
        List<ReportIssueE> issueRemoveList = reportMapper.queryRemoveIssueDurationSprint(sprintE);
        for (Date date : coordinateE.getxAxis()) {
            Integer storyPoint = 0;
            if (issueAddList != null && !issueAddList.isEmpty()) {
                List<ReportIssueE> reportIssueAddList = issueAddList.stream().filter(reportIssueE -> reportIssueE.getDate().before(date))
                        .collect(Collectors.toList());
                for (ReportIssueE reportIssueE : reportIssueAddList) {
                    storyPoint += reportIssueE.getStoryPoint();
                }
            }
            if (issueRemoveList != null && !issueRemoveList.isEmpty()) {
                List<ReportIssueE> reportIssueRemoveList = issueRemoveList.stream().filter(reportIssueE -> reportIssueE.getDate().before(date))
                        .collect(Collectors.toList());
                for (ReportIssueE reportIssueE : reportIssueRemoveList) {
                    storyPoint -= reportIssueE.getStoryPoint();
                }
            }
            yAxis.add(storyPointCount + storyPoint);
        }
        coordinateE.setyAxis(yAxis);
    }
}
