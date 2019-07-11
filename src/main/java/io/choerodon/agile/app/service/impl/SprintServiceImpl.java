package io.choerodon.agile.app.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Ordering;
import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.api.validator.SprintValidator;
import io.choerodon.agile.app.assembler.*;
import io.choerodon.agile.app.service.IssueAccessDataService;
import io.choerodon.agile.app.service.SprintService;
import io.choerodon.agile.app.service.WorkCalendarRefService;
import io.choerodon.agile.infra.common.aspect.DataLogRedisUtil;
import io.choerodon.agile.infra.dataobject.SprintConvertDTO;
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
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
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
    private WorkCalendarRefService workCalendarRefService;
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

    @Autowired
    private DataLogRedisUtil dataLogRedisUtil;

    @Autowired
    private IssueAccessDataService issueAccessDataService;


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
    private static final String INSERT_ERROR = "error.sprint.insert";
    private static final String DELETE_ERROR = "error.sprint.delete";
    private static final String UPDATE_ERROR = "error.sprint.update";


    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public synchronized SprintDetailVO createSprint(Long projectId) {
        ProjectInfoDTO projectInfo = new ProjectInfoDTO();
        projectInfo.setProjectId(projectId);
        projectInfo = projectInfoMapper.selectOne(projectInfo);
        if (projectInfo == null) {
            throw new CommonException(PROJECT_NOT_FOUND_ERROR);
        }
        SprintDTO sprintDTO = sprintMapper.queryLastSprint(projectId);
        SprintConvertDTO sprint = new SprintConvertDTO();
        if (sprintDTO == null) {
            sprint.createSprint(projectInfo);
        } else {
            SprintConvertDTO sprintConvertDTO = sprintCreateAssembler.toTarget(sprintDTO, SprintConvertDTO.class);
            sprint.createSprint(sprintConvertDTO);
        }
        return sprintCreateAssembler.toTarget(create(sprint), SprintDetailVO.class);
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
    public SprintDetailVO updateSprint(Long projectId, SprintUpdateVO sprintUpdateVO) {
        if (!Objects.equals(projectId, sprintUpdateVO.getProjectId())) {
            throw new CommonException(NOT_EQUAL_ERROR);
        }
        if (sprintUpdateVO.getSprintName() != null && checkNameUpdate(projectId, sprintUpdateVO.getSprintId(), sprintUpdateVO.getSprintName())) {
            throw new CommonException("error.sprintName.exist");
        }
        sprintValidator.checkDate(sprintUpdateVO);
        SprintConvertDTO sprintConvertDTO = sprintUpdateAssembler.toTarget(sprintUpdateVO, SprintConvertDTO.class);
        sprintConvertDTO.trimSprintName();
        return sprintUpdateAssembler.toTarget(update(sprintConvertDTO), SprintDetailVO.class);
    }

    @Override
    public Boolean deleteSprint(Long projectId, Long sprintId) {
        SprintDTO sprintDTO = new SprintDTO();
        sprintDTO.setProjectId(projectId);
        sprintDTO.setSprintId(sprintId);
        SprintConvertDTO sprintConvertDTO = sprintSearchAssembler.toTarget(sprintMapper.selectOne(sprintDTO), SprintConvertDTO.class);
        if (sprintConvertDTO == null) {
            throw new CommonException(NOT_FOUND_ERROR);
        }
        sprintConvertDTO.judgeDelete();
        moveIssueToBacklog(projectId, sprintId);
        issueAccessDataService.batchRemoveFromSprint(projectId, sprintId);
        delete(sprintConvertDTO);
        return true;
    }

    private void moveIssueToBacklog(Long projectId, Long sprintId) {
        List<MoveIssueDTO> moveIssueDTOS = new ArrayList<>();
        Long targetSprintId = 0L;
        List<Long> moveIssueRankIds = sprintMapper.queryAllRankIssueIds(projectId, sprintId);
        beforeRank(projectId, targetSprintId, moveIssueDTOS, moveIssueRankIds);
        if (moveIssueDTOS.isEmpty()) {
            return;
        }
        issueAccessDataService.batchUpdateIssueRank(projectId, moveIssueDTOS);
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
        List<IssueIdSprintIdVO> issueIdSprintIdVOS = issueMapper.querySprintAllIssueIdsByCondition(projectId, customUserDetails.getUserId(),
                StringUtil.cast(searchParamMap.get(ADVANCED_SEARCH_ARGS)), filterSql, assigneeFilterIds);
        List<SprintSearchVO> sprintSearches = new ArrayList<>();
        BackLogIssueVO backLogIssueVO = new BackLogIssueVO();
        Map<Long, PriorityVO> priorityMap = issueFeignClient.queryByOrganizationId(organizationId).getBody();
        Map<Long, StatusMapVO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
        setStatusIsCompleted(projectId, statusMapDTOMap);
        Map<Long, IssueTypeVO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
        if (issueIdSprintIdVOS != null && !issueIdSprintIdVOS.isEmpty()) {
            handleSprintIssueData(issueIdSprintIdVOS, issueIds, sprintSearches, backLogIssueVO, projectId, priorityMap, statusMapDTOMap, issueTypeDTOMap);
        } else {
            handleSprintNoIssue(sprintSearches, projectId);
        }
        backlog.put(SPRINT_DATA, sprintSearches);
        backlog.put(BACKLOG_DATA, backLogIssueVO);
        return backlog;
    }

    private void setStatusIsCompleted(Long projectId, Map<Long, StatusMapVO> statusMapDTOMap) {
        IssueStatusDTO issueStatusDTO = new IssueStatusDTO();
        issueStatusDTO.setProjectId(projectId);
        Map<Long, Boolean> statusCompletedMap = issueStatusMapper.select(issueStatusDTO).stream().collect(Collectors.toMap(IssueStatusDTO::getStatusId, IssueStatusDTO::getCompleted));
        statusMapDTOMap.entrySet().forEach(entry -> entry.getValue().setCompleted(statusCompletedMap.getOrDefault(entry.getKey(), false)));
    }

    private void handleSprintNoIssue(List<SprintSearchVO> sprintSearches, Long projectId) {
        SprintSearchDTO sprintSearchDTO = sprintMapper.queryActiveSprintNoIssueIds(projectId);
        Set<Long> assigneeIds = sprintMapper.queryBacklogSprintAssigneeIds(projectId);
        Map<Long, UserMessageDTO> usersMap = userService.queryUsersMap(new ArrayList<>(assigneeIds), true);
        if (sprintSearchDTO != null) {
            List<AssigneeIssueDTO> assigneeIssueDTOS = sprintMapper.queryAssigneeIssueByActiveSprintId(projectId, sprintSearchDTO.getSprintId());
            if (assigneeIssueDTOS != null && !assigneeIssueDTOS.isEmpty()) {
                sprintSearchDTO.setAssigneeIssueDTOList(assigneeIssueDTOS);
            }
            SprintSearchVO activeSprint = sprintSearchAssembler.doToDTO(sprintSearchDTO, usersMap, null, null, null);
            sprintSearches.add(activeSprint);
        }
        List<SprintSearchDTO> sprintSearchDTOS = sprintMapper.queryPlanSprintNoIssueIds(projectId);
        List<SprintSearchVO> planSprints = sprintSearchAssembler.doListToDTO(sprintSearchDTOS, usersMap, null, null, null);
        if (planSprints != null && !planSprints.isEmpty()) {
            sprintSearches.addAll(planSprints);
        }
    }

    private void handleSprintIssueData(List<IssueIdSprintIdVO> issueIdSprintIdVOS, List<Long> issueIds, List<SprintSearchVO> sprintSearches, BackLogIssueVO backLogIssueVO, Long projectId, Map<Long, PriorityVO> priorityMap, Map<Long, StatusMapVO> statusMapDTOMap, Map<Long, IssueTypeVO> issueTypeDTOMap) {
        List<Long> allIssueIds = issueIdSprintIdVOS.stream().map(IssueIdSprintIdVO::getIssueId).collect(Collectors.toList());
        //查询出所有经办人用户id
        Set<Long> assigneeIds = sprintMapper.queryAssigneeIdsByIssueIds(projectId, allIssueIds);
        Map<Long, UserMessageDTO> usersMap = userService.queryUsersMap(new ArrayList<>(assigneeIds), true);
        SprintSearchDTO sprintSearchDTO = sprintMapper.queryActiveSprintNoIssueIds(projectId);
        if (sprintSearchDTO != null) {
            List<Long> activeSprintIssueIds = issueIdSprintIdVOS.stream().filter(x -> sprintSearchDTO.getSprintId().equals(x.getSprintId())).map(IssueIdSprintIdVO::getIssueId).collect(Collectors.toList());
            sprintSearchDTO.setIssueSearchDTOList(!activeSprintIssueIds.isEmpty() ? sprintMapper.queryActiveSprintIssueSearchByIssueIds(projectId, activeSprintIssueIds, sprintSearchDTO.getSprintId()) : new ArrayList<>());
            sprintSearchDTO.setAssigneeIssueDTOList(sprintMapper.queryAssigneeIssueByActiveSprintId(projectId, sprintSearchDTO.getSprintId()));
            SprintSearchVO activeSprint = sprintSearchAssembler.doToDTO(sprintSearchDTO, usersMap, priorityMap, statusMapDTOMap, issueTypeDTOMap);
            activeSprint.setIssueCount(activeSprint.getIssueSearchVOList() == null ? 0 : activeSprint.getIssueSearchVOList().size());
            Map<String, List<Long>> statusMap = issueFeignClient.queryStatusByProjectId(projectId, SchemeApplyType.AGILE).getBody()
                    .stream().collect(Collectors.groupingBy(StatusMapVO::getType, Collectors.mapping(StatusMapVO::getId, Collectors.toList())));
            BigDecimal zero = new BigDecimal(0);
            activeSprint.setTodoStoryPoint(statusMap.get(CATEGORY_TODO_CODE) != null && !statusMap.get(CATEGORY_TODO_CODE).isEmpty() && !activeSprintIssueIds.isEmpty() ? sprintMapper.queryStoryPoint(statusMap.get(CATEGORY_TODO_CODE), activeSprintIssueIds, projectId) : zero);
            activeSprint.setDoingStoryPoint(statusMap.get(CATEGORY_DOING_CODE) != null && !statusMap.get(CATEGORY_DOING_CODE).isEmpty() && !activeSprintIssueIds.isEmpty() ? sprintMapper.queryStoryPoint(statusMap.get(CATEGORY_DOING_CODE), activeSprintIssueIds, projectId) : zero);
            activeSprint.setDoneStoryPoint(statusMap.get(CATEGORY_DONE_CODE) != null && !statusMap.get(CATEGORY_DONE_CODE).isEmpty() && !activeSprintIssueIds.isEmpty() ? sprintMapper.queryStoryPoint(statusMap.get(CATEGORY_DONE_CODE), activeSprintIssueIds, projectId) : zero);
            sprintSearches.add(activeSprint);
        }
        List<SprintSearchDTO> sprintSearchDTOS = sprintMapper.queryPlanSprint(projectId, allIssueIds);
        if (sprintSearchDTOS != null && !sprintSearchDTOS.isEmpty()) {
            List<SprintSearchVO> planSprints = sprintSearchAssembler.doListToDTO(sprintSearchDTOS, usersMap, priorityMap, statusMapDTOMap, issueTypeDTOMap);
            planSprints.parallelStream().forEachOrdered(planSprint -> planSprint.setIssueCount(planSprint.getIssueSearchVOList() == null ? 0 : planSprint.getIssueSearchVOList().size()));
            sprintSearches.addAll(planSprints);
        }
        if (issueIds != null && !issueIds.isEmpty()) {
            List<IssueSearchDTO> backLogIssue = sprintMapper.queryBacklogIssues(projectId, issueIds);
            backLogIssueVO.setBackLogIssue(issueSearchAssembler.doListToDTO(backLogIssue, usersMap, priorityMap, statusMapDTOMap, issueTypeDTOMap));
            backLogIssueVO.setBacklogIssueCount(backLogIssue.size());
        }
    }

    @Override
    public List<SprintNameVO> queryNameByOptions(Long projectId, List<String> sprintStatusCodes) {
        return sprintNameAssembler.toTargetList(sprintMapper.queryNameByOptions(projectId, sprintStatusCodes), SprintNameVO.class);
    }

    @Override
    public SprintDetailVO startSprint(Long projectId, SprintUpdateVO sprintUpdateVO) {
        if (!Objects.equals(projectId, sprintUpdateVO.getProjectId())) {
            throw new CommonException(NOT_EQUAL_ERROR);
        }
        if (sprintMapper.selectCountByStartedSprint(projectId) != 0) {
            throw new CommonException(START_SPRINT_ERROR);
        }
        SprintConvertDTO sprintConvertDTO = sprintUpdateAssembler.toTarget(sprintUpdateVO, SprintConvertDTO.class);
        sprintConvertDTO.checkDate();
        sprintValidator.checkSprintStartInProgram(sprintConvertDTO);
        sprintConvertDTO.startSprint();
        if (sprintUpdateVO.getWorkDates() != null && !sprintUpdateVO.getWorkDates().isEmpty()) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            sprintUpdateVO.getWorkDates().forEach(workDates -> {
                WorkCalendarRefDTO workCalendarRefDTO = new WorkCalendarRefDTO();
                workCalendarRefDTO.setSprintId(sprintConvertDTO.getSprintId());
                workCalendarRefDTO.setProjectId(sprintConvertDTO.getProjectId());
                workCalendarRefDTO.setWorkDay(workDates.getWorkDay());
                try {
                    calendar.setTime(dateFormat.parse(workDates.getWorkDay()));
                } catch (ParseException e) {
                    throw new CommonException("ParseException{}", e);
                }
                workCalendarRefDTO.setYear(calendar.get(Calendar.YEAR));
                workCalendarRefDTO.setStatus(workDates.getStatus());
                workCalendarRefService.create(workCalendarRefDTO);
            });
        }
        issueAccessDataService.updateStayDate(projectId, sprintConvertDTO.getSprintId(), new Date());
        return sprintUpdateAssembler.toTarget(update(sprintConvertDTO), SprintDetailVO.class);
    }

    @Override
    public Boolean completeSprint(Long projectId, SprintCompleteVO sprintCompleteVO) {
        if (!Objects.equals(projectId, sprintCompleteVO.getProjectId())) {
            throw new CommonException(NOT_EQUAL_ERROR);
        }
        sprintValidator.judgeCompleteSprint(projectId, sprintCompleteVO.getIncompleteIssuesDestination());
        SprintDTO sprintDTO = new SprintDTO();
        sprintDTO.setProjectId(projectId);
        sprintDTO.setSprintId(sprintCompleteVO.getSprintId());
        SprintConvertDTO sprintConvertDTO = sprintUpdateAssembler.toTarget(sprintMapper.selectOne(sprintDTO), SprintConvertDTO.class);
        sprintConvertDTO.completeSprint();
        update(sprintConvertDTO);
        moveNotDoneIssueToTargetSprint(projectId, sprintCompleteVO);
        return true;
    }

    private void moveNotDoneIssueToTargetSprint(Long projectId, SprintCompleteVO sprintCompleteVO) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        List<MoveIssueDTO> moveIssueDTOS = new ArrayList<>();
        Long targetSprintId = sprintCompleteVO.getIncompleteIssuesDestination();
        List<Long> moveIssueRankIds = sprintMapper.queryIssueIdOrderByRankDesc(projectId, sprintCompleteVO.getSprintId());
        moveIssueRankIds.addAll(sprintMapper.queryUnDoneSubOfParentIds(projectId, sprintCompleteVO.getSprintId()));
        beforeRank(projectId, sprintCompleteVO.getIncompleteIssuesDestination(), moveIssueDTOS, moveIssueRankIds);
        if (moveIssueDTOS.isEmpty()) {
            return;
        }
        List<Long> moveIssueIds = sprintMapper.queryIssueIds(projectId, sprintCompleteVO.getSprintId());
        moveIssueIds.addAll(issueMapper.querySubTaskIds(projectId, sprintCompleteVO.getSprintId()));
        moveIssueIds.addAll(sprintMapper.queryParentsDoneSubtaskUnDoneIds(projectId, sprintCompleteVO.getSprintId()));
        if (targetSprintId != null && !Objects.equals(targetSprintId, 0L)) {
            issueAccessDataService.issueToDestinationByIdsCloseSprint(projectId, targetSprintId, moveIssueIds, new Date(), customUserDetails.getUserId());
        }
        issueAccessDataService.batchUpdateIssueRank(projectId, moveIssueDTOS);
    }

    private void beforeRank(Long projectId, Long targetSprintId, List<MoveIssueDTO> moveIssueDTOS, List<Long> moveIssueIds) {
        if (moveIssueIds.isEmpty()) {
            return;
        }
        String minRank = sprintMapper.queryMinRank(projectId, targetSprintId);
        if (minRank == null) {
            minRank = RankUtil.mid();
            for (Long issueId : moveIssueIds) {
                moveIssueDTOS.add(new MoveIssueDTO(issueId, minRank));
                minRank = RankUtil.genPre(minRank);
            }
        } else {
            for (Long issueId : moveIssueIds) {
                minRank = RankUtil.genPre(minRank);
                moveIssueDTOS.add(new MoveIssueDTO(issueId, minRank));
            }
        }
    }

    @Override
    public SprintCompleteMessageVO queryCompleteMessageBySprintId(Long projectId, Long sprintId) {
        SprintCompleteMessageVO sprintCompleteMessage = new SprintCompleteMessageVO();
        sprintCompleteMessage.setSprintNames(sprintNameAssembler.toTargetList(sprintMapper.queryPlanSprintName(projectId), SprintNameVO.class));
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
    public SprintDetailVO querySprintById(Long projectId, Long sprintId) {
        SprintDTO sprintDTO = new SprintDTO();
        sprintDTO.setProjectId(projectId);
        sprintDTO.setSprintId(sprintId);
        SprintDetailVO sprintDetailVO = sprintSearchAssembler.toTarget(sprintMapper.selectOne(sprintDTO), SprintDetailVO.class);
        if (sprintDetailVO != null) {
            sprintDetailVO.setIssueCount(sprintMapper.queryIssueCount(projectId, sprintId));
        }
        return sprintDetailVO;
    }

    @Override
    public PageInfo<IssueListVO> queryIssueByOptions(Long projectId, Long sprintId, String status, PageRequest pageRequest, Long organizationId) {
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
            SprintConvertDTO sprintConvertDTO = sprintCreateAssembler.toTarget(sprintDTO, SprintConvertDTO.class);
            return sprintConvertDTO.assembleName(sprintConvertDTO.getSprintName());
        }
    }

    @Override
    public SprintDetailVO createBySprintName(Long projectId, String sprintName) {
        if (checkName(projectId, sprintName)) {
            throw new CommonException("error.sprintName.exist");
        }
        SprintConvertDTO sprintConvertDTO = new SprintConvertDTO();
        sprintConvertDTO.setProjectId(projectId);
        sprintConvertDTO.setSprintName(sprintName);
        sprintConvertDTO.setStatusCode(STATUS_SPRINT_PLANNING_CODE);
        return sprintCreateAssembler.toTarget(create(sprintConvertDTO), SprintDetailVO.class);
    }

    @Override
    public List<SprintUnClosedVO> queryUnClosedSprint(Long projectId) {
        return ConvertHelper.convertList(sprintMapper.queryUnClosedSprint(projectId), SprintUnClosedVO.class);
    }

    @Override
    public ActiveSprintVO queryActiveSprint(Long projectId, Long organizationId) {
        ActiveSprintVO result = new ActiveSprintVO();
        SprintDTO activeSprint = getActiveSprint(projectId);
        if (activeSprint != null) {
            result = ConvertHelper.convert(activeSprint, ActiveSprintVO.class);
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
                            SprintConvertDTO sprintConvertDTO = new SprintConvertDTO();
                            sprintConvertDTO.setPiId(sprint.getPiId());
                            sprintConvertDTO.setEndDate(sprint.getEndDate());
                            sprintConvertDTO.setStartDate(sprint.getStartDate());
                            sprintConvertDTO.setSprintName(sprint.getSprintName());
                            sprintConvertDTO.setProjectId(projectId);
                            sprintConvertDTO.setStatusCode(sprint.getStatusCode());
                            create(sprintConvertDTO);
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
                        SprintCompleteVO sprintCompleteVO = new SprintCompleteVO();
                        sprintCompleteVO.setProjectId(projectId);
                        sprintCompleteVO.setSprintId(sprintId);
                        sprintCompleteVO.setIncompleteIssuesDestination(0L);
                        completeSprint(projectId, sprintCompleteVO);
                    }
                }
            }
        }
    }



    @Override
    public SprintConvertDTO create(SprintConvertDTO sprintConvertDTO) {
        SprintDTO sprintDTO = modelMapper.map(sprintConvertDTO, SprintDTO.class);
        if (sprintMapper.insertSelective(sprintDTO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        //清除冲刺报表相关缓存
        dataLogRedisUtil.deleteByCreateSprint(sprintConvertDTO);
        return modelMapper.map(sprintMapper.selectByPrimaryKey(sprintDTO.getSprintId()), SprintConvertDTO.class);
    }

    @Override
    public SprintConvertDTO update(SprintConvertDTO sprintConvertDTO) {
        SprintDTO sprintDTO = modelMapper.map(sprintConvertDTO, SprintDTO.class);
        if (sprintMapper.updateByPrimaryKeySelective(sprintDTO) != 1) {
            throw new CommonException(UPDATE_ERROR);
        }
        //清除冲刺报表相关缓存
        dataLogRedisUtil.deleteByUpdateSprint(sprintConvertDTO);
        return modelMapper.map(sprintMapper.selectByPrimaryKey(sprintDTO.getSprintId()), SprintConvertDTO.class);
    }

    @Override
    public Boolean delete(SprintConvertDTO sprintConvertDTO) {
        SprintDTO sprintDTO = modelMapper.map(sprintConvertDTO, SprintDTO.class);
        if (sprintMapper.delete(sprintDTO) != 1) {
            throw new CommonException(DELETE_ERROR);
        }
        //清除冲刺报表相关缓存
        dataLogRedisUtil.deleteByUpdateSprint(sprintConvertDTO);
        return true;
    }

    @Override
    public void updateSprintNameByBatch(Long programId, List<Long> sprintIds) {
        sprintMapper.updateSprintNameByBatch(programId, sprintIds);
    }
}
