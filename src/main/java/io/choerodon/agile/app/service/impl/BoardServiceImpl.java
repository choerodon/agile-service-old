package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.BoardSprintDTO;
import io.choerodon.agile.api.dto.IssueMoveDTO;
import io.choerodon.agile.app.service.SprintService;
import io.choerodon.agile.domain.agile.entity.*;
import io.choerodon.agile.domain.agile.repository.*;
import io.choerodon.agile.infra.common.utils.DateUtil;
import io.choerodon.agile.infra.mapper.*;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.api.dto.BoardDTO;
import io.choerodon.agile.app.service.BoardColumnService;
import io.choerodon.agile.app.service.BoardService;
import io.choerodon.agile.infra.dataobject.*;
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


    @Override
    public void create(Long projectId, String boardName) {
        BoardE boardResult = createBoard(projectId, boardName);
        boardColumnService.createColumnWithRelateStatus(boardResult);
    }

    @Override
    public BoardDTO update(Long projectId, Long boardId, BoardDTO boardDTO) {
        if (!projectId.equals(boardDTO.getProjectId())) {
            throw new CommonException("error.projectId.notEqual");
        }
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

    private void addIssueInfos(IssueForBoardDO issue, List<Long> parentIds, List<Long> assigneeIds, List<Long> ids, List<Long> epicIds) {
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
    }

    private void getDatas(List<SubStatus> subStatuses, List<Long> parentIds, List<Long> assigneeIds, List<Long> ids, List<Long> epicIds) {
        subStatuses.forEach(subStatus -> subStatus.getIssues().forEach(issueForBoardDO -> addIssueInfos(issueForBoardDO, parentIds, assigneeIds, ids, epicIds)));
    }


    public void putDatasAndSort(List<ColumnAndIssueDO> columns, List<Long> parentIds, List<Long> assigneeIds, Long boardId, List<Long> epicIds) {
        //子任务经办人为自己，父任务经办人不为自己的情况
        List<Long> issueIds = new ArrayList<>();
        for (ColumnAndIssueDO column : columns) {
            List<SubStatus> subStatuses = column.getSubStatuses();
            getDatas(subStatuses, parentIds, assigneeIds, issueIds, epicIds);
            Collections.sort(subStatuses, (o1, o2) -> o2.getIssues().size() - o1.getIssues().size());
        }
        handleParentIdsWithSubIssues(parentIds, issueIds, columns, boardId);
        Collections.sort(parentIds);
        Collections.sort(assigneeIds);
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

    private BoardSprintDTO putCurrentSprint(SprintDO activeSprint) {
        if (activeSprint != null) {
            BoardSprintDTO boardSprintDTO = new BoardSprintDTO();
            boardSprintDTO.setSprintId(activeSprint.getSprintId());
            boardSprintDTO.setSprintName(activeSprint.getSprintName());
            if (activeSprint.getEndDate() != null) {
                boardSprintDTO.setDayRemain(DateUtil.differentDaysByMillisecond(new Date(), activeSprint.getEndDate()));
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

    @Override
    public JSONObject queryAllData(Long projectId, Long boardId, Long assigneeId, Boolean onlyStory, List<Long> quickFilterIds) {
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
        putDatasAndSort(columns, parentIds, assigneeIds, boardId, epicIds);
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
        jsonObject.put("currentSprint", putCurrentSprint(activeSprint));
        //处理用户默认看板设置，保存最近一次的浏览
        handleUserSetting(boardId, projectId);
        return jsonObject;
    }

    private void handleUserSetting(Long boardId, Long projectId) {
        UserSettingDO userSettingDO = new UserSettingDO();
        userSettingDO.setProjectId(projectId);
        userSettingDO.setUserId(DetailsHelper.getUserDetails().getUserId());
        UserSettingDO query = userSettingMapper.selectOne(userSettingDO);
        if (query == null) {
            userSettingDO.setDefaultBoardId(boardId);
            userSettingRepository.create(ConvertHelper.convert(userSettingDO, UserSettingE.class));
        } else if (query.getDefaultBoardId() != null && !query.getDefaultBoardId().equals(boardId)) {
            query.setDefaultBoardId(boardId);
            userSettingRepository.update(ConvertHelper.convert(query, UserSettingE.class));
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
    public void initBoard(Long projectId, String boardName) {
        BoardE boardResult = createBoard(projectId, boardName);
        boardColumnService.initBoardColumns(projectId, boardResult.getBoardId());
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

    @Override
    public IssueMoveDTO move(Long projectId, Long issueId, IssueMoveDTO issueMoveDTO) {
        Long boardId = issueMoveDTO.getBoardId();
        IssueDO issueDO = issueMapper.selectByPrimaryKey(issueMoveDTO.getIssueId());
        BoardDO boardDO = boardMapper.selectByPrimaryKey(boardId);
        checkColumnContraint(projectId, issueMoveDTO, boardDO.getColumnConstraint(), issueDO.getStatusId());
        IssueE issueE = ConvertHelper.convert(issueMoveDTO, IssueE.class);
        return ConvertHelper.convert(issueRepository.update(issueE, new String[]{"statusId"}), IssueMoveDTO.class);
    }

    @Override
    public List<BoardDTO> queryByProjectId(Long projectId) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        return ConvertHelper.convertList(boardMapper.queryByProjectIdWithUser(userId, projectId), BoardDTO.class);
    }
}
