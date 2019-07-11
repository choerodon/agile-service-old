package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSONObject;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.app.assembler.IssueAssembler;
import io.choerodon.agile.app.assembler.ReportAssembler;
import io.choerodon.agile.app.service.DataLogService;
import io.choerodon.agile.app.service.ReportService;
import io.choerodon.agile.domain.agile.converter.SprintConverter;
import io.choerodon.agile.infra.dataobject.ReportIssueConvertDTO;
import io.choerodon.agile.infra.dataobject.SprintConvertDTO;
import io.choerodon.agile.infra.feign.UserFeignClient;
import io.choerodon.agile.infra.common.utils.PageUtil;
import io.choerodon.agile.infra.repository.DataLogRepository;
import io.choerodon.agile.app.service.UserService;
import io.choerodon.agile.infra.common.enums.SchemeApplyType;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.feign.StateMachineFeignClient;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.core.convertor.ConvertHelper;

import com.github.pagehelper.PageInfo;

import io.choerodon.core.exception.CommonException;

import com.github.pagehelper.PageHelper;

import io.choerodon.base.domain.PageRequest;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/19
 */
@Service
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
    private UserService userService;
    @Autowired
    private IssueFeignClient issueFeignClient;
    @Autowired
    private StateMachineFeignClient stateMachineFeignClient;
    @Autowired
    private DataLogRepository dataLogRepository;
    @Autowired
    private DataLogService dataLogService;
    @Autowired
    private UserFeignClient userFeignClient;

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
    private static final String PRIORITY = "priority";
    private static final String STATUS = "status";
    private static final String SPRINT = "sprint";
    private static final String EPIC = "epic";
    private static final String RESOLUTION = "resolution";
    private static final String LABEL = "label";
    private static final String TYPE_ISSUE_COUNT = "issue_count";
    private static final String TYPE_STORY_POINT = "story_point";
    private static final String TYPE_REMAIN_TIME = "remain_time";
    private static final String VERSION_CHART = "version_chart";
    private static final String EPIC_CHART = "epic_chart";
    private static final String AGILE = "Agile";
    private static final String EPIC_OR_VERSION_NOT_FOUND_ERROR = "error.EpicOrVersion.notFound";
    private static final String SPRINT_DO_LIST = "sprintDOList";
    private static final String START_DATE = "startDate";
    private static final String E_PIC = "Epic";
    private static final String ASC = "asc";
    private static final ExecutorService pool = Executors.newFixedThreadPool(3);

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportServiceImpl.class);

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public void setReportMapper(ReportMapper reportMapper) {
        this.reportMapper = reportMapper;
    }

    @Override
    public List<IssueTypeDistributionChartVO> queryIssueTypeDistributionChart(Long projectId) {
        return reportAssembler.toIssueTypeDistributionChartDTO(projectId, reportMapper.queryIssueTypeDistributionChart(projectId));
    }

    @Override
    public List<IssueTypeDistributionChartVO> queryVersionProgressChart(Long projectId) {
        return reportAssembler.toIssueTypeVersionDistributionChartDTO(projectId, reportMapper.queryVersionProgressChart(projectId));
    }

    @Override
    public List<IssuePriorityDistributionChartVO> queryIssuePriorityDistributionChart(Long projectId, Long organizationId) {
        List<Long> priorityIds = issueFeignClient.queryByOrganizationIdList(organizationId).getBody().stream().map(PriorityVO::getId).collect(Collectors.toList());
        return reportAssembler.toIssuePriorityDistributionChartDTO(projectId, reportMapper.queryIssuePriorityDistributionChart(projectId, priorityIds));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void fixCumulativeFlowDiagram() {
        Set<Long> issueIds = reportMapper.queryIssueDOByFixCumulativeData();
        Set<Long> removeIssueIdS = reportMapper.queryRemoveIssueIds();
        issueIds.removeAll(removeIssueIdS);
        Set<Long> dataLogIds = Collections.synchronizedSet(new HashSet<>());
        Set<DataLogStatusChangeDTO> dataLogStatusChangeDTOS = Collections.synchronizedSet(new HashSet<>());
        issueIds.parallelStream().forEach(issueId -> {
            List<FixCumulativeData> fixCumulativeData = reportMapper.queryFixCumulativeData(issueId);
            if (fixCumulativeData != null && !fixCumulativeData.isEmpty() && fixCumulativeData.size() > 1) {
                Set<FixCumulativeData> remove = new HashSet<>();
                List<FixCumulativeData> statusIds = new ArrayList<>();
                Boolean condition = false;
                for (int i = 0; i < fixCumulativeData.size() - 1; i++) {
                    FixCumulativeData preData = fixCumulativeData.get(i);
                    FixCumulativeData nextData = fixCumulativeData.get(i + 1);
                    if (!preData.getNewStatusId().equals(nextData.getOldStatusId())) {
                        remove.add(preData);
                        remove.add(nextData);
                        condition = true;
                    } else {
                        if (condition) {
                            FixCumulativeData fixData = new FixCumulativeData();
                            fixData.setOldStatusId(preData.getOldStatusId());
                            fixData.setNewStatusId(nextData.getOldStatusId());
                            statusIds.add(fixData);
                            condition = false;
                        }
                    }
                    if (preData.getNewStatusId().equals(nextData.getOldStatusId()) && nextData.getOldStatusId() == 0 && preData.getOldStatusId() != 0) {
                        dataLogIds.add(nextData.getLogId());
                        DataLogStatusChangeDTO dataLogStatusChangeDTO = new DataLogStatusChangeDTO();
                        dataLogStatusChangeDTO.setLogId(preData.getLogId());
                        dataLogStatusChangeDTO.setNewValue(nextData.getNewStatusId());
                        dataLogStatusChangeDTOS.add(dataLogStatusChangeDTO);
                    }
                }
                if (!remove.isEmpty()) {
                    List<FixCumulativeData> removeDataList = new ArrayList<>(remove);
                    if (!statusIds.isEmpty()) {
                        statusIds.forEach(statusId -> {
                            List<FixCumulativeData> fixCumulativeDataList = fixCumulativeData.stream().filter(fixCumulativeData1 -> fixCumulativeData1.getNewStatusId().equals(statusId.getNewStatusId())
                                    && fixCumulativeData1.getOldStatusId().equals(statusId.getOldStatusId())).collect(Collectors.toList());
                            remove.remove(fixCumulativeDataList.get(0));
                            dataLogIds.addAll(remove.stream().map(FixCumulativeData::getLogId).collect(Collectors.toList()));
                        });
                    } else {
                        remove.remove(removeDataList.get(0));
                        dataLogIds.addAll(remove.stream().map(FixCumulativeData::getLogId).collect(Collectors.toList()));
                    }
                }
            }
        });
        if (!dataLogIds.isEmpty()) {
            dataLogService.batchDeleteErrorDataLog(dataLogIds);
        }
        if (!dataLogStatusChangeDTOS.isEmpty()) {
            dataLogService.batchUpdateErrorDataLog(dataLogStatusChangeDTOS);
        }
    }

    @Override
    public List<ReportIssueVO> queryBurnDownReport(Long projectId, Long sprintId, String type, String ordinalType) {
        List<ReportIssueConvertDTO> reportIssueConvertDTOList = getBurnDownReport(projectId, sprintId, type);
        return ConvertHelper.convertList(ordinalType.equals(ASC) ? reportIssueConvertDTOList.stream().
                sorted(Comparator.comparing(ReportIssueConvertDTO::getDate)).collect(Collectors.toList()) : reportIssueConvertDTOList.stream().
                sorted(Comparator.comparing(ReportIssueConvertDTO::getDate).reversed()).collect(Collectors.toList()), ReportIssueVO.class);
    }

    private List<ReportIssueConvertDTO> getBurnDownReport(Long projectId, Long sprintId, String type) {
        List<ReportIssueConvertDTO> reportIssueConvertDTOList = new ArrayList<>();
        SprintDTO sprintDTO = new SprintDTO();
        sprintDTO.setSprintId(sprintId);
        sprintDTO.setProjectId(projectId);
        SprintConvertDTO sprintConvertDTO = sprintConverter.doToEntity(sprintMapper.selectOne(sprintDTO));
        if (sprintConvertDTO != null && !sprintConvertDTO.getStatusCode().equals(SPRINT_PLANNING_CODE)) {
            sprintConvertDTO.initStartAndEndTime();
            switch (type) {
                case STORY_POINTS:
                    queryStoryPointsOrRemainingEstimatedTime(sprintConvertDTO, reportIssueConvertDTOList, FIELD_STORY_POINTS);
                    break;
                case REMAINING_ESTIMATED_TIME:
                    queryStoryPointsOrRemainingEstimatedTime(sprintConvertDTO, reportIssueConvertDTOList, FIELD_TIMEESTIMATE);
                    break;
                case ISSUE_COUNT:
                    queryIssueCount(sprintConvertDTO, reportIssueConvertDTOList);
                    break;
                default:
                    queryStoryPointsOrRemainingEstimatedTime(sprintConvertDTO, reportIssueConvertDTOList, FIELD_STORY_POINTS);
                    break;
            }
        } else {
            throw new CommonException(REPORT_SPRINT_ERROR);
        }
        return reportIssueConvertDTOList;
    }

    private JSONObject handleSameDay(List<ReportIssueConvertDTO> reportIssueConvertDTOList) {
        JSONObject jsonObject = new JSONObject();
        DateFormat bf = new SimpleDateFormat("yyyy-MM-dd");
        TreeMap<String, BigDecimal> report = new TreeMap<>();
        //处理同一天
        reportIssueConvertDTOList.forEach(reportIssueConvertDTO -> {
            if (reportIssueConvertDTO.getStatistical()) {
                String date = bf.format(reportIssueConvertDTO.getDate());
                if (report.get(date) == null) {
                    BigDecimal zero = new BigDecimal(0);
                    BigDecimal count = report.lastEntry() == null ? zero : report.lastEntry().getValue();
                    report.put(date, count.add(reportIssueConvertDTO.getNewValue()).subtract(reportIssueConvertDTO.getOldValue()));
                } else {
                    report.put(date, report.get(date).add(reportIssueConvertDTO.getNewValue()).subtract(reportIssueConvertDTO.getOldValue()));
                }
            }
        });
        jsonObject.put("coordinate", report);
        //需要返回给前端期望值（开启冲刺的和）
        jsonObject.put("expectCount", handleExpectCount(reportIssueConvertDTOList));
        return jsonObject;
    }

    private BigDecimal handleExpectCount(List<ReportIssueConvertDTO> reportIssueConvertDTOList) {
        BigDecimal expectCount = new BigDecimal(0);
        List<ReportIssueConvertDTO> startReportIssue = reportIssueConvertDTOList.stream().filter(reportIssueConvertDTO -> "startSprint".equals(reportIssueConvertDTO.getType())).collect(Collectors.toList());
        if (startReportIssue != null && !startReportIssue.isEmpty()) {
            for (ReportIssueConvertDTO reportIssueConvertDTO : startReportIssue) {
                if (reportIssueConvertDTO.getStatistical()) {
                    expectCount = expectCount.add(reportIssueConvertDTO.getNewValue().subtract(reportIssueConvertDTO.getOldValue()));
                }
            }
        }
        return expectCount;
    }


    @Override
    @Cacheable(cacheNames = AGILE, key = "'CumulativeFlowDiagram' + #projectId + ':' + #cumulativeFlowFilterVO.toString()")
    public List<CumulativeFlowDiagramVO> queryCumulativeFlowDiagram(Long projectId, CumulativeFlowFilterVO cumulativeFlowFilterVO) {
        //获取当前符合条件的所有issueIds
        String filterSql = null;
        if (cumulativeFlowFilterVO.getQuickFilterIds() != null && !cumulativeFlowFilterVO.getQuickFilterIds().isEmpty()) {
            filterSql = sprintService.getQuickFilter(cumulativeFlowFilterVO.getQuickFilterIds());
        }
        //epic没有计算在里面
        List<Long> allIssueIds = reportMapper.queryAllIssueIdsByFilter(projectId, filterSql);
        if (allIssueIds != null && !allIssueIds.isEmpty() && cumulativeFlowFilterVO.getColumnIds() != null && !cumulativeFlowFilterVO.getColumnIds().isEmpty()) {
            return getCumulativeFlowDiagram(allIssueIds, projectId, cumulativeFlowFilterVO);
        } else if (cumulativeFlowFilterVO.getColumnIds() == null || cumulativeFlowFilterVO.getColumnIds().isEmpty()) {
            throw new CommonException(REPORT_FILTER_ERROR);
        } else {
            return new ArrayList<>();
        }
    }

    private void handleColumnCoordinate(List<ColumnChangeVO> columnChangeVOList,
                                        CumulativeFlowDiagramVO cumulativeFlowDiagramVO,
                                        Date startDate,
                                        Date endDate) {
        List<CoordinateVO> coordinateVOS = new ArrayList<>();
        List<ColumnChangeVO> columnChange = columnChangeVOList.stream().filter(columnChangeVO ->
                Objects.equals(columnChangeVO.getColumnFrom(), cumulativeFlowDiagramVO.getColumnId().toString())
                        || Objects.equals(columnChangeVO.getColumnTo(), cumulativeFlowDiagramVO.getColumnId().toString())).collect(Collectors.toList());
        if (columnChange != null && !columnChange.isEmpty()) {
            DateFormat bf = new SimpleDateFormat("yyyy-MM-dd");
            TreeMap<String, Integer> report = handleColumnCoordinateReport(columnChange, startDate, endDate, cumulativeFlowDiagramVO, bf);
            report.forEach((k, v) -> {
                CoordinateVO coordinateVO = new CoordinateVO();
                coordinateVO.setIssueCount(v);
                try {
                    coordinateVO.setDate(bf.parse(k));
                } catch (ParseException e) {
                    LOGGER.error("Exception:{}", e);
                }
                coordinateVOS.add(coordinateVO);
            });
            cumulativeFlowDiagramVO.setCoordinateVOList(coordinateVOS);
        }
    }

    private TreeMap<String, Integer> handleColumnCoordinateReport(List<ColumnChangeVO> columnChange, Date startDate, Date endDate, CumulativeFlowDiagramVO cumulativeFlowDiagramVO, DateFormat bf) {
        TreeMap<String, Integer> report = new TreeMap<>();
        if (columnChange.get(0).getDate().after(startDate)) {
            report.put(bf.format(startDate), 0);
        }
        String columnId = cumulativeFlowDiagramVO.getColumnId().toString();
        //处理同一天数据
        columnChange.forEach(columnChangeVO -> handleColumnCoordinateSameDate(bf, report, columnChangeVO, columnId));
        Date lastDate = columnChange.get(columnChange.size() - 1).getDate();
        if (lastDate.before(endDate)) {
            report.put(bf.format(endDate), report.lastEntry().getValue());
        }
        return report;
    }

    private void handleColumnCoordinateSameDate(DateFormat bf, TreeMap<String, Integer> report, ColumnChangeVO columnChangeVO, String columnId) {
        String date = bf.format(columnChangeVO.getDate());
        if (report.get(date) == null) {
            Integer count = report.lastEntry() == null ? 0 : report.lastEntry().getValue();
            if (columnChangeVO.getColumnFrom().equals(columnId)) {
                report.put(date, count - 1);
            } else {
                report.put(date, count + 1);
            }
        } else {
            if (columnChangeVO.getColumnFrom().equals(columnId)) {
                report.put(date, report.get(date) - 1);
            } else {
                report.put(date, report.get(date) + 1);
            }

        }
    }


    private void handleCumulativeFlowChangeDuringDate(Long projectId, Date startDate, Date endDate, List<Long> columnIds, List<Long> allIssueIds, List<ColumnChangeVO> result) {
        List<ColumnChangeVO> changeIssueDuringDate = reportAssembler.toTargetList
                (reportMapper.queryChangeIssueDuringDate(projectId, startDate, endDate, allIssueIds, columnIds), ColumnChangeVO.class);
        List<BoardColumnStatusRelDTO> relDOs = boardColumnMapper.queryRelByColumnIds(columnIds);
        Map<Long, Long> relMap = relDOs.stream().collect(Collectors.toMap(BoardColumnStatusRelDTO::getStatusId, BoardColumnStatusRelDTO::getColumnId));
        changeIssueDuringDate.parallelStream().forEach(changeDto -> {
            Long columnTo = relMap.get(Long.parseLong(changeDto.getNewValue()));
            Long columnFrom = relMap.get(Long.parseLong(changeDto.getOldValue()));
            changeDto.setColumnTo(columnTo == null ? "0" : columnTo + "");
            changeDto.setColumnFrom(columnFrom == null ? "0" : columnFrom + "");
        });
        changeIssueDuringDate = changeIssueDuringDate.stream().filter(x -> x.getColumnFrom() != x.getColumnTo()).collect(Collectors.toList());
        if (changeIssueDuringDate != null && !changeIssueDuringDate.isEmpty()) {
            result.addAll(changeIssueDuringDate);
        }
    }

    @Override
    public PageInfo<IssueListVO> queryIssueByOptions(Long projectId, Long versionId, String status, String type, PageRequest pageRequest, Long organizationId) {
        ProductVersionDTO versionDO = new ProductVersionDTO();
        versionDO.setProjectId(projectId);
        versionDO.setVersionId(versionId);
        versionDO = versionMapper.selectOne(versionDO);
        if (versionDO == null || Objects.equals(versionDO.getStatusCode(), VERSION_ARCHIVED_CODE)) {
            throw new CommonException(VERSION_REPORT_ERROR);
        }
        pageRequest.setSort(PageUtil.sortResetOrder(pageRequest.getSort(), "ai", new HashMap<>()));
        //pageRequest.resetOrder("ai", new HashMap<>());
        PageInfo<IssueDTO> reportIssuePage = PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize(),
                PageUtil.sortToSql(pageRequest.getSort())).doSelectPageInfo(() -> reportMapper.
                queryReportIssues(projectId, versionId, status, type));
        Map<Long, PriorityVO> priorityMap = issueFeignClient.queryByOrganizationId(organizationId).getBody();
        Map<Long, IssueTypeVO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
        Map<Long, StatusMapVO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
        return PageUtil.buildPageInfoWithPageInfoList(reportIssuePage, issueAssembler.issueDoToIssueListDto(reportIssuePage.getList(), priorityMap, statusMapDTOMap, issueTypeDTOMap));
    }

    @Override
    public Map<String, Object> queryVersionLineChart(Long projectId, Long versionId, String type) {
        Map<String, Object> versionReportMap = new HashMap<>();
        ProductVersionDTO versionDO = new ProductVersionDTO();
        versionDO.setProjectId(projectId);
        versionDO.setVersionId(versionId);
        versionDO = versionMapper.selectOne(versionDO);
        if (versionDO == null || Objects.equals(versionDO.getStatusCode(), VERSION_ARCHIVED_CODE)) {
            throw new CommonException(VERSION_REPORT_ERROR);
        }
        List<VersionReportVO> versionReport = new ArrayList<>();
        List<Long> nowVersionIssue = reportMapper.queryIssueIdByVersionId(projectId, versionId);
        Date startDate = versionDO.getStartDate() != null ? versionDO.getStartDate() : versionDO.getCreationDate();
        Date endDate = new Date();
        List<VersionIssueChangeDTO> versionChangeIssue = reportMapper.queryChangeIssue(projectId, versionId, startDate, endDate);
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

    private void statisticsByIssueCount(List<VersionReportVO> versionReport, Long projectId, List<Long> nowVersionIssue, List<VersionIssueChangeDTO> versionChangeIssue, Date startDate, Date endDate) {
        VersionIssueChangeDTO nowVersionIssueChange = new VersionIssueChangeDTO();
        List<VersionIssueChangeDTO> versionIssues = new ArrayList<>();
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
        Map<Date, List<IssueChangeVO>> completedIssuesMap = statisticalCompletedChange(projectId, versionIssues, dateSet, FIELD_STORY_POINTS);
        Map<Date, List<IssueChangeVO>> unCompletedIssuesMap = statisticalUnCompletedChange(projectId, versionIssues, dateSet, FIELD_STORY_POINTS);
        Map<Date, List<IssueChangeVO>> addIssuesMap = statisticalAddChangeIssue(projectId, versionChangeIssue, dateSet, FIELD_STORY_POINTS);
        Map<Date, List<IssueChangeVO>> removeIssuesMap = statisticalRemoveChangeIssue(projectId, versionChangeIssue, dateSet, FIELD_STORY_POINTS);
        VersionReportVO nowVersionReportVO = new VersionReportVO();
        nowVersionReportVO.setChangeDate(endDate);
        nowVersionReportVO.setTotalField(nowIssueCount);
        nowVersionReportVO.setCompletedField(nowCompletedIssueCount);
        versionReport.add(nowVersionReportVO);
        for (Date date : dateSet) {
            VersionReportVO versionReportVO = new VersionReportVO();
            List<IssueChangeVO> completedIssue = changeIssueNowDate(completedIssuesMap, date);
            List<Long> completedIssueIds = completedIssue.stream().map(IssueChangeVO::getIssueId).collect(Collectors.toList());
            List<IssueChangeVO> unCompletedIssue = changeIssueNowDate(unCompletedIssuesMap, date);
            List<IssueChangeVO> addIssue = changeIssueNowDate(addIssuesMap, date);
            List<IssueChangeVO> removeIssue = changeIssueNowDate(removeIssuesMap, date);
            Integer addCompletedCount = addIssue.stream().filter(addChangeIssue -> addChangeIssue.getCompleted() && !completedIssueIds.contains(addChangeIssue.getIssueId())).collect(Collectors.toList()).size();
            Integer removeCompletedCount = removeIssue.stream().filter(IssueChangeVO::getCompleted).collect(Collectors.toList()).size();
            nowIssueCount = nowIssueCount - addIssue.size() + removeIssue.size();
            nowCompletedIssueCount = nowCompletedIssueCount - completedIssue.size() + unCompletedIssue.size() - addCompletedCount + removeCompletedCount;
            versionReportVO.setChangeDate(date);
            versionReportVO.setTotalField(nowIssueCount);
            versionReportVO.setCompletedField(nowCompletedIssueCount);
            versionReportVO.setCompletedIssues(completedIssue);
            versionReportVO.setUnCompletedIssues(unCompletedIssue);
            versionReportVO.setAddIssues(addIssue);
            versionReportVO.setRemoveIssues(removeIssue);
            versionReport.add(versionReportVO);
        }
    }

    private void statisticsByStoryPointsOrRemainingTime(List<VersionReportVO> versionReport, Long projectId, List<Long> nowVersionIssue, List<VersionIssueChangeDTO> versionChangeIssue, Date startDate, Date endDate, String field) {
        VersionIssueChangeDTO nowVersionIssueChange = new VersionIssueChangeDTO();
        List<VersionIssueChangeDTO> versionIssues = new ArrayList<>();
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
        Map<Date, List<IssueChangeVO>> fieldChangeIssuesMap = statisticalFieldChange(projectId, versionIssues, dateSet, field);
        Map<Date, List<IssueChangeVO>> completedIssuesMap = statisticalCompletedChange(projectId, versionIssues, dateSet, field);
        Map<Date, List<IssueChangeVO>> unCompletedIssuesMap = statisticalUnCompletedChange(projectId, versionIssues, dateSet, field);
        Map<Date, List<IssueChangeVO>> addIssuesMap = statisticalAddChangeIssue(projectId, versionChangeIssue, dateSet, field);
        Map<Date, List<IssueChangeVO>> removeIssuesMap = statisticalRemoveChangeIssue(projectId, versionChangeIssue, dateSet, field);
        VersionReportVO nowVersionReportVO = new VersionReportVO();
        nowVersionReportVO.setChangeDate(endDate);
        nowVersionReportVO.setTotalField(nowTotalField);
        nowVersionReportVO.setCompletedField(nowCompletedField);
        nowVersionReportVO.setUnEstimatedPercentage(nowUnEstimatedPercentage);
        versionReport.add(nowVersionReportVO);
        for (Date date : dateSet) {
            VersionReportVO versionReportVO = new VersionReportVO();
            List<IssueChangeVO> fieldChangeIssue = changeIssueNowDate(fieldChangeIssuesMap, date);
            List<Long> fileChangeIds = fieldChangeIssue.stream().map(IssueChangeVO::getIssueId).collect(Collectors.toList());
            List<IssueChangeVO> completedIssue = changeIssueNowDate(completedIssuesMap, date);
            List<IssueChangeVO> unCompletedIssue = changeIssueNowDate(unCompletedIssuesMap, date);
            List<IssueChangeVO> addIssue = changeIssueNowDate(addIssuesMap, date);
            List<Long> addIssueIds = addIssue.stream().map(IssueChangeVO::getIssueId).collect(Collectors.toList());
            List<IssueChangeVO> removeIssue = changeIssueNowDate(removeIssuesMap, date);
            Integer changeField = fieldChangeIssue.stream().mapToInt(fieldChange -> Integer.valueOf(fieldChange.getChangeField())).sum();
            Integer changeCompletedField = fieldChangeIssue.stream().filter(IssueChangeVO::getCompleted).mapToInt(fieldChange -> Integer.valueOf(fieldChange.getChangeField())).sum();
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
            versionReportVO.setChangeDate(date);
            versionReportVO.setTotalField(nowTotalField);
            versionReportVO.setCompletedField(nowCompletedField);
            versionReportVO.setUnEstimatedPercentage(nowUnEstimatedPercentage);
            versionReportVO.setFieldChangIssues(fieldChangeIssue);
            versionReportVO.setCompletedIssues(completedIssue);
            versionReportVO.setUnCompletedIssues(unCompletedIssue);
            versionReportVO.setAddIssues(addIssue);
            versionReportVO.setRemoveIssues(removeIssue);
            versionReport.add(versionReportVO);
        }
    }

    private Integer completedCalculationUnEstimated(List<IssueChangeVO> completedIssue, String field, List<Long> addIssueIds) {
        return completedIssue.stream().filter(fieldChange ->
                (Objects.equals(fieldChange.getTypeCode(), ISSUE_STORY_CODE) || field.equals(FIELD_TIMEESTIMATE)) && fieldChange.getNewValue() == null && !addIssueIds.contains(fieldChange.getIssueId())
        ).collect(Collectors.toList()).size();
    }

    private Integer completedChangeIssueField(List<IssueChangeVO> completedIssue, List<Long> fileChangeIds) {
        return completedIssue.stream().filter(fieldChange -> fieldChange.getNewValue() != null && !fileChangeIds.contains(fieldChange.getIssueId())).mapToInt(fieldChange -> Integer.valueOf(fieldChange.getNewValue())).sum();
    }

    private Integer addChangeCompletedIssueField(List<IssueChangeVO> addIssue, List<Long> fileChangeIds) {
        return addIssue.stream().filter(fieldChange -> fieldChange.getCompleted() && fieldChange.getNewValue() != null && !fileChangeIds.contains(fieldChange.getIssueId())).mapToInt(fieldChange -> Integer.valueOf(fieldChange.getNewValue())).sum();
    }

    private Integer addChangeIssueField(List<IssueChangeVO> addIssue, List<Long> fileChangeIds) {
        return addIssue.stream().filter(fieldChange -> fieldChange.getNewValue() != null && !fileChangeIds.contains(fieldChange.getIssueId())).mapToInt(fieldChange -> Integer.valueOf(fieldChange.getNewValue())).sum();
    }

    private Integer calculationEstimated(List<IssueChangeVO> issueChange, String field, List<Long> addIssueIds) {
        return issueChange.stream().filter(fieldChange ->
                (Objects.equals(fieldChange.getTypeCode(), ISSUE_STORY_CODE) || field.equals(FIELD_TIMEESTIMATE)) && fieldChange.getOldValue() == null && !addIssueIds.contains(fieldChange.getIssueId())
        ).collect(Collectors.toList()).size();
    }

    private Integer changeCompletedIssueField(List<IssueChangeVO> issueChange) {
        return issueChange.stream().filter(fieldChange -> fieldChange.getCompleted() && fieldChange.getNewValue() != null).mapToInt(fieldChange -> Integer.valueOf(fieldChange.getNewValue())).sum();
    }

    private Integer changeIssueField(List<IssueChangeVO> issueChange) {
        return issueChange.stream().filter(fieldChange -> fieldChange.getNewValue() != null).mapToInt(fieldChange -> Integer.valueOf(fieldChange.getNewValue())).sum();
    }

    private List<IssueChangeVO> changeIssueNowDate(Map<Date, List<IssueChangeVO>> changeIssuesMap, Date date) {
        return changeIssuesMap.get(date) != null ? changeIssuesMap.get(date) : new ArrayList<>();
    }

    private Integer calculationUnEstimated(List<IssueChangeVO> issueChange, String field) {
        return issueChange.stream().filter(fieldChange ->
                (Objects.equals(fieldChange.getTypeCode(), ISSUE_STORY_CODE) || field.equals(FIELD_TIMEESTIMATE)) && fieldChange.getNewValue() == null
        ).collect(Collectors.toList()).size();
    }

    private Map<Date, List<IssueChangeVO>> statisticalRemoveChangeIssue(Long projectId, List<VersionIssueChangeDTO> versionChangeIssue, Set<Date> dateSet, String field) {
        //issue移除
        List<VersionIssueChangeDTO> versionRemoveChangeIssue = versionChangeIssue.stream().filter(versionIssueChangeDTO -> !versionIssueChangeDTO.getRemoveIssueIds().isEmpty()).map(versionIssueChangeDTO -> {
            versionIssueChangeDTO.setIssueIds(versionIssueChangeDTO.getRemoveIssueIds());
            return versionIssueChangeDTO;
        }).collect(Collectors.toList());
        List<IssueChangeDTO> versionRemoveChangeIssues = versionRemoveChangeIssue.isEmpty() ? new ArrayList<>() : reportMapper.queryChangIssue(projectId, versionRemoveChangeIssue, field);
        List<IssueChangeVO> removeIssues = issueAssembler.toTargetList(versionRemoveChangeIssues, IssueChangeVO.class);
        Map<Date, List<IssueChangeVO>> removeIssuesMap = removeIssues.stream().collect(Collectors.groupingBy(IssueChangeVO::getChangeDate));
        dateSet.addAll(removeIssuesMap.keySet());
        return removeIssuesMap;
    }

    private Map<Date, List<IssueChangeVO>> statisticalAddChangeIssue(Long projectId, List<VersionIssueChangeDTO> versionChangeIssue, Set<Date> dateSet, String field) {
        //issue移入
        List<VersionIssueChangeDTO> versionAddChangeIssue = versionChangeIssue.stream().filter(versionIssueChangeDTO -> !versionIssueChangeDTO.getAddIssueIds().isEmpty()).map(versionIssueChangeDTO -> {
            versionIssueChangeDTO.setIssueIds(versionIssueChangeDTO.getAddIssueIds());
            return versionIssueChangeDTO;
        }).collect(Collectors.toList());
        List<IssueChangeDTO> versionAddChangeIssues = versionAddChangeIssue.isEmpty() ? new ArrayList<>() : reportMapper.queryChangIssue(projectId, versionAddChangeIssue, field);
        List<IssueChangeVO> addIssues = issueAssembler.toTargetList(versionAddChangeIssues, IssueChangeVO.class);
        Map<Date, List<IssueChangeVO>> addIssuesMap = addIssues.stream().collect(Collectors.groupingBy(IssueChangeVO::getChangeDate));
        dateSet.addAll(addIssuesMap.keySet());
        return addIssuesMap;
    }

    private Map<Date, List<IssueChangeVO>> statisticalUnCompletedChange(Long projectId, List<VersionIssueChangeDTO> versionIssues, Set<Date> dateSet, String field) {
        //issue由完成变为未完成
        List<VersionIssueChangeDTO> unCompletedChangeIssues = reportMapper.queryCompletedChangeIssue(projectId, versionIssues, false);
        List<IssueChangeVO> unCompletedIssues = issueAssembler.toTargetList(unCompletedChangeIssues.isEmpty() ? new ArrayList<>() : reportMapper.queryChangIssue(projectId, unCompletedChangeIssues, field), IssueChangeVO.class);
        Map<Date, List<IssueChangeVO>> unCompletedIssuesMap = unCompletedIssues.stream().collect(Collectors.groupingBy(IssueChangeVO::getChangeDate));
        dateSet.addAll(unCompletedIssuesMap.keySet());
        return unCompletedIssuesMap;
    }

    private Map<Date, List<IssueChangeVO>> statisticalCompletedChange(Long projectId, List<VersionIssueChangeDTO> versionIssues, Set<Date> dateSet, String field) {
        //issue由未完成变为完成
        List<VersionIssueChangeDTO> completedChangeIssues = reportMapper.queryCompletedChangeIssue(projectId, versionIssues, true);
        List<IssueChangeVO> completedIssues = issueAssembler.toTargetList(completedChangeIssues.isEmpty() ? new ArrayList<>() : reportMapper.queryChangIssue(projectId, completedChangeIssues, field), IssueChangeVO.class);
        Map<Date, List<IssueChangeVO>> completedIssuesMap = completedIssues.stream().collect(Collectors.groupingBy(IssueChangeVO::getChangeDate));
        dateSet.addAll(completedIssuesMap.keySet());
        return completedIssuesMap;
    }

    private Map<Date, List<IssueChangeVO>> statisticalFieldChange(Long projectId, List<VersionIssueChangeDTO> versionIssues, Set<Date> dateSet, String field) {
        List<IssueChangeVO> changeIssues = issueAssembler.toTargetList(reportMapper.queryChangeFieldIssue(projectId, versionIssues, field), IssueChangeVO.class);
        Map<Date, List<IssueChangeVO>> changeIssuesMap = changeIssues.stream().collect(Collectors.groupingBy(IssueChangeVO::getChangeDate));
        dateSet.addAll(changeIssuesMap.keySet());
        return changeIssuesMap;
    }

    private void statisticalTimePointIssue(List<Long> nowVersionIssue, List<VersionIssueChangeDTO> versionIssues, List<VersionIssueChangeDTO> versionChangeIssue, Date startDate) {
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

    private void handleCumulativeFlowAddDuringDate(Long projectId, List<Long> allIssueIds, List<ColumnChangeVO> result, Date startDate, Date endDate, List<Long> columnIds) {
        List<ColumnChangeVO> addIssueDuringDate = reportAssembler.toTargetList(reportMapper.queryAddIssueDuringDate(projectId, startDate, endDate, allIssueIds, columnIds), ColumnChangeVO.class);
        if (addIssueDuringDate != null && !addIssueDuringDate.isEmpty()) {
            //新创建的issue没有生成列变更日志，所以StatusTo字段为空，说明是新创建的issue，要进行处理
            List<Long> statusToNullIssueIds = addIssueDuringDate.stream().filter(columnChangeVO -> columnChangeVO.getStatusTo() == null).map(ColumnChangeVO::getIssueId).collect(Collectors.toList());
            if (statusToNullIssueIds != null && !statusToNullIssueIds.isEmpty()) {
                //查询issue当前的状态
                Map<Long, ColumnStatusRelDTO> columnStatusRelMap = columnStatusRelMapper.queryByIssueIdAndColumnIds(statusToNullIssueIds, columnIds)
                        .stream().collect(Collectors.toMap(ColumnStatusRelDTO::getIssueId, Function.identity()));
                addIssueDuringDate.parallelStream().forEach(columnChangeVO -> {
                    if (statusToNullIssueIds.contains(columnChangeVO.getIssueId())) {
                        ColumnStatusRelDTO columnStatusRelDTO = columnStatusRelMap.get(columnChangeVO.getIssueId());
                        if (columnStatusRelDTO != null) {
                            columnChangeVO.setColumnTo(columnStatusRelDTO.getColumnId().toString());
                            columnChangeVO.setStatusTo(columnStatusRelDTO.getStatusId().toString());
                        }
                    }
                });
            }
            result.addAll(addIssueDuringDate);
        }
    }

    private void queryIssueCount(SprintConvertDTO sprintConvertDTO, List<ReportIssueConvertDTO> reportIssueConvertDTOList) {
        SprintDTO sprintDTO = sprintConverter.entityToDo(sprintConvertDTO);
        //获取冲刺开启前的issue
        List<Long> issueIdBeforeSprintList;
        //获取当前冲刺期间加入的issue
        List<Long> issueIdAddList;
        //获取当前冲刺期间移除的issue
        List<Long> issueIdRemoveList;
        //异步任务
        CompletableFuture<List<Long>> task1 = CompletableFuture
                .supplyAsync(() -> reportMapper.queryIssueIdsBeforeSprintStart(sprintDTO), pool);
        CompletableFuture<List<Long>> task2 = CompletableFuture
                .supplyAsync(() -> reportMapper.queryAddIssueIdsDuringSprint(sprintDTO), pool);
        CompletableFuture<List<Long>> task3 = CompletableFuture
                .supplyAsync(() -> reportMapper.queryRemoveIssueIdsDuringSprint(sprintDTO), pool);
        issueIdBeforeSprintList = task1.join();
        issueIdAddList = task2.join();
        issueIdRemoveList = task3.join();
        //获取冲刺开启前的issue统计
        handleIssueCountBeforeSprint(sprintDTO, reportIssueConvertDTOList, issueIdBeforeSprintList);
        //获取当前冲刺期间加入的issue
        handleAddIssueCountDuringSprint(sprintDTO, reportIssueConvertDTOList, issueIdAddList);
        //获取当前冲刺期间移除的issue
        handleRemoveCountDuringSprint(sprintDTO, reportIssueConvertDTOList, issueIdRemoveList);
        //获取冲刺结束时的issue
        handleIssueCountAfterSprint(sprintDTO, reportIssueConvertDTOList);
        //获取冲刺期间所有操作到的issue
        List<Long> issueAllList = getAllIssueDuringSprint(issueIdBeforeSprintList, issueIdAddList, issueIdRemoveList);
        //获取当前冲刺期间移动到done状态的issue
        handleAddDoneIssueCountDuringSprint(sprintDTO, reportIssueConvertDTOList, issueAllList);
        //获取当前冲刺期间移出done状态的issue
        handleRemoveDoneIssueCountDuringSprint(sprintDTO, reportIssueConvertDTOList, issueAllList);
    }

    private void queryStoryPointsOrRemainingEstimatedTime(SprintConvertDTO sprintConvertDTO, List<ReportIssueConvertDTO> reportIssueConvertDTOList, String field) {
        SprintDTO sprintDTO = sprintConverter.entityToDo(sprintConvertDTO);
        //获取冲刺开启前的issue
        List<Long> issueIdBeforeSprintList;
        //获取当前冲刺期间加入的issue
        List<Long> issueIdAddList;
        //获取当前冲刺期间移除的issue
        List<Long> issueIdRemoveList;
        //异步任务
        CompletableFuture<List<Long>> task1 = CompletableFuture
                .supplyAsync(() -> reportMapper.queryIssueIdsBeforeSprintStart(sprintDTO), pool);
        CompletableFuture<List<Long>> task2 = CompletableFuture
                .supplyAsync(() -> reportMapper.queryAddIssueIdsDuringSprint(sprintDTO), pool);
        CompletableFuture<List<Long>> task3 = CompletableFuture
                .supplyAsync(() -> reportMapper.queryRemoveIssueIdsDuringSprint(sprintDTO), pool);
        issueIdBeforeSprintList = task1.join();
        issueIdAddList = task2.join();
        issueIdRemoveList = task3.join();
        //获取当前冲刺开启前的issue信息
        handleIssueValueBeforeSprint(sprintDTO, reportIssueConvertDTOList, issueIdBeforeSprintList, field);
        //获取当前冲刺期间加入的issue信息
        handleAddIssueValueDuringSprint(sprintDTO, reportIssueConvertDTOList, issueIdAddList, field);
        //获取当前冲刺期间移除的issue信息
        handleRemoveIssueValueDuringSprint(sprintDTO, reportIssueConvertDTOList, issueIdRemoveList, field);
        //获取冲刺期间所有操作到的issue变更信息
        List<Long> issueAllList = getAllIssueDuringSprint(issueIdBeforeSprintList, issueIdAddList, issueIdRemoveList);
        handleChangeIssueValueDuringSprint(sprintDTO, reportIssueConvertDTOList, issueAllList, field);
        //获取当前冲刺期间移动到done状态的issue信息
        handleAddDoneIssueValueDuringSprint(sprintDTO, reportIssueConvertDTOList, field, issueAllList);
        //获取当前冲刺期间移出done状态的issue信息
        handleRemoveDoneIssueValueDuringSprint(sprintDTO, reportIssueConvertDTOList, field, issueAllList);
        //获取冲刺结束时的issue(结束前状态为done的issue计入统计字段设为false)
        handleIssueValueAfterSprint(sprintDTO, reportIssueConvertDTOList, field);
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


    private void handleIssueCountAfterSprint(SprintDTO sprintDTO, List<ReportIssueConvertDTO> reportIssueConvertDTOList) {
        if (sprintDTO.getStatusCode().equals(SPRINT_CLOSED)) {
            List<ReportIssueConvertDTO> issueAfterSprintList = ConvertHelper.convertList(reportMapper.queryIssueCountAfterSprint(sprintDTO), ReportIssueConvertDTO.class);
            if (issueAfterSprintList != null && !issueAfterSprintList.isEmpty()) {
                reportIssueConvertDTOList.addAll(issueAfterSprintList);
            } else {
                ReportIssueConvertDTO reportIssueConvertDTO = new ReportIssueConvertDTO();
                reportIssueConvertDTO.initEndSprint(sprintDTO.getActualEndDate());
                reportIssueConvertDTOList.add(reportIssueConvertDTO);
            }
        }
    }

    private void handleRemoveDoneIssueCountDuringSprint(SprintDTO sprintDTO, List<ReportIssueConvertDTO> reportIssueConvertDTOList, List<Long> issueAllList) {
        // 获取当前冲刺期间移除done状态的issue
        List<Long> issueIdRemoveDoneList = issueAllList != null && !issueAllList.isEmpty() ? reportMapper.queryRemoveDoneIssueIdsDuringSprint(sprintDTO, issueAllList) : null;
        List<ReportIssueConvertDTO> issueRemoveDoneList = issueIdRemoveDoneList != null && !issueIdRemoveDoneList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryRemoveIssueDoneDetailDurationSprint(issueIdRemoveDoneList, sprintDTO), ReportIssueConvertDTO.class) : null;
        if (issueRemoveDoneList != null && !issueRemoveDoneList.isEmpty()) {
            reportIssueConvertDTOList.addAll(issueRemoveDoneList);
        }
    }

    private void handleAddDoneIssueCountDuringSprint(SprintDTO sprintDTO, List<ReportIssueConvertDTO> reportIssueConvertDTOList, List<Long> issueAllList) {
        // 获取当前冲刺期间移动到done状态的issue
        List<Long> issueIdAddDoneList = issueAllList != null && !issueAllList.isEmpty() ? reportMapper.queryAddDoneIssueIdsDuringSprint(sprintDTO, issueAllList) : null;
        List<ReportIssueConvertDTO> issueAddDoneList = issueIdAddDoneList != null && !issueIdAddDoneList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryAddIssueDoneDetailDuringSprint(issueIdAddDoneList, sprintDTO), ReportIssueConvertDTO.class) : null;
        if (issueAddDoneList != null && !issueAddDoneList.isEmpty()) {
            reportIssueConvertDTOList.addAll(issueAddDoneList);
        }
    }

    private void handleRemoveCountDuringSprint(SprintDTO sprintDTO, List<ReportIssueConvertDTO> reportIssueConvertDTOList, List<Long> issueIdRemoveList) {
        List<ReportIssueConvertDTO> issueRemoveList = issueIdRemoveList != null && !issueIdRemoveList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryRemoveIssueDuringSprint(issueIdRemoveList, sprintDTO), ReportIssueConvertDTO.class) : null;
        if (issueRemoveList != null && !issueRemoveList.isEmpty()) {
            reportIssueConvertDTOList.addAll(issueRemoveList);
        }
    }

    private void handleAddIssueCountDuringSprint(SprintDTO sprintDTO, List<ReportIssueConvertDTO> reportIssueConvertDTOList, List<Long> issueIdAddList) {
        List<ReportIssueConvertDTO> issueAddList = issueIdAddList != null && !issueIdAddList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryAddIssueDuringSprint(issueIdAddList, sprintDTO), ReportIssueConvertDTO.class) : null;
        if (issueAddList != null && !issueAddList.isEmpty()) {
            reportIssueConvertDTOList.addAll(issueAddList);
        }
    }

    private void handleIssueCountBeforeSprint(SprintDTO sprintDTO, List<ReportIssueConvertDTO> reportIssueConvertDTOList, List<Long> issueIdBeforeSprintList) {
        //获取冲刺开启前状态为done的issue
        List<Long> doneBeforeIssue = !issueIdBeforeSprintList.isEmpty() ? reportMapper.queryDoneIssueIdsBeforeSprintStart(issueIdBeforeSprintList, sprintDTO) : null;
        List<ReportIssueConvertDTO> issueBeforeSprintList = !issueIdBeforeSprintList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryAddIssueBeforeDuringSprint(issueIdBeforeSprintList, sprintDTO), ReportIssueConvertDTO.class) : null;
        // 过滤开启冲刺前状态为done的issue，统计字段设为false（表示跳过统计）
        if (issueBeforeSprintList != null && !issueBeforeSprintList.isEmpty()) {
            if (doneBeforeIssue != null && !doneBeforeIssue.isEmpty()) {
                issueBeforeSprintList.stream().filter(reportIssueConvertDTO ->
                        doneBeforeIssue.contains(reportIssueConvertDTO.getIssueId()))
                        .forEach(reportIssueConvertDTO -> reportIssueConvertDTO.setStatistical(false));
            }
            reportIssueConvertDTOList.addAll(issueBeforeSprintList);
        } else {
            ReportIssueConvertDTO reportIssueConvertDTO = new ReportIssueConvertDTO();
            reportIssueConvertDTO.initStartSprint(sprintDTO.getStartDate());
            reportIssueConvertDTOList.add(reportIssueConvertDTO);
        }
    }

    private void handleIssueValueAfterSprint(SprintDTO sprintDTO, List<ReportIssueConvertDTO> reportIssueConvertDTOList, String field) {
        if (sprintDTO.getStatusCode().equals(SPRINT_CLOSED)) {
            List<ReportIssueConvertDTO> issueAfterSprintList = ConvertHelper.convertList(reportMapper.queryIssueValueAfterSprint(sprintDTO, field), ReportIssueConvertDTO.class);
            if (issueAfterSprintList != null && !issueAfterSprintList.isEmpty()) {
                reportIssueConvertDTOList.addAll(issueAfterSprintList);
            } else {
                ReportIssueConvertDTO reportIssueConvertDTO = new ReportIssueConvertDTO();
                reportIssueConvertDTO.initEndSprint(sprintDTO.getEndDate());
                reportIssueConvertDTOList.add(reportIssueConvertDTO);
            }
        }
    }

    private void handleRemoveDoneIssueValueDuringSprint(SprintDTO sprintDTO, List<ReportIssueConvertDTO> reportIssueConvertDTOList, String field, List<Long> issueAllList) {
        // 获取当前冲刺期间移出done状态的issue
        List<Long> issueIdRemoveDoneList = issueAllList != null && !issueAllList.isEmpty() ? reportMapper.queryRemoveDoneIssueIdsDuringSprint(sprintDTO, issueAllList) : null;
        List<ReportIssueDTO> reportIssueDTOS = Collections.synchronizedList(new ArrayList<>());
        List<ReportIssueConvertDTO> issueRemoveDoneList = new ArrayList<>();
        if (issueIdRemoveDoneList != null && !issueIdRemoveDoneList.isEmpty()) {
            //todo 还需要优化
            issueIdRemoveDoneList.parallelStream().forEach(issueIdRemoveDone -> reportIssueDTOS.addAll(reportMapper.queryRemoveIssueDoneValueDurationSprint(issueIdRemoveDone, sprintDTO, field)));
            issueRemoveDoneList = !reportIssueDTOS.isEmpty() ? ConvertHelper.convertList(reportIssueDTOS, ReportIssueConvertDTO.class) : null;
        }
        if (issueRemoveDoneList != null && !issueRemoveDoneList.isEmpty()) {
            reportIssueConvertDTOList.addAll(issueRemoveDoneList);
        }
    }

    private void handleAddDoneIssueValueDuringSprint(SprintDTO sprintDTO, List<ReportIssueConvertDTO> reportIssueConvertDTOList, String field, List<Long> issueAllList) {
        // 获取当前冲刺期间移动到done状态的issue
        List<Long> issueIdAddDoneList = issueAllList != null && !issueAllList.isEmpty() ? reportMapper.queryAddDoneIssueIdsDuringSprint(sprintDTO, issueAllList) : null;
        List<ReportIssueDTO> reportIssueDTOS = Collections.synchronizedList(new ArrayList<>());
        List<ReportIssueConvertDTO> issueAddDoneList = new ArrayList<>();
        if (issueIdAddDoneList != null && !issueIdAddDoneList.isEmpty()) {
            //todo 还需要优化
            issueIdAddDoneList.parallelStream().forEach(issueIdAddDone -> reportIssueDTOS.addAll(reportMapper.queryAddIssueDoneValueDuringSprint(issueIdAddDone, sprintDTO, field)));
            issueAddDoneList = !reportIssueDTOS.isEmpty() ? ConvertHelper.convertList(reportIssueDTOS, ReportIssueConvertDTO.class) : null;
        }
        if (issueAddDoneList != null && !issueAddDoneList.isEmpty()) {
            reportIssueConvertDTOList.addAll(issueAddDoneList);
        }
        // 如果有移动到done的issue，判断如果该issue之后有被移出冲刺，则移出时时间不再计算（置为false）
        List<ReportIssueConvertDTO> remove = reportIssueConvertDTOList.stream().filter(x -> x.getType().equals("removeDuringSprint")).collect(Collectors.toList());
        // 查看上一个resolution是完成，则移出时时间不再计算（置为false）
        remove.forEach(x -> {
            ReportIssueDTO reportIssueDTO = reportMapper.queryLastResolutionBeforeMoveOutSprint(sprintDTO.getProjectId(), x.getIssueId(), x.getDate());
            if (reportIssueDTO != null && reportIssueDTO.getNewValue() != null) {
                x.setStatistical(false);
            }
        });
    }

    private void handleChangeIssueValueDuringSprint(SprintDTO sprintDTO, List<ReportIssueConvertDTO> reportIssueConvertDTOList, List<Long> issueAllList, String field) {
        //获取冲刺期间所有的当前值的变更
        List<ReportIssueConvertDTO> issueChangeList = issueAllList != null && !issueAllList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryIssueChangeValueDurationSprint(issueAllList, sprintDTO, field), ReportIssueConvertDTO.class) : null;
        if (issueChangeList != null && !issueChangeList.isEmpty()) {
            issueChangeList.parallelStream().forEach(reportIssueConvertDTO -> {
                //变更时间是在done状态，计入统计字段设为false
                handleDoneStatusIssue(reportIssueConvertDTO);
                //变更时间是在移出冲刺期间，计入统计字段设为false
                handleRemoveIssue(reportIssueConvertDTO, sprintDTO.getSprintId());
            });
            reportIssueConvertDTOList.addAll(issueChangeList);
        }
    }

    private void handleRemoveIssue(ReportIssueConvertDTO reportIssueConvertDTO, Long sprintId) {
        Boolean result = reportMapper.checkIssueRemove(reportIssueConvertDTO.getIssueId(), reportIssueConvertDTO.getDate(), sprintId);
        result = (result != null && !result) || result == null;
        if (result) {
            reportIssueConvertDTO.setStatistical(false);
        }
    }

    private void handleRemoveIssueValueDuringSprint(SprintDTO sprintDTO, List<ReportIssueConvertDTO> reportIssueConvertDTOList, List<Long> issueIdRemoveList, String field) {
        List<ReportIssueConvertDTO> issueRemoveList = issueIdRemoveList != null && !issueIdRemoveList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryRemoveIssueValueDurationSprint(issueIdRemoveList, sprintDTO, field), ReportIssueConvertDTO.class) : null;
        if (issueRemoveList != null && !issueRemoveList.isEmpty()) {
            //移除时，状态为done的不计入统计
            reportIssueConvertDTOList.addAll(issueRemoveList);
        }

    }

    private void handleDoneStatusIssue(ReportIssueConvertDTO reportIssueConvertDTO) {
        Boolean result = reportMapper.checkIssueDoneStatus(reportIssueConvertDTO.getIssueId(), reportIssueConvertDTO.getDate());
        if (result != null && !result) {
            reportIssueConvertDTO.setStatistical(false);
        }
    }

    private void handleAddIssueValueDuringSprint(SprintDTO sprintDTO, List<ReportIssueConvertDTO> reportIssueConvertDTOList, List<Long> issueIdAddList, String field) {
        List<ReportIssueConvertDTO> issueAddList = issueIdAddList != null && !issueIdAddList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryAddIssueValueDuringSprint(issueIdAddList, sprintDTO, field), ReportIssueConvertDTO.class) : null;
        if (issueAddList != null && !issueAddList.isEmpty()) {
            reportIssueConvertDTOList.addAll(issueAddList);
        }
    }

    private void handleIssueValueBeforeSprint(SprintDTO sprintDTO, List<ReportIssueConvertDTO> reportIssueConvertDTOList, List<Long> issueIdBeforeSprintList, String field) {
        List<ReportIssueConvertDTO> issueBeforeList = !issueIdBeforeSprintList.isEmpty() ? ConvertHelper.convertList(reportMapper.queryValueBeforeSprintStart(issueIdBeforeSprintList, sprintDTO, field), ReportIssueConvertDTO.class) : null;
        // 获取冲刺开启前状态为done的issue
        List<Long> doneIssueBeforeSprint = !issueIdBeforeSprintList.isEmpty() ? reportMapper.queryDoneIssueIdsBeforeSprintStart(issueIdBeforeSprintList, sprintDTO) : null;
        // 过滤开启冲刺前状态为done的issue，统计字段设为false（表示跳过统计）
        if (issueBeforeList != null) {
            if (doneIssueBeforeSprint != null && !doneIssueBeforeSprint.isEmpty()) {
                issueBeforeList.stream().filter(reportIssueConvertDTO -> doneIssueBeforeSprint.contains(reportIssueConvertDTO.getIssueId())).
                        forEach(reportIssueConvertDTO -> reportIssueConvertDTO.setStatistical(false));
            }
            reportIssueConvertDTOList.addAll(issueBeforeList);
        } else {
            ReportIssueConvertDTO reportIssueConvertDTO = new ReportIssueConvertDTO();
            reportIssueConvertDTO.initStartSprint(sprintDTO.getStartDate());
            reportIssueConvertDTOList.add(reportIssueConvertDTO);
        }
    }

    private List<CoordinateVO> getCumulativeFlowDiagramDuringDate(CumulativeFlowDiagramVO cumulativeFlowDiagramVO, CumulativeFlowFilterVO cumulativeFlowFilterVO) {
        if (cumulativeFlowDiagramVO.getCoordinateVOList() != null && !cumulativeFlowDiagramVO.getCoordinateVOList().isEmpty()) {
            return cumulativeFlowDiagramVO.getCoordinateVOList().stream().filter(coordinateVO ->
                    (coordinateVO.getDate().before(cumulativeFlowFilterVO.getEndDate()) || coordinateVO.getDate().equals(cumulativeFlowFilterVO.getEndDate()))
                            && (coordinateVO.getDate().after(cumulativeFlowFilterVO.getStartDate()) || coordinateVO.getDate().equals(cumulativeFlowFilterVO.getStartDate()))).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }

    }

    private List<CumulativeFlowDiagramVO> getCumulativeFlowDiagram(List<Long> allIssueIds, Long projectId, CumulativeFlowFilterVO cumulativeFlowFilterVO) {
        ProjectInfoDTO query = new ProjectInfoDTO();
        query.setProjectId(projectId);
        ProjectInfoDTO projectInfoDTO = projectInfoMapper.selectOne(query);
        if (projectInfoDTO == null) {
            throw new CommonException("error.cumulativeFlow.projectInfoNotFound");
        }
        if (cumulativeFlowFilterVO.getBoardId() == null) {
            throw new CommonException("error.cumulativeFlow.boardIdNotFound");
        }
        List<Long> columnIds = boardColumnMapper.queryColumnIdsByBoardId(cumulativeFlowFilterVO.getBoardId(), projectId);
        //设置时间区间
        Date startDate = projectInfoDTO.getCreationDate();
        Date endDate = new Date();
        List<ColumnChangeVO> result = new ArrayList<>();
        //所有在当前时间内创建的issue
        handleCumulativeFlowAddDuringDate(projectId, allIssueIds, result, startDate, endDate, columnIds);
        //所有在当前时间内状态改变的issue
        handleCumulativeFlowChangeDuringDate(projectId, startDate, endDate, columnIds, allIssueIds, result);
        //过滤并排序
        List<ColumnChangeVO> columnChangeVOList = result.stream().filter(columnChangeVO ->
                columnChangeVO.getColumnTo() != null && columnChangeVO.getColumnFrom() != null && !columnChangeVO.getColumnFrom().equals(columnChangeVO.getColumnTo()))
                .filter(columnChangeVO ->
                        (columnChangeVO.getDate().before(cumulativeFlowFilterVO.getEndDate()) || columnChangeVO.getDate().equals(cumulativeFlowFilterVO.getEndDate()))
                                && (columnChangeVO.getDate().after(cumulativeFlowFilterVO.getStartDate()) || columnChangeVO.getDate().equals(cumulativeFlowFilterVO.getStartDate()))).sorted(Comparator.comparing(ColumnChangeVO::getDate)).collect(Collectors.toList());
        //对传入时间点的数据给与坐标
        List<CumulativeFlowDiagramVO> cumulativeFlowDiagramVOList = reportAssembler.columnListDoToDto(boardColumnMapper.queryColumnByColumnIds(columnIds));
        cumulativeFlowDiagramVOList.parallelStream().forEachOrdered(cumulativeFlowDiagramVO -> {
            handleColumnCoordinate(columnChangeVOList, cumulativeFlowDiagramVO, cumulativeFlowFilterVO.getStartDate(), cumulativeFlowFilterVO.getEndDate());
            //过滤日期
            cumulativeFlowDiagramVO.setCoordinateVOList(getCumulativeFlowDiagramDuringDate(cumulativeFlowDiagramVO, cumulativeFlowFilterVO));
        });
        return cumulativeFlowDiagramVOList.stream().filter(cumulativeFlowDiagramVO -> cumulativeFlowFilterVO.getColumnIds().contains(cumulativeFlowDiagramVO.getColumnId())).collect(Collectors.toList());
    }

    private String getNowTime() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(d);
    }

    private List<VelocitySprintDTO> dealStoryPointResult(List<VelocitySingleDTO> committedList, List<VelocitySingleDTO> completedList, List<VelocitySprintDTO> sprintDOList, List<VelocitySprintDTO> result) {
        for (VelocitySprintDTO temp : sprintDOList) {
            BigDecimal committedStoryPoints = new BigDecimal(0);
            BigDecimal completedStoryPoints = new BigDecimal(0);
            for (VelocitySingleDTO committed : committedList) {
                if (committed.getSprintId().equals(temp.getSprintId())) {
                    committedStoryPoints = committedStoryPoints.add(committed.getStoryPoint());
                }
            }
            for (VelocitySingleDTO completed : completedList) {
                if (completed.getSprintId().equals(temp.getSprintId())) {
                    completedStoryPoints = completedStoryPoints.add(completed.getStoryPoint());
                }
            }
            temp.setCommittedStoryPoints(committedStoryPoints);
            temp.setCompletedStoryPoints(completedStoryPoints);
            result.add(temp);
        }
        return result;
    }

    private List<VelocitySprintDTO> dealIssueCountResult(List<VelocitySingleDTO> committedList, List<VelocitySingleDTO> completedList, List<VelocitySprintDTO> sprintDOList, List<VelocitySprintDTO> result) {
        for (VelocitySprintDTO temp : sprintDOList) {
            int committedIssueNum = 0;
            int completedIssueNum = 0;
            for (VelocitySingleDTO committed : committedList) {
                if (committed.getSprintId().equals(temp.getSprintId())) {
                    committedIssueNum += 1;
                }
            }
            for (VelocitySingleDTO completed : completedList) {
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

    private List<VelocitySprintDTO> dealRemainTimeResult(List<VelocitySingleDTO> committedList, List<VelocitySingleDTO> completedList, List<VelocitySprintDTO> sprintDOList, List<VelocitySprintDTO> result) {
        for (VelocitySprintDTO temp : sprintDOList) {
            BigDecimal committedRemainTime = new BigDecimal(0);
            BigDecimal completedRemainTime = new BigDecimal(0);
            for (VelocitySingleDTO committed : committedList) {
                if (committed.getSprintId().equals(temp.getSprintId())) {
                    committedRemainTime = committedRemainTime.add(committed.getRemainTime());
                }
            }
            for (VelocitySingleDTO completed : completedList) {
                if (completed.getSprintId().equals(temp.getSprintId())) {
                    completedRemainTime = completedRemainTime.add(completed.getRemainTime());
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
    public List<VelocitySprintVO> queryVelocityChart(Long projectId, String type) {
        List<VelocitySprintDTO> sprintDOList = reportMapper.selectAllSprint(projectId);
        if (sprintDOList.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> ids = sprintDOList.stream().map(VelocitySprintDTO::getSprintId).collect(Collectors.toList());
        String now = getNowTime();
        List<VelocitySprintDTO> result = new ArrayList<>();
        switch (type) {
            case TYPE_ISSUE_COUNT:
                List<VelocitySingleDTO> issueCountCommitted = reportMapper.selectByIssueCountCommitted(projectId, ids, now);
                List<VelocitySingleDTO> issueCountCompleted = reportMapper.selectByIssueCountCompleted(projectId, ids, now);
                dealIssueCountResult(issueCountCommitted, issueCountCompleted, sprintDOList, result);
                break;
            case TYPE_STORY_POINT:
                List<VelocitySingleDTO> storyPointCommitted = reportMapper.selectByStoryPointAndNumCommitted(projectId, ids, now);
                List<VelocitySingleDTO> storyPointCompleted = reportMapper.selectByStoryPointAndNumCompleted(projectId, ids, now);
                dealStoryPointResult(storyPointCommitted, storyPointCompleted, sprintDOList, result);
                break;
            case TYPE_REMAIN_TIME:
                List<VelocitySingleDTO> remainTimeCommitted = reportMapper.selectByRemainTimeCommitted(projectId, ids, now);
                List<VelocitySingleDTO> remainTimeCompleted = reportMapper.selectByRemainTimeCompleted(projectId, ids, now);
                dealRemainTimeResult(remainTimeCommitted, remainTimeCompleted, sprintDOList, result);
                break;
            default:
                break;
        }
        return ConvertHelper.convertList(result, VelocitySprintVO.class);
    }

    @Override
    @Cacheable(cacheNames = AGILE, key = "'PieChart' + #projectId + ':' + #fieldName + ':' + #startDate+ ':' + #endDate+ ':' + #sprintId+':' + #versionId")
    public List<PieChartVO> queryPieChart(Long projectId, String fieldName, Long organizationId, Date startDate, Date endDate, Long sprintId, Long versionId) {
        switch (fieldName) {
            case ASSIGNEE:
                return handlePieChartByAssignee(projectId, startDate, endDate, sprintId, versionId);
            case COMPONENT:
                return handlePieChartByType(projectId, "component_id", false, startDate, endDate, sprintId, versionId);
            case ISSUE_TYPE:
                return handlePieChartByTypeCode(projectId, startDate, endDate, sprintId, versionId);
            case VERSION:
                return handlePieChartByType(projectId, "version_id", false, startDate, endDate, sprintId, versionId);
            case PRIORITY:
                return handlePieChartByPriorityType(projectId, organizationId, startDate, endDate, sprintId, versionId);
            case STATUS:
                return handlePieChartByStatusType(projectId, startDate, endDate, sprintId, versionId);
            case SPRINT:
                return handlePieChartByType(projectId, "sprint_id", false, startDate, endDate, sprintId, versionId);
            case EPIC:
                return handlePieChartByEpic(projectId, startDate, endDate, sprintId, versionId);
            case RESOLUTION:
                return handlePieChartByType(projectId, RESOLUTION, false, startDate, endDate, sprintId, versionId);
            case LABEL:
                return handlePieChartByType(projectId, "label_id", false, startDate, endDate, sprintId, versionId);
            default:
                break;
        }
        return new ArrayList<>();
    }

    private List<PieChartVO> handlePieChartByPriorityType(Long projectId, Long organizationId, Date startDate, Date endDate, Long sprintId, Long versionId) {
        List<PieChartVO> pieChartVOS = handlePieChartByType(projectId, "priority_id", true, startDate, endDate, sprintId, versionId);
        Map<Long, PriorityVO> priorityMap = issueFeignClient.queryByOrganizationId(organizationId).getBody();
        pieChartVOS.forEach(pieChartDTO -> {
            pieChartDTO.setPriorityVO(priorityMap.get(Long.parseLong(pieChartDTO.getTypeName())));
            pieChartDTO.setName(pieChartDTO.getPriorityVO().getName());
        });
        return pieChartVOS;
    }

    private List<PieChartVO> handlePieChartByStatusType(Long projectId, Date startDate, Date endDate, Long sprintId, Long versionId) {
        Integer total = reportMapper.queryIssueCountByFieldName(projectId, "status_id", startDate, endDate, sprintId, versionId);
        List<PieChartDTO> pieChartDTOS = reportMapper.queryPieChartByParam(projectId, true, "status_id", false, total,
                startDate, endDate, sprintId, versionId);
        if (pieChartDTOS != null && !pieChartDTOS.isEmpty()) {
            List<PieChartVO> pieChartVOS = reportAssembler.toTargetList(pieChartDTOS, PieChartVO.class);
            Map<Long, StatusMapVO> statusMap = ConvertUtil.getIssueStatusMap(projectId);
            pieChartVOS.forEach(pieChartDTO -> pieChartDTO.setName(statusMap.get(Long.parseLong(pieChartDTO.getTypeName())).getName()));
            return pieChartVOS;
        } else {
            return new ArrayList<>();
        }
    }

    private List<PieChartVO> handlePieChartByTypeCode(Long projectId, Date startDate, Date endDate, Long sprintId, Long versionId) {
        Integer total = reportMapper.queryIssueCountByFieldName(projectId, "type_code", startDate, endDate, sprintId, versionId);
        List<PieChartDTO> pieChartDTOS = reportMapper.queryPieChartByParam(projectId, true, "issue_type_id", true, total,
                startDate, endDate, sprintId, versionId);
        if (pieChartDTOS != null && !pieChartDTOS.isEmpty()) {
            List<PieChartVO> pieChartVOS = reportAssembler.toTargetList(pieChartDTOS, PieChartVO.class);
            Map<Long, IssueTypeVO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, SchemeApplyType.AGILE);
            pieChartVOS.forEach(pieChartDTO -> {
                IssueTypeVO issueTypeVO = issueTypeDTOMap.get(Long.parseLong(pieChartDTO.getTypeName()));
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("color", issueTypeVO.getColour());
                jsonObject.put("icon", issueTypeVO.getIcon());
                pieChartDTO.setJsonObject(jsonObject);
                pieChartDTO.setName(issueTypeVO.getName());
            });
            return pieChartVOS;
        } else {
            return new ArrayList<>();
        }
    }

    private List<PieChartVO> handlePieChartByEpic(Long projectId, Date startDate, Date endDate, Long sprintId, Long versionId) {
        Integer total = reportMapper.queryIssueCountByFieldName(projectId, "epic_id", startDate, endDate, sprintId, versionId);
        return reportAssembler.toTargetList(reportMapper.queryPieChartByEpic(projectId, total, startDate, endDate, sprintId, versionId), PieChartVO.class);
    }

    private List<PieChartVO> handlePieChartByType(Long projectId, String fieldName, Boolean own, Date startDate, Date endDate, Long sprintId, Long versionId) {
        Integer total = reportMapper.queryIssueCountByFieldName(projectId, fieldName, startDate, endDate, sprintId, versionId);
        List<PieChartDTO> pieChartDTOS = reportMapper.queryPieChartByParam(projectId, own, fieldName, false, total, startDate, endDate, sprintId, versionId);
        return reportAssembler.toTargetList(pieChartDTOS, PieChartVO.class);
    }

    private List<PieChartVO> handlePieChartByAssignee(Long projectId, Date startDate, Date endDate, Long sprintId, Long versionId) {
        Integer total = reportMapper.queryIssueCountByFieldName(projectId, "assignee_id", startDate, endDate, sprintId, versionId);
        List<PieChartDTO> pieChartDTOS = reportMapper.queryPieChartByParam(projectId, true, "assignee_id", false, total, startDate, endDate, sprintId, versionId);
        List<PieChartVO> pieChartVOList = reportAssembler.toTargetList(pieChartDTOS, PieChartVO.class);
        if (pieChartVOList != null && !pieChartVOList.isEmpty()) {
            List<Long> userIds = pieChartVOList.stream().filter(pieChartDTO ->
                    pieChartDTO.getTypeName() != null && !"0".equals(pieChartDTO.getTypeName())).map(pieChartDTO ->
                    Long.parseLong(pieChartDTO.getTypeName())).collect(Collectors.toList());
            Map<Long, UserMessageDTO> usersMap = userService.queryUsersMap(userIds, true);
            pieChartVOList.parallelStream().forEach(pieChartDTO -> {
                JSONObject jsonObject = new JSONObject();
                if (pieChartDTO.getTypeName() != null && usersMap.get(Long.parseLong(pieChartDTO.getTypeName())) != null) {
                    UserMessageDTO userMessageDTO = usersMap.get(Long.parseLong(pieChartDTO.getTypeName()));
                    String assigneeName = userMessageDTO.getName();
                    String assigneeLoginName = userMessageDTO.getLoginName();
                    String assigneeRealName = userMessageDTO.getRealName();
                    String assigneeImageUrl = userMessageDTO.getImageUrl();
                    String email = userMessageDTO.getEmail();
                    pieChartDTO.setName(assigneeName);
                    pieChartDTO.setLoginName(assigneeLoginName);
                    pieChartDTO.setRealName(assigneeRealName);
                    jsonObject.put("assigneeImageUrl", assigneeImageUrl);
                    jsonObject.put("email", email);
                } else {
                    jsonObject.put("assigneeImageUrl", null);
                    jsonObject.put("email", null);
                }
                pieChartDTO.setJsonObject(jsonObject);
            });
        }
        return pieChartVOList;
    }

    private void setStoryPointProperties(GroupDataChartDTO g1,
                                         List<GroupDataChartDTO> storyPointsAll,
                                         List<GroupDataChartDTO> storyPointsCompleted,
                                         List<GroupDataChartDTO> storyPointCountEstimate) {
        for (GroupDataChartDTO g2 : storyPointsAll) {
            if (g1.getGroupDay().equals(g2.getGroupDay())) {
                g1.setAllStoryPoints(g2.getAllStoryPoints());
                break;
            }
        }
        for (GroupDataChartDTO g3 : storyPointsCompleted) {
            if (g1.getGroupDay().equals(g3.getGroupDay())) {
                g1.setCompletedStoryPoints(g3.getCompletedStoryPoints());
                break;
            }
        }
        int flag = 0;
        for (GroupDataChartDTO g4 : storyPointCountEstimate) {
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


    private List<GroupDataChartDTO> dealStoryPointFinal(List<GroupDataChartDTO> storyPointsAll, List<GroupDataChartDTO> storyPointsCompleted, List<GroupDataChartDTO> storyPointCountAll, List<GroupDataChartDTO> storyPointCountEstimate) {
        for (GroupDataChartDTO g1 : storyPointCountAll) {
            setStoryPointProperties(g1, storyPointsAll, storyPointsCompleted, storyPointCountEstimate);
        }
        return storyPointCountAll;
    }

    private void setRemainTimeCompleted(GroupDataChartDTO g1, List<GroupDataChartDTO> remainTimeRemainCompleted, List<GroupDataChartDTO> remainTimeWorkLogCompleted) {
        for (GroupDataChartDTO g2 : remainTimeRemainCompleted) {
            if (g1.getGroupDay().equals(g2.getGroupDay())) {
                g1.setCompletedRemainTimes(g2.getCompletedRemainTimes());
                break;
            }
        }
        for (GroupDataChartDTO g3 : remainTimeWorkLogCompleted) {
            if (g1.getGroupDay().equals(g3.getGroupDay())) {
                g1.setCompletedRemainTimes(g1.getCompletedRemainTimes().add(g3.getCompletedRemainTimes()));
                break;
            }
        }
    }

    private void setRemainTimeAll(GroupDataChartDTO g1, List<GroupDataChartDTO> remainTimeRemainAll, List<GroupDataChartDTO> remainTimeWorkLogAll) {
        for (GroupDataChartDTO g4 : remainTimeRemainAll) {
            if (g1.getGroupDay().equals(g4.getGroupDay())) {
                g1.setAllRemainTimes(g4.getAllRemainTimes());
                break;
            }
        }
        for (GroupDataChartDTO g5 : remainTimeWorkLogAll) {
            if (g1.getGroupDay().equals(g5.getGroupDay())) {
                g1.setAllRemainTimes(g1.getAllRemainTimes().add(g5.getAllRemainTimes()));
                break;
            }
        }
    }

    private void setRemainTimeUnEstimateCount(GroupDataChartDTO g1, List<GroupDataChartDTO> remainTimeCountEstimate) {
        int flag = 0;
        for (GroupDataChartDTO g6 : remainTimeCountEstimate) {
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

    private List<GroupDataChartDTO> dealRemainTimeFinal(List<GroupDataChartDTO> remainTimeRemainCompleted, List<GroupDataChartDTO> remainTimeWorkLogCompleted,
                                                        List<GroupDataChartDTO> remainTimeRemainAll, List<GroupDataChartDTO> remainTimeWorkLogAll,
                                                        List<GroupDataChartDTO> remainTimeCountAll, List<GroupDataChartDTO> remainTimeCountEstimate) {
        for (GroupDataChartDTO g1 : remainTimeCountAll) {
            setRemainTimeCompleted(g1, remainTimeRemainCompleted, remainTimeWorkLogCompleted);
            setRemainTimeAll(g1, remainTimeRemainAll, remainTimeWorkLogAll);
            setRemainTimeUnEstimateCount(g1, remainTimeCountEstimate);
        }
        return remainTimeCountAll;
    }

    private List<GroupDataChartDTO> dealIssueCountFinal(List<GroupDataChartDTO> issueCountAll, List<GroupDataChartDTO> issueCountCompleted) {
        for (GroupDataChartDTO g1 : issueCountAll) {
            for (GroupDataChartDTO g2 : issueCountCompleted) {
                if (g1.getGroupDay().equals(g2.getGroupDay())) {
                    g1.setIssueCompletedCount(g2.getIssueCompletedCount());
                }
            }
        }
        return issueCountAll;
    }

    @Override
    @Cacheable(cacheNames = AGILE, key = "'EpicChart' + #projectId + ':' + #epicId + ':' + #type")
    public List<GroupDataChartDTO> queryEpicChart(Long projectId, Long epicId, String type) {
        List<GroupDataChartDTO> result = null;
        switch (type) {
            case TYPE_STORY_POINT:
                List<GroupDataChartDTO> storyPointsAll = reportMapper.selectByStoryPointAllFinal(projectId, epicId, EPIC_CHART);
                List<GroupDataChartDTO> storyPointsCompleted = reportMapper.selectByStoryPointCompletedFinal(projectId, epicId, EPIC_CHART);
                List<GroupDataChartDTO> storyPointCountAll = reportMapper.selectByStoryPointCountAll(projectId, epicId, EPIC_CHART);
                List<GroupDataChartDTO> storyPointCountEstimate = reportMapper.selectByStoryPointCountEstimate(projectId, epicId, EPIC_CHART);
                result = dealStoryPointFinal(storyPointsAll, storyPointsCompleted, storyPointCountAll, storyPointCountEstimate);
                break;
            case TYPE_ISSUE_COUNT:
                List<GroupDataChartDTO> issueCountAll = reportMapper.selectByIssueCountAllFinal(projectId, epicId, EPIC_CHART);
                List<GroupDataChartDTO> issueCountCompleted = reportMapper.selectByIssueCountCompletedFinal(projectId, epicId, EPIC_CHART);
                result = dealIssueCountFinal(issueCountAll, issueCountCompleted);
                break;
            case TYPE_REMAIN_TIME:
                List<GroupDataChartDTO> remainTimeRemainCompleted = reportMapper.selectByRemainTimeRemainCompleted(projectId, epicId, EPIC_CHART);
                List<GroupDataChartDTO> remainTimeWorkLogCompleted = reportMapper.selectByRemainTimeWorkLogCompleted(projectId, epicId, EPIC_CHART);
                List<GroupDataChartDTO> remainTimeRemainAll = reportMapper.selectByRemainTimeRemainAll(projectId, epicId, EPIC_CHART);
                List<GroupDataChartDTO> remainTimeWorkLogAll = reportMapper.selectByRemainTimeWorkLogAll(projectId, epicId, EPIC_CHART);
                List<GroupDataChartDTO> remainTimeCountAll = reportMapper.selectByRemainTimeCountAll(projectId, epicId, EPIC_CHART);
                List<GroupDataChartDTO> remainTimeCountEstimate = reportMapper.selectByRemainTimeCountEstimate(projectId, epicId, EPIC_CHART);
                result = dealRemainTimeFinal(remainTimeRemainCompleted, remainTimeWorkLogCompleted, remainTimeRemainAll, remainTimeWorkLogAll, remainTimeCountAll, remainTimeCountEstimate);
                break;
            default:
                break;
        }
        return result == null ? new ArrayList<>() : result;
    }

    @Override
    public List<GroupDataChartListDTO> queryEpicChartList(Long projectId, Long epicId, Long organizationId) {
        List<GroupDataChartListDTO> groupDataChartListDOList = reportMapper.selectEpicIssueList(projectId, epicId);
        Map<Long, PriorityVO> priorityMap = issueFeignClient.queryByOrganizationId(organizationId).getBody();
        Map<Long, IssueTypeVO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
        Map<Long, StatusMapVO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
        for (GroupDataChartListDTO groupDataChartListDTO : groupDataChartListDOList) {
            groupDataChartListDTO.setPriorityVO(priorityMap.get(groupDataChartListDTO.getPriorityId()));
            groupDataChartListDTO.setStatusMapVO(statusMapDTOMap.get(groupDataChartListDTO.getStatusId()));
            groupDataChartListDTO.setIssueTypeVO(issueTypeDTOMap.get(groupDataChartListDTO.getIssueTypeId()));
        }
        return groupDataChartListDOList;
    }

    @Override
    @Cacheable(cacheNames = AGILE, key = "'BurnDownCoordinate' + #projectId + ':' + #sprintId + ':' + #type")
    public JSONObject queryBurnDownCoordinate(Long projectId, Long sprintId, String type) {
        List<ReportIssueConvertDTO> reportIssueConvertDTOList = getBurnDownReport(projectId, sprintId, type);
        return handleSameDay(reportIssueConvertDTOList.stream().filter(reportIssueConvertDTO -> !"endSprint".equals(reportIssueConvertDTO.getType())).
                sorted(Comparator.comparing(ReportIssueConvertDTO::getDate)).collect(Collectors.toList()));
    }

    private BigDecimal calculateStoryPoints(List<IssueBurnDownReportDTO> issueDOS) {
        BigDecimal sum = new BigDecimal(0);
        for (IssueBurnDownReportDTO issueBurnDownReportDTO : issueDOS) {
            sum = sum.add(issueBurnDownReportDTO.getStoryPoints());
        }
        return sum;
    }

    private BigDecimal calculateCompletedStoryPoints(List<IssueBurnDownReportDTO> issueDOS) {
        BigDecimal sum = new BigDecimal(0);
        for (IssueBurnDownReportDTO issueBurnDownReportDTO : issueDOS) {
            if (issueBurnDownReportDTO.getCompleted()) {
                sum = sum.add(issueBurnDownReportDTO.getStoryPoints());
            }
        }
        return sum;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Cacheable(cacheNames = AGILE, key = "'BurnDownCoordinateByType' + #projectId + ':' + #type  + ':' + #id")
    public List<BurnDownReportCoordinateVO> queryBurnDownCoordinateByType(Long projectId, Long id, String type) {
        List<IssueBurnDownReportDTO> issueDOList = E_PIC.equals(type) ? issueMapper.queryIssueByEpicId(projectId, id) : issueMapper.queryIssueByVersionId(projectId, id);
        if (issueDOList != null && !issueDOList.isEmpty()) {
            if (issueDOList.stream().noneMatch(issueDO -> issueDO.getStoryPoints() != null)) {
                return new ArrayList<>();
            } else {
                JSONObject jsonObject = handleSprintListAndStartDate(id, projectId, type);
                List<SprintDTO> sprintDTOList = (List<SprintDTO>) jsonObject.get(SPRINT_DO_LIST);
                Date startDate = (Date) jsonObject.get(START_DATE);
                List<IssueBurnDownReportDTO> issueDOS = issueDOList.stream().filter(issueDO -> issueDO.getStoryPoints() != null).collect(Collectors.toList());
                List<BurnDownReportCoordinateVO> reportCoordinateDTOS = new ArrayList<>();
                if (sprintDTOList != null && !sprintDTOList.isEmpty()) {
                    handleBurnDownCoordinateByTypeExistSprint(issueDOS, reportCoordinateDTOS, startDate, sprintDTOList, type);
                } else {
                    BigDecimal addNum = calculateStoryPoints(issueDOS);
                    BigDecimal done = calculateCompletedStoryPoints(issueDOS);
                    reportCoordinateDTOS.add(new BurnDownReportCoordinateVO(new BigDecimal(0), addNum, done, addNum.subtract(done),
                            type + "开始时的预估", startDate, new Date()));
                }
                return reportCoordinateDTOS;
            }
        } else {
            return new ArrayList<>();
        }
    }

    private JSONObject handleSprintListAndStartDate(Long id, Long projectId, String type) {
        Date startDate;
        List<SprintDTO> sprintDTOList;
        if (E_PIC.equals(type)) {
            IssueDTO issueDTO = issueMapper.queryEpicWithStatusByIssueId(id, projectId);
            if (issueDTO != null) {
                startDate = issueDTO.getCreationDate();
                if (issueDTO.getCompleted() && issueDTO.getDoneDate() != null) {
                    sprintDTOList = sprintMapper.queryNotPlanSprintByProjectId(projectId, startDate, issueDTO.getDoneDate());
                } else {
                    sprintDTOList = sprintMapper.queryNotPlanSprintByProjectId(projectId, startDate, null);
                }
            } else {
                throw new CommonException(EPIC_OR_VERSION_NOT_FOUND_ERROR);
            }
        } else {
            ProductVersionDTO query = new ProductVersionDTO();
            query.setProjectId(projectId);
            query.setVersionId(id);
            ProductVersionDTO productVersionDTO = versionMapper.selectOne(query);
            if (productVersionDTO != null) {
                startDate = productVersionDTO.getStartDate() == null ? productVersionDTO.getCreationDate() : productVersionDTO.getStartDate();
                sprintDTOList = sprintMapper.queryNotPlanSprintByProjectId(projectId, startDate, productVersionDTO.getReleaseDate());
            } else {
                throw new CommonException(EPIC_OR_VERSION_NOT_FOUND_ERROR);
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(SPRINT_DO_LIST, sprintDTOList);
        jsonObject.put(START_DATE, startDate);
        return jsonObject;
    }

    @Override
    @SuppressWarnings("unchecked")
    public BurnDownReportVO queryBurnDownReportByType(Long projectId, Long id, String type, Long organizationId) {
        BurnDownReportVO burnDownReportVO = new BurnDownReportVO();
        Boolean typeCondition = "Epic".equals(type);
        List<IssueBurnDownReportDTO> issueDOList = typeCondition ? issueMapper.queryIssueByEpicId(projectId, id) : issueMapper.queryIssueByVersionId(projectId, id);
        burnDownReportVO.setJsonObject(new JSONObject());
        handleBurnDownReportTypeData(burnDownReportVO, id, projectId, typeCondition);
        if (issueDOList != null && !issueDOList.isEmpty()) {
            Map<Long, PriorityVO> priorityMap = issueFeignClient.queryByOrganizationId(organizationId).getBody();
            Map<Long, StatusMapVO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
            Map<Long, IssueTypeVO> issueTypeDTOMap = ConvertUtil.getIssueTypeMap(projectId, SchemeApplyType.AGILE);
            List<IssueBurnDownReportDTO> incompleteIssues = issueDOList.stream().filter(issueDO -> !issueDO.getCompleted()).collect(Collectors.toList());
            burnDownReportVO.setIncompleteIssues(reportAssembler.issueBurnDownReportDoToDto(incompleteIssues, issueTypeDTOMap, statusMapDTOMap, priorityMap));
            JSONObject jsonObject = handleSprintListAndStartDate(id, projectId, type);
            List<SprintDTO> sprintDTOList = (List<SprintDTO>) jsonObject.get(SPRINT_DO_LIST);
            if (sprintDTOList != null && !sprintDTOList.isEmpty()) {
                List<IssueBurnDownReportDTO> completeIssues = issueDOList.stream().filter(issueDO -> issueDO.getCompleted() && issueDO.getDoneDate() != null).collect(Collectors.toList());
                handleBurnDownReportSprintData(sprintDTOList, completeIssues, burnDownReportVO, priorityMap, statusMapDTOMap, issueTypeDTOMap);
            }
        }
        return burnDownReportVO;
    }

    private void handleBurnDownReportSprintData(List<SprintDTO> sprintDTOList, List<IssueBurnDownReportDTO> completeIssues, BurnDownReportVO burnDownReportVO, Map<Long, PriorityVO> priorityMap, Map<Long, StatusMapVO> statusMapDTOMap, Map<Long, IssueTypeVO> issueTypeDTOMap) {
        List<SprintBurnDownReportVO> sprintBurnDownReportVOS = new ArrayList<>();
        if (sprintDTOList.size() == 1) {
            SprintBurnDownReportVO sprintBurnDownReportVO = reportAssembler.sprintBurnDownReportDoToDto(sprintDTOList.get(0));
            List<IssueBurnDownReportDTO> singleCompleteIssues = completeIssues.stream().filter(issueDO ->
                    issueDO.getDoneDate().after(sprintBurnDownReportVO.getStartDate())).collect(Collectors.toList());
            sprintBurnDownReportVO.setCompleteIssues(reportAssembler.issueBurnDownReportDoToDto(singleCompleteIssues, issueTypeDTOMap, statusMapDTOMap, priorityMap));
            sprintBurnDownReportVOS.add(sprintBurnDownReportVO);
        } else {
            for (int i = 0; i < sprintDTOList.size() - 1; i++) {
                SprintBurnDownReportVO sprintBurnDownReportVO = reportAssembler.sprintBurnDownReportDoToDto(sprintDTOList.get(i));
                Date startDateOne = sprintBurnDownReportVO.getStartDate();
                Date startDateTwo = sprintDTOList.get(i + 1).getStartDate();
                List<IssueBurnDownReportDTO> duringSprintCompleteIssues = handleDuringSprintIncompleteIssues(completeIssues, startDateOne, startDateTwo);
                sprintBurnDownReportVO.setCompleteIssues(reportAssembler.issueBurnDownReportDoToDto(duringSprintCompleteIssues, issueTypeDTOMap, statusMapDTOMap, priorityMap));
                sprintBurnDownReportVOS.add(sprintBurnDownReportVO);
                if (i == sprintDTOList.size() - 2) {
                    SprintBurnDownReportVO lastSprintBurnDownReportVO = reportAssembler.sprintBurnDownReportDoToDto(sprintDTOList.get(i + 1));
                    List<IssueBurnDownReportDTO> lastCompleteIssues = completeIssues.stream().filter(issueDO ->
                            issueDO.getDoneDate().after(lastSprintBurnDownReportVO.getStartDate())).collect(Collectors.toList());
                    lastSprintBurnDownReportVO.setCompleteIssues(reportAssembler.issueBurnDownReportDoToDto(lastCompleteIssues, issueTypeDTOMap, statusMapDTOMap, priorityMap));
                    lastSprintBurnDownReportVO.setEndDate(lastSprintBurnDownReportVO.getEndDate() == null ? new Date() : lastSprintBurnDownReportVO.getEndDate());
                    sprintBurnDownReportVOS.add(lastSprintBurnDownReportVO);
                }
            }
        }
        burnDownReportVO.setSprintBurnDownReportVOS(sprintBurnDownReportVOS);
    }

    private List<IssueBurnDownReportDTO> handleDuringSprintIncompleteIssues(List<IssueBurnDownReportDTO> completeIssues, Date startDateOne, Date startDateTwo) {
        List<IssueBurnDownReportDTO> duringSprintIncompleteIssues = new ArrayList<>();
        for (IssueBurnDownReportDTO issueDO : completeIssues) {
            if (issueDO.getDoneDate().after(startDateOne) && issueDO.getDoneDate().before(startDateTwo)) {
                duringSprintIncompleteIssues.add(issueDO);
            }
        }
        return duringSprintIncompleteIssues;
    }


    private void handleBurnDownReportTypeData(BurnDownReportVO burnDownReportVO, Long id, Long projectId, Boolean typeCondition) {
        if (typeCondition) {
            IssueDTO issueDTO = issueMapper.queryEpicDetailByIssueId(id, projectId);
            burnDownReportVO.getJsonObject().put("epicName", issueDTO.getEpicName());
            burnDownReportVO.getJsonObject().put("issueNum", issueDTO.getIssueNum());
            burnDownReportVO.getJsonObject().put("issueId", issueDTO.getIssueId());
        } else {
            ProductVersionDTO query = new ProductVersionDTO();
            query.setVersionId(id);
            query.setProjectId(projectId);
            ProductVersionDTO productVersionDTO = versionMapper.selectByPrimaryKey(query);
            burnDownReportVO.getJsonObject().put("name", productVersionDTO.getName());
            burnDownReportVO.getJsonObject().put("versionId", productVersionDTO.getVersionId());
            burnDownReportVO.getJsonObject().put(START_DATE, productVersionDTO.getStartDate());
            burnDownReportVO.getJsonObject().put("releaseDate", productVersionDTO.getReleaseDate());
        }
    }

    private void handleBurnDownCoordinateByTypeExistSprint(List<IssueBurnDownReportDTO> issueDOS, List<BurnDownReportCoordinateVO> reportCoordinateDTOS,
                                                           Date startDate, List<SprintDTO> sprintDTOList, String type) {
        List<IssueBurnDownReportDTO> issueFilters = issueDOS.stream().filter(issueDO -> issueDO.getAddDate().before(sprintDTOList.get(0).getStartDate())).collect(Collectors.toList());
        BigDecimal addNum = calculateStoryPoints(issueFilters);
        List<IssueBurnDownReportDTO> issueCompletedFilters = issueDOS.stream().filter(issueDO -> issueDO.getCompleted() && issueDO.getDoneDate() != null && issueDO.getDoneDate().before(sprintDTOList.get(0).getStartDate())).collect(Collectors.toList());
        BigDecimal done = calculateStoryPoints(issueCompletedFilters);
        reportCoordinateDTOS.add(new BurnDownReportCoordinateVO(new BigDecimal(0), addNum, done, addNum.subtract(done),
                type + "开始时的预估", startDate, sprintDTOList.get(0).getStartDate()));
        if (sprintDTOList.size() == 1) {
            handleSprintSingle(reportCoordinateDTOS, issueDOS, sprintDTOList);
        } else {
            handleSprintMultitude(reportCoordinateDTOS, issueDOS, sprintDTOList);
        }
    }

    private void handleSprintMultitude(List<BurnDownReportCoordinateVO> reportCoordinateDTOS, List<IssueBurnDownReportDTO> issueDOS, List<SprintDTO> sprintDTOList) {
        for (int i = 0; i < sprintDTOList.size() - 1; i++) {
            Date startDateOne = sprintDTOList.get(i).getStartDate();
            Date startDateTwo = sprintDTOList.get(i + 1).getStartDate();
            Date endDate = sprintDTOList.get(i).getActualEndDate() == null ? sprintDTOList.get(i).getEndDate() : sprintDTOList.get(i).getActualEndDate();
            handleReportCoordinateDuringSprint(issueDOS, startDateOne, startDateTwo, reportCoordinateDTOS, endDate, sprintDTOList.get(i).getSprintName());
            if (i == sprintDTOList.size() - 2) {
                BigDecimal startLast = reportCoordinateDTOS.get(reportCoordinateDTOS.size() - 1).getLeft();
                List<IssueBurnDownReportDTO> addList = issueDOS.stream().filter(issueDO -> issueDO.getAddDate().after(startDateTwo)).collect(Collectors.toList());
                BigDecimal addLast = calculateStoryPoints(addList);
                List<IssueBurnDownReportDTO> doneList = issueDOS.stream().filter(issueDO -> issueDO.getCompleted() && issueDO.getDoneDate() != null && issueDO.getDoneDate().after(startDateTwo)).collect(Collectors.toList());
                BigDecimal doneLast = calculateStoryPoints(doneList);
                BigDecimal left = startLast.add(addLast).subtract(doneLast);
                endDate = sprintDTOList.get(i + 1).getActualEndDate() == null ? sprintDTOList.get(i + 1).getEndDate() : sprintDTOList.get(i + 1).getActualEndDate();
                reportCoordinateDTOS.add(new BurnDownReportCoordinateVO(startLast, addLast, doneLast, left,
                        sprintDTOList.get(i + 1).getSprintName(), sprintDTOList.get(i + 1).getStartDate(), endDate));
            }

        }
    }

    private void handleReportCoordinateDuringSprint(List<IssueBurnDownReportDTO> issueDOS, Date startDateOne, Date startDateTwo, List<BurnDownReportCoordinateVO> reportCoordinateDTOS, Date endDate, String sprintName) {
        BigDecimal addNum = new BigDecimal(0);
        BigDecimal done = new BigDecimal(0);
        BigDecimal start = reportCoordinateDTOS.get(reportCoordinateDTOS.size() - 1).getLeft();
        for (IssueBurnDownReportDTO issueDO : issueDOS) {
            if (issueDO.getAddDate().after(startDateOne) && issueDO.getAddDate().before(startDateTwo)) {
                addNum = addNum.add(issueDO.getStoryPoints());
            }
            if (issueDO.getCompleted() && issueDO.getDoneDate() != null && issueDO.getDoneDate().after(startDateOne) && issueDO.getDoneDate().before(startDateTwo)) {
                done = done.add(issueDO.getStoryPoints());
            }
        }
        BigDecimal left = start.add(addNum).subtract(done);

        if (!(start.compareTo(BigDecimal.ZERO) == 0 && addNum.compareTo(BigDecimal.ZERO) == 0 && done.compareTo(BigDecimal.ZERO) == 0 && left.compareTo(BigDecimal.ZERO) == 0)) {
            reportCoordinateDTOS.add(new BurnDownReportCoordinateVO(start, addNum, done, left,
                    sprintName, startDateOne, endDate));
        }
    }


    private void handleSprintSingle(List<BurnDownReportCoordinateVO> reportCoordinateDTOS, List<IssueBurnDownReportDTO> issueDOS, List<SprintDTO> sprintDTOList) {
        BigDecimal start = reportCoordinateDTOS.get(0).getLeft();
        List<IssueBurnDownReportDTO> addList = issueDOS.stream().filter(issueDO -> issueDO.getAddDate().after(sprintDTOList.get(0).getStartDate())).collect(Collectors.toList());
        BigDecimal addNum = calculateStoryPoints(addList);
        List<IssueBurnDownReportDTO> doneList = issueDOS.stream().filter(issueDO -> issueDO.getCompleted() && issueDO.getDoneDate().after(sprintDTOList.get(0).getStartDate())).collect(Collectors.toList());
        BigDecimal done = calculateStoryPoints(doneList);
        Date endDate = sprintDTOList.get(0).getActualEndDate() == null ? sprintDTOList.get(0).getEndDate() : sprintDTOList.get(0).getActualEndDate();
        reportCoordinateDTOS.add(new BurnDownReportCoordinateVO(start, addNum, done, start.add(addNum).subtract(done),
                sprintDTOList.get(0).getSprintName(), sprintDTOList.get(0).getStartDate(), endDate));
    }


    @Override
    @Cacheable(cacheNames = AGILE, key = "'VersionChart' + #projectId + ':' + #versionId + ':' + #type")
    public List<GroupDataChartDTO> queryVersionChart(Long projectId, Long versionId, String type) {
        List<GroupDataChartDTO> result = null;
        switch (type) {
            case TYPE_ISSUE_COUNT:
                List<GroupDataChartDTO> issueCountAll = reportMapper.selectByIssueCountAllFinal(projectId, versionId, VERSION_CHART);
                List<GroupDataChartDTO> issueCountCompleted = reportMapper.selectByIssueCountCompletedFinal(projectId, versionId, VERSION_CHART);
                result = dealIssueCountFinal(issueCountAll, issueCountCompleted);
                break;
            case TYPE_STORY_POINT:
                List<GroupDataChartDTO> storyPointsAll = reportMapper.selectByStoryPointAllFinal(projectId, versionId, VERSION_CHART);
                List<GroupDataChartDTO> storyPointsCompleted = reportMapper.selectByStoryPointCompletedFinal(projectId, versionId, VERSION_CHART);
                List<GroupDataChartDTO> storyPointCountAll = reportMapper.selectByStoryPointCountAll(projectId, versionId, VERSION_CHART);
                List<GroupDataChartDTO> storyPointCountEstimate = reportMapper.selectByStoryPointCountEstimate(projectId, versionId, VERSION_CHART);
                result = dealStoryPointFinal(storyPointsAll, storyPointsCompleted, storyPointCountAll, storyPointCountEstimate);
                break;
            case TYPE_REMAIN_TIME:
                List<GroupDataChartDTO> remainTimeRemainCompleted = reportMapper.selectByRemainTimeRemainCompleted(projectId, versionId, VERSION_CHART);
                List<GroupDataChartDTO> remainTimeWorkLogCompleted = reportMapper.selectByRemainTimeWorkLogCompleted(projectId, versionId, VERSION_CHART);
                List<GroupDataChartDTO> remainTimeRemainAll = reportMapper.selectByRemainTimeRemainAll(projectId, versionId, VERSION_CHART);
                List<GroupDataChartDTO> remainTimeWorkLogAll = reportMapper.selectByRemainTimeWorkLogAll(projectId, versionId, VERSION_CHART);
                List<GroupDataChartDTO> remainTimeCountAll = reportMapper.selectByRemainTimeCountAll(projectId, versionId, VERSION_CHART);
                List<GroupDataChartDTO> remainTimeCountEstimate = reportMapper.selectByRemainTimeCountEstimate(projectId, versionId, VERSION_CHART);
                result = dealRemainTimeFinal(remainTimeRemainCompleted, remainTimeWorkLogCompleted, remainTimeRemainAll, remainTimeWorkLogAll, remainTimeCountAll, remainTimeCountEstimate);
                break;
            default:
                break;
        }
        return result == null ? new ArrayList<>() : result;
    }

    @Override
    public List<GroupDataChartListDTO> queryVersionChartList(Long projectId, Long versionId, Long organizationId) {
        List<GroupDataChartListDTO> groupDataChartListDOList = reportMapper.selectVersionIssueList(projectId, versionId);
        Map<Long, PriorityVO> priorityMap = issueFeignClient.queryByOrganizationId(organizationId).getBody();
        Map<Long, IssueTypeVO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
        Map<Long, StatusMapVO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
        for (GroupDataChartListDTO groupDataChartListDTO : groupDataChartListDOList) {
            groupDataChartListDTO.setPriorityVO(priorityMap.get(groupDataChartListDTO.getPriorityId()));
            groupDataChartListDTO.setStatusMapVO(statusMapDTOMap.get(groupDataChartListDTO.getStatusId()));
            groupDataChartListDTO.setIssueTypeVO(issueTypeDTOMap.get(groupDataChartListDTO.getIssueTypeId()));
        }
        return groupDataChartListDOList;
    }
}

