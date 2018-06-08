package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.app.assembler.*;
import io.choerodon.agile.domain.agile.repository.UserRepository;
import io.choerodon.agile.domain.agile.rule.SprintRule;
import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.agile.infra.common.utils.SearchUtil;
import io.choerodon.agile.infra.common.utils.StringUtil;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.mapper.IssueMapper;
import io.choerodon.agile.infra.mapper.ProjectInfoMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.app.service.SprintService;
import io.choerodon.agile.domain.agile.entity.SprintE;
import io.choerodon.agile.domain.agile.repository.IssueRepository;
import io.choerodon.agile.domain.agile.repository.SprintRepository;
import io.choerodon.agile.infra.mapper.SprintMapper;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jian_zhang02@163.com on 2018/5/15.
 */
@Service
@Transactional(rollbackFor = CommonException.class)
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
    private SprintRule sprintRule;

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
        issueRepository.removeFromSprint(projectId, sprintId);
        sprintRepository.deleteSprint(sprintE);
        return true;
    }

    @Override
    public Map<String, Object> queryByProjectId(Long projectId, Map<String, Object> searchParamMap) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        Map<String, Object> backlog = new HashMap<>();
        Map<String, Object> result = SearchUtil.setParam(searchParamMap);
        List<SprintSearchDTO> sprintSearchs = new ArrayList<>();
        Long activeSprintId = Long.MIN_VALUE;
        List<IssueSearchDO> issueSearchDOList = issueMapper.searchIssue(projectId, customUserDetails.getUserId(), StringUtil.cast(result.get(ADVANCED_SEARCH_ARGS)));
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
    public List<SprintNameDTO> queryNameByProjectId(Long projectId) {
        return sprintNameAssembler.doListToDTO(sprintMapper.queryNameByProjectId(projectId));
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
        issueRepository.subTaskToDestination(projectId, sprintCompleteDTO.getSprintId(), sprintCompleteDTO.getIncompleteIssuesDestination());
        moveNotDoneIssueToTargetSprint(projectId, sprintCompleteDTO);
        issueRepository.issueToDestination(projectId, sprintCompleteDTO.getSprintId(), sprintCompleteDTO.getIncompleteIssuesDestination());
        return true;
    }

    private void moveNotDoneIssueToTargetSprint(Long projectId, SprintCompleteDTO sprintCompleteDTO) {
        List<MoveIssueDO> moveIssueDOS = new ArrayList<>();
        beforeRank(projectId, sprintCompleteDTO.getSprintId(), sprintCompleteDTO.getIncompleteIssuesDestination(), moveIssueDOS);
        if (moveIssueDOS.isEmpty()) {
            return;
        }
        issueRepository.batchIssueToSprint(projectId, sprintCompleteDTO.getIncompleteIssuesDestination(), moveIssueDOS);
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
}
