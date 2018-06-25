package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.app.assembler.*;
import io.choerodon.agile.domain.agile.entity.DataLogE;
import io.choerodon.agile.domain.agile.repository.DataLogRepository;
import io.choerodon.agile.domain.agile.repository.UserRepository;
import io.choerodon.agile.domain.agile.rule.SprintRule;
import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.agile.infra.common.utils.SearchUtil;
import io.choerodon.agile.infra.common.utils.StringUtil;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.mapper.*;
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
    private DataLogRepository dataLogRepository;

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
    private static final String FIEID_SPRINT = "Sprint";

    @Override
    public SprintDetailDTO createSprint(Long projectId) {
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
            SprintE sprintE = sprintCreateAssembler.doToEntity(sprintDO);
            sprint.createSprint(sprintE);
        }
        return sprintCreateAssembler.entityToDto(sprintRepository.createSprint(sprint));
    }

    @Override
    public SprintDetailDTO updateSprint(Long projectId, SprintUpdateDTO sprintUpdateDTO) {
        if (!Objects.equals(projectId, sprintUpdateDTO.getProjectId())) {
            throw new CommonException(NOT_EQUAL_ERROR);
        }
        sprintRule.checkDate(sprintUpdateDTO);
        SprintE sprintE = sprintUpdateAssembler.dtoToEntity(sprintUpdateDTO);
        sprintE.trimSprintName();
        return sprintUpdateAssembler.entityToDto(sprintRepository.updateSprint(sprintE));
    }

    private DataLogE getAllDataLogByDelete(Long projectId, Long sprintId, Long issueId) {
        List<SprintNameDTO> sprintNames = sprintNameAssembler.doListToDTO(issueMapper.querySprintNameByIssueId(issueId));
        String oldSprintIdStr = sprintNames.stream().map(sprintName -> sprintName.getSprintId().toString()).collect(Collectors.joining(","));
        String oldSprintNameStr = sprintNames.stream().map(sprintName -> sprintName.getSprintName()).collect(Collectors.joining(","));
        String newSprintIdStr = "";
        String newSprintNameStr = "";
        int idx = 0;
        for (SprintNameDTO sprintName : sprintNames) {
            if (sprintId.equals(sprintName.getSprintId())) {
                continue;
            }
            if (idx == 0) {
                newSprintIdStr = sprintName.getSprintId().toString();
                newSprintNameStr = sprintName.getSprintName();
                idx++;
            } else {
                newSprintIdStr = newSprintIdStr + "," + sprintName.getSprintId().toString();
                newSprintNameStr = newSprintNameStr + "," + sprintName.getSprintName();
            }
        }
        String oldValue = oldSprintIdStr;
        String oldString = oldSprintNameStr;
        DataLogE dataLogE = new DataLogE();
        dataLogE.setProjectId(projectId);
        dataLogE.setField(FIEID_SPRINT);
        dataLogE.setIssueId(issueId);
        dataLogE.setOldValue("".equals(oldValue) ? null : oldValue);
        dataLogE.setOldString("".equals(oldString) ? null : oldString);
        dataLogE.setNewValue("".equals(newSprintIdStr) ? null : newSprintIdStr);
        dataLogE.setNewString("".equals(newSprintNameStr)? null : newSprintNameStr);
        return dataLogE;
    }

    private List<DataLogE> getDeleteDataLog(Long projectId, Long sprintId) {
        List<Long> moveIssueIds = sprintMapper.queryIssueIds(projectId, sprintId);
        moveIssueIds.addAll(issueMapper.querySubTaskIds(projectId, sprintId));
        List<DataLogE> dataLogEList = new ArrayList<>();
        for (Long issueId : moveIssueIds) {
            dataLogEList.add(getAllDataLogByDelete(projectId, sprintId, issueId));
        }
        return dataLogEList;
    }

    private void dataLogDeleteSprint(List<DataLogE> dataLogEList) {
        for (DataLogE dataLogE : dataLogEList) {
            dataLogRepository.create(dataLogE);
        }
    }

    @Override
    public Boolean deleteSprint(Long projectId, Long sprintId) {
        SprintDO sprintDO = new SprintDO();
        sprintDO.setProjectId(projectId);
        sprintDO.setSprintId(sprintId);
        SprintE sprintE = sprintSearchAssembler.doToEntity(sprintMapper.selectOne(sprintDO));
        if (sprintE == null) {
            throw new CommonException(NOT_FOUND_ERROR);
        }
        sprintE.judgeDelete();
        List<DataLogE> dataLogEList = getDeleteDataLog(projectId, sprintId);
        issueRepository.removeFromSprint(projectId, sprintId);
        sprintRepository.deleteSprint(sprintE);
        dataLogDeleteSprint(dataLogEList);
        return true;
    }

    private String getQuickFilter(List<Long> quickFilterIds) {
        List<String> sqlQuerys = quickFilterMapper.selectSqlQueryByIds(quickFilterIds);
        StringBuilder sql = new StringBuilder("select issue_id from agile_issue where ");
        int idx = 0;
        for (String filter : sqlQuerys) {
            if (idx != 0) {
                sql.append(" and " + filter);
            } else {
                sql.append(filter);
                idx += 1;
            }
        }
        return sql.toString();
    }

    @Override
    public Map<String, Object> queryByProjectId(Long projectId, Map<String, Object> searchParamMap, List<Long> quickFilterIds) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        Map<String, Object> backlog = new HashMap<>();
        Map<String, Object> result = SearchUtil.setParam(searchParamMap);
        List<SprintSearchDTO> sprintSearchs = new ArrayList<>();
        Long activeSprintId = Long.MIN_VALUE;
        String filterSql = null;
        if (quickFilterIds != null && !quickFilterIds.isEmpty()) {
            filterSql = getQuickFilter(quickFilterIds);
        }
        List<IssueSearchDO> issueSearchDOList = issueMapper.searchIssue(projectId, customUserDetails.getUserId(), StringUtil.cast(result.get(ADVANCED_SEARCH_ARGS)), filterSql);
        List<Long> assigneeIds = issueSearchDOList.stream().filter(issue -> issue.getAssigneeId() != null && !Objects.equals(issue.getAssigneeId(), 0L)).map(IssueSearchDO::getAssigneeId).distinct().collect(Collectors.toList());
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(assigneeIds, true);
        List<IssueSearchDTO> issues = issueSearchAssembler.doListToDTO(issueSearchDOList, usersMap);
        SprintSearchDTO activeSprint = sprintSearchAssembler.doToDTO(sprintMapper.queryActiveSprint(projectId));
        if (activeSprint != null) {
            activeSprintId = activeSprint.getSprintId();
            List<AssigneeIssueDTO> assigneeIssues = issueSearchAssembler.doListToAssigneeIssueDTO(sprintMapper.queryAssigneeIssueCountById(projectId, activeSprintId, customUserDetails.getUserId(), StringUtil.cast(result.get(ADVANCED_SEARCH_ARGS))), usersMap);
            activeSprint.setAssigneeIssues(assigneeIssues);
            activeSprint.setIssueCount(sprintMapper.queryIssueCount(projectId, activeSprintId));
            activeSprint.setTodoStoryPoint(sprintMapper.queryStoryPoint(projectId, activeSprintId, customUserDetails.getUserId(), CATEGORY_TODO_CODE, StringUtil.cast(result.get(ADVANCED_SEARCH_ARGS))));
            activeSprint.setDoingStoryPoint(sprintMapper.queryStoryPoint(projectId, activeSprintId, customUserDetails.getUserId(), CATEGORY_DOING_CODE, StringUtil.cast(result.get(ADVANCED_SEARCH_ARGS))));
            activeSprint.setDoneStoryPoint(sprintMapper.queryStoryPoint(projectId, activeSprintId, customUserDetails.getUserId(), CATEGORY_DONE_CODE, StringUtil.cast(result.get(ADVANCED_SEARCH_ARGS))));
            sprintSearchs.add(activeSprint);
        }
        List<SprintSearchDTO> planSprints = sprintSearchAssembler.doListToDTO(sprintMapper.queryPlanSprint(projectId));
        if (!planSprints.isEmpty()) {
            List<Long> planSprintIds = planSprints.stream().map(SprintSearchDTO::getSprintId).collect(Collectors.toList());
            Map<Long, Integer> issueCountMap = sprintMapper.queryIssueCountMap(projectId, planSprintIds).stream().collect(Collectors.toMap(IssueCountDO::getId, IssueCountDO::getIssueCount));
            Map<Long, List<AssigneeIssueDTO>> assigneeIssueMap = issueSearchAssembler.doListToAssigneeIssueDTO(sprintMapper.queryAssigneeIssueCount(projectId, planSprintIds, customUserDetails.getUserId(), StringUtil.cast(result.get(ADVANCED_SEARCH_ARGS))), usersMap).stream().collect(Collectors.groupingBy(AssigneeIssueDTO::getSprintId));
            planSprints.forEach(planSprint -> {
                planSprint.setIssueCount(issueCountMap.get(planSprint.getSprintId()));
                planSprint.setAssigneeIssues(assigneeIssueMap.get(planSprint.getSprintId()));
            });
            sprintSearchs.addAll(planSprints);
        }
        Long finalActiveSprintId = activeSprintId;
        Map<Long, List<IssueSearchDTO>> issuesMap = issues.stream().filter(issue -> issue.getSprintId() != null && (Objects.equals(issue.getSprintId(), finalActiveSprintId) || !Objects.equals(issue.getCategoryCode(), CATEGORY_DONE_CODE))).collect(Collectors.groupingBy(IssueSearchDTO::getSprintId));
        List<IssueSearchDTO> backLogIssue = issues.stream().filter(issue -> (issue.getSprintId() == null || Objects.equals(issue.getSprintId(), 0L)) && !Objects.equals(issue.getCategoryCode(), CATEGORY_DONE_CODE)).collect(Collectors.toList());
        sprintSearchs.forEach(sprintSearch -> sprintSearch.setIssueSearchDTOList(issuesMap.get(sprintSearch.getSprintId())));
        Integer backlogIssueCount = issueMapper.queryBacklogIssueCount(projectId);
        BackLogIssueDTO backLogIssueDTO = new BackLogIssueDTO(backlogIssueCount, backLogIssue);
        backlog.put(SPRINT_DATA, sprintSearchs);
        backlog.put(BACKLOG_DATA, backLogIssueDTO);
        return backlog;
    }

    @Override
    public List<SprintNameDTO> queryNameByOptions(Long projectId, List<String> sprintStatusCodes) {
        return sprintNameAssembler.doListToDTO(sprintMapper.queryNameByOptions(projectId, sprintStatusCodes));
    }

    @Override
    public SprintDetailDTO startSprint(Long projectId, SprintUpdateDTO sprintUpdateDTO) {
        if (!Objects.equals(projectId, sprintUpdateDTO.getProjectId())) {
            throw new CommonException(NOT_EQUAL_ERROR);
        }
        if (sprintMapper.selectCountByStartedSprint(projectId) != 0) {
            throw new CommonException(START_SPRINT_ERROR);
        }
        SprintE sprintE = sprintUpdateAssembler.dtoToEntity(sprintUpdateDTO);
        sprintE.checkDate();
        sprintE.startSprint();
        return sprintUpdateAssembler.entityToDto(sprintRepository.updateSprint(sprintE));
    }

    @Override
    public Boolean completeSprint(Long projectId, SprintCompleteDTO sprintCompleteDTO) {
        if (!Objects.equals(projectId, sprintCompleteDTO.getProjectId())) {
            throw new CommonException(NOT_EQUAL_ERROR);
        }
        sprintRule.judgeCompleteSprint(projectId, sprintCompleteDTO.getSprintId(), sprintCompleteDTO.getIncompleteIssuesDestination());
        SprintDO sprintDO = new SprintDO();
        sprintDO.setProjectId(projectId);
        sprintDO.setSprintId(sprintCompleteDTO.getSprintId());
        SprintE sprintE = sprintUpdateAssembler.doToEntity(sprintMapper.selectOne(sprintDO));
        sprintE.completeSprint();
        sprintRepository.updateSprint(sprintE);
        moveNotDoneIssueToTargetSprint(projectId, sprintCompleteDTO);
        return true;
    }

    private DataLogE getNewDataLog(Long projectId, Long issueId, Long destinationSprintId) {
        String newValue;
        String newString;
        String oldValue;
        String oldString;
        List<SprintNameDTO> closeSprintNames = sprintNameAssembler.doListToDTO(issueMapper.queryCloseSprintNameByIssueId(issueId));
        SprintNameDTO sprintName = sprintNameAssembler.doToDTO(sprintMapper.querySprintNameBySprintId(projectId, destinationSprintId));
        String closeSprintIdStr = closeSprintNames.stream().map(closeSprintName -> closeSprintName.getSprintId().toString()).collect(Collectors.joining(","));
        String closeSprintNameStr = closeSprintNames.stream().map(closeSprintName -> closeSprintName.getSprintName()).collect(Collectors.joining(","));
        newValue = closeSprintIdStr;
        newString = closeSprintNameStr;
        oldValue = closeSprintIdStr;
        oldString = closeSprintNameStr;
        if (sprintName != null) {
            newValue = ("".equals(oldValue) ? sprintName.getSprintId().toString() : oldValue + "," + sprintName.getSprintId().toString());
            newString = ("".equals(oldString) ? sprintName.getSprintName() : oldString + "," + sprintName.getSprintName());
        }
        DataLogE result = new DataLogE();
        result.setProjectId(projectId);
        result.setField(FIEID_SPRINT);
        result.setIssueId(issueId);
        result.setOldValue("".equals(oldValue) ? null : oldValue);
        result.setOldString("".equals(oldString) ? null : oldString);
        result.setNewValue("".equals(newValue) ? null : newValue);
        result.setNewString("".equals(newString) ? null : newString);
        return result;
    }

    private void dataLogCompleteSprint(List<DataLogE> dataLogEList) {
        for (DataLogE dataLogE : dataLogEList) {
            dataLogRepository.create(dataLogE);
        }
    }

    private void moveNotDoneIssueToTargetSprint(Long projectId, SprintCompleteDTO sprintCompleteDTO) {
        List<MoveIssueDO> moveIssueDOS = new ArrayList<>();
        Long targetSprintId = sprintCompleteDTO.getIncompleteIssuesDestination();
        beforeRank(projectId, sprintCompleteDTO.getSprintId(), sprintCompleteDTO.getIncompleteIssuesDestination(), moveIssueDOS);
        if (moveIssueDOS.isEmpty()) {
            return;
        }
        List<Long> moveIssueIds = sprintMapper.queryIssueIds(projectId, sprintCompleteDTO.getSprintId());
        moveIssueIds.addAll(issueMapper.querySubTaskIds(projectId, sprintCompleteDTO.getSprintId()));
        if (targetSprintId != null && !Objects.equals(targetSprintId, 0L)) {
            List<DataLogE> newDataLogs = new ArrayList<>();
            for (Long issueId : moveIssueIds) {
                newDataLogs.add(getNewDataLog(projectId, issueId, sprintCompleteDTO.getIncompleteIssuesDestination()));
            }
            issueRepository.issueToDestinationByIds(projectId, targetSprintId, moveIssueIds);
            dataLogCompleteSprint(newDataLogs);
        }
        issueRepository.batchUpdateIssueRank(projectId, moveIssueDOS);
    }

    private void beforeRank(Long projectId, Long sprintId, Long targetSprintId, List<MoveIssueDO> moveIssueDOS) {
        List<Long> moveIssueIds = sprintMapper.queryIssueIdOrderByRankDesc(projectId, sprintId);
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
        sprintCompleteMessage.setSprintNames(sprintNameAssembler.doListToDTO(sprintMapper.queryPlanSprintName(projectId)));
        sprintCompleteMessage.setParentsDoneUnfinishedSubtasks(issueAssembler.issueNumDOToIssueNumDTO(sprintMapper.queryParentsDoneUnfinishedSubtasks(projectId, sprintId)));
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
        SprintDetailDTO sprintDetailDTO = sprintSearchAssembler.doToDetailDTO(sprintMapper.selectOne(sprintDO));
        if (sprintDetailDTO != null) {
            sprintDetailDTO.setIssueCount(sprintMapper.queryIssueCount(projectId, sprintId));
        }
        return sprintDetailDTO;
    }

    @Override
    public Page<IssueListDTO> queryIssueByOptions(Long projectId, Long sprintId, String status, PageRequest pageRequest) {
        SprintDO sprintDO = new SprintDO();
        sprintDO.setProjectId(projectId);
        sprintDO.setSprintId(sprintId);
        SprintDO sprint = sprintMapper.selectOne(sprintDO);
        if (sprint == null || Objects.equals(sprint.getStatusCode(), SPRINT_PLANNING_CODE)) {
            throw new CommonException(SPRINT_REPORT_ERROR);
        }
        Date actualEndDate = sprint.getActualEndDate();
        Page<Long> reportIssuePage = new Page<>();
        Page<IssueListDTO> reportPage = new Page<>();
        pageRequest.resetOrder("ai", new HashMap<>());
        switch (status) {
            case DONE:
                reportIssuePage = PageHelper.doPageAndSort(pageRequest, () -> reportMapper.queryReportIssueIds(projectId, sprintId, actualEndDate, true));
                break;
            case UNFINISHED:
                reportIssuePage = PageHelper.doPageAndSort(pageRequest, () -> reportMapper.queryReportIssueIds(projectId, sprintId, actualEndDate, false));
                break;
            case REMOVE:
                reportIssuePage = PageHelper.doPageAndSort(pageRequest, () -> reportMapper.queryRemoveIssueIdsDuringSprint(sprint));
                break;
            default:
                break;
        }
        List<Long> reportIssueIds = reportIssuePage.getContent();
        if (reportIssueIds.isEmpty()) {
            return reportPage;
        }
        //冲刺报告查询的issue
        List<IssueDO> reportIssues = reportMapper.queryIssueByIssueIds(projectId, reportIssueIds);
        //冲刺中新添加的issue
        List<Long> issueIdBeforeSprintList = reportMapper.queryIssueIdsBeforeSprintStart(sprintDO);
        List<Long> issueIdAddList = reportMapper.queryAddIssueIdsDuringSprint(sprintDO, issueIdBeforeSprintList);
        //冲刺报告中issue的故事点
        List<SprintReportIssueStatusDO> reportIssueStoryPoints = reportMapper.queryIssueStoryPoints(projectId, reportIssueIds, actualEndDate);
        Map<Long, SprintReportIssueStatusDO> reportIssueStoryPointsMap = reportIssueStoryPoints.stream().collect(Collectors.toMap(SprintReportIssueStatusDO::getIssueId, sprintReportIssueStatusDO -> sprintReportIssueStatusDO));
        //冲刺完成前issue的最后变更状态
        List<SprintReportIssueStatusDO> reportIssueBeforeStatus = reportMapper.queryIssueStatus(projectId, reportIssueIds, actualEndDate, true);
        Map<Long, SprintReportIssueStatusDO> reportIssueBeforeStatusMap = reportIssueBeforeStatus.stream().collect(Collectors.toMap(SprintReportIssueStatusDO::getIssueId, sprintReportIssueStatusDO -> sprintReportIssueStatusDO));
        //冲刺完成后issue的最初变更状态
        reportIssueIds.removeAll(reportIssueBeforeStatusMap.keySet());
        List<SprintReportIssueStatusDO> reportIssueAfterStatus = new ArrayList<>();
        if (!reportIssueIds.isEmpty()) {
            reportIssueAfterStatus = reportMapper.queryIssueStatus(projectId, reportIssueIds, actualEndDate, false);
        }
        Map<Long, SprintReportIssueStatusDO> reportIssueAfterStatusMap = reportIssueAfterStatus.stream().collect(Collectors.toMap(SprintReportIssueStatusDO::getIssueId, sprintReportIssueStatusDO -> sprintReportIssueStatusDO));
        reportIssues = reportIssues.stream().map(reportIssue -> {
            SprintReportIssueStatusDO issueStoryPoints = reportIssueStoryPointsMap.get(reportIssue.getIssueId());
            Integer storyPoints = issueStoryPoints == null ? 0 : Integer.parseInt(issueStoryPoints.getStoryPoints());
            SprintReportIssueStatusDO issueBeforeStatus = reportIssueBeforeStatusMap.get(reportIssue.getIssueId());
            SprintReportIssueStatusDO issueAfterStatus = reportIssueAfterStatusMap.get(reportIssue.getIssueId());
            String statusCode = issueBeforeStatus == null ? (issueAfterStatus == null ? reportIssue.getStatusCode() : issueAfterStatus.getCategoryCode()) : issueBeforeStatus.getCategoryCode();
            String statusName = issueBeforeStatus == null ? (issueAfterStatus == null ? reportIssue.getStatusName() : issueAfterStatus.getStatusName()) : issueBeforeStatus.getStatusName();
            reportIssue.setAddIssue(issueIdAddList.contains(reportIssue.getIssueId()));
            reportIssue.setStoryPoints(storyPoints);
            reportIssue.setStatusCode(statusCode);
            reportIssue.setStatusName(statusName);
            return reportIssue;
        }).collect(Collectors.toList());
        reportPage.setTotalPages(reportIssuePage.getTotalPages());
        reportPage.setTotalElements(reportIssuePage.getTotalElements());
        reportPage.setSize(reportIssuePage.getSize());
        reportPage.setNumberOfElements(reportIssuePage.getNumberOfElements());
        reportPage.setNumber(reportIssuePage.getNumber());
        reportPage.setContent(issueAssembler.issueDoToIssueListDto(reportIssues));
        return reportPage;
    }
}
