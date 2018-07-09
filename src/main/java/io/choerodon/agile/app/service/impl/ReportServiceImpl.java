package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.app.assembler.IssueAssembler;
import io.choerodon.agile.app.assembler.ReportAssembler;
import io.choerodon.agile.app.service.ReportService;
import io.choerodon.agile.domain.agile.converter.SprintConverter;
import io.choerodon.agile.domain.agile.entity.ReportIssueE;
import io.choerodon.agile.domain.agile.entity.SprintE;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
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
    private SprintServiceImpl sprintService;
    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private SprintConverter sprintConverter;
    @Autowired
    private ColumnStatusRelMapper columnStatusRelMapper;
    @Autowired
    private BoardColumnMapper boardColumnMapper;
    @Autowired
    private ReportAssembler reportAssembler;
    @Autowired
    private ProductVersionMapper versionMapper;
    @Autowired
    private IssueAssembler issueAssembler;

    private static final String STORY_POINTS = "storyPoints";
    private static final String REMAINING_ESTIMATED_TIME = "remainingEstimatedTime";
    private static final String ISSUE_COUNT = "issueCount";
    private static final String SPRINT_PLANNING_CODE = "sprint_planning";
    private static final String REPORT_SPRINT_ERROR = "error.report.sprintError";
    private static final String REPORT_FILTER_ERROR = "error.cumulativeFlowDiagram.filter";
    private static final String FILED_TIMEESTIMATE = "timeestimate";
    private static final String FILED_STORY_POINTS = "Story Points";
    private static final String SPRINT_CLOSED = "closed";
    private static final String VERSION_ARCHIVED_CODE = "archived";
    private static final String VERSION_REPORT_ERROR = "error.report.version";

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

    @Override
    public CumulativeFlowDiagramDTO queryCumulativeFlowDiagram(Long projectId, CumulativeFlowFilterDTO cumulativeFlowFilterDTO) {
        //获取当前符合条件的所有issueIds
        CumulativeFlowDiagramDTO cumulativeFlowDiagramDTO = new CumulativeFlowDiagramDTO();
        cumulativeFlowDiagramDTO.setColumnDTOList(new ArrayList<>());
        cumulativeFlowDiagramDTO.setColumnChangeDTOList(new ArrayList<>());
        String filterSql = null;
        if (cumulativeFlowFilterDTO.getQuickFilterIds() != null && !cumulativeFlowFilterDTO.getQuickFilterIds().isEmpty()) {
            filterSql = sprintService.getQuickFilter(cumulativeFlowFilterDTO.getQuickFilterIds());
        }
        List<Long> allIssueIds = reportMapper.queryAllIssueIdsByFilter(projectId, filterSql);
        if (allIssueIds != null && !allIssueIds.isEmpty() && cumulativeFlowFilterDTO.getColumnIds() != null && !cumulativeFlowFilterDTO.getColumnIds().isEmpty()) {
            List<ColumnChangeDTO> result = new ArrayList<>();
            //所有在当前时间内创建的issue
            handleCumulativeFlowAddDuringDate(allIssueIds, result, cumulativeFlowFilterDTO);
            //所有在当前时间内状态改变的issue
            handleCumulativeFlowChangeDuringDate(cumulativeFlowFilterDTO, allIssueIds, result);
            //过滤并排序
            List<ColumnChangeDTO> columnChangeDTOList = new ArrayList<>();
            columnChangeDTOList.addAll(result.stream().filter(columnChangeDTO ->
                    columnChangeDTO.getColumnTo() != null && !columnChangeDTO.getColumnFrom().equals(columnChangeDTO.getColumnTo()))
                    .sorted(Comparator.comparing(ColumnChangeDTO::getDate)).collect(Collectors.toList()));
            //对传入时间点的数据给与坐标
            if (!columnChangeDTOList.isEmpty()) {
                if (columnChangeDTOList.get(0).getDate().after(cumulativeFlowFilterDTO.getStartDate())) {
                    addStartColumnChangeByDate(columnChangeDTOList, cumulativeFlowFilterDTO.getStartDate());
                }
                if (!columnChangeDTOList.get(columnChangeDTOList.size() - 1).getDate().before(cumulativeFlowFilterDTO.getStartDate())) {
                    addEndColumnChangeByDate(columnChangeDTOList, cumulativeFlowFilterDTO.getEndDate());
                }
            } else {
                addStartColumnChangeByDate(columnChangeDTOList, cumulativeFlowFilterDTO.getStartDate());
                addEndColumnChangeByDate(columnChangeDTOList, cumulativeFlowFilterDTO.getEndDate());
            }

            cumulativeFlowDiagramDTO.getColumnChangeDTOList().addAll(columnChangeDTOList);
            cumulativeFlowDiagramDTO.setColumnDTOList(reportAssembler.columnListDoToDto(boardColumnMapper.queryColumnByColumnIds(cumulativeFlowFilterDTO.getColumnIds())));
            return cumulativeFlowDiagramDTO;
        } else if (cumulativeFlowFilterDTO.getColumnIds() == null || cumulativeFlowFilterDTO.getColumnIds().isEmpty()) {
            throw new CommonException(REPORT_FILTER_ERROR);
        } else {
            return cumulativeFlowDiagramDTO;
        }
    }

    private void addEndColumnChangeByDate(List<ColumnChangeDTO> columnChangeDTOList, Date endDate) {
        ColumnChangeDTO columnChangeDTO = new ColumnChangeDTO();
        columnChangeDTO.setDate(endDate);
        columnChangeDTO.setColumnTo("0");
        columnChangeDTO.setColumnFrom("0");
        columnChangeDTOList.add(columnChangeDTOList.size(), columnChangeDTO);
    }

    private void addStartColumnChangeByDate(List<ColumnChangeDTO> columnChangeDTOList, Date startDate) {
        ColumnChangeDTO columnChangeDTO = new ColumnChangeDTO();
        columnChangeDTO.setDate(startDate);
        columnChangeDTO.setColumnTo("0");
        columnChangeDTO.setColumnFrom("0");
        columnChangeDTOList.add(0, columnChangeDTO);
    }

    private void handleCumulativeFlowChangeDuringDate(CumulativeFlowFilterDTO cumulativeFlowFilterDTO, List<Long> allIssueIds, List<ColumnChangeDTO> result) {
        List<ColumnChangeDTO> changeIssueDuringDate = reportAssembler.columnChangeListDoToDto(reportMapper.queryChangeIssueDuringDate(cumulativeFlowFilterDTO.getStartDate(),
                cumulativeFlowFilterDTO.getEndDate(), allIssueIds, cumulativeFlowFilterDTO.getColumnIds()));
        if (changeIssueDuringDate != null && !changeIssueDuringDate.isEmpty()) {
            result.addAll(changeIssueDuringDate);
        }
    }

    @Override
    public Page<IssueListDTO> queryIssueByOptions(Long projectId, Long versionId, String status, PageRequest pageRequest) {
        ProductVersionDO versionDO = new ProductVersionDO();
        versionDO.setProjectId(projectId);
        versionDO.setVersionId(versionId);
        versionDO = versionMapper.selectOne(versionDO);
        if (versionDO == null || Objects.equals(versionDO.getStatusCode(), VERSION_ARCHIVED_CODE)) {
            throw new CommonException(VERSION_REPORT_ERROR);
        }
        pageRequest.resetOrder("ai", new HashMap<>());
        Page<IssueDO> reportIssuePage = PageHelper.doPageAndSort(pageRequest, () -> reportMapper.queryReportIssues(projectId, versionId, status));
        Page<IssueListDTO> reportPage = new Page<>();
        reportPage.setTotalPages(reportIssuePage.getTotalPages());
        reportPage.setTotalElements(reportIssuePage.getTotalElements());
        reportPage.setSize(reportIssuePage.getSize());
        reportPage.setNumberOfElements(reportIssuePage.getNumberOfElements());
        reportPage.setNumber(reportIssuePage.getNumber());
        reportPage.setContent(issueAssembler.issueDoToIssueListDto(reportIssuePage.getContent()));
        return reportPage;
    }

    @Override
    public Map<String, Object> queryVersionLineChart(Long projectId, Long versionId) {
        Map<String, Object> versionReportMap = new HashMap<>();
        ProductVersionDO versionDO = new ProductVersionDO();
        versionDO.setProjectId(projectId);
        versionDO.setVersionId(versionId);
        versionDO = versionMapper.selectOne(versionDO);
        if (versionDO == null || Objects.equals(versionDO.getStatusCode(), VERSION_ARCHIVED_CODE)) {
            throw new CommonException(VERSION_REPORT_ERROR);
        }
        List<VersionIssueChangeDO> versionIssues = new ArrayList<>();
        VersionIssueChangeDO nowVersionIssueChang = new VersionIssueChangeDO();
        List<Long> nowVersionIssue = reportMapper.queryIssueIdByVersionId(projectId, versionId);
        //查统计最末时间点的相关信息
        Integer nowTotalStoryPoints = reportMapper.queryTotalStoryPoints(projectId, nowVersionIssue);
        Integer nowCompletedStoryPoints = reportMapper.queryCompleteStoryPoints(projectId, nowVersionIssue);
        double nowUnEstimateCount = reportMapper.queryUnEstimateCount(projectId, nowVersionIssue);
        double nowIssueCount = nowVersionIssue.size();
        double nowUnEstimatedPercentage = nowUnEstimateCount / nowIssueCount;

        Date startDate = versionDO.getStartDate() != null ? versionDO.getStartDate() : versionDO.getCreationDate();
        Date endDate = new Date();
        List<VersionIssueChangeDO> versionChangeIssue = reportMapper.queryChangeIssue(projectId, versionId, startDate, endDate);

        //空指针异常
        nowVersionIssueChang.setPreDate(versionChangeIssue.isEmpty() ? startDate : versionChangeIssue.get(0).getChangeDate());

        nowVersionIssueChang.setChangeDate(endDate);
        nowVersionIssueChang.setIssueIds(new ArrayList<>(nowVersionIssue));
        versionIssues.add(nowVersionIssueChang);

        for (int i = 0; i < versionChangeIssue.size(); i++) {
            int j = i + 1;
            if (j < versionChangeIssue.size()) {
                versionChangeIssue.get(i).setPreDate(versionChangeIssue.get(j).getChangeDate());
            } else if (j == versionChangeIssue.size()) {
                versionChangeIssue.get(i).setPreDate(startDate);
            }
            nowVersionIssue.removeAll(versionChangeIssue.get(i).getAddIssueIds());
            nowVersionIssue.addAll(versionChangeIssue.get(i).getRemoveIssueIds());
            versionChangeIssue.get(i).setIssueIds(new ArrayList<>(nowVersionIssue));
        }
        versionIssues.addAll(versionChangeIssue);
        Set<Date> dateSet = new TreeSet<>((first, second) -> second.compareTo(first));
        //issue故事点变更
        List<IssueChangeDTO> storyPointChangeIssues = issueAssembler.issueChangeDOListToIssueChangeDTO(reportMapper.queryStoryPointChangeIssue(projectId, versionIssues));
        Map<Date, List<IssueChangeDTO>> storyPointChangeIssuesMap = storyPointChangeIssues.stream().collect(Collectors.groupingBy(IssueChangeDTO::getChangeDate));
        dateSet.addAll(storyPointChangeIssuesMap.keySet());

        //issue由未完成变为完成
        List<VersionIssueChangeDO> completedChangeIssues = reportMapper.queryCompletedChangeIssue(projectId, versionIssues, true);
        List<IssueChangeDTO> completedIssues = issueAssembler.issueChangeDOListToIssueChangeDTO(completedChangeIssues.isEmpty() ? new ArrayList<>() : reportMapper.queryChangIssue(projectId, completedChangeIssues));
        Map<Date, List<IssueChangeDTO>> completedIssuesMap = completedIssues.stream().collect(Collectors.groupingBy(IssueChangeDTO::getChangeDate));
        dateSet.addAll(completedIssuesMap.keySet());


        //issue由完成变为未完成
        List<VersionIssueChangeDO> unCompletedChangeIssues = reportMapper.queryCompletedChangeIssue(projectId, versionIssues, false);
        List<IssueChangeDTO> unCompletedIssues = issueAssembler.issueChangeDOListToIssueChangeDTO(unCompletedChangeIssues.isEmpty() ? new ArrayList<>() : reportMapper.queryChangIssue(projectId, unCompletedChangeIssues));
        Map<Date, List<IssueChangeDTO>> unCompletedIssuesMap = unCompletedIssues.stream().collect(Collectors.groupingBy(IssueChangeDTO::getChangeDate));
        dateSet.addAll(unCompletedIssuesMap.keySet());

        List<VersionIssueChangeDO> versionAddChangeIssue = versionChangeIssue.stream().filter(versionIssueChangeDO -> !versionIssueChangeDO.getAddIssueIds().isEmpty()).map(versionIssueChangeDO -> {
            versionIssueChangeDO.setIssueIds(versionIssueChangeDO.getAddIssueIds());
            return versionIssueChangeDO;
        }).collect(Collectors.toList());
        //issue移入
        List<IssueChangeDO> versionAddChangeIssues = versionAddChangeIssue.isEmpty() ? new ArrayList<>() : reportMapper.queryChangIssue(projectId, versionAddChangeIssue);
        List<IssueChangeDTO> addIssues = issueAssembler.issueChangeDOListToIssueChangeDTO(versionAddChangeIssues);
        Map<Date, List<IssueChangeDTO>> addIssuesMap = addIssues.stream().collect(Collectors.groupingBy(IssueChangeDTO::getChangeDate));
        dateSet.addAll(addIssuesMap.keySet());


        List<VersionIssueChangeDO> versionRemoveChangeIssue = versionChangeIssue.stream().filter(versionIssueChangeDO -> !versionIssueChangeDO.getRemoveIssueIds().isEmpty()).map(versionIssueChangeDO -> {
            versionIssueChangeDO.setIssueIds(versionIssueChangeDO.getRemoveIssueIds());
            return versionIssueChangeDO;
        }).collect(Collectors.toList());
        //issue移除
        List<IssueChangeDO> versionRemoveChangeIssues = versionRemoveChangeIssue.isEmpty() ? new ArrayList<>() : reportMapper.queryChangIssue(projectId, versionRemoveChangeIssue);
        List<IssueChangeDTO> removeIssues = issueAssembler.issueChangeDOListToIssueChangeDTO(versionRemoveChangeIssues);
        Map<Date, List<IssueChangeDTO>> removeIssuesMap = removeIssues.stream().collect(Collectors.groupingBy(IssueChangeDTO::getChangeDate));
        dateSet.addAll(removeIssuesMap.keySet());


        List<VersionReportDTO> versionReport = new ArrayList<>();
        VersionReportDTO nowVersionReportDTO = new VersionReportDTO();
        nowVersionReportDTO.setChangeDate(endDate);
        nowVersionReportDTO.setTotalStoryPoints(nowTotalStoryPoints);
        nowVersionReportDTO.setCompletedStoryPoints(nowCompletedStoryPoints);
        nowVersionReportDTO.setUnEstimatedPercentage(nowUnEstimatedPercentage);
        versionReport.add(nowVersionReportDTO);

        for (Date date : dateSet) {
            VersionReportDTO versionReportDTO = new VersionReportDTO();
            List<IssueChangeDTO> storyPointChangeIssue = storyPointChangeIssuesMap.get(date) != null ? storyPointChangeIssuesMap.get(date) : new ArrayList<>();
            List<IssueChangeDTO> completedIssue = completedIssuesMap.get(date) != null ? completedIssuesMap.get(date) : new ArrayList<>();
            List<IssueChangeDTO> unCompletedIssue = unCompletedIssuesMap.get(date) != null ? unCompletedIssuesMap.get(date) : new ArrayList<>();
            List<IssueChangeDTO> addIssue = addIssuesMap.get(date) != null ? addIssuesMap.get(date) : new ArrayList<>();
            List<IssueChangeDTO> removeIssue = removeIssuesMap.get(date) != null ? removeIssuesMap.get(date) : new ArrayList<>();

            Integer changeStoryPoints = storyPointChangeIssue.stream().mapToInt(storyPoint -> Integer.valueOf(storyPoint.getChangeStoryPoints())).sum();
            Integer addStoryPoints = addIssue.stream().mapToInt(storyPoint -> storyPoint.getNewValue() != null ? Integer.valueOf(storyPoint.getNewValue()) : 0).sum();
            Integer removePoints = removeIssue.stream().mapToInt(storyPoint -> storyPoint.getNewValue() != null ? Integer.valueOf(storyPoint.getNewValue()) : 0).sum();
            Integer completedPoints = completedIssue.stream().mapToInt(storyPoint -> storyPoint.getNewValue() != null ? Integer.valueOf(storyPoint.getNewValue()) : 0).sum();
            Integer unCompletedPoints = unCompletedIssue.stream().mapToInt(storyPoint -> storyPoint.getNewValue() != null ? Integer.valueOf(storyPoint.getNewValue()) : 0).sum();

            nowIssueCount = nowIssueCount - addIssue.size() + removeIssue.size();

            Integer changUnEstimatedCount = storyPointChangeIssue.stream().filter(storyPointChange -> storyPointChange.getNewValue() == null).collect(Collectors.toList()).size();
            Integer addUnEstimatedCount = addIssue.stream().filter(storyPointChange -> storyPointChange.getNewValue() == null).collect(Collectors.toList()).size();
            Integer removeUnEstimatedCount = removeIssue.stream().filter(storyPointChange -> storyPointChange.getNewValue() == null).collect(Collectors.toList()).size();
            nowUnEstimateCount = nowUnEstimateCount - changUnEstimatedCount - addUnEstimatedCount + removeUnEstimatedCount;

            nowTotalStoryPoints = nowTotalStoryPoints - changeStoryPoints - addStoryPoints + removePoints;
            nowCompletedStoryPoints = nowCompletedStoryPoints - completedPoints + unCompletedPoints;
            nowUnEstimatedPercentage = nowUnEstimateCount / nowIssueCount;
            versionReportDTO.setChangeDate(date);
            versionReportDTO.setTotalStoryPoints(nowTotalStoryPoints);
            versionReportDTO.setCompletedStoryPoints(nowCompletedStoryPoints);
            versionReportDTO.setUnEstimatedPercentage(nowUnEstimatedPercentage);
            versionReportDTO.setStoryPointsChangIssues(storyPointChangeIssue);
            versionReportDTO.setCompletedIssues(completedIssue);
            versionReportDTO.setUnCompletedIssues(unCompletedIssue);
            versionReportDTO.setAddIssues(addIssue);
            versionReportDTO.setRemoveIssues(removeIssue);
            versionReport.add(versionReportDTO);
        }
        versionReportMap.put("version", versionDO);
        versionReportMap.put("versionReport", versionReport);
        return versionReportMap;
    }

    private void handleCumulativeFlowAddDuringDate(List<Long> allIssueIds, List<ColumnChangeDTO> result, CumulativeFlowFilterDTO cumulativeFlowFilterDTO) {
        List<ColumnChangeDTO> addIssueDuringDate = reportAssembler.columnChangeListDoToDto(reportMapper.queryAddIssueDuringDate(cumulativeFlowFilterDTO.getStartDate(),
                cumulativeFlowFilterDTO.getEndDate(), allIssueIds, cumulativeFlowFilterDTO.getColumnIds()));
        if (addIssueDuringDate != null && !addIssueDuringDate.isEmpty()) {
            List<Long> statusToNullIssueIds = addIssueDuringDate.stream().filter(columnChangeDTO -> columnChangeDTO.getStatusTo() == null).map(ColumnChangeDTO::getIssueId).collect(Collectors.toList());
            Map<Long, ColumnStatusRelDO> columnStatusRelMap = columnStatusRelMapper.queryByIssueIdAndColumnIds(statusToNullIssueIds, cumulativeFlowFilterDTO.getColumnIds())
                    .stream().collect(Collectors.toMap(ColumnStatusRelDO::getIssueId, Function.identity()));
            addIssueDuringDate.parallelStream().forEach(columnChangeDTO -> {
                if (statusToNullIssueIds.contains(columnChangeDTO.getIssueId())) {
                    ColumnStatusRelDO columnStatusRelDO = columnStatusRelMap.get(columnChangeDTO.getIssueId());
                    if (columnStatusRelDO != null) {
                        columnChangeDTO.setColumnTo(columnStatusRelDO.getColumnId().toString());
                        columnChangeDTO.setStatusTo(columnStatusRelDO.getStatusId().toString());
                    }
                }
            });
            result.addAll(addIssueDuringDate);
        }
    }

    private void queryIssueCount(SprintE sprintE, List<ReportIssueE> reportIssueEList) {
        SprintDO sprintDO = sprintConverter.entityToDo(sprintE);
        //获取冲刺开启前的issue
        List<Long> issueIdBeforeSprintList = reportMapper.queryIssueIdsBeforeSprintStart(sprintDO);
        //获取冲刺开启前的issue统计
        handleIssueCountBeforeSprint(sprintDO, reportIssueEList, issueIdBeforeSprintList);
        //获取当前冲刺期间加入的issue
        List<Long> issueIdAddList = reportMapper.queryAddIssueIdsDuringSprint(sprintDO);
        //获取当前冲刺期间加入的issue
        handleAddIssueCountDuringSprint(sprintDO, reportIssueEList, issueIdAddList);
        //获取当前冲刺期间移除的issue
        List<Long> issueIdRemoveList = reportMapper.queryRemoveIssueIdsDuringSprint(sprintDO);
        //获取当前冲刺期间移除的issue
        handleRemoveCountDuringSprint(sprintDO, reportIssueEList, issueIdRemoveList);
        //获取冲刺结束时的issue
        handleIssueCountAfterSprint(sprintDO, reportIssueEList);
        //获取冲刺期间所有操作到的issue
        List<Long> issueAllList = getAllIssueDuringSprint(issueIdBeforeSprintList, issueIdAddList, issueIdRemoveList);
        //获取当前冲刺期间移动到done状态的issue
        handleAddDoneIssueCountDuringSprint(sprintDO, reportIssueEList, issueAllList);
        //获取当前冲刺期间移出done状态的issue
        handleRemoveDoneIssueCountDuringSprint(sprintDO, reportIssueEList, issueAllList);
    }

    private void queryStoryPointsOrRemainingEstimatedTime(SprintE sprintE, List<ReportIssueE> reportIssueEList, String field) {
        SprintDO sprintDO = sprintConverter.entityToDo(sprintE);
        //获取冲刺开启前的issue
        List<Long> issueIdBeforeSprintList = reportMapper.queryIssueIdsBeforeSprintStart(sprintDO);
        //获取当前冲刺期间加入的issue
        List<Long> issueIdAddList = reportMapper.queryAddIssueIdsDuringSprint(sprintDO);
        //获取当前冲刺期间移除的issue
        List<Long> issueIdRemoveList = reportMapper.queryRemoveIssueIdsDuringSprint(sprintDO);
        //获取当前冲刺开启前的issue信息
        handleIssueValueBeforeSprint(sprintDO, reportIssueEList, issueIdBeforeSprintList, field);
        //获取当前冲刺期间加入的issue信息
        handleAddIssueValueDuringSprint(sprintDO, reportIssueEList, issueIdAddList, field);
        //获取当前冲刺期间移除的issue信息
        handleRemoveIssueValueDuringSprint(sprintDO, reportIssueEList, issueIdRemoveList, field);
        //获取冲刺期间所有操作到的issue变更信息
        List<Long> issueAllList = getAllIssueDuringSprint(issueIdBeforeSprintList, issueIdAddList, issueIdRemoveList);
        handleChangeIssueValueDuringSprint(sprintDO, reportIssueEList, issueAllList, field);
        //获取当前冲刺期间移动到done状态的issue信息
        handleAddDoneIssueValueDuringSprint(sprintDO, reportIssueEList, field, issueAllList);
        //获取当前冲刺期间移出done状态的issue信息
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
            } else {
                ReportIssueE reportIssueE = new ReportIssueE();
                reportIssueE.initEndSprint(sprintDO.getActualEndDate());
                reportIssueEList.add(reportIssueE);
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
        } else {
            ReportIssueE reportIssueE = new ReportIssueE();
            reportIssueE.initStartSprint(sprintDO.getStartDate());
            reportIssueEList.add(reportIssueE);
        }
    }

    private void handleIssueValueAfterSprint(SprintDO sprintDO, List<ReportIssueE> reportIssueEList, String field) {
        if (sprintDO.getStatusCode().equals(SPRINT_CLOSED)) {
            List<ReportIssueE> issueAfterSprintList = ConvertHelper.convertList(reportMapper.queryIssueValueAfterSprint(sprintDO, field), ReportIssueE.class);
            if (issueAfterSprintList != null && !issueAfterSprintList.isEmpty()) {
                reportIssueEList.addAll(issueAfterSprintList);
            } else {
                ReportIssueE reportIssueE = new ReportIssueE();
                reportIssueE.initEndSprint(sprintDO.getEndDate());
                reportIssueEList.add(reportIssueE);
            }
        }
    }

    private void handleRemoveDoneIssueValueDuringSprint(SprintDO sprintDO, List<ReportIssueE> reportIssueEList, String field, List<Long> issueAllList) {
        // 获取当前冲刺期间移出done状态的issue
        List<Long> issueIdRemoveDoneList = issueAllList != null && !issueAllList.isEmpty() ? reportMapper.queryRemoveDoneIssueIdsDuringSprint(sprintDO, issueAllList) : null;
        List<ReportIssueE> issueRemoveDoneList = issueIdRemoveDoneList != null && !issueIdRemoveDoneList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryRemoveIssueDoneValueDurationSprint(issueIdRemoveDoneList, sprintDO, field), ReportIssueE.class) : null;
        if (issueRemoveDoneList != null && !issueRemoveDoneList.isEmpty()) {
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
                //变更时间是在done状态，计入统计字段设为false
                handleDoneStatusIssue(reportIssueE);
                //变更时间是在移出冲刺期间，计入统计字段设为false
                handleRemoveIssue(reportIssueE, sprintDO.getSprintId());
            });
            reportIssueEList.addAll(issueChangeList);
        }
    }

    private void handleRemoveIssue(ReportIssueE reportIssueE, Long sprintId) {
        Boolean result = reportMapper.checkIssueRemove(reportIssueE.getIssueId(), reportIssueE.getDate(), sprintId);
        result = (result != null && !result) || result == null;
        if (result) {
            reportIssueE.setStatistical(false);
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
        Boolean result = reportMapper.checkIssueDoneStatus(reportIssueE.getIssueId(), reportIssueE.getDate());
        if (result != null && !result) {
            reportIssueE.setStatistical(false);
        }
    }

    private void handleAddIssueValueDuringSprint(SprintDO sprintDO, List<ReportIssueE> reportIssueEList, List<Long> issueIdAddList, String field) {
        List<ReportIssueE> issueAddList = issueIdAddList != null && !issueIdAddList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryAddIssueValueDuringSprint(issueIdAddList, sprintDO, field), ReportIssueE.class) : null;
        if (issueAddList != null && !issueAddList.isEmpty()) {
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
        } else {
            ReportIssueE reportIssueE = new ReportIssueE();
            reportIssueE.initStartSprint(sprintDO.getStartDate());
            reportIssueEList.add(reportIssueE);
        }
    }

}

