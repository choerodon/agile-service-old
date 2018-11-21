package io.choerodon.agile.app.service.impl;

import com.google.common.collect.Ordering;
import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.api.validator.WorkCalendarValidator;
import io.choerodon.agile.app.assembler.*;
import io.choerodon.agile.app.service.IssueService;
import io.choerodon.agile.domain.agile.repository.SprintWorkCalendarRefRepository;
import io.choerodon.agile.domain.agile.repository.UserRepository;
import io.choerodon.agile.domain.agile.rule.SprintRule;
import io.choerodon.agile.infra.common.enums.SchemeApplyType;
import io.choerodon.agile.infra.common.utils.DateUtil;
import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.agile.infra.common.utils.StringUtil;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.feign.StateMachineFeignClient;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.app.service.SprintService;
import io.choerodon.agile.domain.agile.entity.SprintE;
import io.choerodon.agile.domain.agile.repository.IssueRepository;
import io.choerodon.agile.domain.agile.repository.SprintRepository;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private UserRepository userRepository;
    @Autowired
    private IssueAssembler issueAssembler;
    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private SprintRule sprintRule;
    @Autowired
    private QuickFilterMapper quickFilterMapper;
    @Autowired
    private DateUtil dateUtil;
    @Autowired
    private SprintWorkCalendarRefMapper sprintWorkCalendarRefMapper;
    @Autowired
    private SprintWorkCalendarRefRepository sprintWorkCalendarRefRepository;

    @Autowired
    private IssueFeignClient issueFeignClient;

    @Autowired
    private StateMachineFeignClient stateMachineFeignClient;
    @Autowired
    private IssueService issueService;

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
        ProjectInfoDO projectInfo = new ProjectInfoDO();
        projectInfo.setProjectId(projectId);
        projectInfo = projectInfoMapper.selectOne(projectInfo);
        if (projectInfo == null) {
            throw new CommonException(PROJECT_NOT_FOUND_ERROR);
        }
        SprintDO sprintDO = sprintMapper.queryLastSprint(projectId);
        SprintE sprint = new SprintE();
        if (sprintDO == null) {
            sprint.createSprint(projectInfo);
        } else {
            SprintE sprintE = sprintCreateAssembler.toTarget(sprintDO, SprintE.class);
            sprint.createSprint(sprintE);
        }
        return sprintCreateAssembler.toTarget(sprintRepository.createSprint(sprint), SprintDetailDTO.class);
    }

    @Override
    public SprintDetailDTO updateSprint(Long projectId, SprintUpdateDTO sprintUpdateDTO) {
        if (!Objects.equals(projectId, sprintUpdateDTO.getProjectId())) {
            throw new CommonException(NOT_EQUAL_ERROR);
        }
        sprintRule.checkDate(sprintUpdateDTO);
        SprintE sprintE = sprintUpdateAssembler.toTarget(sprintUpdateDTO, SprintE.class);
        sprintE.trimSprintName();
        return sprintUpdateAssembler.toTarget(sprintRepository.updateSprint(sprintE), SprintDetailDTO.class);
    }

    @Override
    public Boolean deleteSprint(Long projectId, Long sprintId) {
        SprintDO sprintDO = new SprintDO();
        sprintDO.setProjectId(projectId);
        sprintDO.setSprintId(sprintId);
        SprintE sprintE = sprintSearchAssembler.toTarget(sprintMapper.selectOne(sprintDO), SprintE.class);
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
    public Map<String, Object> queryByProjectId(Long projectId, Map<String, Object> searchParamMap, List<Long> quickFilterIds, Long organizationId) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        Map<String, Object> backlog = new HashMap<>();
        String filterSql = null;
        if (quickFilterIds != null && !quickFilterIds.isEmpty()) {
            filterSql = getQuickFilter(quickFilterIds);
        }
        List<Long> issueIds = issueMapper.querySprintIssueIdsByCondition(projectId, customUserDetails.getUserId(),
                StringUtil.cast(searchParamMap.get(ADVANCED_SEARCH_ARGS)), filterSql);
        List<SprintSearchDTO> sprintSearches = new ArrayList<>();
        BackLogIssueDTO backLogIssueDTO = new BackLogIssueDTO();
        Map<Long, PriorityDTO> priorityMap = issueFeignClient.queryByOrganizationId(organizationId).getBody();
        Map<Long, StatusMapDTO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
        Map<Long, IssueTypeDTO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
        if (issueIds != null && !issueIds.isEmpty()) {
            handleSprintIssueData(issueIds, sprintSearches, backLogIssueDTO, projectId, priorityMap, statusMapDTOMap, issueTypeDTOMap);
        } else {
            handleSprintNoIssue(sprintSearches, projectId);
        }
        backlog.put(SPRINT_DATA, sprintSearches);
        backlog.put(BACKLOG_DATA, backLogIssueDTO);
        return backlog;
    }

    private void handleSprintNoIssue(List<SprintSearchDTO> sprintSearches, Long projectId) {
        SprintSearchDO sprintSearchDO = sprintMapper.queryActiveSprintNoIssueIds(projectId);
        Set<Long> assigneeIds = sprintMapper.queryBacklogSprintAssigneeIds(projectId);
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(new ArrayList<>(assigneeIds), true);
        if (sprintSearchDO != null) {
            List<AssigneeIssueDO> assigneeIssueDOS = sprintMapper.queryAssigneeIssueByActiveSprintId(projectId, sprintSearchDO.getSprintId());
            if (assigneeIssueDOS != null && !assigneeIssueDOS.isEmpty()) {
                sprintSearchDO.setAssigneeIssueDOList(assigneeIssueDOS);
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

    private void handleSprintIssueData(List<Long> issueIds, List<SprintSearchDTO> sprintSearches, BackLogIssueDTO backLogIssueDTO, Long projectId, Map<Long, PriorityDTO> priorityMap, Map<Long, StatusMapDTO> statusMapDTOMap, Map<Long, IssueTypeDTO> issueTypeDTOMap) {
        Set<Long> assigneeIds = sprintMapper.queryAssigneeIdsByIssueIds(issueIds);
        assigneeIds.addAll(sprintMapper.queryBacklogSprintAssigneeIds(projectId));
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(new ArrayList<>(assigneeIds), true);
        SprintSearchDO sprintSearchDO = sprintMapper.queryActiveSprintNoIssueIds(projectId);
        if (sprintSearchDO != null) {
            sprintSearchDO.setIssueSearchDOList(sprintMapper.queryActiveSprintIssueSearchByIssueIds(sprintSearchDO.getSprintId(), issueIds));
            sprintSearchDO.setAssigneeIssueDOList(sprintMapper.queryAssigneeIssueByActiveSprintId(projectId, sprintSearchDO.getSprintId()));
            SprintSearchDTO activeSprint = sprintSearchAssembler.doToDTO(sprintSearchDO, usersMap, priorityMap, statusMapDTOMap, issueTypeDTOMap);
            activeSprint.setIssueCount(activeSprint.getIssueSearchDTOList() == null ? 0 : activeSprint.getIssueSearchDTOList().size());
            Map<String, List<Long>> statusMap = issueFeignClient.queryStatusByProjectId(projectId, SchemeApplyType.AGILE).getBody()
                    .stream().collect(Collectors.groupingBy(StatusMapDTO::getType, Collectors.mapping(StatusMapDTO::getId, Collectors.toList())));
            List<Long> todo = statusMap.get(CATEGORY_TODO_CODE);
            if (todo != null && !todo.isEmpty()) {
                activeSprint.setTodoStoryPoint(sprintMapper.queryStoryPoint(todo, issueIds, projectId, activeSprint.getSprintId()));
            }
            List<Long> doing = statusMap.get(CATEGORY_DOING_CODE);
            if (doing != null && !doing.isEmpty()) {
                activeSprint.setDoingStoryPoint(sprintMapper.queryStoryPoint(doing, issueIds, projectId, activeSprint.getSprintId()));
            }
            List<Long> done = statusMap.get(CATEGORY_DONE_CODE);
            if (done != null && !done.isEmpty()) {
                activeSprint.setDoneStoryPoint(sprintMapper.queryStoryPoint(done, issueIds, projectId, activeSprint.getSprintId()));
            }
            sprintSearches.add(activeSprint);
        }
        List<SprintSearchDO> sprintSearchDTOS = sprintMapper.queryPlanSprint(projectId, issueIds);
        if (sprintSearchDTOS != null && !sprintSearchDTOS.isEmpty()) {
            List<SprintSearchDTO> planSprints = sprintSearchAssembler.doListToDTO(sprintSearchDTOS, usersMap, priorityMap, statusMapDTOMap, issueTypeDTOMap);
            planSprints.parallelStream().forEachOrdered(planSprint -> planSprint.setIssueCount(planSprint.getIssueSearchDTOList() == null ? 0 : planSprint.getIssueSearchDTOList().size()));
            sprintSearches.addAll(planSprints);
        }
        List<IssueSearchDO> backLogIssue = sprintMapper.queryBacklogIssues(projectId, issueIds);
        backLogIssueDTO.setBackLogIssue(issueSearchAssembler.doListToDTO(backLogIssue, usersMap, priorityMap, statusMapDTOMap, issueTypeDTOMap));
        backLogIssueDTO.setBacklogIssueCount(backLogIssue.size());
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
        sprintE.startSprint();
        if (sprintUpdateDTO.getWorkDates() != null && !sprintUpdateDTO.getWorkDates().isEmpty()) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            sprintUpdateDTO.getWorkDates().forEach(workDates -> {
                SprintWorkCalendarRefDO sprintWorkCalendarRefDO = new SprintWorkCalendarRefDO();
                sprintWorkCalendarRefDO.setSprintId(sprintE.getSprintId());
                sprintWorkCalendarRefDO.setProjectId(sprintE.getProjectId());
                sprintWorkCalendarRefDO.setWorkDay(workDates.getWorkDay());
                try {
                    calendar.setTime(dateFormat.parse(workDates.getWorkDay()));
                } catch (ParseException e) {
                    throw new CommonException("ParseException{}", e);
                }
                sprintWorkCalendarRefDO.setYear(calendar.get(Calendar.YEAR));
                sprintWorkCalendarRefDO.setStatus(workDates.getStatus());
                sprintWorkCalendarRefRepository.create(sprintWorkCalendarRefDO);
            });
        }
        return sprintUpdateAssembler.toTarget(sprintRepository.updateSprint(sprintE), SprintDetailDTO.class);
    }

    @Override
    public Boolean completeSprint(Long projectId, SprintCompleteDTO sprintCompleteDTO) {
        if (!Objects.equals(projectId, sprintCompleteDTO.getProjectId())) {
            throw new CommonException(NOT_EQUAL_ERROR);
        }
//        sprintRule.judgeCompleteSprint(projectId, sprintCompleteDTO.getSprintId(), sprintCompleteDTO.getIncompleteIssuesDestination());
        SprintDO sprintDO = new SprintDO();
        sprintDO.setProjectId(projectId);
        sprintDO.setSprintId(sprintCompleteDTO.getSprintId());
        SprintE sprintE = sprintUpdateAssembler.toTarget(sprintMapper.selectOne(sprintDO), SprintE.class);
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
        //状态更新为初始状态
        issueService.batchHandleIssueStatus(projectId, moveIssueIds, sprintCompleteDTO.getSprintId());
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
        sprintCompleteMessage.setParentsDoneUnfinishedSubtasks(issueAssembler.toTargetList(sprintMapper.queryParentsDoneUnfinishedSubtasks(projectId, sprintId), IssueNumDTO.class));
        sprintCompleteMessage.setIncompleteIssues(sprintMapper.queryNotDoneIssueCount(projectId, sprintId));
        sprintCompleteMessage.setPartiallyCompleteIssues(sprintMapper.queryDoneIssueCount(projectId, sprintId));
        return sprintCompleteMessage;
    }

    @Override
    public SprintDO getActiveSprint(Long projectId) {
        return sprintMapper.getActiveSprint(projectId);
    }

    @Override
    public SprintDetailDTO querySprintById(Long projectId, Long sprintId) {
        SprintDO sprintDO = new SprintDO();
        sprintDO.setProjectId(projectId);
        sprintDO.setSprintId(sprintId);
        SprintDetailDTO sprintDetailDTO = sprintSearchAssembler.toTarget(sprintMapper.selectOne(sprintDO), SprintDetailDTO.class);
        if (sprintDetailDTO != null) {
            sprintDetailDTO.setIssueCount(sprintMapper.queryIssueCount(projectId, sprintId));
        }
        return sprintDetailDTO;
    }

    @Override
    public Page<IssueListDTO> queryIssueByOptions(Long projectId, Long sprintId, String status, PageRequest pageRequest, Long organizationId) {
        SprintDO sprintDO = new SprintDO();
        sprintDO.setProjectId(projectId);
        sprintDO.setSprintId(sprintId);
        SprintDO sprint = sprintMapper.selectOne(sprintDO);
        if (sprint == null || Objects.equals(sprint.getStatusCode(), SPRINT_PLANNING_CODE)) {
            throw new CommonException(SPRINT_REPORT_ERROR);
        }
        Date actualEndDate = sprint.getActualEndDate() == null ? new Date() : sprint.getActualEndDate();
        sprint.setActualEndDate(actualEndDate);
        Date startDate = sprint.getStartDate();
        Page<Long> reportIssuePage = new Page<>();
        Page<IssueListDTO> reportPage = new Page<>();
        pageRequest.resetOrder("ai", new HashMap<>());
        switch (status) {
            case DONE:
                reportIssuePage = PageHelper.doPageAndSort(pageRequest, () -> reportMapper.queryReportIssueIds(projectId, sprintId, startDate, actualEndDate, true));
                break;
            case UNFINISHED:
                reportIssuePage = PageHelper.doPageAndSort(pageRequest, () -> reportMapper.queryReportIssueIds(projectId, sprintId, startDate, actualEndDate, false));
                break;
            case REMOVE:
                reportIssuePage = PageHelper.doPageAndSort(pageRequest, () -> reportMapper.queryRemoveIssueIdsDuringSprintWithOutSubEpicIssue(sprint));
                break;
            default:
                break;
        }
        List<Long> reportIssueIds = reportIssuePage.getContent();
        if (reportIssueIds.isEmpty()) {
            return reportPage;
        }
        Map<Long, StatusMapDTO> statusMapDTOMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
        //冲刺报告查询的issue
        List<IssueDO> reportIssues = reportMapper.queryIssueByIssueIds(projectId, reportIssueIds);
        //冲刺中新添加的issue
        List<Long> issueIdBeforeSprintList = reportMapper.queryIssueIdsBeforeSprintStart(sprint);
        List<Long> issueIdAddList = issueIdBeforeSprintList.isEmpty() ? new ArrayList<>() : reportMapper.queryAddIssueIdsDuringSprint(sprint);
        //冲刺报告中issue的故事点
        List<SprintReportIssueStatusDO> reportIssueStoryPoints = reportMapper.queryIssueStoryPoints(projectId, reportIssueIds, actualEndDate);
        Map<Long, SprintReportIssueStatusDO> reportIssueStoryPointsMap = reportIssueStoryPoints.stream().collect(Collectors.toMap(SprintReportIssueStatusDO::getIssueId, sprintReportIssueStatusDO -> sprintReportIssueStatusDO));
        //冲刺完成前issue的最后变更状态
        List<SprintReportIssueStatusDO> reportIssueBeforeStatus = reportMapper.queryBeforeIssueStatus(projectId, reportIssueIds, startDate, actualEndDate);
//        Map<Long, SprintReportIssueStatusDO> reportIssueBeforeStatusMap = reportIssueBeforeStatus.stream().collect(Collectors.toMap(SprintReportIssueStatusDO::getIssueId, sprintReportIssueStatusDO -> sprintReportIssueStatusDO));
        Map<Long, SprintReportIssueStatusDO> reportIssueBeforeStatusMap = new HashMap<>();
        for (SprintReportIssueStatusDO sprintReportIssueStatusDO : reportIssueBeforeStatus) {
            StatusMapDTO statusMapDTO = statusMapDTOMap.get(sprintReportIssueStatusDO.getStatusId());
            sprintReportIssueStatusDO.setCategoryCode(statusMapDTO.getType());
            sprintReportIssueStatusDO.setStatusName(statusMapDTO.getName());
            reportIssueBeforeStatusMap.put(sprintReportIssueStatusDO.getIssueId(), sprintReportIssueStatusDO);
        }
        //冲刺完成后issue的最初变更状态
        reportIssueIds.removeAll(reportIssueBeforeStatusMap.keySet());
        List<SprintReportIssueStatusDO> reportIssueAfterStatus = reportIssueIds.isEmpty() ? new ArrayList<>() : reportMapper.queryAfterIssueStatus(projectId, reportIssueIds, actualEndDate);
//        Map<Long, SprintReportIssueStatusDO> reportIssueAfterStatusMap = reportIssueAfterStatus.stream().collect(Collectors.toMap(SprintReportIssueStatusDO::getIssueId, sprintReportIssueStatusDO -> sprintReportIssueStatusDO));
        Map<Long, SprintReportIssueStatusDO> reportIssueAfterStatusMap = new HashMap<>();
        for (SprintReportIssueStatusDO sprintReportIssueStatusDO : reportIssueAfterStatus) {
            StatusMapDTO statusMapDTO = statusMapDTOMap.get(sprintReportIssueStatusDO.getStatusId());
            sprintReportIssueStatusDO.setCategoryCode(statusMapDTO.getType());
            sprintReportIssueStatusDO.setStatusName(statusMapDTO.getName());
            reportIssueAfterStatusMap.put(sprintReportIssueStatusDO.getIssueId(), sprintReportIssueStatusDO);
        }
        reportIssues = reportIssues.stream().map(reportIssue -> {
            updateReportIssue(reportIssue, reportIssueStoryPointsMap, reportIssueBeforeStatusMap, reportIssueAfterStatusMap, issueIdAddList);
            return reportIssue;
        }).collect(Collectors.toList());
        reportPage.setTotalPages(reportIssuePage.getTotalPages());
        reportPage.setTotalElements(reportIssuePage.getTotalElements());
        reportPage.setSize(reportIssuePage.getSize());
        reportPage.setNumberOfElements(reportIssuePage.getNumberOfElements());
        reportPage.setNumber(reportIssuePage.getNumber());
        Map<Long, PriorityDTO> priorityMap = issueFeignClient.queryByOrganizationId(organizationId).getBody();
        Map<Long, IssueTypeDTO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
        reportPage.setContent(issueAssembler.issueDoToIssueListDto(reportIssues, priorityMap, statusMapDTOMap, issueTypeDTOMap));
        return reportPage;
    }

    private void updateReportIssue(IssueDO reportIssue, Map<Long, SprintReportIssueStatusDO> reportIssueStoryPointsMap, Map<Long, SprintReportIssueStatusDO> reportIssueBeforeStatusMap, Map<Long, SprintReportIssueStatusDO> reportIssueAfterStatusMap, List<Long> issueIdAddList) {
        SprintReportIssueStatusDO issueStoryPoints = reportIssueStoryPointsMap.get(reportIssue.getIssueId());
        Integer storyPoints = 0;
        if (issueStoryPoints != null) {
            storyPoints = issueStoryPoints.getStoryPoints() == null ? 0 : Integer.parseInt(issueStoryPoints.getStoryPoints());
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
        ProjectInfoDO projectInfo = new ProjectInfoDO();
        projectInfo.setProjectId(projectId);
        projectInfo = projectInfoMapper.selectOne(projectInfo);
        if (projectInfo == null) {
            throw new CommonException(PROJECT_NOT_FOUND_ERROR);
        }
        SprintDO sprintDO = sprintMapper.queryLastSprint(projectId);
        if (sprintDO == null) {
            return projectInfo.getProjectCode().trim() + " 1";
        } else {
            SprintE sprintE = sprintCreateAssembler.toTarget(sprintDO, SprintE.class);
            return sprintE.assembleName(sprintE.getSprintName());
        }
    }

    @Override
    public SprintDetailDTO createBySprintName(Long projectId, String sprintName) {
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
        SprintDO activeSprint = getActiveSprint(projectId);
        if (activeSprint != null) {
            result = ConvertHelper.convert(activeSprint, ActiveSprintDTO.class);
            if (result.getEndDate() != null) {
                Date startDate = new Date();
                if (result.getStartDate().after(startDate)) {
                    startDate = result.getStartDate();
                }
                result.setDayRemain(dateUtil.getDaysBetweenDifferentDate(startDate, activeSprint.getEndDate(),
                        sprintWorkCalendarRefMapper.queryHolidayBySprintIdAndProjectId(activeSprint.getSprintId(), activeSprint.getProjectId()),
                        sprintWorkCalendarRefMapper.queryWorkBySprintIdAndProjectId(activeSprint.getSprintId(), activeSprint.getProjectId()), organizationId));
            }
        }
        return result;
    }

    @Override
    public List<String> queryNonWorkdays(Long projectId, Long sprintId, Long organizationId) {
        SprintDO sprintDO = sprintMapper.queryByProjectIdAndSprintId(projectId, sprintId);
        if (sprintDO == null || sprintDO.getStartDate() == null || sprintDO.getEndDate() == null) {
            return new ArrayList<>();
        } else {
            Set<Date> dates = dateUtil.getNonWorkdaysDuring(sprintDO.getStartDate(), sprintDO.getEndDate(), organizationId);
            handleSprintNonWorkdays(dates, sprintDO, projectId);
            List<Date> result = Ordering.from(Date::compareTo).sortedCopy(dates);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            return result.stream().map(sdf::format).collect(Collectors.toList());
        }
    }

    @Override
    public SprintWorkCalendarDTO querySprintWorkCalendarRefs(Long projectId, Integer year) {
        SprintSearchDO sprintSearchDO = sprintMapper.queryActiveSprintNoIssueIds(projectId);
        if (sprintSearchDO != null) {
            SprintWorkCalendarDTO sprintWorkCalendarDTO = sprintSearchAssembler.toTarget(sprintSearchDO, SprintWorkCalendarDTO.class);
            sprintWorkCalendarDTO.setSprintWorkCalendarRefDTOS(sprintCreateAssembler.toTargetList(sprintWorkCalendarRefMapper.queryWithNextYearByYear(projectId, sprintWorkCalendarDTO.getSprintId(), year), SprintWorkCalendarRefDTO.class));
            return sprintWorkCalendarDTO;
        } else {
            return new SprintWorkCalendarDTO();
        }

    }

    @Override
    public SprintWorkCalendarRefDTO createSprintWorkCalendarRef(Long projectId, Long sprintId, SprintWorkCalendarRefCreateDTO sprintWorkCalendarRefCreateDTO) {
        SprintWorkCalendarRefDO sprintWorkCalendarRefDO = sprintCreateAssembler.toTarget(sprintWorkCalendarRefCreateDTO, SprintWorkCalendarRefDO.class);
        sprintWorkCalendarRefDO.setProjectId(projectId);
        sprintWorkCalendarRefDO.setSprintId(sprintId);
        sprintWorkCalendarRefDO.setYear(WorkCalendarValidator.checkWorkDayAndStatus(sprintWorkCalendarRefDO.getWorkDay(), sprintWorkCalendarRefDO.getStatus()));
        return sprintSearchAssembler.toTarget(sprintWorkCalendarRefRepository.create(sprintWorkCalendarRefDO), SprintWorkCalendarRefDTO.class);
    }

    @Override
    public void deleteSprintWorkCalendarRef(Long projectId, Long calendarId) {
        sprintWorkCalendarRefRepository.delete(projectId, calendarId);
    }

    private void handleSprintNonWorkdays(Set<Date> dates, SprintDO sprintDO, Long projectId) {
        Set<Date> remove = new HashSet<>(dates.size() << 1);
        List<Date> workDays = sprintWorkCalendarRefMapper.queryWorkBySprintIdAndProjectId(sprintDO.getSprintId(), projectId);
        List<Date> holidays = sprintWorkCalendarRefMapper.queryHolidayBySprintIdAndProjectId(sprintDO.getSprintId(), projectId);
        workDays.forEach(d -> dates.forEach(date -> {
            if (DateUtil.isSameDay(d, date)) {
                remove.add(date);
            }
        }));
        dates.addAll(holidays.stream().filter(date -> (date.before(sprintDO.getEndDate()) && date.after(sprintDO.getStartDate()) || DateUtil.isSameDay(date, sprintDO.getStartDate()) || DateUtil.isSameDay(date, sprintDO.getEndDate()))).collect(Collectors.toSet()));
        dates.removeAll(remove);
        dateUtil.handleDuplicateDate(dates);
    }

}
