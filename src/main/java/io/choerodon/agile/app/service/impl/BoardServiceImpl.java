package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.api.validator.BoardValidator;
import io.choerodon.agile.app.service.*;
import io.choerodon.agile.domain.agile.entity.*;
import io.choerodon.agile.domain.agile.event.StatusPayload;
import io.choerodon.agile.domain.agile.repository.*;
import io.choerodon.agile.infra.common.enums.SchemeApplyType;
import io.choerodon.agile.infra.common.utils.DateUtil;
import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.agile.infra.common.utils.SiteMsgUtil;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.feign.StateMachineFeignClient;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.statemachine.dto.InputDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BoardServiceImpl implements BoardService {

    private static final String CONTRAINT_NONE = "constraint_none";
    private static final String STORY_POINTS = "story_point";
    private static final String PARENT_CHILD = "parent_child";
    private static final String BOARD = "board";
    private static final String URL_TEMPLATE1 = "#/agile/issue?type=project&id=";
    private static final String URL_TEMPLATE2 = "&name=";
    private static final String URL_TEMPLATE3 = "&paramName=";
    private static final String URL_TEMPLATE4 = "&paramIssueId=";
    private static final String URL_TEMPLATE5 = "&paramOpenIssueId=";
    private static final String URL_TEMPLATE6 = "&organizationId=";
    private static final String PROJECT_ID = "projectId";
    private static final String RANK = "rank";
    private static final String UPDATE_STATUS_MOVE = "updateStatusMove";

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardMapper boardMapper;

    @Autowired
    private BoardColumnService boardColumnService;

    @Autowired
    private BoardColumnMapper boardColumnMapper;

    @Autowired
    private SprintService sprintService;

    @Autowired
    private IssueMapper issueMapper;

    @Autowired
    private ColumnStatusRelRepository columnStatusRelRepository;

    @Autowired
    private BoardColumnRepository boardColumnRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuickFilterMapper quickFilterMapper;

    @Autowired
    private UserSettingMapper userSettingMapper;

    @Autowired
    private UserSettingRepository userSettingRepository;

    @Autowired
    private IssueStatusMapper issueStatusMapper;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private SiteMsgUtil siteMsgUtil;

    @Autowired
    private ProjectInfoMapper projectInfoMapper;

    @Autowired
    private DateUtil dateUtil;

    @Autowired
    private SprintWorkCalendarRefMapper sprintWorkCalendarRefMapper;

    @Autowired
    private StateMachineFeignClient stateMachineFeignClient;

    @Autowired
    private IssueFeignClient issueFeignClient;

    @Autowired
    private StateMachineService stateMachineService;

    @Autowired
    private SprintMapper sprintMapper;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private PiMapper piMapper;

    @Autowired
    private PiFeatureRepository piFeatureRepository;

    @Autowired
    private PiFeatureMapper piFeatureMapper;

    @Autowired
    private ArtMapper artMapper;

    @Override
    public void create(Long projectId, String boardName) {
        if (checkName(projectId, boardName)) {
            throw new CommonException("error.boardName.exist");
        }
        BoardE boardResult = createBoard(projectId, boardName);
        boardColumnService.createColumnWithRelateStatus(boardResult);
    }

    private Boolean checkNameUpdate(Long projectId, Long boardId, String boardName) {
        BoardDO boardDO = boardMapper.selectByPrimaryKey(boardId);
        if (boardName.equals(boardDO.getName())) {
            return false;
        }
        BoardDO check = new BoardDO();
        check.setProjectId(projectId);
        check.setName(boardName);
        List<BoardDO> boardDOList = boardMapper.select(check);
        return boardDOList != null && !boardDOList.isEmpty();
    }

    @Override
    public BoardDTO update(Long projectId, Long boardId, BoardDTO boardDTO) {
        if (boardDTO.getName() != null && checkNameUpdate(projectId, boardId, boardDTO.getName())) {
            throw new CommonException("error.boardName.exist");
        }
        BoardValidator.checkUpdateBoard(projectId, boardDTO);
        boardDTO.setBoardId(boardId);
        BoardE boardE = ConvertHelper.convert(boardDTO, BoardE.class);
        return ConvertHelper.convert(boardRepository.update(boardE), BoardDTO.class);
    }

    @Override
    public void delete(Long projectId, Long boardId) {
        BoardColumnDO boardColumnDO = new BoardColumnDO();
        boardColumnDO.setBoardId(boardId);
        List<BoardColumnDO> boardColumnDOList = boardColumnMapper.select(boardColumnDO);
        for (BoardColumnDO column : boardColumnDOList) {
            ColumnStatusRelE columnStatusRelE = new ColumnStatusRelE();
            columnStatusRelE.setColumnId(column.getColumnId());
            columnStatusRelE.setProjectId(projectId);
            columnStatusRelRepository.delete(columnStatusRelE);
            boardColumnRepository.delete(column.getColumnId());
        }
        boardRepository.delete(boardId);
        //删除默认看板UserSetting
        UserSettingDO userSettingDO = new UserSettingDO();
        userSettingDO.setProjectId(projectId);
        userSettingDO.setTypeCode(BOARD);
        userSettingDO.setBoardId(boardId);
        userSettingDO.setUserId(DetailsHelper.getUserDetails().getUserId());
        userSettingMapper.delete(userSettingDO);
        //更新第一个为默认
        List<BoardDTO> boardDTOS = queryByProjectId(projectId);
        if (!boardDTOS.isEmpty()) {
            Long defaultBoardId = boardDTOS.get(0).getBoardId();
            handleUserSetting(defaultBoardId, projectId);
        }
    }

    @Override
    public BoardDTO queryScrumBoardById(Long projectId, Long boardId) {
        BoardDO boardDO = new BoardDO();
        boardDO.setProjectId(projectId);
        boardDO.setBoardId(boardId);
        return ConvertHelper.convert(boardMapper.selectOne(boardDO), BoardDTO.class);
    }

    public JSONObject putColumnData(List<ColumnAndIssueDO> columns) {
        JSONObject columnsData = new JSONObject();
        columnsData.put("columns", columns);
        return columnsData;
    }

    private void addIssueInfos(IssueForBoardDO issue, List<Long> parentIds, List<Long> assigneeIds, List<Long> ids, List<Long> epicIds, Map<Long, PriorityDTO> priorityMap, Map<Long, IssueTypeDTO> issueTypeDTOMap, Map<Long, List<Long>> parentWithSubs) {
        if (issue.getParentIssueId() != null && issue.getParentIssueId() != 0 && !parentIds.contains(issue.getParentIssueId())) {
            parentIds.add(issue.getParentIssueId());
        } else {
            ids.add(issue.getIssueId());
        }
        if (issue.getAssigneeId() != null && !assigneeIds.contains(issue.getAssigneeId())) {
            assigneeIds.add(issue.getAssigneeId());
        }
        if (issue.getEpicId() != null && !epicIds.contains(issue.getEpicId())) {
            epicIds.add(issue.getEpicId());
        }
        if ("sub_task".equals(issue.getTypeCode()) && issue.getParentIssueId() != null) {
            List<Long> subtaskIds = null;
            subtaskIds = parentWithSubs.get(issue.getParentIssueId());
            if (subtaskIds == null) {
                subtaskIds = new ArrayList<>();
            }
            subtaskIds.add(issue.getIssueId());
            parentWithSubs.put(issue.getParentIssueId(), subtaskIds);
        }
        issue.setPriorityDTO(priorityMap.get(issue.getPriorityId()));
        issue.setIssueTypeDTO(issueTypeDTOMap.get(issue.getIssueTypeId()));
        if (issue.getStayDate() != null) {
            issue.setStayDay(DateUtil.differentDaysByMillisecond(issue.getStayDate(), new Date()));
        } else {
            issue.setStayDay(0);
        }
    }

    private void getDatas(List<SubStatus> subStatuses, List<Long> parentIds, List<Long> assigneeIds, List<Long> ids, List<Long> epicIds, Long organizationId, Map<Long, List<Long>> parentWithSubs, Map<Long, IssueTypeDTO> issueTypeDTOMap) {
        Map<Long, PriorityDTO> priorityMap = issueFeignClient.queryByOrganizationId(organizationId).getBody();
        subStatuses.forEach(subStatus -> subStatus.getIssues().forEach(issueForBoardDO -> addIssueInfos(issueForBoardDO, parentIds, assigneeIds, ids, epicIds, priorityMap, issueTypeDTOMap, parentWithSubs)));
    }

    public void putDatasAndSort(List<ColumnAndIssueDO> columns, List<Long> parentIds, List<Long> assigneeIds, Long boardId, List<Long> epicIds, Boolean condition, Long organizationId, Map<Long, List<Long>> parentWithSubss, Map<Long, StatusMapDTO> statusMap, Map<Long, IssueTypeDTO> issueTypeDTOMap) {
        List<Long> issueIds = new ArrayList<>();
        for (ColumnAndIssueDO column : columns) {
            List<SubStatus> subStatuses = column.getSubStatuses();
            fillStatusData(subStatuses, statusMap);
            getDatas(subStatuses, parentIds, assigneeIds, issueIds, epicIds, organizationId, parentWithSubss, issueTypeDTOMap);
            Collections.sort(subStatuses, (o1, o2) -> o2.getIssues().size() - o1.getIssues().size());
        }
        //选择故事泳道选择仅我的任务后，子任务经办人为自己，父任务经办人不为自己的情况
        if (condition) {
            handleParentIdsWithSubIssues(parentIds, issueIds, columns, boardId);
        }
        Collections.sort(parentIds);
        Collections.sort(assigneeIds);
    }

    private void fillStatusData(List<SubStatus> subStatuses, Map<Long, StatusMapDTO> statusMap) {
        for (SubStatus subStatus : subStatuses) {
            StatusMapDTO status = statusMap.get(subStatus.getStatusId());
            subStatus.setCategoryCode(status.getType());
            subStatus.setName(status.getName());
            Collections.sort(subStatus.getIssues(), Comparator.comparing(IssueForBoardDO::getIssueId));
        }
    }

    private void handleParentIdsWithSubIssues(List<Long> parentIds, List<Long> issueIds, List<ColumnAndIssueDO> columns, Long boardId) {
        if (parentIds != null && !parentIds.isEmpty()) {
            List<Long> subNoParentIds = new ArrayList<>();
            parentIds.forEach(id -> {
                if (!issueIds.contains(id)) {
                    subNoParentIds.add(id);
                }
            });
            if (!subNoParentIds.isEmpty()) {
                List<ColumnAndIssueDO> subNoParentColumns = boardColumnMapper.queryColumnsByIssueIds(subNoParentIds, boardId);
                subNoParentColumns.forEach(columnAndIssueDO -> handleSameColumn(columns, columnAndIssueDO));
            }
        }
    }

    private void handleSameColumn(List<ColumnAndIssueDO> columns, ColumnAndIssueDO columnAndIssueDO) {
        Optional<ColumnAndIssueDO> sameColumn = columns.stream().filter(columnAndIssue -> columnAndIssue.getColumnId().equals(columnAndIssueDO.getColumnId()))
                .findFirst();
        if (sameColumn.isPresent()) {
            sameColumn.get().getSubStatuses().forEach(subStatus -> columnAndIssueDO.getSubStatuses().forEach(s -> {
                if (subStatus.getId().equals(s.getId())) {
                    subStatus.getIssues().addAll(s.getIssues());
                }
            }));
        } else {
            columns.add(columnAndIssueDO);
        }
    }


    private SprintDO getActiveSprint(Long projectId) {
        return sprintService.getActiveSprint(projectId);
    }

    private BoardSprintDTO putCurrentSprint(SprintDO activeSprint, Long organizationId) {
        if (activeSprint != null) {
            BoardSprintDTO boardSprintDTO = new BoardSprintDTO();
            boardSprintDTO.setSprintId(activeSprint.getSprintId());
            boardSprintDTO.setSprintName(activeSprint.getSprintName());
            if (activeSprint.getEndDate() != null) {
                Date startDate = new Date();
                if (activeSprint.getStartDate().after(startDate)) {
                    startDate = activeSprint.getStartDate();
                }
                boardSprintDTO.setDayRemain(dateUtil.getDaysBetweenDifferentDate(startDate, activeSprint.getEndDate(),
                        sprintWorkCalendarRefMapper.queryHolidayBySprintIdAndProjectId(activeSprint.getSprintId(), activeSprint.getProjectId()),
                        sprintWorkCalendarRefMapper.queryWorkBySprintIdAndProjectId(activeSprint.getSprintId(), activeSprint.getProjectId()), organizationId));
            }
            return boardSprintDTO;
        }
        return null;
    }

    private String getQuickFilter(List<Long> quickFilterIds) {
        List<String> sqlQuerys = quickFilterMapper.selectSqlQueryByIds(quickFilterIds);
        if (sqlQuerys.isEmpty()) {
            return null;
        }
        StringBuilder sql = new StringBuilder("select issue_id from agile_issue where ");
        int idx = 0;
        for (String filter : sqlQuerys) {
            if (idx == 0) {
                sql.append(" ( " + filter + " ) ");
                idx += 1;
            } else {
                sql.append(" and " + " ( " + filter + " ) ");
            }
        }
        return sql.toString();
    }

    private List<Long> sortAndJudgeCompleted(Long projectId, List<Long> parentIds) {
        if (parentIds != null && !parentIds.isEmpty()) {
            return boardColumnMapper.sortAndJudgeCompleted(projectId, parentIds);
        } else {
            return new ArrayList<>();
        }
    }

    private List<ParentIssueDO> getParentIssues(Long projectId, List<Long> parentIds, Map<Long, StatusMapDTO> statusMap, Map<Long, IssueTypeDTO> issueTypeDTOMap) {
        if (parentIds == null || parentIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<ParentIssueDO> parentIssueDOList = boardColumnMapper.queryParentIssuesByIds(projectId, parentIds);
        for (ParentIssueDO parentIssueDO : parentIssueDOList) {
            parentIssueDO.setStatusMapDTO(statusMap.get(parentIssueDO.getStatusId()));
            parentIssueDO.setIssueTypeDTO(issueTypeDTOMap.get(parentIssueDO.getIssueTypeId()));
        }
        return parentIssueDOList;
    }

    private List<ColumnIssueNumDO> getAllColumnNum(Long projectId, Long boardId, Long activeSprintId) {
        BoardDO boardDO = boardMapper.selectByPrimaryKey(boardId);
        if (!CONTRAINT_NONE.equals(boardDO.getColumnConstraint())) {
            return boardColumnMapper.getAllColumnNum(projectId, boardId, activeSprintId, boardDO.getColumnConstraint());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public JSONObject queryAllData(Long projectId, Long boardId, Long assigneeId, Boolean onlyStory, List<Long> quickFilterIds, Long organizationId, List<Long> assigneeFilterIds) {
        JSONObject jsonObject = new JSONObject(true);
        SprintDO activeSprint = getActiveSprint(projectId);
        Long activeSprintId = null;
        if (activeSprint != null) {
            activeSprintId = activeSprint.getSprintId();
        }
        String filterSql = null;
        if (quickFilterIds != null && !quickFilterIds.isEmpty()) {
            filterSql = getQuickFilter(quickFilterIds);
        }
        List<Long> assigneeIds = new ArrayList<>();
        List<Long> parentIds = new ArrayList<>();
        List<Long> epicIds = new ArrayList<>();
        List<ColumnAndIssueDO> columns = boardColumnMapper.selectColumnsByBoardId(projectId, boardId, activeSprintId, assigneeId, onlyStory, filterSql, assigneeFilterIds);
        Boolean condition = assigneeId != null && onlyStory;
        Map<Long, List<Long>> parentWithSubs = new HashMap<>();
        Map<Long, StatusMapDTO> statusMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
        Map<Long, IssueTypeDTO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
        putDatasAndSort(columns, parentIds, assigneeIds, boardId, epicIds, condition, organizationId, parentWithSubs, statusMap, issueTypeDTOMap);
        jsonObject.put("parentIds", parentIds);
        jsonObject.put("parentIssues", getParentIssues(projectId, parentIds, statusMap, issueTypeDTOMap));
        jsonObject.put("assigneeIds", assigneeIds);
        jsonObject.put("parentWithSubs", parentWithSubs);
        jsonObject.put("parentCompleted", sortAndJudgeCompleted(projectId, parentIds));
        jsonObject.put("epicInfo", !epicIds.isEmpty() ? boardColumnMapper.selectEpicBatchByIds(epicIds) : null);
        jsonObject.put("allColumnNum", getAllColumnNum(projectId, boardId, activeSprintId));
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(assigneeIds, true);
        Comparator<IssueForBoardDO> comparator = Comparator.comparing(IssueForBoardDO::getRank, nullsFirst(naturalOrder()));
        columns.forEach(columnAndIssueDO -> columnAndIssueDO.getSubStatuses().forEach(subStatus -> {
                    subStatus.getIssues().forEach(issueForBoardDO -> {
                        UserMessageDO userMessageDO = usersMap.get(issueForBoardDO.getAssigneeId());
                        String assigneeName = userMessageDO != null ? userMessageDO.getName() : null;
                        String assigneeLoginName = userMessageDO != null ? userMessageDO.getLoginName() : null;
                        String assigneeRealName = userMessageDO != null ? userMessageDO.getRealName() : null;
                        String imageUrl = userMessageDO != null ? userMessageDO.getImageUrl() : null;
                        issueForBoardDO.setAssigneeName(assigneeName);
                        issueForBoardDO.setAssigneeLoginName(assigneeLoginName);
                        issueForBoardDO.setAssigneeRealName(assigneeRealName);
                        issueForBoardDO.setImageUrl(imageUrl);
                    });
                    subStatus.getIssues().sort(comparator);
                }
        ));
        jsonObject.put("columnsData", putColumnData(columns));
        jsonObject.put("currentSprint", putCurrentSprint(activeSprint, organizationId));
        //处理用户默认看板设置，保存最近一次的浏览
        handleUserSetting(boardId, projectId);
        return jsonObject;
    }

    private void handleUserSetting(Long boardId, Long projectId) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        UserSettingDO userSettingDO = new UserSettingDO();
        userSettingDO.setProjectId(projectId);
        userSettingDO.setTypeCode(BOARD);
        userSettingDO.setBoardId(boardId);
        userSettingDO.setUserId(DetailsHelper.getUserDetails().getUserId());
        UserSettingDO query = userSettingMapper.selectOne(userSettingDO);
        if (query == null) {
            userSettingDO.setDefaultBoard(true);
            userSettingDO.setSwimlaneBasedCode("swimlane_none");
            userSettingRepository.create(ConvertHelper.convert(userSettingDO, UserSettingE.class));
            userSettingRepository.updateOtherBoardNoDefault(boardId, projectId, userId);
        } else if (!query.getDefaultBoard()) {
            query.setDefaultBoard(true);
            userSettingRepository.update(ConvertHelper.convert(query, UserSettingE.class));
            userSettingRepository.updateOtherBoardNoDefault(boardId, projectId, userId);
        }
    }

    private BoardE createBoard(Long projectId, String boardName) {
        BoardE boardE = new BoardE();
        boardE.setProjectId(projectId);
        boardE.setColumnConstraint(CONTRAINT_NONE);
        boardE.setDayInColumn(false);
        boardE.setEstimationStatistic(STORY_POINTS);
        boardE.setName(boardName);
        boardE.setSwimlaneBasedCode(PARENT_CHILD);
        return boardRepository.create(boardE);
    }

    @Override
    public void initBoard(Long projectId, String boardName, List<StatusPayload> statusPayloads) {
        BoardE boardResult = createBoard(projectId, boardName);
        boardColumnService.initBoardColumns(projectId, boardResult.getBoardId(), statusPayloads);
    }

    private String convertProjectName(ProjectDTO projectDTO) {
        String projectName = projectDTO.getName();
        return projectName.replaceAll(" ", "%20");
    }

    @Override
    public IssueMoveDTO move(Long projectId, Long issueId, Long transformId, IssueMoveDTO issueMoveDTO, Boolean isDemo) {
        IssueE issueE = ConvertHelper.convert(issueMoveDTO, IssueE.class);
        //执行状态机转换
        if (isDemo) {
            stateMachineService.executeTransformForDemo(projectId, issueId, transformId, issueMoveDTO.getObjectVersionNumber(),
                    SchemeApplyType.AGILE, new InputDTO(issueId, UPDATE_STATUS_MOVE, JSON.toJSONString(handleIssueMoveRank(projectId, issueMoveDTO))));
        } else {
            stateMachineService.executeTransform(projectId, issueId, transformId, issueMoveDTO.getObjectVersionNumber(),
                    SchemeApplyType.AGILE, new InputDTO(issueId, UPDATE_STATUS_MOVE, JSON.toJSONString(handleIssueMoveRank(projectId, issueMoveDTO))));
        }
        IssueDO issueDO = issueMapper.selectByPrimaryKey(issueId);
        IssueMoveDTO result = ConvertHelper.convert(issueDO, IssueMoveDTO.class);

        // 发送消息
        Boolean completed = issueStatusMapper.selectByStatusId(projectId, issueE.getStatusId()).getCompleted();
        if (completed != null && completed && issueDO.getAssigneeId() != null && SchemeApplyType.AGILE.equals(issueDO.getApplyType())) {
            List<Long> userIds = noticeService.queryUserIdsByProjectId(projectId, "issue_solved", ConvertHelper.convert(issueDO, IssueDTO.class));
            ProjectDTO projectDTO = userRepository.queryProject(projectId);
            if (projectDTO == null) {
                throw new CommonException("error.project.notExist");
            }
            StringBuilder url = new StringBuilder();
            String projectName = convertProjectName(projectDTO);
            ProjectInfoDO projectInfoDO = new ProjectInfoDO();
            projectInfoDO.setProjectId(projectId);
            List<ProjectInfoDO> pioList = projectInfoMapper.select(projectInfoDO);
            ProjectInfoDO pio = null;
            if (pioList != null && !pioList.isEmpty()) {
                pio = pioList.get(0);
            }
            String pioCode = (pio == null ? "" : pio.getProjectCode());
            if ("sub_task".equals(issueDO.getTypeCode())) {
                url.append(URL_TEMPLATE1 + projectId + URL_TEMPLATE2 + projectName + URL_TEMPLATE6 + projectDTO.getOrganizationId() + URL_TEMPLATE3 + pioCode + "-" + issueDO.getIssueNum() + URL_TEMPLATE4 + issueDO.getParentIssueId() + URL_TEMPLATE5 + issueDO.getIssueId());
            } else {
                url.append(URL_TEMPLATE1 + projectId + URL_TEMPLATE2 + projectName + URL_TEMPLATE6 + projectDTO.getOrganizationId() + URL_TEMPLATE3 + pioCode + "-" + issueDO.getIssueNum() + URL_TEMPLATE4 + issueDO.getIssueId() + URL_TEMPLATE5 + issueDO.getIssueId());
            }
            String summary = pioCode + "-" + issueDO.getIssueNum() + "-" + issueDO.getSummary();
            Long[] ids = new Long[1];
            ids[0] = issueDO.getAssigneeId();
            List<UserDO> userDOList = userRepository.listUsersByIds(ids);
            String userName = !userDOList.isEmpty() && userDOList.get(0) != null ? userDOList.get(0).getLoginName() + userDOList.get(0).getRealName() : "";
            siteMsgUtil.issueSolve(userIds, userName, summary, url.toString(), issueDO.getAssigneeId(), projectId);
        }
        return result;
    }

    @Override
    public FeatureMoveDTO moveByProgram(Long projectId, Long issueId, Long transformId, FeatureMoveDTO featureMoveDTO) {
        IssueDO issueDO = issueMapper.selectByPrimaryKey(issueId);
        stateMachineService.executeTransform(projectId, issueId, transformId, featureMoveDTO.getObjectVersionNumber(),
                SchemeApplyType.PROGRAM, new InputDTO(issueId, UPDATE_STATUS_MOVE, JSON.toJSONString(handleFeatureMoveRank(projectId, issueDO))));
        issueDO = issueMapper.selectByPrimaryKey(issueId);
        // deal pi of feature
        if (featureMoveDTO.getPiChange() != null && featureMoveDTO.getPiChange()) {
            Long piId = featureMoveDTO.getPiId();
            if (piId != null && piId != 0) {
                if (piFeatureMapper.selectExistByOptions(projectId, issueId)) {
                    piFeatureMapper.deletePfRelationByOptions(projectId, issueId);
                }
                if (!piFeatureMapper.selectGivenExistByOptions(projectId, issueId, piId)) {
                    piFeatureRepository.create(new PiFeatureE(issueId, piId, projectId));
                }
            }
        }
        return ConvertHelper.convert(issueDO, FeatureMoveDTO.class);
    }

    private JSONObject handleFeatureMoveRank(Long projectId, IssueDO issueDO) {
        JSONObject jsonObject = new JSONObject();
//        if (featureMoveDTO.getRank()) {
//            String rank = null;
//            if (featureMoveDTO.getBefore()) {
//                if (featureMoveDTO.getOutsetIssueId() == null || Objects.equals(featureMoveDTO.getOutsetIssueId(), 0L)) {
//                    String minRank = piMapper.queryPiMinRank(projectId, featureMoveDTO.getPiId());
//                    if (minRank == null) {
//                        rank = RankUtil.mid();
//                    } else {
//                        rank = RankUtil.genPre(minRank);
//                    }
//                } else {
//                    String rightRank = issueMapper.queryRankByProgram(projectId, featureMoveDTO.getOutsetIssueId());
//                    if (rightRank == null) {
//                        //处理子任务没有rank的旧数据
//                        rightRank = handleSubIssueNotRank(projectId, featureMoveDTO.getOutsetIssueId(), featureMoveDTO.getPiId());
//                    }
//                    String leftRank = issueMapper.queryLeftRankByProgram(projectId, featureMoveDTO.getPiId(), rightRank);
//                    if (leftRank == null) {
//                        rank = RankUtil.genPre(rightRank);
//                    } else {
//                        rank = RankUtil.between(leftRank, rightRank);
//                    }
//                }
//            } else {
//                String leftRank = issueMapper.queryRankByProgram(projectId, featureMoveDTO.getOutsetIssueId());
//                if (leftRank == null) {
//                    leftRank = handleSubIssueNotRank(projectId, featureMoveDTO.getOutsetIssueId(), featureMoveDTO.getPiId());
//                }
//                String rightRank = issueMapper.queryRightRankByProgram(projectId, featureMoveDTO.getPiId(), leftRank);
//                if (rightRank == null) {
//                    rank = RankUtil.genNext(leftRank);
//                } else {
//                    rank = RankUtil.between(leftRank, rightRank);
//                }
//            }
//            jsonObject.put(RANK, rank);
//        } else {
//            jsonObject.put(RANK, issueDO.getRank());
//        }
        jsonObject.put(RANK, issueDO.getRank());
        jsonObject.put(PROJECT_ID, projectId);
        return jsonObject;
    }

    private JSONObject handleIssueMoveRank(Long projectId, IssueMoveDTO issueMoveDTO) {
        JSONObject jsonObject = new JSONObject();
        if (issueMoveDTO.getRank()) {
            String rank;
            if (issueMoveDTO.getBefore()) {
                if (issueMoveDTO.getOutsetIssueId() == null || Objects.equals(issueMoveDTO.getOutsetIssueId(), 0L)) {
                    String minRank = sprintMapper.queryMinRank(projectId, issueMoveDTO.getSprintId());
                    if (minRank == null) {
                        rank = RankUtil.mid();
                    } else {
                        rank = RankUtil.genPre(minRank);
                    }
                } else {
                    String rightRank = issueMapper.queryRank(projectId, issueMoveDTO.getOutsetIssueId());
                    if (rightRank == null) {
                        //处理子任务没有rank的旧数据
                        rightRank = handleSubIssueNotRank(projectId, issueMoveDTO.getOutsetIssueId(), issueMoveDTO.getSprintId());
                    }
                    String leftRank = issueMapper.queryLeftRank(projectId, issueMoveDTO.getSprintId(), rightRank);
                    if (leftRank == null) {
                        rank = RankUtil.genPre(rightRank);
                    } else {
                        rank = RankUtil.between(leftRank, rightRank);
                    }
                }
            } else {
                String leftRank = issueMapper.queryRank(projectId, issueMoveDTO.getOutsetIssueId());
                if (leftRank == null) {
                    leftRank = handleSubIssueNotRank(projectId, issueMoveDTO.getOutsetIssueId(), issueMoveDTO.getSprintId());
                }
                String rightRank = issueMapper.queryRightRank(projectId, issueMoveDTO.getSprintId(), leftRank);
                if (rightRank == null) {
                    rank = RankUtil.genNext(leftRank);
                } else {
                    rank = RankUtil.between(leftRank, rightRank);
                }
            }
            jsonObject.put(RANK, rank);
            jsonObject.put(PROJECT_ID, projectId);
            return jsonObject;
        } else {
            return null;
        }
    }

    private String handleSubIssueNotRank(Long projectId, Long outsetIssueId, Long sprintId) {
        IssueDO issueDO = issueMapper.selectByPrimaryKey(outsetIssueId);
        issueDO.setRank(sprintMapper.queryMaxRank(projectId, sprintId));
        issueMapper.updateByPrimaryKeySelective(issueDO);
        return issueDO.getRank();
    }

    @Override
    public List<BoardDTO> queryByProjectId(Long projectId) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        return ConvertHelper.convertList(boardMapper.queryByProjectIdWithUser(userId, projectId), BoardDTO.class);
    }

    @Override
    public UserSettingDTO queryUserSettingBoard(Long projectId, Long boardId) {
        UserSettingDO userSettingDO = queryUserSettingBoardByBoardId(projectId, boardId, DetailsHelper.getUserDetails().getUserId());
        if (userSettingDO == null) {
            UserSettingE userSettingE = new UserSettingE();
            userSettingE.setProjectId(projectId);
            userSettingE.setBoardId(boardId);
            userSettingE.setTypeCode(BOARD);
            userSettingE.setUserId(DetailsHelper.getUserDetails().getUserId());
            userSettingE.setSwimlaneBasedCode("swimlane_none");
            userSettingE.setDefaultBoard(false);
            return ConvertHelper.convert(userSettingRepository.create(userSettingE), UserSettingDTO.class);
        } else {
            return ConvertHelper.convert(userSettingDO, UserSettingDTO.class);
        }
    }

    @Override
    public UserSettingDTO updateUserSettingBoard(Long projectId, Long boardId, String swimlaneBasedCode) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        Long userId = customUserDetails.getUserId();
        UserSettingE userSettingE = ConvertHelper.convert(queryUserSettingBoardByBoardId(projectId, boardId, userId), UserSettingE.class);
        if (userSettingE == null) {
            userSettingE = new UserSettingE();
            userSettingE.setDefaultBoard(false);
            userSettingE.setTypeCode(BOARD);
            userSettingE.setProjectId(projectId);
            userSettingE.setBoardId(boardId);
            userSettingE.setUserId(userId);
            userSettingE.setSwimlaneBasedCode(swimlaneBasedCode);
            userSettingE = userSettingRepository.create(userSettingE);
        } else {
            userSettingE.setSwimlaneBasedCode(swimlaneBasedCode);
            userSettingE = userSettingRepository.update(userSettingE);
        }
        return ConvertHelper.convert(userSettingE, UserSettingDTO.class);
    }

    private UserSettingDO queryUserSettingBoardByBoardId(Long projectId, Long boardId, Long userId) {
        UserSettingDO userSettingDO = new UserSettingDO();
        userSettingDO.setProjectId(projectId);
        userSettingDO.setBoardId(boardId);
        userSettingDO.setTypeCode(BOARD);
        userSettingDO.setUserId(userId);
        return userSettingMapper.selectOne(userSettingDO);
    }

    @Override
    public Boolean checkName(Long projectId, String boardName) {
        BoardDO boardDO = new BoardDO();
        boardDO.setProjectId(projectId);
        boardDO.setName(boardName);
        List<BoardDO> boardDOList = boardMapper.select(boardDO);
        return boardDOList != null && !boardDOList.isEmpty();
    }

    @Override
    public void initBoardByProgram(Long projectId, String boardName, List<StatusPayload> statusPayloads) {
        BoardE boardResult = createBoard(projectId, boardName);
        boardColumnService.initBoardColumnsByProgram(projectId, boardResult.getBoardId(), statusPayloads);
    }

    private void setColumnDeatil(List<ColumnAndIssueDO> columns, Map<Long, StatusMapDTO> statusMap, Map<Long, IssueTypeDTO> issueTypeDTOMap) {
        for (ColumnAndIssueDO column : columns) {
            List<SubStatus> subStatuses = column.getSubStatuses();
            fillStatusData(subStatuses, statusMap);
            for (SubStatus subStatus : subStatuses) {
                List<IssueForBoardDO> issueForBoardDOS = subStatus.getIssues();
                for (IssueForBoardDO issueForBoardDO : issueForBoardDOS) {
                    issueForBoardDO.setIssueTypeDTO(issueTypeDTOMap.get(issueForBoardDO.getIssueTypeId()));
                }
            }
            Collections.sort(subStatuses, (o1, o2) -> o2.getIssues().size() - o1.getIssues().size());
        }
    }

    @Override
    public JSONObject queryByOptionsInProgram(Long projectId, Long boardId, Long organizationId) {
        JSONObject result = new JSONObject(true);
        ArtDO activeArtDO = artMapper.selectActiveArt(projectId);
        PiDO piDO = null;
        if (activeArtDO != null) {
            piDO = piMapper.selectActivePi(projectId, activeArtDO.getId());
        }
        Long activePiId = null;
        if (piDO != null) {
            activePiId = piDO.getId();
        }
        List<ColumnAndIssueDO> columns = boardColumnMapper.selectBoardByProgram(projectId, boardId, activePiId);
        // get status map from organization
        Map<Long, StatusMapDTO> statusMap = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
        Map<Long, IssueTypeDTO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
        // reset status info
        setColumnDeatil(columns, statusMap, issueTypeDTOMap);
        columns.sort(Comparator.comparing(ColumnAndIssueDO::getSequence));
        result.put("columnsData", columns);
        result.put("activePi", piDO);
        handleUserSetting(boardId, projectId);
        return result;
    }
}
