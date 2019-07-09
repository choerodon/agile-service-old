package io.choerodon.agile.app.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Ordering;
import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.api.validator.SprintValidator;
import io.choerodon.agile.app.assembler.*;
import io.choerodon.agile.app.service.SprintService;
import io.choerodon.agile.domain.agile.entity.SprintE;
import io.choerodon.agile.infra.common.enums.SchemeApplyType;
import io.choerodon.agile.infra.common.utils.DateUtil;
import io.choerodon.agile.infra.common.utils.PageUtil;
import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.agile.infra.common.utils.StringUtil;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.feign.StateMachineFeignClient;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.agile.infra.repository.IssueRepository;
import io.choerodon.agile.infra.repository.SprintRepository;
import io.choerodon.agile.infra.repository.SprintWorkCalendarRefRepository;
import io.choerodon.agile.app.service.UserService;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jian_zhang02@163.com on 2018/5/15.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SprintServiceImpl implements SprintService {

    @Autowired
    private SprintRepository sprintRepository;
    @Autowired
    private SprintMapper sprintMapper;
    @Autowired
    private IssueMapper issueMapper;
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private SprintCreateAssembler sprintCreateAssembler;
    @Autowired
    private SprintUpdateAssembler sprintUpdateAssembler;
    @Autowired
    private SprintSearchAssembler sprintSearchAssembler;
    @Autowired
    private SprintNameAssembler sprintNameAssembler;
    @Autowired
    private IssueSearchAssembler issueSearchAssembler;
    @Autowired
    private ProjectInfoMapper projectInfoMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private IssueAssembler issueAssembler;
    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private SprintValidator sprintValidator;
    @Autowired
    private QuickFilterMapper quickFilterMapper;
    @Autowired
    private DateUtil dateUtil;
    @Autowired
    private WorkCalendarRefMapper workCalendarRefMapper;
    @Autowired
    private SprintWorkCalendarRefRepository sprintWorkCalendarRefRepository;
    @Autowired
    private IssueStatusMapper issueStatusMapper;
    @Autowired
    private IssueFeignClient issueFeignClient;

    @Autowired
    private StateMachineFeignClient stateMachineFeignClient;

    @Autowired
    private PiMapper piMapper;

    @Autowired
    private ArtMapper artMapper;

    private static final String ADVANCED_SEARCH_ARGS = "advancedSearchArgs";
    private static final String SPRINT_DATA = "sprintData";
    private static final String BACKLOG_DATA = "backlogData";
    private static final String CATEGORY_DONE_CODE = "done";
    private static final String NOT_EQUAL_ERROR = "error.projectId.notEqual";
    private static final String NOT_FOUND_ERROR = "error.sprint.notFound";
    private static final String CATEGORY_TODO_CODE = "todo";
    private static final String CATEGORY_DOING_CODE = "doing";
    private static final String PROJECT_NOT_FOUND_ERROR = "error.project.notFound";
    private static final String START_SPRINT_ERROR = "error.sprint.hasStartedSprint";
    private static final String DONE = "done";
    private static final String UNFINISHED = "unfinished";
    private static final String REMOVE = "remove";
    private static final String SPRINT_REPORT_ERROR = "error.sprint.report";
    private static final String SPRINT_PLANNING_CODE = "sprint_planning";
    private static final String STATUS_SPRINT_PLANNING_CODE = "sprint_planning";

    @Override
    public synchronized SprintDetailDTO createSprint(Long projectId) {
        ProjectInfoDTO projectInfo = new ProjectInfoDTO();
        projectInfo.setProjectId(projectId);
        projectInfo = projectInfoMapper.selectOne(projectInfo);
        if (projectInfo == null) {
            throw new CommonException(PROJECT_NOT_FOUND_ERROR);
        }
        SprintDTO sprintDTO = sprintMapper.queryLastSprint(projectId);
        SprintE sprint = new SprintE();
        if (sprintDTO == null) {
            sprint.createSprint(projectInfo);
        } else {
            SprintE sprintE = sprintCreateAssembler.toTarget(sprintDTO, SprintE.class);
            sprint.createSprint(sprintE);
        }
        return sprintCreateAssembler.toTarget(sprintRepository.createSprint(sprint), SprintDetailDTO.class);
    }

    private Boolean checkNameUpdate(Long projectId, Long sprintId, String sprintName) {
        SprintDTO sprintDTO = sprintMapper.selectByPrimaryKey(sprintId);
        if (sprintName.equals(sprintDTO.getSprintName())) {
            return false;
        }
        SprintDTO check = new SprintDTO();
        check.setProjectId(projectId);
        check.setSprintName(sprintName);
        List<SprintDTO> sprintDTOList = sprintMapper.select(check);
        return sprintDTOList != null && !sprintDTOList.isEmpty();
    }

    @Override
    public SprintDetailDTO updateSprint(Long projectId, SprintUpdateDTO sprintUpdateDTO) {
        if (!Objects.equals(projectId, sprintUpdateDTO.getProjectId())) {
            throw new CommonException(NOT_EQUAL_ERROR);
        }
        if (sprintUpdateDTO.getSprintName() != null && checkNameUpdate(projectId, sprintUpdateDTO.getSprintId(), sprintUpdateDTO.getSprintName())) {
            throw new CommonException("error.sprintName.exist");
        }
        sprintValidator.checkDate(sprintUpdateDTO);
        SprintE sprintE = sprintUpdateAssembler.toTarget(sprintUpdateDTO, SprintE.class);
        sprintE.trimSprintName();
        return sprintUpdateAssembler.toTarget(sprintRepository.updateSprint(sprintE), SprintDetailDTO.class);
    }

    @Override
    public Boolean deleteSprint(Long projectId, Long sprintId) {
        SprintDTO sprintDTO = new SprintDTO();
        sprintDTO.setProjectId(projectId);
        sprintDTO.setSprintId(sprintId);
        SprintE sprintE = sprintSearchAssembler.toTarget(sprintMapper.selectOne(sprintDTO), SprintE.class);
        if (sprintE == null) {
            throw new CommonException(NOT_FOUND_ERROR);
        }
        sprintE.judgeDelete();
        moveIssueToBacklog(projectId, sprintId);
        issueRepository.batchRemoveFromSprint(projectId, sprintId);
        sprintRepository.deleteSprint(sprintE);
        return true;
    }

    private void moveIssueToBacklog(Long projectId, Long sprintId) {
        List<MoveIssueDO> moveIssueDOS = new ArrayList<>();
        Long targetSprintId = 0L;
        List<Long> moveIssueRankIds = sprintMapper.queryAllRankIssueIds(projectId, sprintId);
        beforeRank(projectId, targetSprintId, moveIssueDOS, moveIssueRankIds);
        if (moveIssueDOS.isEmpty()) {
            return;
        }
        issueRepository.batchUpdateIssueRank(projectId, moveIssueDOS);
    }

    @Override
    public String getQuickFilter(List<Long> quickFilterIds) {
        List<String> sqlQuerys = quickFilterMapper.selectSqlQueryByIds(quickFilterIds);
        if (sqlQuerys.isEmpty()) {
            return null;
        }
        StringBuilder sql = new StringBuilder("select issue_id from agile_issue where ");
        int idx = 0;
        for (String filter : sqlQuerys) {
            if (idx != 0) {
                sql.append(" and " + " ( " + filter + " ) ");
            } else {
                sql.append(" ( " + filter + " ) ");
                idx += 1;
            }
        }
        return sql.toString();
    }

    @Override
    public Map<String, Object> queryByProjectId(Long projectId, Map<String, Object> searchParamMap, List<Long> quickFilterIds, Long organizationId, List<Long> assigneeFilterIds) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        Map<String, Object> backlog = new HashMap<>(2);
        String filterSql = null;
        if (quickFilterIds != null && !quickFilterIds.isEmpty()) {
            filterSql = getQuickFilter(quickFilterIds);
        }
        //待办事项查询相关issue的issueIds，不包含已完成的issue
        List<Long> issueIds = issueMapper.querySprintIssueIdsByCondition(projectId, customUserDetails.getUserId(),
                StringUtil.cast(searchParamMap.get(ADVANCED_SEARCH_ARGS)), filterSql, assigneeFilterIds);
        //待办事项查询相关issue的issueIds，包含已完成的issue
        List<IssueIdSprintIdDTO> issueIdSprintIdDTOs = issueMapper.querySprintAllIssueIdsByCondition(projectId, customUserDetails.getUserId(),
                StringUtil.cast(searchParamMap.get(ADVANCED_SEARCH_ARGS)), filterSql, assigneeFilterIds);
        List<SprintSearchDTO> sprintSearches = new ArrayList<>();
        BackLogIssueDTO backLogIssueDTO = new BackLogIssueDTO();
        Map<Long, PriorityVO> priorityMap = issueFeignClient.queryByOrganizationId(organizationId).getBody();
        Map<Long, StatusMapVO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
        setStatusIsCompleted(projectId, statusMapDTOMap);
        Map<Long, IssueTypeVO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
        if (issueIdSprintIdDTOs != null && !issueIdSprintIdDTOs.isEmpty()) {
            handleSprintIssueData(issueIdSprintIdDTOs, issueIds, sprintSearches, backLogIssueDTO, projectId, priorityMap, statusMapDTOMap, issueTypeDTOMap);
        } else {
            handleSprintNoIssue(sprintSearches, projectId);
        }
        backlog.put(SPRINT_DATA, sprintSearches);
        backlog.put(BACKLOG_DATA, backLogIssueDTO);
        return backlog;
    }

    private void setStatusIsCompleted(Long projectId, Map<Long, StatusMapVO> statusMapDTOMap) {
        IssueStatusDTO issueStatusDTO = new IssueStatusDTO();
        issueStatusDTO.setProjectId(projectId);
        Map<Long, Boolean> statusCompletedMap = issueStatusMapper.select(issueStatusDTO).stream().collect(Collectors.toMap(IssueStatusDTO::getStatusId, IssueStatusDTO::getCompleted));
        statusMapDTOMap.entrySet().forEach(entry -> entry.getValue().setCompleted(statusCompletedMap.getOrDefault(entry.getKey(), false)));
    }

    private void handleSprintNoIssue(List<SprintSearchDTO> sprintSearches, Long projectId) {
        SprintSearchDO sprintSearchDO = sprintMapper.queryActiveSprintNoIssueIds(projectId);
        Set<Long> assigneeIds = sprintMapper.queryBacklogSprintAssigneeIds(projectId);
        Map<Long, UserMessageDO> usersMap = userService.queryUsersMap(new ArrayList<>(assigneeIds), true);
        if (sprintSearchDO != null) {
            List<AssigneeIssueDTO> assigneeIssueDTOS = sprintMapper.queryAssigneeIssueByActiveSprintId(projectId, sprintSearchDO.getSprintId());
            if (assigneeIssueDTOS != null && !assigneeIssueDTOS.isEmpty()) {
                sprintSearchDO.setAssigneeIssueDTOList(assigneeIssueDTOS);
            }
            SprintSearchDTO activeSprint = sprintSearchAssembler.doToDTO(sprintSearchDO, usersMap, null, null, null);
            sprintSearches.add(activeSprint);
        }
        List<SprintSearchDO> sprintSearchDTOS = sprintMapper.queryPlanSprintNoIssueIds(projectId);
        List<SprintSearchDTO> planSprints = sprintSearchAssembler.doListToDTO(sprintSearchDTOS, usersMap, null, null, null);
        if (planSprints != null && !planSprints.isEmpty()) {
            sprintSearches.addAll(planSprints);
        }
    }

    private void handleSprintIssueData(List<IssueIdSprintIdDTO> issueIdSprintIdDTOs, List<Long> issueIds, List<SprintSearchDTO> sprintSearches, BackLogIssueDTO backLogIssueDTO, Long projectId, Map<Long, PriorityVO> priorityMap, Map<Long, StatusMapVO> statusMapDTOMap, Map<Long, IssueTypeVO> issueTypeDTOMap) {
        List<Long> allIssueIds = issueIdSprintIdDTOs.stream().map(IssueIdSprintIdDTO::getIssueId).collect(Collectors.toList());
        //查询出所有经办人用户id
        Set<Long> assigneeIds = sprintMapper.queryAssigneeIdsByIssueIds(projectId, allIssueIds);
        Map<Long, UserMessageDO> usersMap = userService.queryUsersMap(new ArrayList<>(assigneeIds), true);
        SprintSearchDO sprintSearchDO = sprintMapper.queryActiveSprintNoIssueIds(projectId);
        if (sprintSearchDO != null) {
            List<Long> activeSprintIssueIds = issueIdSprintIdDTOs.stream().filter(x -> sprintSearchDO.getSprintId().equals(x.getSprintId())).map(IssueIdSprintIdDTO::getIssueId).collect(Collectors.toList());
            sprintSearchDO.setIssueSearchDTOList(!activeSprintIssueIds.isEmpty() ? sprintMapper.queryActiveSprintIssueSearchByIssueIds(projectId, activeSprintIssueIds, sprintSearchDO.getSprintId()) : new ArrayList<>());
            sprintSearchDO.setAssigneeIssueDTOList(sprintMapper.queryAssigneeIssueByActiveSprintId(projectId, sprintSearchDO.getSprintId()));
            SprintSearchDTO activeSprint = sprintSearchAssembler.doToDTO(sprintSearchDO, usersMap, priorityMap, statusMapDTOMap, issueTypeDTOMap);
            activeSprint.setIssueCount(activeSprint.getIssueSearchVOList() == null ? 0 : activeSprint.getIssueSearchVOList().size());
            Map<String, List<Long>> statusMap = issueFeignClient.queryStatusByProjectId(projectId, SchemeApplyType.AGILE).getBody()
                    .stream().collect(Collectors.groupingBy(StatusMapVO::getType, Collectors.mapping(StatusMapVO::getId, Collectors.toList())));
            BigDecimal zero = new BigDecimal(0);
            activeSprint.setTodoStoryPoint(statusMap.get(CATEGORY_TODO_CODE) != null && !statusMap.get(CATEGORY_TODO_CODE).isEmpty() && !activeSprintIssueIds.isEmpty() ? sprintMapper.queryStoryPoint(statusMap.get(CATEGORY_TODO_CODE), activeSprintIssueIds, projectId) : zero);
            activeSprint.setDoingStoryPoint(statusMap.get(CATEGORY_DOING_CODE) != null && !statusMap.get(CATEGORY_DOING_CODE).isEmpty() && !activeSprintIssueIds.isEmpty() ? sprintMapper.queryStoryPoint(statusMap.get(CATEGORY_DOING_CODE), activeSprintIssueIds, projectId) : zero);
            activeSprint.setDoneStoryPoint(statusMap.get(CATEGORY_DONE_CODE) != null && !statusMap.get(CATEGORY_DONE_CODE).isEmpty() && !activeSprintIssueIds.isEmpty() ? sprintMapper.queryStoryPoint(statusMap.get(CATEGORY_DONE_CODE), activeSprintIssueIds, projectId) : zero);
            sprintSearches.add(activeSprint);
        }
        List<SprintSearchDO> sprintSearchDTOS = sprintMapper.queryPlanSprint(projectId, allIssueIds);
        if (sprintSearchDTOS != null && !sprintSearchDTOS.isEmpty()) {
            List<SprintSearchDTO> planSprints = sprintSearchAssembler.doListToDTO(sprintSearchDTOS, usersMap, priorityMap, statusMapDTOMap, issueTypeDTOMap);
            planSprints.parallelStream().forEachOrdered(planSprint -> planSprint.setIssueCount(planSprint.getIssueSearchVOList() == null ? 0 : planSprint.getIssueSearchVOList().size()));
            sprintSearches.addAll(planSprints);
        }
        if (issueIds != null && !issueIds.isEmpty()) {
            List<IssueSearchDTO> backLogIssue = sprintMapper.queryBacklogIssues(projectId, issueIds);
            backLogIssueDTO.setBackLogIssue(issueSearchAssembler.doListToDTO(backLogIssue, usersMap, priorityMap, statusMapDTOMap, issueTypeDTOMap));
            backLogIssueDTO.setBacklogIssueCount(backLogIssue.size());
        }
    }

    @Override
    public List<SprintNameDTO> queryNameByOptions(Long projectId, List<String> sprintStatusCodes) {
        return sprintNameAssembler.toTargetList(sprintMapper.queryNameByOptions(projectId, sprintStatusCodes), SprintNameDTO.class);
    }

    @Override
    public SprintDetailDTO startSprint(Long projectId, SprintUpdateDTO sprintUpdateDTO) {
        if (!Objects.equals(projectId, sprintUpdateDTO.getProjectId())) {
            throw new CommonException(NOT_EQUAL_ERROR);
        }
        if (sprintMapper.selectCountByStartedSprint(projectId) != 0) {
            throw new CommonException(START_SPRINT_ERROR);
        }
        SprintE sprintE = sprintUpdateAssembler.toTarget(sprintUpdateDTO, SprintE.class);
        sprintE.checkDate();
        sprintValidator.checkSprintStartInProgram(sprintE);
        sprintE.startSprint();
        if (sprintUpdateDTO.getWorkDates() != null && !sprintUpdateDTO.getWorkDates().isEmpty()) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            sprintUpdateDTO.getWorkDates().forEach(workDates -> {
                WorkCalendarRefDO workCalendarRefDO = new WorkCalendarRefDO();
                workCalendarRefDO.setSprintId(sprintE.getSprintId());
                workCalendarRefDO.setProjectId(sprintE.getProjectId());
                workCalendarRefDO.setWorkDay(workDates.getWorkDay());
                try {
                    calendar.setTime(dateFormat.parse(workDates.getWorkDay()));
                } catch (ParseException e) {
                    throw new CommonException("ParseException{}", e);
                }
                workCalendarRefDO.setYear(calendar.get(Calendar.YEAR));
                workCalendarRefDO.setStatus(workDates.getStatus());
                sprintWorkCalendarRefRepository.create(workCalendarRefDO);
            });
        }
        issueRepository.updateStayDate(projectId, sprintE.getSprintId(), new Date());
        return sprintUpdateAssembler.toTarget(sprintRepository.updateSprint(sprintE), SprintDetailDTO.class);
    }

    @Override
    public Boolean completeSprint(Long projectId, SprintCompleteDTO sprintCompleteDTO) {
        if (!Objects.equals(projectId, sprintCompleteDTO.getProjectId())) {
            throw new CommonException(NOT_EQUAL_ERROR);
        }
        sprintValidator.judgeCompleteSprint(projectId, sprintCompleteDTO.getIncompleteIssuesDestination());
        SprintDTO sprintDTO = new SprintDTO();
        sprintDTO.setProjectId(projectId);
        sprintDTO.setSprintId(sprintCompleteDTO.getSprintId());
        SprintE sprintE = sprintUpdateAssembler.toTarget(sprintMapper.selectOne(sprintDTO), SprintE.class);
        sprintE.completeSprint();
        sprintRepository.updateSprint(sprintE);
        moveNotDoneIssueToTargetSprint(projectId, sprintCompleteDTO);
        return true;
    }

    private void moveNotDoneIssueToTargetSprint(Long projectId, SprintCompleteDTO sprintCompleteDTO) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        List<MoveIssueDO> moveIssueDOS = new ArrayList<>();
        Long targetSprintId = sprintCompleteDTO.getIncompleteIssuesDestination();
        List<Long> moveIssueRankIds = sprintMapper.queryIssueIdOrderByRankDesc(projectId, sprintCompleteDTO.getSprintId());
        moveIssueRankIds.addAll(sprintMapper.queryUnDoneSubOfParentIds(projectId, sprintCompleteDTO.getSprintId()));
        beforeRank(projectId, sprintCompleteDTO.getIncompleteIssuesDestination(), moveIssueDOS, moveIssueRankIds);
        if (moveIssueDOS.isEmpty()) {
            return;
        }
        List<Long> moveIssueIds = sprintMapper.queryIssueIds(projectId, sprintCompleteDTO.getSprintId());
        moveIssueIds.addAll(issueMapper.querySubTaskIds(projectId, sprintCompleteDTO.getSprintId()));
        moveIssueIds.addAll(sprintMapper.queryParentsDoneSubtaskUnDoneIds(projectId, sprintCompleteDTO.getSprintId()));
        if (targetSprintId != null && !Objects.equals(targetSprintId, 0L)) {
            issueRepository.issueToDestinationByIdsCloseSprint(projectId, targetSprintId, moveIssueIds, new Date(), customUserDetails.getUserId());
        }
        issueRepository.batchUpdateIssueRank(projectId, moveIssueDOS);
    }

    private void beforeRank(Long projectId, Long targetSprintId, List<MoveIssueDO> moveIssueDOS, List<Long> moveIssueIds) {
        if (moveIssueIds.isEmpty()) {
            return;
        }
        String minRank = sprintMapper.queryMinRank(projectId, targetSprintId);
        if (minRank == null) {
            minRank = RankUtil.mid();
            for (Long issueId : moveIssueIds) {
                moveIssueDOS.add(new MoveIssueDO(issueId, minRank));
                minRank = RankUtil.genPre(minRank);
            }
        } else {
            for (Long issueId : moveIssueIds) {
                minRank = RankUtil.genPre(minRank);
                moveIssueDOS.add(new MoveIssueDO(issueId, minRank));
            }
        }
    }

    @Override
    public SprintCompleteMessageDTO queryCompleteMessageBySprintId(Long projectId, Long sprintId) {
        SprintCompleteMessageDTO sprintCompleteMessage = new SprintCompleteMessageDTO();
        sprintCompleteMessage.setSprintNames(sprintNameAssembler.toTargetList(sprintMapper.queryPlanSprintName(projectId), SprintNameDTO.class));
        sprintCompleteMessage.setParentsDoneUnfinishedSubtasks(issueAssembler.toTargetList(sprintMapper.queryParentsDoneUnfinishedSubtasks(projectId, sprintId), IssueNumVO.class));
        sprintCompleteMessage.setIncompleteIssues(sprintMapper.queryNotDoneIssueCount(projectId, sprintId));
        sprintCompleteMessage.setPartiallyCompleteIssues(sprintMapper.queryDoneIssueCount(projectId, sprintId));
        return sprintCompleteMessage;
    }

    @Override
    public SprintDTO getActiveSprint(Long projectId) {
        return sprintMapper.getActiveSprint(projectId);
    }

    @Override
    public SprintDetailDTO querySprintById(Long projectId, Long sprintId) {
        SprintDTO sprintDTO = new SprintDTO();
        sprintDTO.setProjectId(projectId);
        sprintDTO.setSprintId(sprintId);
        SprintDetailDTO sprintDetailDTO = sprintSearchAssembler.toTarget(sprintMapper.selectOne(sprintDTO), SprintDetailDTO.class);
        if (sprintDetailDTO != null) {
            sprintDetailDTO.setIssueCount(sprintMapper.queryIssueCount(projectId, sprintId));
        }
        return sprintDetailDTO;
    }

    @Override
    public PageInfo<IssueListDTO> queryIssueByOptions(Long projectId, Long sprintId, String status, PageRequest pageRequest, Long organizationId) {
        SprintDTO sprintDTO = new SprintDTO();
        sprintDTO.setProjectId(projectId);
        sprintDTO.setSprintId(sprintId);
        SprintDTO sprint = sprintMapper.selectOne(sprintDTO);
        if (sprint == null || Objects.equals(sprint.getStatusCode(), SPRINT_PLANNING_CODE)) {
            throw new CommonException(SPRINT_REPORT_ERROR);
        }
        Date actualEndDate = sprint.getActualEndDate() == null ? new Date() : sprint.getActualEndDate();
        sprint.setActualEndDate(actualEndDate);
        Date startDate = sprint.getStartDate();
        PageInfo<Long> reportIssuePage = new PageInfo<>(new ArrayList<>());
        pageRequest.setSort(PageUtil.sortResetOrder(pageRequest.getSort(), "ai", new HashMap<>()));
        //pageRequest.resetOrder("ai", new HashMap<>());
        switch (status) {
            case DONE:
                reportIssuePage = PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize(), PageUtil.sortToSql(pageRequest.getSort())).doSelectPageInfo(() -> reportMapper.queryReportIssueIds(projectId, sprintId, startDate, actualEndDate, true));
                break;
            case UNFINISHED:
                reportIssuePage = PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize(), PageUtil.sortToSql(pageRequest.getSort())).doSelectPageInfo(() -> reportMapper.queryReportIssueIds(projectId, sprintId, startDate, actualEndDate, false));
                break;
            case REMOVE:
                reportIssuePage = PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize(), PageUtil.sortToSql(pageRequest.getSort())).doSelectPageInfo(() -> reportMapper.queryRemoveIssueIdsDuringSprintWithOutSubEpicIssue(sprint));
                break;
            default:
                break;
        }
        List<Long> reportIssueIds = reportIssuePage.getList();
        if (reportIssueIds.isEmpty()) {
            return new PageInfo<>(new ArrayList<>());
        }
        Map<Long, StatusMapVO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
        //冲刺报告查询的issue
        List<IssueDTO> reportIssues = reportMapper.queryIssueByIssueIds(projectId, reportIssueIds);
        //冲刺中新添加的issue
        List<Long> issueIdBeforeSprintList = reportMapper.queryIssueIdsBeforeSprintStart(sprint);
        List<Long> issueIdAddList = issueIdBeforeSprintList.isEmpty() ? new ArrayList<>() : reportMapper.queryAddIssueIdsDuringSprint(sprint);
        //冲刺报告中issue的故事点
        List<SprintReportIssueStatusDO> reportIssueStoryPoints = reportMapper.queryIssueStoryPoints(projectId, reportIssueIds, actualEndDate);
        Map<Long, SprintReportIssueStatusDO> reportIssueStoryPointsMap = reportIssueStoryPoints.stream().collect(Collectors.toMap(SprintReportIssueStatusDO::getIssueId, sprintReportIssueStatusDO -> sprintReportIssueStatusDO));
        //冲刺完成前issue的最后变更状态
        List<SprintReportIssueStatusDO> reportIssueBeforeStatus = reportMapper.queryBeforeIssueStatus(projectId, reportIssueIds, startDate, actualEndDate);
        Map<Long, SprintReportIssueStatusDO> reportIssueBeforeStatusMap = new HashMap<>();
        for (SprintReportIssueStatusDO sprintReportIssueStatusDO : reportIssueBeforeStatus) {
            StatusMapVO statusMapVO = statusMapDTOMap.get(sprintReportIssueStatusDO.getStatusId());
            sprintReportIssueStatusDO.setCategoryCode(statusMapVO.getType());
            sprintReportIssueStatusDO.setStatusName(statusMapVO.getName());
            reportIssueBeforeStatusMap.put(sprintReportIssueStatusDO.getIssueId(), sprintReportIssueStatusDO);
        }
        //冲刺完成后issue的最初变更状态
        reportIssueIds.removeAll(reportIssueBeforeStatusMap.keySet());
        List<SprintReportIssueStatusDO> reportIssueAfterStatus = reportIssueIds.isEmpty() ? new ArrayList<>() : reportMapper.queryAfterIssueStatus(projectId, reportIssueIds, actualEndDate);
        Map<Long, SprintReportIssueStatusDO> reportIssueAfterStatusMap = new HashMap<>();
        for (SprintReportIssueStatusDO sprintReportIssueStatusDO : reportIssueAfterStatus) {
            StatusMapVO statusMapVO = statusMapDTOMap.get(sprintReportIssueStatusDO.getStatusId());
            sprintReportIssueStatusDO.setCategoryCode(statusMapVO.getType());
            sprintReportIssueStatusDO.setStatusName(statusMapVO.getName());
            reportIssueAfterStatusMap.put(sprintReportIssueStatusDO.getIssueId(), sprintReportIssueStatusDO);
        }
        reportIssues = reportIssues.stream().map(reportIssue -> {
            updateReportIssue(reportIssue, reportIssueStoryPointsMap, reportIssueBeforeStatusMap, reportIssueAfterStatusMap, issueIdAddList);
            return reportIssue;
        }).collect(Collectors.toList());
        Map<Long, PriorityVO> priorityMap = issueFeignClient.queryByOrganizationId(organizationId).getBody();
        Map<Long, IssueTypeVO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
        return PageUtil.buildPageInfoWithPageInfoList(reportIssuePage, issueAssembler.issueDoToIssueListDto(reportIssues, priorityMap, statusMapDTOMap, issueTypeDTOMap));
    }

    private void updateReportIssue(IssueDTO reportIssue, Map<Long, SprintReportIssueStatusDO> reportIssueStoryPointsMap, Map<Long, SprintReportIssueStatusDO> reportIssueBeforeStatusMap, Map<Long, SprintReportIssueStatusDO> reportIssueAfterStatusMap, List<Long> issueIdAddList) {
        SprintReportIssueStatusDO issueStoryPoints = reportIssueStoryPointsMap.get(reportIssue.getIssueId());
        BigDecimal zero = new BigDecimal(0);
        BigDecimal storyPoints = zero;
        if (issueStoryPoints != null) {
            storyPoints = issueStoryPoints.getStoryPoints() == null ? zero : new BigDecimal(issueStoryPoints.getStoryPoints());
        }
        SprintReportIssueStatusDO issueBeforeStatus = reportIssueBeforeStatusMap.get(reportIssue.getIssueId());
        SprintReportIssueStatusDO issueAfterStatus = reportIssueAfterStatusMap.get(reportIssue.getIssueId());
        String statusCode;
        String statusName;
        if (issueBeforeStatus != null) {
            statusCode = issueBeforeStatus.getCategoryCode();
            statusName = issueBeforeStatus.getStatusName();
        } else if (issueAfterStatus != null) {
            statusCode = issueAfterStatus.getCategoryCode();
            statusName = issueAfterStatus.getStatusName();
        } else {
            statusCode = reportIssue.getStatusCode();
            statusName = reportIssue.getStatusName();
        }
        reportIssue.setAddIssue(issueIdAddList.contains(reportIssue.getIssueId()));
        reportIssue.setStoryPoints(storyPoints);
        reportIssue.setStatusCode(statusCode);
        reportIssue.setStatusName(statusName);
    }

    @Override
    public String queryCurrentSprintCreateName(Long projectId) {
        ProjectInfoDTO projectInfo = new ProjectInfoDTO();
        projectInfo.setProjectId(projectId);
        projectInfo = projectInfoMapper.selectOne(projectInfo);
        if (projectInfo == null) {
            throw new CommonException(PROJECT_NOT_FOUND_ERROR);
        }
        SprintDTO sprintDTO = sprintMapper.queryLastSprint(projectId);
        if (sprintDTO == null) {
            return projectInfo.getProjectCode().trim() + " 1";
        } else {
            SprintE sprintE = sprintCreateAssembler.toTarget(sprintDTO, SprintE.class);
            return sprintE.assembleName(sprintE.getSprintName());
        }
    }

    @Override
    public SprintDetailDTO createBySprintName(Long projectId, String sprintName) {
        if (checkName(projectId, sprintName)) {
            throw new CommonException("error.sprintName.exist");
        }
        SprintE sprintE = new SprintE();
        sprintE.setProjectId(projectId);
        sprintE.setSprintName(sprintName);
        sprintE.setStatusCode(STATUS_SPRINT_PLANNING_CODE);
        return sprintCreateAssembler.toTarget(sprintRepository.createSprint(sprintE), SprintDetailDTO.class);
    }

    @Override
    public List<SprintUnClosedDTO> queryUnClosedSprint(Long projectId) {
        return ConvertHelper.convertList(sprintMapper.queryUnClosedSprint(projectId), SprintUnClosedDTO.class);
    }

    @Override
    public ActiveSprintDTO queryActiveSprint(Long projectId, Long organizationId) {
        ActiveSprintDTO result = new ActiveSprintDTO();
        SprintDTO activeSprint = getActiveSprint(projectId);
        if (activeSprint != null) {
            result = ConvertHelper.convert(activeSprint, ActiveSprintDTO.class);
            if (result.getEndDate() != null) {
                Date startDate = new Date();
                if (result.getStartDate().after(startDate)) {
                    startDate = result.getStartDate();
                }
                result.setDayRemain(dateUtil.getDaysBetweenDifferentDate(startDate, activeSprint.getEndDate(),
                        workCalendarRefMapper.queryHolidayBySprintIdAndProjectId(activeSprint.getSprintId(), activeSprint.getProjectId()),
                        workCalendarRefMapper.queryWorkBySprintIdAndProjectId(activeSprint.getSprintId(), activeSprint.getProjectId()), organizationId));
            }
        }
        return result;
    }

    @Override
    public List<String> queryNonWorkdays(Long projectId, Long sprintId, Long organizationId) {
        SprintDTO sprintDTO = sprintMapper.queryByProjectIdAndSprintId(projectId, sprintId);
        if (sprintDTO == null || sprintDTO.getStartDate() == null || sprintDTO.getEndDate() == null) {
            return new ArrayList<>();
        } else {
            Set<Date> dates = dateUtil.getNonWorkdaysDuring(sprintDTO.getStartDate(), sprintDTO.getEndDate(), organizationId);
            handleSprintNonWorkdays(dates, sprintDTO, projectId);
            List<Date> result = Ordering.from(Date::compareTo).sortedCopy(dates);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            return result.stream().map(sdf::format).collect(Collectors.toList());
        }
    }

    private void handleSprintNonWorkdays(Set<Date> dates, SprintDTO sprintDTO, Long projectId) {
        Set<Date> remove = new HashSet<>(dates.size() << 1);
        List<Date> workDays = workCalendarRefMapper.queryWorkBySprintIdAndProjectId(sprintDTO.getSprintId(), projectId);
        List<Date> holidays = workCalendarRefMapper.queryHolidayBySprintIdAndProjectId(sprintDTO.getSprintId(), projectId);
        workDays.forEach(d -> dates.forEach(date -> {
            if (DateUtil.isSameDay(d, date)) {
                remove.add(date);
            }
        }));
        dates.addAll(holidays.stream().filter(date -> (date.before(sprintDTO.getEndDate()) && date.after(sprintDTO.getStartDate()) || DateUtil.isSameDay(date, sprintDTO.getStartDate()) || DateUtil.isSameDay(date, sprintDTO.getEndDate()))).collect(Collectors.toSet()));
        dates.removeAll(remove);
        dateUtil.handleDuplicateDate(dates);
    }

    @Override
    public Boolean checkName(Long projectId, String sprinName) {
        SprintDTO sprintDTO = new SprintDTO();
        sprintDTO.setProjectId(projectId);
        sprintDTO.setSprintName(sprinName);
        List<SprintDTO> sprintDTOList = sprintMapper.select(sprintDTO);
        return sprintDTOList != null && !sprintDTOList.isEmpty();
    }

    @Override
    public void addSprintsWhenJoinProgram(Long programId, Long projectId) {
        ArtDTO activeArtDTO = artMapper.selectActiveArt(programId);
        if (activeArtDTO != null) {
            PiDTO res = piMapper.selectActivePi(programId, activeArtDTO.getId());
            if (res != null) {
                List<SprintDTO> existList = sprintMapper.selectListByPiId(projectId, res.getId());
                if (existList == null || existList.isEmpty()) {
                    List<SprintDTO> sprintDTOList = sprintMapper.selectListByPiId(programId, res.getId());
                    if (sprintDTOList != null && !sprintDTOList.isEmpty()) {
                        for (SprintDTO sprint : sprintDTOList) {
                            SprintE sprintE = new SprintE();
                            sprintE.setPiId(sprint.getPiId());
                            sprintE.setEndDate(sprint.getEndDate());
                            sprintE.setStartDate(sprint.getStartDate());
                            sprintE.setSprintName(sprint.getSprintName());
                            sprintE.setProjectId(projectId);
                            sprintE.setStatusCode(sprint.getStatusCode());
                            sprintRepository.createSprint(sprintE);
                        }
                    }
                }
            }
        }

    }

    @Override
    public void completeSprintsByActivePi(Long programId, Long projectId) {
        ArtDTO activeArtDTO = artMapper.selectActiveArt(programId);
        if (activeArtDTO != null) {
            PiDTO res = piMapper.selectActivePi(programId, activeArtDTO.getId());
            if (res != null) {
                List<Long> sprintList = sprintMapper.selectNotDoneByPiId(projectId, res.getId());
                if (sprintList != null && !sprintList.isEmpty()) {
                    for (Long sprintId : sprintList) {
                        SprintCompleteDTO sprintCompleteDTO = new SprintCompleteDTO();
                        sprintCompleteDTO.setProjectId(projectId);
                        sprintCompleteDTO.setSprintId(sprintId);
                        sprintCompleteDTO.setIncompleteIssuesDestination(0L);
                        completeSprint(projectId, sprintCompleteDTO);
                    }
                }
            }
        }
    }
}
