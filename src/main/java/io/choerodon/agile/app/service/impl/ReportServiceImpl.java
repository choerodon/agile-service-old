package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.app.assembler.IssueAssembler;
import io.choerodon.agile.app.assembler.ReportAssembler;
import io.choerodon.agile.app.service.ReportService;
import io.choerodon.agile.domain.agile.converter.SprintConverter;
import io.choerodon.agile.domain.agile.entity.ReportIssueE;
import io.choerodon.agile.domain.agile.entity.SprintE;
import io.choerodon.agile.domain.agile.repository.UserRepository;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private IssueMapper issueMapper;
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
    @Autowired
    private ProjectInfoMapper projectInfoMapper;
    @Autowired
    private UserRepository userRepository;

    private static final String STORY_POINTS = "storyPoints";
    private static final String REMAINING_ESTIMATED_TIME = "remainingEstimatedTime";
    private static final String ISSUE_COUNT = "issueCount";
    private static final String SPRINT_PLANNING_CODE = "sprint_planning";
    private static final String REPORT_SPRINT_ERROR = "error.report.sprintError";
    private static final String REPORT_FILTER_ERROR = "error.cumulativeFlowDiagram.filter";
    private static final String FIELD_TIMEESTIMATE = "timeestimate";
    private static final String FIELD_STORY_POINTS = "Story Points";
    private static final String FIELD_REMAINING_TIME_NAME = "remaining_time";
    private static final String FIELD_STORY_POINTS_NAME = "story_points";
    private static final String SPRINT_CLOSED = "closed";
    private static final String VERSION_ARCHIVED_CODE = "archived";
    private static final String VERSION_REPORT_ERROR = "error.report.version";
    private static final String ISSUE_STORY_CODE = "story";
    private static final String ASSIGNEE = "assignee";
    private static final String COMPONENT = "component";
    private static final String ISSUE_TYPE = "typeCode";
    private static final String VERSION = "version";
    private static final String PRIORITY = "priorityCode";
    private static final String STATUS = "statusCode";
    private static final String SPRINT = "sprint";
    private static final String EPIC = "epic";
    private static final String RESOLUTION = "resolution";
    private static final String TYPE_ISSUE_COUNT = "issue_count";
    private static final String TYPE_STORY_POINT = "story_point";
    private static final String TYPE_REMAIN_TIME = "remain_time";
    private static final String VERSION_CHART = "version_chart";
    private static final String EPIC_CHART = "epic_chart";
    private static final String AGILE = "Agile";

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportServiceImpl.class);

    @Override
    public void setReportMapper(ReportMapper reportMapper) {
        this.reportMapper = reportMapper;
    }

    @Override
    public List<IssueTypeDistributionChartDTO> queryIssueTypeDistributionChart(Long projectId) {
        return reportAssembler.toTargetList(reportMapper.queryIssueTypeDistributionChart(projectId),IssueTypeDistributionChartDTO.class);
    }

    @Override
    public List<IssueTypeDistributionChartDTO> queryVersionProgressChart(Long projectId) {
        return reportAssembler.toTargetList(reportMapper.queryVersionProgressChart(projectId),IssueTypeDistributionChartDTO.class);
    }


    @Override
    public List<ReportIssueDTO> queryBurnDownReport(Long projectId, Long sprintId, String type) {
        List<ReportIssueE> reportIssueEList = getBurnDownReport(projectId, sprintId, type);
        return ConvertHelper.convertList(reportIssueEList.stream().
                sorted(Comparator.comparing(ReportIssueE::getDate)).collect(Collectors.toList()), ReportIssueDTO.class);
    }

    private List<ReportIssueE> getBurnDownReport(Long projectId, Long sprintId, String type) {
        List<ReportIssueE> reportIssueEList = new ArrayList<>();
        SprintDO sprintDO = new SprintDO();
        sprintDO.setSprintId(sprintId);
        sprintDO.setProjectId(projectId);
        SprintE sprintE = sprintConverter.doToEntity(sprintMapper.selectOne(sprintDO));
        if (sprintE != null && !sprintE.getStatusCode().equals(SPRINT_PLANNING_CODE)) {
            sprintE.initStartAndEndTime();
            switch (type) {
                case STORY_POINTS:
                    queryStoryPointsOrRemainingEstimatedTime(sprintE, reportIssueEList, FIELD_STORY_POINTS);
                    break;
                case REMAINING_ESTIMATED_TIME:
                    queryStoryPointsOrRemainingEstimatedTime(sprintE, reportIssueEList, FIELD_TIMEESTIMATE);
                    break;
                case ISSUE_COUNT:
                    queryIssueCount(sprintE, reportIssueEList);
                    break;
                default:
                    queryStoryPointsOrRemainingEstimatedTime(sprintE, reportIssueEList, FIELD_STORY_POINTS);
                    break;
            }
        } else {
            throw new CommonException(REPORT_SPRINT_ERROR);
        }
        return reportIssueEList;
    }

    private JSONObject handleSameDay(List<ReportIssueE> reportIssueEList) {
        JSONObject jsonObject = new JSONObject();
        DateFormat bf = new SimpleDateFormat("yyyy-MM-dd");
        TreeMap<String, Integer> report = new TreeMap<>();
        //处理同一天
        reportIssueEList.forEach(reportIssueE -> {
            if (reportIssueE.getStatistical()) {
                String date = bf.format(reportIssueE.getDate());
                if (report.get(date) == null) {
                    Integer count = report.lastEntry() == null ? 0 : report.lastEntry().getValue();
                    report.put(date, count + reportIssueE.getNewValue() - reportIssueE.getOldValue());
                } else {
                    report.put(date, report.get(date) + reportIssueE.getNewValue() - reportIssueE.getOldValue());
                }
            }
        });
        jsonObject.put("coordinate", report);
        //需要返回给前端期望值（开启冲刺的和）
        jsonObject.put("expectCount", handleExpectCount(reportIssueEList));
        return jsonObject;
    }

    private Integer handleExpectCount(List<ReportIssueE> reportIssueEList) {
        Integer expectCount = 0;
        List<ReportIssueE> startReportIssue = reportIssueEList.stream().filter(reportIssueE -> "startSprint".equals(reportIssueE.getType())).collect(Collectors.toList());
        if (startReportIssue != null && !startReportIssue.isEmpty()) {
            for (ReportIssueE reportIssueE : startReportIssue) {
                if (reportIssueE.getStatistical()) {
                    expectCount += reportIssueE.getNewValue() - reportIssueE.getOldValue();
                }
            }
        }
        return expectCount;
    }


    @Override
    @Cacheable(cacheNames = AGILE, key =
            "'CumulativeFlowDiagram' + #projectId + ':' + #cumulativeFlowFilterDTO.toString()")
    public List<CumulativeFlowDiagramDTO> queryCumulativeFlowDiagram(Long projectId, CumulativeFlowFilterDTO cumulativeFlowFilterDTO) {
        //获取当前符合条件的所有issueIds
        String filterSql = null;
        if (cumulativeFlowFilterDTO.getQuickFilterIds() != null && !cumulativeFlowFilterDTO.getQuickFilterIds().isEmpty()) {
            filterSql = sprintService.getQuickFilter(cumulativeFlowFilterDTO.getQuickFilterIds());
        }
        List<Long> allIssueIds = reportMapper.queryAllIssueIdsByFilter(projectId, filterSql);
        if (allIssueIds != null && !allIssueIds.isEmpty() && cumulativeFlowFilterDTO.getColumnIds() != null && !cumulativeFlowFilterDTO.getColumnIds().isEmpty()) {
            return getCumulativeFlowDiagram(allIssueIds, projectId, cumulativeFlowFilterDTO);
        } else if (cumulativeFlowFilterDTO.getColumnIds() == null || cumulativeFlowFilterDTO.getColumnIds().isEmpty()) {
            throw new CommonException(REPORT_FILTER_ERROR);
        } else {
            return new ArrayList<>();
        }
    }

    private void handleColumnCoordinate(List<ColumnChangeDTO> columnChangeDTOList,
                                        CumulativeFlowDiagramDTO cumulativeFlowDiagramDTO,
                                        Date startDate,
                                        Date endDate) {
        List<CoordinateDTO> coordinateDTOS = new ArrayList<>();
        List<ColumnChangeDTO> columnChange = columnChangeDTOList.stream().filter(columnChangeDTO ->
                Objects.equals(columnChangeDTO.getColumnFrom(), cumulativeFlowDiagramDTO.getColumnId().toString())
                        || Objects.equals(columnChangeDTO.getColumnTo(), cumulativeFlowDiagramDTO.getColumnId().toString())).collect(Collectors.toList());
        if (columnChange != null && !columnChange.isEmpty()) {
            DateFormat bf = new SimpleDateFormat("yyyy-MM-dd");
            TreeMap<String, Integer> report = handleColumnCoordinateReport(columnChange, startDate, endDate, cumulativeFlowDiagramDTO, bf);
            report.forEach((k, v) -> {
                CoordinateDTO coordinateDTO = new CoordinateDTO();
                coordinateDTO.setIssueCount(v);
                try {
                    coordinateDTO.setDate(bf.parse(k));
                } catch (ParseException e) {
                    LOGGER.error("Exception", e);
                }
                coordinateDTOS.add(coordinateDTO);
            });
            cumulativeFlowDiagramDTO.setCoordinateDTOList(coordinateDTOS);
        }
    }

    private TreeMap<String, Integer> handleColumnCoordinateReport(List<ColumnChangeDTO> columnChange, Date startDate, Date endDate, CumulativeFlowDiagramDTO cumulativeFlowDiagramDTO, DateFormat bf) {
        TreeMap<String, Integer> report = new TreeMap<>();
        if (columnChange.get(0).getDate().after(startDate)) {
            report.put(bf.format(startDate), 0);
        }
        String columnId = cumulativeFlowDiagramDTO.getColumnId().toString();
        columnChange.forEach(columnChangeDTO -> handleColumnCoordinateSameDate(bf, report, columnChangeDTO, columnId));
        Date lastDate = columnChange.get(columnChange.size() - 1).getDate();
        if (lastDate.before(endDate)) {
            report.put(bf.format(endDate), report.lastEntry().getValue());
        }
        return report;
    }

    private void handleColumnCoordinateSameDate(DateFormat bf, TreeMap<String, Integer> report, ColumnChangeDTO columnChangeDTO, String columnId) {
        String date = bf.format(columnChangeDTO.getDate());
        if (report.get(date) == null) {
            Integer count = report.lastEntry() == null ? 0 : report.lastEntry().getValue();
            if (columnChangeDTO.getColumnFrom().equals(columnId)) {
                report.put(date, count - 1);
            } else {
                report.put(date, count + 1);
            }
        } else {
            if (columnChangeDTO.getColumnFrom().equals(columnId)) {
                report.put(date, report.get(date) - 1);
            } else {
                report.put(date, report.get(date) + 1);
            }

        }
    }


    private void handleCumulativeFlowChangeDuringDate(Date startDate, Date endDate, List<Long> columnIds, List<Long> allIssueIds, List<ColumnChangeDTO> result) {
        List<ColumnChangeDTO> changeIssueDuringDate = reportAssembler.toTargetList
                (reportMapper.queryChangeIssueDuringDate(startDate, endDate, allIssueIds, columnIds), ColumnChangeDTO.class);
        if (changeIssueDuringDate != null && !changeIssueDuringDate.isEmpty()) {
            result.addAll(changeIssueDuringDate);
        }
    }

    @Override
    public Page<IssueListDTO> queryIssueByOptions(Long projectId, Long versionId, String status, String type, PageRequest pageRequest) {
        ProductVersionDO versionDO = new ProductVersionDO();
        versionDO.setProjectId(projectId);
        versionDO.setVersionId(versionId);
        versionDO = versionMapper.selectOne(versionDO);
        if (versionDO == null || Objects.equals(versionDO.getStatusCode(), VERSION_ARCHIVED_CODE)) {
            throw new CommonException(VERSION_REPORT_ERROR);
        }
        pageRequest.resetOrder("ai", new HashMap<>());
        Page<IssueDO> reportIssuePage = PageHelper.doPageAndSort(pageRequest, () -> reportMapper.queryReportIssues(projectId, versionId, status, type));
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
    public Map<String, Object> queryVersionLineChart(Long projectId, Long versionId, String type) {
        Map<String, Object> versionReportMap = new HashMap<>();
        ProductVersionDO versionDO = new ProductVersionDO();
        versionDO.setProjectId(projectId);
        versionDO.setVersionId(versionId);
        versionDO = versionMapper.selectOne(versionDO);
        if (versionDO == null || Objects.equals(versionDO.getStatusCode(), VERSION_ARCHIVED_CODE)) {
            throw new CommonException(VERSION_REPORT_ERROR);
        }
        List<VersionReportDTO> versionReport = new ArrayList<>();
        List<Long> nowVersionIssue = reportMapper.queryIssueIdByVersionId(projectId, versionId);
        Date startDate = versionDO.getStartDate() != null ? versionDO.getStartDate() : versionDO.getCreationDate();
        Date endDate = new Date();
        List<VersionIssueChangeDO> versionChangeIssue = reportMapper.queryChangeIssue(projectId, versionId, startDate, endDate);
        switch (type) {
            case STORY_POINTS:
                statisticsByStoryPointsOrRemainingTime(versionReport, projectId, nowVersionIssue, versionChangeIssue, startDate, endDate, FIELD_STORY_POINTS);
                break;
            case REMAINING_ESTIMATED_TIME:
                statisticsByStoryPointsOrRemainingTime(versionReport, projectId, nowVersionIssue, versionChangeIssue, startDate, endDate, FIELD_TIMEESTIMATE);
                break;
            case ISSUE_COUNT:
                statisticsByIssueCount(versionReport, projectId, nowVersionIssue, versionChangeIssue, startDate, endDate);
                break;
            default:
                break;
        }
        versionReportMap.put(VERSION, versionDO);
        versionReportMap.put("versionReport", versionReport);
        return versionReportMap;
    }

    private void statisticsByIssueCount(List<VersionReportDTO> versionReport, Long projectId, List<Long> nowVersionIssue, List<VersionIssueChangeDO> versionChangeIssue, Date startDate, Date endDate) {
        VersionIssueChangeDO nowVersionIssueChange = new VersionIssueChangeDO();
        List<VersionIssueChangeDO> versionIssues = new ArrayList<>();
        //查统计最末时间点的相关信息
        Integer nowCompletedIssueCount = 0;
        if (!nowVersionIssue.isEmpty()) {
            nowCompletedIssueCount = reportMapper.queryCompletedIssueCount(projectId, nowVersionIssue);
            //空指针异常
            nowVersionIssueChange.setPreDate(versionChangeIssue.isEmpty() ? startDate : versionChangeIssue.get(0).getChangeDate());
            nowVersionIssueChange.setChangeDate(endDate);
            nowVersionIssueChange.setIssueIds(new ArrayList<>(nowVersionIssue));
            versionIssues.add(nowVersionIssueChange);
        }
        Integer nowIssueCount = nowVersionIssue.size();
        statisticalTimePointIssue(nowVersionIssue, versionIssues, versionChangeIssue, startDate);
        if (versionIssues.isEmpty()) {
            return;
        }
        Set<Date> dateSet = new TreeSet<>(Comparator.reverseOrder());
        Map<Date, List<IssueChangeDTO>> completedIssuesMap = statisticalCompletedChange(projectId, versionIssues, dateSet, FIELD_STORY_POINTS);
        Map<Date, List<IssueChangeDTO>> unCompletedIssuesMap = statisticalUnCompletedChange(projectId, versionIssues, dateSet, FIELD_STORY_POINTS);
        Map<Date, List<IssueChangeDTO>> addIssuesMap = statisticalAddChangeIssue(projectId, versionChangeIssue, dateSet, FIELD_STORY_POINTS);
        Map<Date, List<IssueChangeDTO>> removeIssuesMap = statisticalRemoveChangeIssue(projectId, versionChangeIssue, dateSet, FIELD_STORY_POINTS);
        VersionReportDTO nowVersionReportDTO = new VersionReportDTO();
        nowVersionReportDTO.setChangeDate(endDate);
        nowVersionReportDTO.setTotalField(nowIssueCount);
        nowVersionReportDTO.setCompletedField(nowCompletedIssueCount);
        versionReport.add(nowVersionReportDTO);
        for (Date date : dateSet) {
            VersionReportDTO versionReportDTO = new VersionReportDTO();
            List<IssueChangeDTO> completedIssue = changeIssueNowDate(completedIssuesMap, date);
            List<Long> completedIssueIds = completedIssue.stream().map(IssueChangeDTO::getIssueId).collect(Collectors.toList());
            List<IssueChangeDTO> unCompletedIssue = changeIssueNowDate(unCompletedIssuesMap, date);
            List<IssueChangeDTO> addIssue = changeIssueNowDate(addIssuesMap, date);
            List<IssueChangeDTO> removeIssue = changeIssueNowDate(removeIssuesMap, date);
            Integer addCompletedCount = addIssue.stream().filter(addChangeIssue -> addChangeIssue.getCompleted() && !completedIssueIds.contains(addChangeIssue.getIssueId())).collect(Collectors.toList()).size();
            Integer removeCompletedCount = removeIssue.stream().filter(IssueChangeDTO::getCompleted).collect(Collectors.toList()).size();
            nowIssueCount = nowIssueCount - addIssue.size() + removeIssue.size();
            nowCompletedIssueCount = nowCompletedIssueCount - completedIssue.size() + unCompletedIssue.size() - addCompletedCount + removeCompletedCount;
            versionReportDTO.setChangeDate(date);
            versionReportDTO.setTotalField(nowIssueCount);
            versionReportDTO.setCompletedField(nowCompletedIssueCount);
            versionReportDTO.setCompletedIssues(completedIssue);
            versionReportDTO.setUnCompletedIssues(unCompletedIssue);
            versionReportDTO.setAddIssues(addIssue);
            versionReportDTO.setRemoveIssues(removeIssue);
            versionReport.add(versionReportDTO);
        }
    }

    private void statisticsByStoryPointsOrRemainingTime(List<VersionReportDTO> versionReport, Long projectId, List<Long> nowVersionIssue, List<VersionIssueChangeDO> versionChangeIssue, Date startDate, Date endDate, String field) {
        VersionIssueChangeDO nowVersionIssueChange = new VersionIssueChangeDO();
        List<VersionIssueChangeDO> versionIssues = new ArrayList<>();
        String fieldName = field.equals(FIELD_STORY_POINTS) ? FIELD_STORY_POINTS_NAME : FIELD_REMAINING_TIME_NAME;
        //查统计最末时间点的相关信息
        Integer nowTotalField = 0;
        Integer nowCompletedField = 0;
        double nowUnEstimateCount = 0;
        if (!nowVersionIssue.isEmpty()) {
            nowTotalField = reportMapper.queryTotalField(projectId, nowVersionIssue, fieldName);
            nowCompletedField = reportMapper.queryCompleteField(projectId, nowVersionIssue, fieldName);
            nowUnEstimateCount = reportMapper.queryUnEstimateCount(projectId, nowVersionIssue, fieldName);
            //空指针异常
            nowVersionIssueChange.setPreDate(versionChangeIssue.isEmpty() ? startDate : versionChangeIssue.get(0).getChangeDate());
            nowVersionIssueChange.setChangeDate(endDate);
            nowVersionIssueChange.setIssueIds(new ArrayList<>(nowVersionIssue));
            versionIssues.add(nowVersionIssueChange);
        }
        double nowIssueCount = nowVersionIssue.size();
        double nowUnEstimatedPercentage = nowIssueCount == 0 ? 0 : nowUnEstimateCount / nowIssueCount;
        statisticalTimePointIssue(nowVersionIssue, versionIssues, versionChangeIssue, startDate);
        if (versionIssues.isEmpty()) {
            return;
        }
        Set<Date> dateSet = new TreeSet<>(Comparator.reverseOrder());
        Map<Date, List<IssueChangeDTO>> fieldChangeIssuesMap = statisticalFieldChange(projectId, versionIssues, dateSet, field);
        Map<Date, List<IssueChangeDTO>> completedIssuesMap = statisticalCompletedChange(projectId, versionIssues, dateSet, field);
        Map<Date, List<IssueChangeDTO>> unCompletedIssuesMap = statisticalUnCompletedChange(projectId, versionIssues, dateSet, field);
        Map<Date, List<IssueChangeDTO>> addIssuesMap = statisticalAddChangeIssue(projectId, versionChangeIssue, dateSet, field);
        Map<Date, List<IssueChangeDTO>> removeIssuesMap = statisticalRemoveChangeIssue(projectId, versionChangeIssue, dateSet, field);
        VersionReportDTO nowVersionReportDTO = new VersionReportDTO();
        nowVersionReportDTO.setChangeDate(endDate);
        nowVersionReportDTO.setTotalField(nowTotalField);
        nowVersionReportDTO.setCompletedField(nowCompletedField);
        nowVersionReportDTO.setUnEstimatedPercentage(nowUnEstimatedPercentage);
        versionReport.add(nowVersionReportDTO);
        for (Date date : dateSet) {
            VersionReportDTO versionReportDTO = new VersionReportDTO();
            List<IssueChangeDTO> fieldChangeIssue = changeIssueNowDate(fieldChangeIssuesMap, date);
            List<Long> fileChangeIds = fieldChangeIssue.stream().map(IssueChangeDTO::getIssueId).collect(Collectors.toList());
            List<IssueChangeDTO> completedIssue = changeIssueNowDate(completedIssuesMap, date);
            List<IssueChangeDTO> unCompletedIssue = changeIssueNowDate(unCompletedIssuesMap, date);
            List<IssueChangeDTO> addIssue = changeIssueNowDate(addIssuesMap, date);
            List<Long> addIssueIds = addIssue.stream().map(IssueChangeDTO::getIssueId).collect(Collectors.toList());
            List<IssueChangeDTO> removeIssue = changeIssueNowDate(removeIssuesMap, date);
            Integer changeField = fieldChangeIssue.stream().mapToInt(fieldChange -> Integer.valueOf(fieldChange.getChangeField())).sum();
            Integer changeCompletedField = fieldChangeIssue.stream().filter(IssueChangeDTO::getCompleted).mapToInt(fieldChange -> Integer.valueOf(fieldChange.getChangeField())).sum();
            Integer addField = addChangeIssueField(addIssue, fileChangeIds);
            Integer addCompletedField = addChangeCompletedIssueField(addIssue, fileChangeIds);
            Integer removeField = changeIssueField(removeIssue);
            Integer removeCompletedField = changeCompletedIssueField(removeIssue);
            Integer completedField = completedChangeIssueField(completedIssue, fileChangeIds);
            Integer unCompletedField = changeIssueField(unCompletedIssue);
            nowIssueCount = nowIssueCount - addIssue.size() + removeIssue.size();
            Integer changUnEstimatedCount = calculationUnEstimated(fieldChangeIssue, field);
            Integer changEstimatedCount = calculationEstimated(fieldChangeIssue, field, addIssueIds);
            Integer addUnEstimatedCount = calculationUnEstimated(addIssue, field);
            Integer removeUnEstimatedCount = calculationUnEstimated(removeIssue, field);
            Integer completedIssueUnEstimatedCount = completedCalculationUnEstimated(completedIssue, field, addIssueIds);
            Integer unCompletedIssueUnEstimatedCount = calculationUnEstimated(unCompletedIssue, field);
            nowUnEstimateCount = nowUnEstimateCount - changUnEstimatedCount + changEstimatedCount - addUnEstimatedCount + removeUnEstimatedCount + completedIssueUnEstimatedCount - unCompletedIssueUnEstimatedCount;
            nowTotalField = nowTotalField - changeField - addField + removeField;
            nowCompletedField = nowCompletedField - completedField + unCompletedField - changeCompletedField - addCompletedField + removeCompletedField;
            nowUnEstimatedPercentage = nowIssueCount == 0 ? 0 : nowUnEstimateCount / nowIssueCount;
            versionReportDTO.setChangeDate(date);
            versionReportDTO.setTotalField(nowTotalField);
            versionReportDTO.setCompletedField(nowCompletedField);
            versionReportDTO.setUnEstimatedPercentage(nowUnEstimatedPercentage);
            versionReportDTO.setFieldChangIssues(fieldChangeIssue);
            versionReportDTO.setCompletedIssues(completedIssue);
            versionReportDTO.setUnCompletedIssues(unCompletedIssue);
            versionReportDTO.setAddIssues(addIssue);
            versionReportDTO.setRemoveIssues(removeIssue);
            versionReport.add(versionReportDTO);
        }
    }

    private Integer completedCalculationUnEstimated(List<IssueChangeDTO> completedIssue, String field, List<Long> addIssueIds) {
        return completedIssue.stream().filter(fieldChange ->
                (Objects.equals(fieldChange.getTypeCode(), ISSUE_STORY_CODE) || field.equals(FIELD_TIMEESTIMATE)) && fieldChange.getNewValue() == null && !addIssueIds.contains(fieldChange.getIssueId())
        ).collect(Collectors.toList()).size();
    }

    private Integer completedChangeIssueField(List<IssueChangeDTO> completedIssue, List<Long> fileChangeIds) {
        return completedIssue.stream().filter(fieldChange -> fieldChange.getNewValue() != null && !fileChangeIds.contains(fieldChange.getIssueId())).mapToInt(fieldChange -> Integer.valueOf(fieldChange.getNewValue())).sum();
    }

    private Integer addChangeCompletedIssueField(List<IssueChangeDTO> addIssue, List<Long> fileChangeIds) {
        return addIssue.stream().filter(fieldChange -> fieldChange.getCompleted() && fieldChange.getNewValue() != null && !fileChangeIds.contains(fieldChange.getIssueId())).mapToInt(fieldChange -> Integer.valueOf(fieldChange.getNewValue())).sum();
    }

    private Integer addChangeIssueField(List<IssueChangeDTO> addIssue, List<Long> fileChangeIds) {
        return addIssue.stream().filter(fieldChange -> fieldChange.getNewValue() != null && !fileChangeIds.contains(fieldChange.getIssueId())).mapToInt(fieldChange -> Integer.valueOf(fieldChange.getNewValue())).sum();
    }

    private Integer calculationEstimated(List<IssueChangeDTO> issueChange, String field, List<Long> addIssueIds) {
        return issueChange.stream().filter(fieldChange ->
                (Objects.equals(fieldChange.getTypeCode(), ISSUE_STORY_CODE) || field.equals(FIELD_TIMEESTIMATE)) && fieldChange.getOldValue() == null && !addIssueIds.contains(fieldChange.getIssueId())
        ).collect(Collectors.toList()).size();
    }

    private Integer changeCompletedIssueField(List<IssueChangeDTO> issueChange) {
        return issueChange.stream().filter(fieldChange -> fieldChange.getCompleted() && fieldChange.getNewValue() != null).mapToInt(fieldChange -> Integer.valueOf(fieldChange.getNewValue())).sum();
    }

    private Integer changeIssueField(List<IssueChangeDTO> issueChange) {
        return issueChange.stream().filter(fieldChange -> fieldChange.getNewValue() != null).mapToInt(fieldChange -> Integer.valueOf(fieldChange.getNewValue())).sum();
    }

    private List<IssueChangeDTO> changeIssueNowDate(Map<Date, List<IssueChangeDTO>> changeIssuesMap, Date date) {
        return changeIssuesMap.get(date) != null ? changeIssuesMap.get(date) : new ArrayList<>();
    }

    private Integer calculationUnEstimated(List<IssueChangeDTO> issueChange, String field) {
        return issueChange.stream().filter(fieldChange ->
                (Objects.equals(fieldChange.getTypeCode(), ISSUE_STORY_CODE) || field.equals(FIELD_TIMEESTIMATE)) && fieldChange.getNewValue() == null
        ).collect(Collectors.toList()).size();
    }

    private Map<Date, List<IssueChangeDTO>> statisticalRemoveChangeIssue(Long projectId, List<VersionIssueChangeDO> versionChangeIssue, Set<Date> dateSet, String field) {
        //issue移除
        List<VersionIssueChangeDO> versionRemoveChangeIssue = versionChangeIssue.stream().filter(versionIssueChangeDO -> !versionIssueChangeDO.getRemoveIssueIds().isEmpty()).map(versionIssueChangeDO -> {
            versionIssueChangeDO.setIssueIds(versionIssueChangeDO.getRemoveIssueIds());
            return versionIssueChangeDO;
        }).collect(Collectors.toList());
        List<IssueChangeDO> versionRemoveChangeIssues = versionRemoveChangeIssue.isEmpty() ? new ArrayList<>() : reportMapper.queryChangIssue(projectId, versionRemoveChangeIssue, field);
        List<IssueChangeDTO> removeIssues = issueAssembler.toTargetList(versionRemoveChangeIssues, IssueChangeDTO.class);
        Map<Date, List<IssueChangeDTO>> removeIssuesMap = removeIssues.stream().collect(Collectors.groupingBy(IssueChangeDTO::getChangeDate));
        dateSet.addAll(removeIssuesMap.keySet());
        return removeIssuesMap;
    }

    private Map<Date, List<IssueChangeDTO>> statisticalAddChangeIssue(Long projectId, List<VersionIssueChangeDO> versionChangeIssue, Set<Date> dateSet, String field) {
        //issue移入
        List<VersionIssueChangeDO> versionAddChangeIssue = versionChangeIssue.stream().filter(versionIssueChangeDO -> !versionIssueChangeDO.getAddIssueIds().isEmpty()).map(versionIssueChangeDO -> {
            versionIssueChangeDO.setIssueIds(versionIssueChangeDO.getAddIssueIds());
            return versionIssueChangeDO;
        }).collect(Collectors.toList());
        List<IssueChangeDO> versionAddChangeIssues = versionAddChangeIssue.isEmpty() ? new ArrayList<>() : reportMapper.queryChangIssue(projectId, versionAddChangeIssue, field);
        List<IssueChangeDTO> addIssues = issueAssembler.toTargetList(versionAddChangeIssues, IssueChangeDTO.class);
        Map<Date, List<IssueChangeDTO>> addIssuesMap = addIssues.stream().collect(Collectors.groupingBy(IssueChangeDTO::getChangeDate));
        dateSet.addAll(addIssuesMap.keySet());
        return addIssuesMap;
    }

    private Map<Date, List<IssueChangeDTO>> statisticalUnCompletedChange(Long projectId, List<VersionIssueChangeDO> versionIssues, Set<Date> dateSet, String field) {
        //issue由完成变为未完成
        List<VersionIssueChangeDO> unCompletedChangeIssues = reportMapper.queryCompletedChangeIssue(projectId, versionIssues, false);
        List<IssueChangeDTO> unCompletedIssues = issueAssembler.toTargetList(unCompletedChangeIssues.isEmpty() ? new ArrayList<>() : reportMapper.queryChangIssue(projectId, unCompletedChangeIssues, field), IssueChangeDTO.class);
        Map<Date, List<IssueChangeDTO>> unCompletedIssuesMap = unCompletedIssues.stream().collect(Collectors.groupingBy(IssueChangeDTO::getChangeDate));
        dateSet.addAll(unCompletedIssuesMap.keySet());
        return unCompletedIssuesMap;
    }

    private Map<Date, List<IssueChangeDTO>> statisticalCompletedChange(Long projectId, List<VersionIssueChangeDO> versionIssues, Set<Date> dateSet, String field) {
        //issue由未完成变为完成
        List<VersionIssueChangeDO> completedChangeIssues = reportMapper.queryCompletedChangeIssue(projectId, versionIssues, true);
        List<IssueChangeDTO> completedIssues = issueAssembler.toTargetList(completedChangeIssues.isEmpty() ? new ArrayList<>() : reportMapper.queryChangIssue(projectId, completedChangeIssues, field), IssueChangeDTO.class);
        Map<Date, List<IssueChangeDTO>> completedIssuesMap = completedIssues.stream().collect(Collectors.groupingBy(IssueChangeDTO::getChangeDate));
        dateSet.addAll(completedIssuesMap.keySet());
        return completedIssuesMap;
    }

    private Map<Date, List<IssueChangeDTO>> statisticalFieldChange(Long projectId, List<VersionIssueChangeDO> versionIssues, Set<Date> dateSet, String field) {
        List<IssueChangeDTO> changeIssues = issueAssembler.toTargetList(reportMapper.queryChangeFieldIssue(projectId, versionIssues, field), IssueChangeDTO.class);
        Map<Date, List<IssueChangeDTO>> changeIssuesMap = changeIssues.stream().collect(Collectors.groupingBy(IssueChangeDTO::getChangeDate));
        dateSet.addAll(changeIssuesMap.keySet());
        return changeIssuesMap;
    }

    private void statisticalTimePointIssue(List<Long> nowVersionIssue, List<VersionIssueChangeDO> versionIssues, List<VersionIssueChangeDO> versionChangeIssue, Date startDate) {
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
            if (!nowVersionIssue.isEmpty()) {
                versionIssues.add(versionChangeIssue.get(i));
            }
        }

    }

    private void handleCumulativeFlowAddDuringDate(List<Long> allIssueIds, List<ColumnChangeDTO> result, Date startDate, Date endDate, List<Long> columnIds) {
        List<ColumnChangeDTO> addIssueDuringDate = reportAssembler.toTargetList
                (reportMapper.queryAddIssueDuringDate(startDate, endDate, allIssueIds, columnIds), ColumnChangeDTO.class);
        if (addIssueDuringDate != null && !addIssueDuringDate.isEmpty()) {
            List<Long> statusToNullIssueIds = addIssueDuringDate.stream().filter(columnChangeDTO -> columnChangeDTO.getStatusTo() == null).map(ColumnChangeDTO::getIssueId).collect(Collectors.toList());
            if (statusToNullIssueIds != null && !statusToNullIssueIds.isEmpty()) {
                Map<Long, ColumnStatusRelDO> columnStatusRelMap = columnStatusRelMapper.queryByIssueIdAndColumnIds(statusToNullIssueIds, columnIds)
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
            }
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
            reportIssueEList.addAll(issueRemoveList);
        }
    }

    private void handleAddIssueCountDuringSprint(SprintDO sprintDO, List<ReportIssueE> reportIssueEList, List<Long> issueIdAddList) {
        List<ReportIssueE> issueAddList = issueIdAddList != null && !issueIdAddList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryAddIssueDuringSprint(issueIdAddList, sprintDO), ReportIssueE.class) : null;
        if (issueAddList != null && !issueAddList.isEmpty()) {
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

    private List<CoordinateDTO> getCumulativeFlowDiagramDuringDate(CumulativeFlowDiagramDTO cumulativeFlowDiagramDTO, CumulativeFlowFilterDTO cumulativeFlowFilterDTO) {
        if (cumulativeFlowDiagramDTO.getCoordinateDTOList() != null && !cumulativeFlowDiagramDTO.getCoordinateDTOList().isEmpty()) {
            return cumulativeFlowDiagramDTO.getCoordinateDTOList().stream().filter(coordinateDTO ->
                    (coordinateDTO.getDate().before(cumulativeFlowFilterDTO.getEndDate()) || coordinateDTO.getDate().equals(cumulativeFlowFilterDTO.getEndDate()))
                            && (coordinateDTO.getDate().after(cumulativeFlowFilterDTO.getStartDate()) || coordinateDTO.getDate().equals(cumulativeFlowFilterDTO.getStartDate()))).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }

    }

    private List<CumulativeFlowDiagramDTO> getCumulativeFlowDiagram(List<Long> allIssueIds, Long projectId, CumulativeFlowFilterDTO cumulativeFlowFilterDTO) {
        ProjectInfoDO query = new ProjectInfoDO();
        query.setProjectId(projectId);
        ProjectInfoDO projectInfoDO = projectInfoMapper.selectOne(query);
        if (projectInfoDO == null) {
            throw new CommonException("error.cumulativeFlow.projectInfoNotFound");
        }
        if (cumulativeFlowFilterDTO.getBoardId() == null) {
            throw new CommonException("error.cumulativeFlow.boardIdNotFound");
        }
        List<Long> columnIds = boardColumnMapper.queryColumnIdsByBoardId(cumulativeFlowFilterDTO.getBoardId(), projectId);
        //设置时间区间
        Date startDate = projectInfoDO.getCreationDate();
        Date endDate = new Date();
        List<ColumnChangeDTO> result = new ArrayList<>();
        //所有在当前时间内创建的issue
        handleCumulativeFlowAddDuringDate(allIssueIds, result, startDate, endDate, columnIds);
        //所有在当前时间内状态改变的issue
        handleCumulativeFlowChangeDuringDate(startDate, endDate, columnIds, allIssueIds, result);
        //过滤并排序
        List<ColumnChangeDTO> columnChangeDTOList = result.stream().filter(columnChangeDTO ->
                columnChangeDTO.getColumnTo() != null && !columnChangeDTO.getColumnFrom().equals(columnChangeDTO.getColumnTo()))
                .sorted(Comparator.comparing(ColumnChangeDTO::getDate)).collect(Collectors.toList());
        //对传入时间点的数据给与坐标
        List<CumulativeFlowDiagramDTO> cumulativeFlowDiagramDTOList = reportAssembler.columnListDoToDto(boardColumnMapper.queryColumnByColumnIds(columnIds));
        cumulativeFlowDiagramDTOList.parallelStream().forEachOrdered(cumulativeFlowDiagramDTO -> {
            handleColumnCoordinate(columnChangeDTOList, cumulativeFlowDiagramDTO, cumulativeFlowFilterDTO.getStartDate(), cumulativeFlowFilterDTO.getEndDate());
            //过滤日期
            cumulativeFlowDiagramDTO.setCoordinateDTOList(getCumulativeFlowDiagramDuringDate(cumulativeFlowDiagramDTO, cumulativeFlowFilterDTO));
        });
        return cumulativeFlowDiagramDTOList.stream().filter(cumulativeFlowDiagramDTO -> cumulativeFlowFilterDTO.getColumnIds().contains(cumulativeFlowDiagramDTO.getColumnId())).collect(Collectors.toList());
    }

    private String getNowTime() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(d);
    }

    private List<VelocitySprintDO> dealStoryPointResult(List<VelocitySingleDO> committedList, List<VelocitySingleDO> completedList, List<VelocitySprintDO> sprintDOList, List<VelocitySprintDO> result) {
        for (VelocitySprintDO temp : sprintDOList) {
            int committedStoryPoints = 0;
            int completedStoryPoints = 0;
            for (VelocitySingleDO committed : committedList) {
                if (committed.getSprintId().equals(temp.getSprintId())) {
                    committedStoryPoints += committed.getStoryPoint();
                }
            }
            for (VelocitySingleDO completed : completedList) {
                if (completed.getSprintId().equals(temp.getSprintId())) {
                    completedStoryPoints += completed.getStoryPoint();
                }
            }
            temp.setCommittedStoryPoints(committedStoryPoints);
            temp.setCompletedStoryPoints(completedStoryPoints);
            result.add(temp);
        }
        return result;
    }

    private List<VelocitySprintDO> dealIssueCountResult(List<VelocitySingleDO> committedList, List<VelocitySingleDO> completedList, List<VelocitySprintDO> sprintDOList, List<VelocitySprintDO> result) {
        for (VelocitySprintDO temp : sprintDOList) {
            int committedIssueNum = 0;
            int completedIssueNum = 0;
            for (VelocitySingleDO committed : committedList) {
                if (committed.getSprintId().equals(temp.getSprintId())) {
                    committedIssueNum += 1;
                }
            }
            for (VelocitySingleDO completed : completedList) {
                if (completed.getSprintId().equals(temp.getSprintId())) {
                    completedIssueNum += 1;
                }
            }
            temp.setCommittedIssueCount(committedIssueNum);
            temp.setCompletedIssueCount(completedIssueNum);
            result.add(temp);
        }
        return result;
    }

    private List<VelocitySprintDO> dealRemainTimeResult(List<VelocitySingleDO> committedList, List<VelocitySingleDO> completedList, List<VelocitySprintDO> sprintDOList, List<VelocitySprintDO> result) {
        for (VelocitySprintDO temp : sprintDOList) {
            int committedRemainTime = 0;
            int completedRemainTime = 0;
            for (VelocitySingleDO committed : committedList) {
                if (committed.getSprintId().equals(temp.getSprintId())) {
                    committedRemainTime += committed.getRemainTime();
                }
            }
            for (VelocitySingleDO completed : completedList) {
                if (completed.getSprintId().equals(temp.getSprintId())) {
                    completedRemainTime += completed.getRemainTime();
                }
            }
            temp.setCommittedRemainTime(committedRemainTime);
            temp.setCompletedRemainTime(completedRemainTime);
            result.add(temp);
        }
        return result;
    }

    @Override
    @Cacheable(cacheNames = AGILE, key = "'VelocityChart' + #projectId + ':' + #type")
    public List<VelocitySprintDTO> queryVelocityChart(Long projectId, String type) {
        List<VelocitySprintDO> sprintDOList = reportMapper.selectRecentSprint(projectId);
        if (sprintDOList.isEmpty()) {
            return new ArrayList();
        }
        List<Long> ids = new ArrayList<>();
        for (VelocitySprintDO velocitySprintDO : sprintDOList) {
            ids.add(velocitySprintDO.getSprintId());
        }
        String now = getNowTime();
        List<VelocitySprintDO> result = new ArrayList<>();
        switch (type) {
            case TYPE_ISSUE_COUNT:
                List<VelocitySingleDO> issueCountCommitted = reportMapper.selectByIssueCountCommitted(projectId, ids, now);
                List<VelocitySingleDO> issueCountCompleted = reportMapper.selectByIssueCountCompleted(projectId, ids, now);
                dealIssueCountResult(issueCountCommitted, issueCountCompleted, sprintDOList, result);
                break;
            case TYPE_STORY_POINT:
                List<VelocitySingleDO> storyPointCommitted = reportMapper.selectByStoryPointAndNumCommitted(projectId, ids, now);
                List<VelocitySingleDO> storyPointCompleted = reportMapper.selectByStoryPointAndNumCompleted(projectId, ids, now);
                dealStoryPointResult(storyPointCommitted, storyPointCompleted, sprintDOList, result);
                break;
            case TYPE_REMAIN_TIME:
                List<VelocitySingleDO> remainTimeCommitted = reportMapper.selectByRemainTimeCommitted(projectId, ids, now);
                List<VelocitySingleDO> remainTimecompleted = reportMapper.selectByRemainTimeCompleted(projectId, ids, now);
                dealRemainTimeResult(remainTimeCommitted, remainTimecompleted, sprintDOList, result);
                break;
            default:
                break;
        }
        return ConvertHelper.convertList(result, VelocitySprintDTO.class);
    }

    @Override
    @Cacheable(cacheNames = AGILE, key = "'PieChart' + #projectId + ':' + #fieldName")
    public List<PieChartDTO> queryPieChart(Long projectId, String fieldName) {
        switch (fieldName) {
            case ASSIGNEE:
                return handlePieChartByAssignee(projectId);
            case COMPONENT:
                return handlePieChartByType(projectId, "component_id", false, false);
            case ISSUE_TYPE:
                return handlePieChartByType(projectId, "type_code", true, true);
            case VERSION:
                return handlePieChartByType(projectId, "version_id", false, false);
            case PRIORITY:
                return handlePieChartByType(projectId, "priority_code", true, true);
            case STATUS:
                return handlePieChartByType(projectId, "status_id", true, false);
            case SPRINT:
                return handlePieChartByType(projectId, "sprint_id", false, false);
            case EPIC:
                return handlePieChartByEpic(projectId);
            case RESOLUTION:
                return handlePieChartByType(projectId, RESOLUTION, false, false);
            default:
                break;
        }
        return new ArrayList<>();
    }

    private List<PieChartDTO> handlePieChartByEpic(Long projectId) {
        Integer total = reportMapper.queryIssueCountByFieldName(projectId, "epic_id");
        return reportAssembler.toTargetList(reportMapper.queryPieChartByEpic(projectId, total), PieChartDTO.class);
    }

    private List<PieChartDTO> handlePieChartByType(Long projectId, String fieldName, Boolean own, Boolean typeCode) {
        Integer total = reportMapper.queryIssueCountByFieldName(projectId, fieldName);
        List<PieChartDO> pieChartDOS = reportMapper.queryPieChartByParam(projectId, own, fieldName, typeCode, total);
        return reportAssembler.toTargetList(pieChartDOS, PieChartDTO.class);
    }

    private List<PieChartDTO> handlePieChartByAssignee(Long projectId) {
        Integer total = reportMapper.queryIssueCountByFieldName(projectId, "assignee_id");
        List<PieChartDO> pieChartDOS = reportMapper.queryPieChartByParam(projectId, true, "assignee_id", false, total);
        List<PieChartDTO> pieChartDTOList = reportAssembler.toTargetList(pieChartDOS, PieChartDTO.class);
        if (pieChartDTOList != null && !pieChartDTOList.isEmpty()) {
            List<Long> userIds = pieChartDTOList.stream().filter(pieChartDTO ->
                    pieChartDTO.getTypeName() != null && !"0".equals(pieChartDTO.getTypeName())).map(pieChartDTO ->
                    Long.parseLong(pieChartDTO.getTypeName())).collect(Collectors.toList());
            Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(userIds, true);
            pieChartDTOList.parallelStream().forEach(pieChartDTO -> {
                JSONObject jsonObject = new JSONObject();
                if (pieChartDTO.getTypeName() != null && usersMap.get(Long.parseLong(pieChartDTO.getTypeName())) != null) {
                    String assigneeName = usersMap.get(Long.parseLong(pieChartDTO.getTypeName())).getName();
                    String assigneeImageUrl = usersMap.get(Long.parseLong(pieChartDTO.getTypeName())).getImageUrl();
                    String email = usersMap.get(Long.parseLong(pieChartDTO.getTypeName())).getEmail();
                    pieChartDTO.setName(assigneeName);
                    jsonObject.put("assigneeImageUrl", assigneeImageUrl);
                    jsonObject.put("email", email);
                } else {
                    jsonObject.put("assigneeImageUrl", null);
                    jsonObject.put("email", null);
                }
                pieChartDTO.setJsonObject(jsonObject);
            });
        }
        return pieChartDTOList;
    }

    private void setStoryPointProperties(GroupDataChartDO g1,
                                         List<GroupDataChartDO> storyPointsAll,
                                         List<GroupDataChartDO> storyPointsCompleted,
                                         List<GroupDataChartDO> storyPointCountEstimate) {
        for (GroupDataChartDO g2 : storyPointsAll) {
            if (g1.getGroupDay().equals(g2.getGroupDay())) {
                g1.setAllStoryPoints(g2.getAllStoryPoints());
                break;
            }
        }
        for (GroupDataChartDO g3 : storyPointsCompleted) {
            if (g1.getGroupDay().equals(g3.getGroupDay())) {
                g1.setCompletedStoryPoints(g3.getCompletedStoryPoints());
                break;
            }
        }
        int flag = 0;
        for (GroupDataChartDO g4 : storyPointCountEstimate) {
            if (g1.getGroupDay().equals(g4.getGroupDay())) {
                flag = 1;
                g1.setUnEstimateIssueCount(g1.getIssueCount() - g4.getUnEstimateIssueCount());
                break;
            }
        }
        if (flag == 0) {
            g1.setUnEstimateIssueCount(g1.getIssueCount());
        }
    }


    private List<GroupDataChartDO> dealStoryPointFinal(List<GroupDataChartDO> storyPointsAll, List<GroupDataChartDO> storyPointsCompleted, List<GroupDataChartDO> storyPointCountAll, List<GroupDataChartDO> storyPointCountEstimate) {
        for (GroupDataChartDO g1 : storyPointCountAll) {
            setStoryPointProperties(g1, storyPointsAll, storyPointsCompleted, storyPointCountEstimate);
        }
        return storyPointCountAll;
    }

    private void setRemainTimeCompleted(GroupDataChartDO g1, List<GroupDataChartDO> remainTimeRemainCompleted, List<GroupDataChartDO> remainTimeWorkLogCompleted) {
        for (GroupDataChartDO g2 : remainTimeRemainCompleted) {
            if (g1.getGroupDay().equals(g2.getGroupDay())) {
                g1.setCompletedRemainTimes(g2.getCompletedRemainTimes());
                break;
            }
        }
        for (GroupDataChartDO g3 : remainTimeWorkLogCompleted) {
            if (g1.getGroupDay().equals(g3.getGroupDay())) {
                g1.setCompletedRemainTimes(g1.getCompletedRemainTimes() + g3.getCompletedRemainTimes());
                break;
            }
        }
    }

    private void setRemainTimeAll(GroupDataChartDO g1, List<GroupDataChartDO> remainTimeRemainAll, List<GroupDataChartDO> remainTimeWorkLogAll) {
        for (GroupDataChartDO g4 : remainTimeRemainAll) {
            if (g1.getGroupDay().equals(g4.getGroupDay())) {
                g1.setAllRemainTimes(g4.getAllRemainTimes());
                break;
            }
        }
        for (GroupDataChartDO g5 : remainTimeWorkLogAll) {
            if (g1.getGroupDay().equals(g5.getGroupDay())) {
                g1.setAllRemainTimes(g1.getAllRemainTimes() + g5.getAllRemainTimes());
                break;
            }
        }
    }

    private void setRemainTimeUnEstimateCount(GroupDataChartDO g1, List<GroupDataChartDO> remainTimeCountEstimate) {
        int flag = 0;
        for (GroupDataChartDO g6 : remainTimeCountEstimate) {
            if (g1.getGroupDay().equals(g6.getGroupDay())) {
                flag = 1;
                g1.setUnEstimateIssueCount(g1.getIssueCount() - g6.getUnEstimateIssueCount());
                break;
            }
        }
        if (flag == 0) {
            g1.setUnEstimateIssueCount(g1.getIssueCount());
        }
    }

    private List<GroupDataChartDO> dealRemainTimeFinal(List<GroupDataChartDO> remainTimeRemainCompleted, List<GroupDataChartDO> remainTimeWorkLogCompleted,
                                                       List<GroupDataChartDO> remainTimeRemainAll, List<GroupDataChartDO> remainTimeWorkLogAll,
                                                       List<GroupDataChartDO> remainTimeCountAll, List<GroupDataChartDO> remainTimeCountEstimate) {
        for (GroupDataChartDO g1 : remainTimeCountAll) {
            setRemainTimeCompleted(g1, remainTimeRemainCompleted, remainTimeWorkLogCompleted);
            setRemainTimeAll(g1, remainTimeRemainAll, remainTimeWorkLogAll);
            setRemainTimeUnEstimateCount(g1, remainTimeCountEstimate);
        }
        return remainTimeCountAll;
    }

    private List<GroupDataChartDO> dealIssueCountFinal(List<GroupDataChartDO> issueCountAll, List<GroupDataChartDO> issueCountCompleted) {
        for (GroupDataChartDO g1 : issueCountAll) {
            for (GroupDataChartDO g2 : issueCountCompleted) {
                if (g1.getGroupDay().equals(g2.getGroupDay())) {
                    g1.setIssueCompletedCount(g2.getIssueCompletedCount());
                }
            }
        }
        return issueCountAll;
    }

    @Override
    @Cacheable(cacheNames = AGILE, key = "'EpicChart' + #projectId + ':' + #epicId + ':' + #type")
    public List<GroupDataChartDO> queryEpicChart(Long projectId, Long epicId, String type) {
        List<GroupDataChartDO> result = null;
        switch (type) {
            case TYPE_STORY_POINT:
                List<GroupDataChartDO> storyPointsAll = reportMapper.selectByStoryPointAllFinal(projectId, epicId, EPIC_CHART);
                List<GroupDataChartDO> storyPointsCompleted = reportMapper.selectByStoryPointCompletedFinal(projectId, epicId, EPIC_CHART);
                List<GroupDataChartDO> storyPointCountAll = reportMapper.selectByStoryPointCountAll(projectId, epicId, EPIC_CHART);
                List<GroupDataChartDO> storyPointCountEstimate = reportMapper.selectByStoryPointCountEstimate(projectId, epicId, EPIC_CHART);
                result = dealStoryPointFinal(storyPointsAll, storyPointsCompleted, storyPointCountAll, storyPointCountEstimate);
                break;
            case TYPE_ISSUE_COUNT:
                List<GroupDataChartDO> issueCountAll = reportMapper.selectByIssueCountAllFinal(projectId, epicId, EPIC_CHART);
                List<GroupDataChartDO> issueCountCompleted = reportMapper.selectByIssueCountCompletedFinal(projectId, epicId, EPIC_CHART);
                result = dealIssueCountFinal(issueCountAll, issueCountCompleted);
                break;
            case TYPE_REMAIN_TIME:
                List<GroupDataChartDO> remainTimeRemainCompleted = reportMapper.selectByRemainTimeRemainCompleted(projectId, epicId, EPIC_CHART);
                List<GroupDataChartDO> remainTimeWorkLogCompleted = reportMapper.selectByRemainTimeWorkLogCompleted(projectId, epicId, EPIC_CHART);
                List<GroupDataChartDO> remainTimeRemainAll = reportMapper.selectByRemainTimeRemainAll(projectId, epicId, EPIC_CHART);
                List<GroupDataChartDO> remainTimeWorkLogAll = reportMapper.selectByRemainTimeWorkLogAll(projectId, epicId, EPIC_CHART);
                List<GroupDataChartDO> remainTimeCountAll = reportMapper.selectByRemainTimeCountAll(projectId, epicId, EPIC_CHART);
                List<GroupDataChartDO> remainTimeCountEstimate = reportMapper.selectByRemainTimeCountEstimate(projectId, epicId, EPIC_CHART);
                result = dealRemainTimeFinal(remainTimeRemainCompleted, remainTimeWorkLogCompleted, remainTimeRemainAll, remainTimeWorkLogAll, remainTimeCountAll, remainTimeCountEstimate);
                break;
            default:
                break;
        }
        return result == null ? new ArrayList<>() : result;
    }

    @Override
    public List<GroupDataChartListDO> queryEpicChartList(Long projectId, Long epicId) {
        return reportMapper.selectEpicIssueList(projectId, epicId);
    }

    @Override
    @Cacheable(cacheNames = AGILE, key = "'BurnDownCoordinate' + #projectId + ':' + #sprintId + ':' + #type")
    public JSONObject queryBurnDownCoordinate(Long projectId, Long sprintId, String type) {
        List<ReportIssueE> reportIssueEList = getBurnDownReport(projectId, sprintId, type);
        return handleSameDay(reportIssueEList.stream().filter(reportIssueE -> !"endSprint".equals(reportIssueE.getType())).
                sorted(Comparator.comparing(ReportIssueE::getDate)).collect(Collectors.toList()));
    }

    @Override
    public List<BurnDownReportCoordinateDTO> queryBurnDownCoordinateByType(Long projectId, Long id, String type) {
        List<IssueBurnDownReportDO> issueDOList = "Epic".equals(type) ? issueMapper.queryIssueByEpicId(projectId, id) : issueMapper.queryIssueByVersionId(projectId, id);
        if (issueDOList != null && !issueDOList.isEmpty()) {
            if (issueDOList.stream().noneMatch(issueDO -> issueDO.getStoryPoints() != null)) {
                return new ArrayList<>();
            } else {
                Date startDate = "Epic".equals(type) ? issueMapper.selectByPrimaryKey(id).getCreationDate() : versionMapper.selectByPrimaryKey(id).getCreationDate();
                List<SprintDO> sprintDOList = sprintMapper.queryNotPlanSprintByProjectId(projectId, startDate);
                List<IssueBurnDownReportDO> issueDOS = issueDOList.stream().filter(issueDO -> issueDO.getStoryPoints() != null).collect(Collectors.toList());
                List<BurnDownReportCoordinateDTO> reportCoordinateDTOS = new ArrayList<>();
                if (sprintDOList != null && !sprintDOList.isEmpty()) {
                    handleBurnDownCoordinateByTypeExistSprint(issueDOS, reportCoordinateDTOS, startDate, sprintDOList, type);
                } else {
                    Integer count = issueDOS.stream().mapToInt(IssueBurnDownReportDO::getStoryPoints).sum() -
                            issueDOS.stream().filter(IssueBurnDownReportDO::getCompleted).mapToInt(IssueBurnDownReportDO::getStoryPoints).sum();
                    reportCoordinateDTOS.add(new BurnDownReportCoordinateDTO(count, 0, 0, count,
                            type + "开始时的预估", startDate, new Date()));
                }
                return reportCoordinateDTOS;
            }
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public BurnDownReportDTO queryBurnDownReportByType(Long projectId, Long id, String type) {
        BurnDownReportDTO burnDownReportDTO = new BurnDownReportDTO();
        Boolean typeCondition = "Epic".equals(type);
        List<IssueBurnDownReportDO> issueDOList = typeCondition ? issueMapper.queryIssueByEpicId(projectId, id) : issueMapper.queryIssueByVersionId(projectId, id);
        burnDownReportDTO.setJsonObject(new JSONObject());
        handleBurnDownReportTypeData(burnDownReportDTO, id, projectId, typeCondition);
        if (issueDOList != null && !issueDOList.isEmpty()) {
            List<IssueBurnDownReportDO> incompleteIssues = issueDOList.stream().filter(issueDO -> !issueDO.getCompleted()).collect(Collectors.toList());
            burnDownReportDTO.setIncompleteIssues(reportAssembler.issueBurnDownReportDoToDto(incompleteIssues));
            Date startDate = typeCondition ? issueMapper.selectByPrimaryKey(id).getCreationDate() : versionMapper.selectByPrimaryKey(id).getCreationDate();
            List<SprintDO> sprintDOList = sprintMapper.queryNotPlanSprintByProjectId(projectId, startDate);
            if (sprintDOList != null && !sprintDOList.isEmpty()) {
                List<IssueBurnDownReportDO> completeIssues = issueDOList.stream().filter(issueDO -> issueDO.getCompleted() && issueDO.getDoneDate() != null).collect(Collectors.toList());
                handleBurnDownReportSprintData(sprintDOList, completeIssues, burnDownReportDTO);
            }
        }
        return burnDownReportDTO;
    }

    private void handleBurnDownReportSprintData(List<SprintDO> sprintDOList, List<IssueBurnDownReportDO> completeIssues, BurnDownReportDTO burnDownReportDTO) {
        List<SprintBurnDownReportDTO> sprintBurnDownReportDTOS = new ArrayList<>();
        if (sprintDOList.size() == 1) {
            SprintBurnDownReportDTO sprintBurnDownReportDTO = reportAssembler.sprintBurnDownReportDoToDto(sprintDOList.get(0));
            List<IssueBurnDownReportDO> singleCompleteIssues = completeIssues.stream().filter(issueDO ->
                    issueDO.getDoneDate().after(sprintBurnDownReportDTO.getStartDate())).collect(Collectors.toList());
            sprintBurnDownReportDTO.setCompleteIssues(reportAssembler.issueBurnDownReportDoToDto(singleCompleteIssues));
            sprintBurnDownReportDTOS.add(sprintBurnDownReportDTO);
        } else {
            for (int i = 0; i < sprintDOList.size() - 1; i++) {
                SprintBurnDownReportDTO sprintBurnDownReportDTO = reportAssembler.sprintBurnDownReportDoToDto(sprintDOList.get(i));
                Date startDateOne = sprintBurnDownReportDTO.getStartDate();
                Date startDateTwo = sprintDOList.get(i + 1).getStartDate();
                List<IssueBurnDownReportDO> duringSprintIncompleteIssues = handleDuringSprintIncompleteIssues(completeIssues, startDateOne, startDateTwo);
                sprintBurnDownReportDTO.setCompleteIssues(reportAssembler.issueBurnDownReportDoToDto(duringSprintIncompleteIssues));
                sprintBurnDownReportDTOS.add(sprintBurnDownReportDTO);
                if (i == sprintDOList.size() - 2) {
                    SprintBurnDownReportDTO lastSprintBurnDownReportDTO = reportAssembler.sprintBurnDownReportDoToDto(sprintDOList.get(i + 1));
                    List<IssueBurnDownReportDO> lastCompleteIssues = completeIssues.stream().filter(issueDO ->
                            issueDO.getDoneDate().after(lastSprintBurnDownReportDTO.getStartDate())).collect(Collectors.toList());
                    lastSprintBurnDownReportDTO.setCompleteIssues(reportAssembler.issueBurnDownReportDoToDto(lastCompleteIssues));
                    lastSprintBurnDownReportDTO.setEndDate(lastSprintBurnDownReportDTO.getEndDate() == null ? new Date() : lastSprintBurnDownReportDTO.getEndDate());
                    sprintBurnDownReportDTOS.add(lastSprintBurnDownReportDTO);
                }
            }
        }
        burnDownReportDTO.setSprintBurnDownReportDTOS(sprintBurnDownReportDTOS);
    }

    private List<IssueBurnDownReportDO> handleDuringSprintIncompleteIssues(List<IssueBurnDownReportDO> completeIssues, Date startDateOne, Date startDateTwo) {
        List<IssueBurnDownReportDO> duringSprintIncompleteIssues = new ArrayList<>();
        for (IssueBurnDownReportDO issueDO : completeIssues) {
            if (issueDO.getDoneDate().after(startDateOne) && issueDO.getDoneDate().before(startDateTwo)) {
                duringSprintIncompleteIssues.add(issueDO);
            }
        }
        return duringSprintIncompleteIssues;
    }


    private void handleBurnDownReportTypeData(BurnDownReportDTO burnDownReportDTO, Long id, Long projectId, Boolean typeCondition) {
        if (typeCondition) {
            IssueDO issueDO = issueMapper.queryEpicDetailByIssueId(id, projectId);
            burnDownReportDTO.getJsonObject().put("epicName", issueDO.getEpicName());
            burnDownReportDTO.getJsonObject().put("issueNum", issueDO.getIssueNum());
            burnDownReportDTO.getJsonObject().put("issueId", issueDO.getIssueId());
        } else {
            ProductVersionDO query = new ProductVersionDO();
            query.setVersionId(id);
            query.setProjectId(projectId);
            ProductVersionDO productVersionDO = versionMapper.selectByPrimaryKey(query);
            burnDownReportDTO.getJsonObject().put("name", productVersionDO.getName());
            burnDownReportDTO.getJsonObject().put("versionId", productVersionDO.getVersionId());
            burnDownReportDTO.getJsonObject().put("startDate", productVersionDO.getStartDate());
            burnDownReportDTO.getJsonObject().put("releaseDate", productVersionDO.getReleaseDate());
        }
    }

    private void handleBurnDownCoordinateByTypeExistSprint(List<IssueBurnDownReportDO> issueDOS, List<BurnDownReportCoordinateDTO> reportCoordinateDTOS,
                                                           Date startDate, List<SprintDO> sprintDOList, String type) {
        Integer count = issueDOS.stream().filter(issueDO -> issueDO.getAddDate().before(sprintDOList.get(0).getStartDate()))
                .mapToInt(IssueBurnDownReportDO::getStoryPoints).sum()
                - issueDOS.stream().filter(issueDO -> issueDO.getCompleted() && issueDO.getDoneDate().before(sprintDOList.get(0).getStartDate()))
                .mapToInt(IssueBurnDownReportDO::getStoryPoints).sum();
        reportCoordinateDTOS.add(new BurnDownReportCoordinateDTO(count, 0, 0, count,
                type + "开始时的预估", startDate, sprintDOList.get(0).getStartDate()));
        if (sprintDOList.size() == 1) {
            handleSprintSingle(reportCoordinateDTOS, issueDOS, sprintDOList);
        } else {
            handleSprintMultitude(reportCoordinateDTOS, issueDOS, sprintDOList);
        }
    }

    private void handleSprintMultitude(List<BurnDownReportCoordinateDTO> reportCoordinateDTOS, List<IssueBurnDownReportDO> issueDOS, List<SprintDO> sprintDOList) {
        for (int i = 0; i < sprintDOList.size() - 1; i++) {
            Date startDateOne = sprintDOList.get(i).getStartDate();
            Date startDateTwo = sprintDOList.get(i + 1).getStartDate();
            Date endDate = sprintDOList.get(i).getActualEndDate() == null ? sprintDOList.get(i).getEndDate() : sprintDOList.get(i).getActualEndDate();
            handleReportCoordinateDuringSprint(issueDOS, startDateOne, startDateTwo, reportCoordinateDTOS, endDate, sprintDOList.get(i).getSprintName());
            if (i == sprintDOList.size() - 2) {
                Integer startLast = reportCoordinateDTOS.get(reportCoordinateDTOS.size() - 1).getLeft();
                Integer addLast = issueDOS.stream().filter(issueDO -> issueDO.getAddDate().after(startDateTwo))
                        .mapToInt(IssueBurnDownReportDO::getStoryPoints).sum();
                Integer doneLast = issueDOS.stream().filter(issueDO -> issueDO.getCompleted() && issueDO.getDoneDate().after(startDateTwo))
                        .mapToInt(IssueBurnDownReportDO::getStoryPoints).sum();
                reportCoordinateDTOS.add(new BurnDownReportCoordinateDTO(startLast, addLast, doneLast, startLast + addLast - doneLast,
                        sprintDOList.get(sprintDOList.size() - 1).getSprintName(), sprintDOList.get(sprintDOList.size() - 1).getStartDate(), endDate));
            }

        }
    }

    private void handleReportCoordinateDuringSprint(List<IssueBurnDownReportDO> issueDOS, Date startDateOne, Date startDateTwo, List<BurnDownReportCoordinateDTO> reportCoordinateDTOS, Date endDate, String sprintName) {
        Integer add = 0;
        Integer done = 0;
        Integer start = reportCoordinateDTOS.get(reportCoordinateDTOS.size() - 1).getLeft();
        for (IssueBurnDownReportDO issueDO : issueDOS) {
            if (issueDO.getAddDate().after(startDateOne) && issueDO.getAddDate().before(startDateTwo)) {
                add += issueDO.getStoryPoints();
            }
            if (issueDO.getCompleted() && issueDO.getDoneDate() != null && issueDO.getDoneDate().after(startDateOne) && issueDO.getDoneDate().before(startDateTwo)) {
                done += issueDO.getStoryPoints();
            }
        }
        reportCoordinateDTOS.add(new BurnDownReportCoordinateDTO(start, add, done, start + add - done,
                sprintName, startDateOne, endDate));
    }


    private void handleSprintSingle(List<BurnDownReportCoordinateDTO> reportCoordinateDTOS, List<IssueBurnDownReportDO> issueDOS, List<SprintDO> sprintDOList) {
        Integer start = reportCoordinateDTOS.get(0).getLeft();
        Integer add = issueDOS.stream().filter(issueDO -> issueDO.getAddDate().after(sprintDOList.get(0).getStartDate()))
                .mapToInt(IssueBurnDownReportDO::getStoryPoints).sum();
        Integer done = issueDOS.stream().filter(issueDO -> issueDO.getCompleted() && issueDO.getDoneDate().after(sprintDOList.get(0).getStartDate()))
                .mapToInt(IssueBurnDownReportDO::getStoryPoints).sum();
        Date endDate = sprintDOList.get(0).getActualEndDate() == null ? sprintDOList.get(0).getEndDate() : sprintDOList.get(0).getActualEndDate();
        reportCoordinateDTOS.add(new BurnDownReportCoordinateDTO(start, add, done, start + add - done,
                sprintDOList.get(0).getSprintName(), sprintDOList.get(0).getStartDate(), endDate));
    }


    @Override
    @Cacheable(cacheNames = AGILE, key = "'VersionChart' + #projectId + ':' + #versionId + ':' + #type")
    public List<GroupDataChartDO> queryVersionChart(Long projectId, Long versionId, String type) {
        List<GroupDataChartDO> result = null;
        switch (type) {
            case TYPE_ISSUE_COUNT:
                List<GroupDataChartDO> issueCountAll = reportMapper.selectByIssueCountAllFinal(projectId, versionId, VERSION_CHART);
                List<GroupDataChartDO> issueCountCompleted = reportMapper.selectByIssueCountCompletedFinal(projectId, versionId, VERSION_CHART);
                result = dealIssueCountFinal(issueCountAll, issueCountCompleted);
                break;
            case TYPE_STORY_POINT:
                List<GroupDataChartDO> storyPointsAll = reportMapper.selectByStoryPointAllFinal(projectId, versionId, VERSION_CHART);
                List<GroupDataChartDO> storyPointsCompleted = reportMapper.selectByStoryPointCompletedFinal(projectId, versionId, VERSION_CHART);
                List<GroupDataChartDO> storyPointCountAll = reportMapper.selectByStoryPointCountAll(projectId, versionId, VERSION_CHART);
                List<GroupDataChartDO> storyPointCountEstimate = reportMapper.selectByStoryPointCountEstimate(projectId, versionId, VERSION_CHART);
                result = dealStoryPointFinal(storyPointsAll, storyPointsCompleted, storyPointCountAll, storyPointCountEstimate);
                break;
            case TYPE_REMAIN_TIME:
                List<GroupDataChartDO> remainTimeRemainCompleted = reportMapper.selectByRemainTimeRemainCompleted(projectId, versionId, VERSION_CHART);
                List<GroupDataChartDO> remainTimeWorkLogCompleted = reportMapper.selectByRemainTimeWorkLogCompleted(projectId, versionId, VERSION_CHART);
                List<GroupDataChartDO> remainTimeRemainAll = reportMapper.selectByRemainTimeRemainAll(projectId, versionId, VERSION_CHART);
                List<GroupDataChartDO> remainTimeWorkLogAll = reportMapper.selectByRemainTimeWorkLogAll(projectId, versionId, VERSION_CHART);
                List<GroupDataChartDO> remainTimeCountAll = reportMapper.selectByRemainTimeCountAll(projectId, versionId, VERSION_CHART);
                List<GroupDataChartDO> remainTimeCountEstimate = reportMapper.selectByRemainTimeCountEstimate(projectId, versionId, VERSION_CHART);
                result = dealRemainTimeFinal(remainTimeRemainCompleted, remainTimeWorkLogCompleted, remainTimeRemainAll, remainTimeWorkLogAll, remainTimeCountAll, remainTimeCountEstimate);
                break;
            default:
                break;
        }
        return result == null ? new ArrayList<>() : result;
    }

    @Override
    public List<GroupDataChartListDO> queryVersionChartList(Long projectId, Long versionId) {
        return reportMapper.selectVersionIssueList(projectId, versionId);
    }
}

