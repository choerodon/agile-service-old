package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.app.assembler.*;
import io.choerodon.agile.domain.agile.repository.UserRepository;
import io.choerodon.agile.domain.agile.rule.SprintRule;
import io.choerodon.agile.infra.common.utils.RankUtil;
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
    public Map<String, Object> queryByProjectId(Long projectId, Map<String, Object> searchParamMap, List<Long> quickFilterIds) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        Map<String, Object> backlog = new HashMap<>();
        String filterSql = null;
        if (quickFilterIds != null && !quickFilterIds.isEmpty()) {
            filterSql = getQuickFilter(quickFilterIds);
        }
        List<Long> issueIds = issueMapper.querySprintIssueIdsByCondition(projectId, customUserDetails.getUserId(),
                StringUtil.cast(searchParamMap.get(ADVANCED_SEARCH_ARGS)), filterSql);
        List<SprintSearchDTO> sprintSearchs = new ArrayList<>();
        BackLogIssueDTO backLogIssueDTO = new BackLogIssueDTO();
        if (issueIds != null && !issueIds.isEmpty()) {
            handleSprintIssueData(issueIds, sprintSearchs, backLogIssueDTO, projectId);
        }
        backlog.put(SPRINT_DATA, sprintSearchs);
        backlog.put(BACKLOG_DATA, backLogIssueDTO);
        return backlog;
    }

    private void handleSprintIssueData(List<Long> issueIds, List<SprintSearchDTO> sprintSearchs, BackLogIssueDTO backLogIssueDTO, Long projectId) {
        Long activeSprintId = Long.MIN_VALUE;
        List<Long> assigneeIds = sprintMapper.queryAssigneeIdsByIssueIds(issueIds);
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(assigneeIds, true);
        SprintSearchDTO activeSprint = sprintSearchAssembler.doToDTO(sprintMapper.queryActiveSprint(projectId, issueIds), usersMap);
        if (activeSprint != null) {
            activeSprint.setIssueCount(activeSprint.getIssueSearchDTOList().size());
            activeSprint.setTodoStoryPoint(sprintMapper.queryStoryPoint(CATEGORY_TODO_CODE, issueIds, projectId, activeSprintId));
            activeSprint.setDoingStoryPoint(sprintMapper.queryStoryPoint(CATEGORY_DOING_CODE, issueIds, projectId, activeSprintId));
            activeSprint.setDoneStoryPoint(sprintMapper.queryStoryPoint(CATEGORY_DONE_CODE, issueIds, projectId, activeSprintId));
            sprintSearchs.add(activeSprint);
        }
        List<SprintSearchDTO> planSprints = sprintSearchAssembler.doListToDTO(sprintMapper.queryPlanSprint(projectId, issueIds), usersMap);
        if (!planSprints.isEmpty()) {
            planSprints.parallelStream().forEachOrdered(planSprint -> planSprint.setIssueCount(planSprint.getIssueSearchDTOList().size()));
            sprintSearchs.addAll(planSprints);
        }
        List<IssueSearchDO> backLogIssue = sprintMapper.queryBacklogIssues(projectId);
        backLogIssueDTO.setBackLogIssue(issueSearchAssembler.doListToDTO(backLogIssue, usersMap));
        backLogIssueDTO.setBacklogIssueCount(backLogIssue.size());
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
        if (targetSprintId != null && !Objects.equals(targetSprintId, 0L)) {
            issueRepository.issueToDestinationByIds(projectId, targetSprintId, moveIssueIds, new Date(), customUserDetails.getUserId());
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
        sprintCompleteMessage.setSprintNames(sprintNameAssembler.doListToDTO(sprintMapper.queryPlanSprintName(projectId)));
        sprintCompleteMessage.setParentsDoneUnfinishedSubtasks(issueAssembler.issueNumDOListToIssueNumDTO(sprintMapper.queryParentsDoneUnfinishedSubtasks(projectId, sprintId)));
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
        Map<Long, SprintReportIssueStatusDO> reportIssueBeforeStatusMap = reportIssueBeforeStatus.stream().collect(Collectors.toMap(SprintReportIssueStatusDO::getIssueId, sprintReportIssueStatusDO -> sprintReportIssueStatusDO));
        //冲刺完成后issue的最初变更状态
        reportIssueIds.removeAll(reportIssueBeforeStatusMap.keySet());
        List<SprintReportIssueStatusDO> reportIssueAfterStatus = reportIssueIds.isEmpty() ? new ArrayList<>() : reportMapper.queryAfterIssueStatus(projectId, reportIssueIds, actualEndDate);
        Map<Long, SprintReportIssueStatusDO> reportIssueAfterStatusMap = reportIssueAfterStatus.stream().collect(Collectors.toMap(SprintReportIssueStatusDO::getIssueId, sprintReportIssueStatusDO -> sprintReportIssueStatusDO));
        reportIssues = reportIssues.stream().map(reportIssue -> {
            updateReportIssue(reportIssue, reportIssueStoryPointsMap, reportIssueBeforeStatusMap, reportIssueAfterStatusMap, issueIdAddList);
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
}
