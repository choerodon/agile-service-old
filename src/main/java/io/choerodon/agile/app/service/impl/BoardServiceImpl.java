package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.BoardSprintDTO;
import io.choerodon.agile.api.dto.IssueMoveDTO;
import io.choerodon.agile.app.service.SprintService;
import io.choerodon.agile.domain.agile.entity.ColumnStatusRelE;
import io.choerodon.agile.domain.agile.entity.IssueE;
import io.choerodon.agile.domain.agile.repository.BoardColumnRepository;
import io.choerodon.agile.domain.agile.repository.ColumnStatusRelRepository;
import io.choerodon.agile.domain.agile.repository.IssueRepository;
import io.choerodon.agile.infra.common.utils.DateUtil;
import io.choerodon.agile.infra.mapper.IssueMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.api.dto.BoardDTO;
import io.choerodon.agile.app.service.BoardColumnService;
import io.choerodon.agile.app.service.BoardService;
import io.choerodon.agile.domain.agile.entity.BoardE;
import io.choerodon.agile.domain.agile.repository.BoardRepository;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.mapper.BoardColumnMapper;
import io.choerodon.agile.infra.mapper.BoardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class BoardServiceImpl implements BoardService {

    private static final String CONTRAINT_NONE = "constraint_none";
    private static final String CONTRAINT_ISSUE = "issue";
    private static final String CONTRAINT_ISSUE_WITHOUT_SUBTASK = "issue_without_sub_task";
    private static final String STORY_POINTS = "story_point";
    private static final String STORY = "story";

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
    private DateUtil dateUtil;

    @Autowired
    private ColumnStatusRelRepository columnStatusRelRepository;

    @Autowired
    private BoardColumnRepository boardColumnRepository;


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

    private void getParents(List<SubStatus> subStatuses, List<Long> parentIds) {
        for (SubStatus status : subStatuses) {
            for (IssueForBoardDO issue : status.getIssues()) {
                if (issue.getParentIssueId() != null && issue.getParentIssueId() != 0) {
                    if (parentIds.contains(issue.getParentIssueId())) {
                        continue;
                    }
                    parentIds.add(issue.getParentIssueId());
                }
            }
        }
    }

    public List<Long> putParentIdsAndSort(List<ColumnAndIssueDO> columns) {
        List<Long> parentIds = new ArrayList<>();
        for (ColumnAndIssueDO column : columns) {
            List<SubStatus> subStatuses = column.getSubStatuses();
            getParents(subStatuses, parentIds);
            Collections.sort(subStatuses, (o1, o2) -> o2.getIssues().size() - o1.getIssues().size());
        }
        Collections.sort(parentIds);
        return parentIds;
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
                boardSprintDTO.setDayRemain(dateUtil.differentDaysByMillisecond(new Date(),activeSprint.getEndDate()));
            }
            return boardSprintDTO;
        }
        return null;
    }

    @Override
    public JSONObject queryAllData(Long projectId, Long boardId, Long assigneeId, Boolean onlyStory) {
        JSONObject jsonObject = new JSONObject(true);
        SprintDO activeSprint = getActiveSprint(projectId);
        Long activeSprintId = null;
        if (activeSprint != null) {
            activeSprintId = activeSprint.getSprintId();
        }
        List<ColumnAndIssueDO> columns = boardColumnMapper.selectColumnsByBoardId(projectId, boardId, activeSprintId, assigneeId, onlyStory);
        jsonObject.put("parentIds", putParentIdsAndSort(columns));
        jsonObject.put("columnsData", putColumnData(columns));
        jsonObject.put("currentSprint",putCurrentSprint(activeSprint));
        return jsonObject;
    }

    private BoardE createBoard(Long projectId, String boardName) {
        BoardE boardE = new BoardE();
        boardE.setProjectId(projectId);
        boardE.setColumnConstraint(CONTRAINT_NONE);
        boardE.setDayInColumn(false);
        boardE.setEstimationStatistic(STORY_POINTS);
        boardE.setName(boardName);
        boardE.setSwimlaneBasedCode(STORY);
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
            throw new CommonException("error.minNum.cannotReduce");
        }
        if (currentMaxNum != null && !originStatusId.equals(currentStatusId) && currentIssueCount >= currentMaxNum) {
            throw new CommonException("error.manNum.cannotAdd");
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
        checkColumnContraint(projectId, issueMoveDTO, boardDO.getColumnConstraint(),issueDO.getStatusId());
        IssueE issueE = ConvertHelper.convert(issueMoveDTO, IssueE.class);
        return ConvertHelper.convert(issueRepository.updateSelective(issueE), IssueMoveDTO.class);
    }

    @Override
    public List<BoardDTO> queryByProjectId(Long projectId) {
        BoardDO boardDO = new BoardDO();
        boardDO.setProjectId(projectId);
        return ConvertHelper.convertList(boardMapper.select(boardDO), BoardDTO.class);
    }
}
