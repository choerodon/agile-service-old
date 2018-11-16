package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.api.validator.BoardValidator;
import io.choerodon.agile.app.service.*;
import io.choerodon.agile.domain.agile.entity.BoardE;
import io.choerodon.agile.domain.agile.entity.ColumnStatusRelE;
import io.choerodon.agile.domain.agile.entity.IssueE;
import io.choerodon.agile.domain.agile.entity.UserSettingE;
import io.choerodon.agile.domain.agile.event.StatusPayload;
import io.choerodon.agile.domain.agile.repository.*;
import io.choerodon.agile.infra.common.enums.SchemeApplyType;
import io.choerodon.agile.infra.common.utils.DateUtil;
import io.choerodon.agile.infra.common.utils.SiteMsgUtil;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.feign.StateMachineFeignClient;
import io.choerodon.agile.infra.feign.UserFeignClient;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BoardServiceImpl implements BoardService {

    private static final String CONTRAINT_NONE = "constraint_none";
    private static final String CONTRAINT_ISSUE = "issue";
    private static final String CONTRAINT_ISSUE_WITHOUT_SUBTASK = "issue_without_sub_task";
    private static final String STORY_POINTS = "story_point";
    private static final String PARENT_CHILD = "parent_child";
    private static final String BOARD = "board";
    private static final String URL_TEMPLATE1 = "#/agile/issue?type=project&id=";
    private static final String URL_TEMPLATE2 = "&name=";
    private static final String URL_TEMPLATE3 = "&paramName=";
    private static final String URL_TEMPLATE4 = "&paramIssueId=";
    private static final String URL_TEMPLATE5 = "&paramOpenIssueId=";
    private static final String URL_TEMPLATE6 = "&organizationId=";

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardMapper boardMapper;

    @Autowired
    private BoardColumnService boardColumnService;

    @Autowired
    private BoardColumnMapper boardColumnMapper;

    @Autowired
    private IssueRepository issueRepository;

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
    private UserFeignClient userFeignClient;

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

    @Override
    public void create(Long projectId, String boardName) {
        BoardE boardResult = createBoard(projectId, boardName);
        boardColumnService.createColumnWithRelateStatus(boardResult);
    }

    @Override
    public BoardDTO update(Long projectId, Long boardId, BoardDTO boardDTO) {
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

    private void addIssueInfos(IssueForBoardDO issue, List<Long> parentIds, List<Long> assigneeIds, List<Long> ids, List<Long> epicIds, Map<Long, PriorityDTO> priorityMap, Map<Long, IssueTypeDTO> issueTypeDTOMap) {
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
        issue.setPriorityDTO(priorityMap.get(issue.getPriorityId()));
        issue.setIssueTypeDTO(issueTypeDTOMap.get(issue.getIssueTypeId()));
    }

    private void getDatas(List<SubStatus> subStatuses, List<Long> parentIds, List<Long> assigneeIds, List<Long> ids, List<Long> epicIds, Long organizationId) {
        Map<Long, PriorityDTO> priorityMap = issueFeignClient.queryByOrganizationId(organizationId).getBody();
        Map<Long, IssueTypeDTO> issueTypeDTOMap = issueFeignClient.listIssueTypeMap(organizationId).getBody();
        subStatuses.forEach(subStatus -> subStatus.getIssues().forEach(issueForBoardDO -> addIssueInfos(issueForBoardDO, parentIds, assigneeIds, ids, epicIds, priorityMap, issueTypeDTOMap)));
    }


//    public void putDatasAndSort(List<ColumnAndIssueDO> columns, List<Long> parentIds, List<Long> assigneeIds, Long boardId, List<Long> epicIds, Boolean condition) {
//        List<Long> issueIds = new ArrayList<>();
//        for (ColumnAndIssueDO column : columns) {
//            List<SubStatus> subStatuses = column.getSubStatuses();
//            getDatas(subStatuses, parentIds, assigneeIds, issueIds, epicIds);
//            Collections.sort(subStatuses, (o1, o2) -> o2.getIssues().size() - o1.getIssues().size());
//        }
//        //选择故事泳道选择仅我的任务后，子任务经办人为自己，父任务经办人不为自己的情况
//        if (condition) {
//            handleParentIdsWithSubIssues(parentIds, issueIds, columns, boardId);
//        }
//        Collections.sort(parentIds);
//        Collections.sort(assigneeIds);
//    }

    public void putDatasAndSort(List<ColumnAndIssueDO> columns, List<Long> parentIds, List<Long> assigneeIds, Long boardId, List<Long> epicIds, Boolean condition, Long organizationId) {
        List<Long> issueIds = new ArrayList<>();
        for (ColumnAndIssueDO column : columns) {
            List<SubStatus> subStatuses = column.getSubStatuses();
            fillStatusData(subStatuses, organizationId);
            getDatas(subStatuses, parentIds, assigneeIds, issueIds, epicIds, organizationId);
            Collections.sort(subStatuses, (o1, o2) -> o2.getIssues().size() - o1.getIssues().size());
        }
        //选择故事泳道选择仅我的任务后，子任务经办人为自己，父任务经办人不为自己的情况
        if (condition) {
            handleParentIdsWithSubIssues(parentIds, issueIds, columns, boardId);
        }
        Collections.sort(parentIds);
        Collections.sort(assigneeIds);
    }

    private void fillStatusData(List<SubStatus> subStatuses, Long organizationId) {
        Map<Long, StatusMapDTO> map = stateMachineFeignClient.queryAllStatusMap(organizationId).getBody();
        for (SubStatus subStatus : subStatuses) {
            StatusMapDTO status = map.get(subStatus.getStatusId());
            subStatus.setCategoryCode(status.getType());
            subStatus.setName(status.getName());
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

//    @Override
//    public JSONObject queryAllData(Long projectId, Long boardId, Long assigneeId, Boolean onlyStory, List<Long> quickFilterIds, Long organizationId) {
//        JSONObject jsonObject = new JSONObject(true);
//        SprintDO activeSprint = getActiveSprint(projectId);
//        Long activeSprintId = null;
//        if (activeSprint != null) {
//            activeSprintId = activeSprint.getSprintId();
//        }
//        String filterSql = null;
//        if (quickFilterIds != null && !quickFilterIds.isEmpty()) {
//            filterSql = getQuickFilter(quickFilterIds);
//        }
//        List<Long> assigneeIds = new ArrayList<>();
//        List<Long> parentIds = new ArrayList<>();
//        List<Long> epicIds = new ArrayList<>();
//        List<ColumnAndIssueDO> columns = boardColumnMapper.selectColumnsByBoardId(projectId, boardId, activeSprintId, assigneeId, onlyStory, filterSql);
//        Boolean condition = assigneeId != null && onlyStory;
//        putDatasAndSort(columns, parentIds, assigneeIds, boardId, epicIds, condition);
//        jsonObject.put("parentIds", parentIds);
//        jsonObject.put("assigneeIds", assigneeIds);
//        jsonObject.put("epicInfo", !epicIds.isEmpty() ? boardColumnMapper.selectEpicBatchByIds(epicIds) : null);
//        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(assigneeIds, true);
//        columns.forEach(columnAndIssueDO -> columnAndIssueDO.getSubStatuses().forEach(subStatus -> subStatus.getIssues().forEach(issueForBoardDO -> {
//            String assigneeName = usersMap.get(issueForBoardDO.getAssigneeId()) != null ? usersMap.get(issueForBoardDO.getAssigneeId()).getName() : null;
//            String imageUrl = assigneeName != null ? usersMap.get(issueForBoardDO.getAssigneeId()).getImageUrl() : null;
//            issueForBoardDO.setAssigneeName(assigneeName);
//            issueForBoardDO.setImageUrl(imageUrl);
//        })));
//        jsonObject.put("columnsData", putColumnData(columns));
//        jsonObject.put("currentSprint", putCurrentSprint(activeSprint, organizationId));
//        //处理用户默认看板设置，保存最近一次的浏览
//        handleUserSetting(boardId, projectId);
//        return jsonObject;
//    }

    @Override
    public JSONObject queryAllData(Long projectId, Long boardId, Long assigneeId, Boolean onlyStory, List<Long> quickFilterIds, Long organizationId) {
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
        List<ColumnAndIssueDO> columns = boardColumnMapper.selectColumnsByBoardId(projectId, boardId, activeSprintId, assigneeId, onlyStory, filterSql);
        Boolean condition = assigneeId != null && onlyStory;
        putDatasAndSort(columns, parentIds, assigneeIds, boardId, epicIds, condition, organizationId);
        jsonObject.put("parentIds", parentIds);
        jsonObject.put("assigneeIds", assigneeIds);
        jsonObject.put("epicInfo", !epicIds.isEmpty() ? boardColumnMapper.selectEpicBatchByIds(epicIds) : null);
        Map<Long, UserMessageDO> usersMap = userRepository.queryUsersMap(assigneeIds, true);
        columns.forEach(columnAndIssueDO -> columnAndIssueDO.getSubStatuses().forEach(subStatus -> subStatus.getIssues().forEach(issueForBoardDO -> {
            String assigneeName = usersMap.get(issueForBoardDO.getAssigneeId()) != null ? usersMap.get(issueForBoardDO.getAssigneeId()).getName() : null;
            String imageUrl = assigneeName != null ? usersMap.get(issueForBoardDO.getAssigneeId()).getImageUrl() : null;
            issueForBoardDO.setAssigneeName(assigneeName);
            issueForBoardDO.setImageUrl(imageUrl);
        })));
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

    private void checkNumberContraint(BoardColumnCheckDO boardColumnCheckDO, BoardColumnCheckDO originBoardColumnCheckDO, Long currentStatusId, Long originStatusId) {
        Long currentMaxNum = boardColumnCheckDO.getMaxNum();
        Long currentIssueCount = boardColumnCheckDO.getIssueCount();
        Long originMinNum = originBoardColumnCheckDO.getMinNum();
        Long originIssueCount = originBoardColumnCheckDO.getIssueCount();
        if (originMinNum != null && !originStatusId.equals(currentStatusId) && originIssueCount <= originMinNum) {
            throw new CommonException("error.minNum.cannotReduce", originBoardColumnCheckDO.getName());
        }
        if (currentMaxNum != null && !originStatusId.equals(currentStatusId) && currentIssueCount >= currentMaxNum) {
            throw new CommonException("error.maxNum.cannotAdd", boardColumnCheckDO.getName());
        }
    }

    private void checkColumnContraint(Long projectId, IssueMoveDTO issueMoveDTO, String columnContraint, Long originStatusId) {
        Long statusId = issueMoveDTO.getStatusId();
        SprintDO activeSprint = getActiveSprint(projectId);
        Long activeSprintId = null;
        if (activeSprint != null) {
            activeSprintId = activeSprint.getSprintId();
        }
        if (columnContraint.equals(CONTRAINT_ISSUE)) {
            BoardColumnCheckDO boardColumnCheckDO = boardColumnMapper.selectColumnByColumnId(projectId, issueMoveDTO.getColumnId(), activeSprintId);
            BoardColumnCheckDO originBoardColumnCheckDO = boardColumnMapper.selectColumnByColumnId(projectId, issueMoveDTO.getOriginColumnId(), activeSprintId);
            checkNumberContraint(boardColumnCheckDO, originBoardColumnCheckDO, statusId, originStatusId);
        } else if (columnContraint.equals(CONTRAINT_ISSUE_WITHOUT_SUBTASK)) {
            BoardColumnCheckDO boardColumnCheckDO = boardColumnMapper.selectColumnByColumnIdWithSubIssue(projectId, issueMoveDTO.getColumnId(), activeSprintId);
            BoardColumnCheckDO originBoardColumnCheckDO = boardColumnMapper.selectColumnByColumnIdWithSubIssue(projectId, issueMoveDTO.getOriginColumnId(), activeSprintId);
            checkNumberContraint(boardColumnCheckDO, originBoardColumnCheckDO, statusId, originStatusId);
        }
    }

    private String convertProjectName(ProjectDTO projectDTO) {
        String projectName = projectDTO.getName();
        String result = projectName.replaceAll(" ", "%20");
        return result;
    }

    @Override
    public IssueMoveDTO move(Long projectId, Long issueId, Long transformId, IssueMoveDTO issueMoveDTO) {
        Long boardId = issueMoveDTO.getBoardId();
        IssueDO issueDO = issueMapper.selectByPrimaryKey(issueMoveDTO.getIssueId());
        BoardDO boardDO = boardMapper.selectByPrimaryKey(boardId);
        checkColumnContraint(projectId, issueMoveDTO, boardDO.getColumnConstraint(), issueDO.getStatusId());
        IssueE issueE = ConvertHelper.convert(issueMoveDTO, IssueE.class);
//        IssueMoveDTO result = ConvertHelper.convert(issueRepository.update(issueE, new String[]{"statusId"}), IssueMoveDTO.class);
        //执行状态机转换
        stateMachineService.executeTransform(projectId, issueId, transformId, issueMoveDTO.getObjectVersionNumber(), SchemeApplyType.AGILE);
        issueDO = issueMapper.selectByPrimaryKey(issueId);
        IssueMoveDTO result = ConvertHelper.convert(issueDO, IssueMoveDTO.class);

        // 发送消息
        Boolean completed = issueStatusMapper.selectByStatusId(projectId, issueE.getStatusId()).getCompleted();
        if (completed != null && completed && issueDO.getAssigneeId() != null && !"issue_test".equals(issueDO.getTypeCode())) {
            List<Long> userIds = noticeService.queryUserIdsByProjectId(projectId, "issue_solved", ConvertHelper.convert(issueDO, IssueDTO.class));
            ProjectDTO projectDTO = userRepository.queryProject(projectId);
            if (projectDTO == null) {
                throw new CommonException("error.project.notExist");
            }
            StringBuilder url = new StringBuilder();
            String projectName = convertProjectName(projectDTO);
            if ("sub_task".equals(issueDO.getTypeCode())) {
                IssueDO pIssue = issueMapper.selectByPrimaryKey(issueDO.getParentIssueId());
                String num = "";
                if (pIssue != null) {
                    num = pIssue.getIssueNum();
                }
                url.append(URL_TEMPLATE1 + projectId + URL_TEMPLATE2 + projectName + URL_TEMPLATE6 + projectDTO.getOrganizationId() + URL_TEMPLATE3 + projectDTO.getCode() + "-" + num + URL_TEMPLATE4 + issueDO.getParentIssueId() + URL_TEMPLATE5 + issueDO.getIssueId());
            } else {
                url.append(URL_TEMPLATE1 + projectId + URL_TEMPLATE2 + projectName + URL_TEMPLATE6 + projectDTO.getOrganizationId() + URL_TEMPLATE3 + projectDTO.getCode() + "-" + issueDO.getIssueNum() + URL_TEMPLATE4 + issueDO.getIssueId() + URL_TEMPLATE5 + issueDO.getIssueId());
            }
            ProjectInfoDO projectInfoDO = new ProjectInfoDO();
            projectInfoDO.setProjectId(projectId);
            List<ProjectInfoDO> pioList = projectInfoMapper.select(projectInfoDO);
            ProjectInfoDO pio = null;
            if (pioList != null && !pioList.isEmpty()) {
                pio = pioList.get(0);
            }
            String pioCode = (pio == null ? "" : pio.getProjectCode());
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
}
