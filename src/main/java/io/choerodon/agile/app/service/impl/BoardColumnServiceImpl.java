package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.api.validator.BoardColumnValidator;
import io.choerodon.agile.domain.agile.entity.BoardE;
import io.choerodon.agile.domain.agile.event.StatusPayload;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.dataobject.BoardColumnDO;
import io.choerodon.agile.infra.dataobject.ColumnStatusRelDO;
import io.choerodon.agile.infra.dataobject.ColumnWithStatusRelDO;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.mapper.ColumnStatusRelMapper;
import io.choerodon.agile.infra.mapper.IssueStatusMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.agile.app.service.BoardColumnService;
import io.choerodon.agile.domain.agile.entity.BoardColumnE;
import io.choerodon.agile.domain.agile.entity.ColumnStatusRelE;
import io.choerodon.agile.domain.agile.entity.IssueStatusE;
import io.choerodon.agile.domain.agile.repository.BoardColumnRepository;
import io.choerodon.agile.domain.agile.repository.ColumnStatusRelRepository;
import io.choerodon.agile.domain.agile.repository.IssueStatusRepository;
import io.choerodon.agile.infra.mapper.BoardColumnMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BoardColumnServiceImpl implements BoardColumnService {

    private static final String TODO = "待处理";
    private static final String DOING = "处理中";
    private static final String DONE = "已完成";
    private static final String TODO_CODE = "todo";
    private static final String DOING_CODE = "doing";
    private static final String DONE_CODE = "done";
    private static final Integer POSITION = 0;
    private static final Integer SEQUENCE_ONE = 0;
    private static final Integer SEQUENCE_TWO = 1;
    private static final Integer SEQUENCE_THREE = 2;
    private static final String COLUMN_COLOR_TODO = "column_color_todo";
    private static final String COLUMN_COLOR_DOING = "column_color_doing";
    private static final String COLUMN_COLOR_DONE = "column_color_done";
    private static final String COLUMN_COLOR_NO_STATUS = "column_color_no_status";

    @Autowired
    private BoardColumnRepository boardColumnRepository;

    @Autowired
    private ColumnStatusRelRepository columnStatusRelRepository;

    @Autowired
    private IssueStatusRepository issueStatusRepository;

    @Autowired
    private BoardColumnMapper boardColumnMapper;

    @Autowired
    private ColumnStatusRelMapper columnStatusRelMapper;

    @Autowired
    private IssueStatusMapper issueStatusMapper;

    @Autowired
    private IssueFeignClient issueFeignClient;

    private void updateSequence(BoardColumnDTO boardColumnDTO) {
        List<BoardColumnDO> boardColumnDOList = boardColumnMapper.selectByBoardIdOrderBySequence(boardColumnDTO.getBoardId());
        BoardColumnDO lastColumn = boardColumnDOList.get(boardColumnDOList.size() - 1);
        Integer lastSequence = lastColumn.getSequence();
        lastColumn.setSequence(lastSequence + 1);
        boardColumnRepository.update(ConvertHelper.convert(lastColumn, BoardColumnE.class));
        boardColumnDTO.setSequence(lastSequence);
    }

    private void setColumnProperties(BoardColumnDTO boardColumnDTO,BoardColumnDO boardColumnDO, String categoryDO, String categoryDTO) {
        boardColumnDO.setCategoryCode(categoryDO);
        if (!boardColumnMapper.select(boardColumnDO).isEmpty()) {
            boardColumnDTO.setCategoryCode(categoryDTO);
            updateSequence(boardColumnDTO);
        }
    }

    private void createCheck(BoardColumnDTO boardColumnDTO) {
        BoardColumnDO boardColumnDO = new BoardColumnDO();
        boardColumnDO.setBoardId(boardColumnDTO.getBoardId());
        switch (boardColumnDTO.getCategoryCode()) {
            case DONE_CODE:
                setColumnProperties(boardColumnDTO, boardColumnDO, DONE_CODE, DOING_CODE);
                break;
            case TODO_CODE:
                setColumnProperties(boardColumnDTO, boardColumnDO, TODO_CODE, DOING_CODE);
                break;
            case DOING_CODE:
                boardColumnDO.setCategoryCode(DONE_CODE);
                if (boardColumnMapper.select(boardColumnDO).isEmpty()) {
                    boardColumnDTO.setCategoryCode(DONE_CODE);
                } else {
                    updateSequence(boardColumnDTO);
                }
                break;
            default:
                break;
        }
    }

//    @Override
//    public Boolean checkSameStatusName(Long projectId, String statusName) {
//        return issueStatusRepository.checkSameStatus(projectId, statusName);
//    }

    private void setColumnColor(BoardColumnDTO boardColumnDTO, Boolean checkStatus) {
        if (!checkStatus) {
            switch (boardColumnDTO.getCategoryCode()) {
                case TODO_CODE:
                    boardColumnDTO.setColorCode(COLUMN_COLOR_TODO);
                    break;
                case DOING_CODE:
                    boardColumnDTO.setColorCode(COLUMN_COLOR_DOING);
                    break;
                case DONE_CODE:
                    boardColumnDTO.setColorCode(COLUMN_COLOR_DONE);
                    break;
                default:
                    break;
            }
        } else {
            boardColumnDTO.setColorCode(COLUMN_COLOR_NO_STATUS);
        }
    }

    private Boolean checkColumnStatusExist(Long projectId, Long statusId) {
        ColumnStatusRelDO columnStatusRelDO = new ColumnStatusRelDO();
        columnStatusRelDO.setProjectId(projectId);
        columnStatusRelDO.setStatusId(statusId);
        List<ColumnStatusRelDO> columnStatusRelDOS = columnStatusRelMapper.select(columnStatusRelDO);
        if (columnStatusRelDOS != null && !columnStatusRelDOS.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public BoardColumnDTO create(Long projectId, String categoryCode, BoardColumnDTO boardColumnDTO) {
        BoardColumnValidator.checkCreateBoardColumnDTO(projectId, boardColumnDTO);
        // 创建列
        createCheck(boardColumnDTO);
        StatusInfoDTO statusInfoDTO = new StatusInfoDTO();
        statusInfoDTO.setType(boardColumnDTO.getCategoryCode());
        statusInfoDTO.setName(boardColumnDTO.getName());
        ResponseEntity<StatusInfoDTO> responseEntity = issueFeignClient.createStatusForAgile(projectId, statusInfoDTO);
        if (responseEntity.getStatusCode().value() == 200 && responseEntity.getBody() != null && responseEntity.getBody().getId() != null) {
            Long statusId = responseEntity.getBody().getId();
            Boolean checkStatus = checkColumnStatusExist(projectId, statusId);
            setColumnColor(boardColumnDTO, checkStatus);
            BoardColumnE boardColumnE = boardColumnRepository.create(ConvertHelper.convert(boardColumnDTO, BoardColumnE.class));
            // 创建默认状态
            IssueStatusDO issueStatusDO = new IssueStatusDO();
            issueStatusDO.setProjectId(projectId);
            issueStatusDO.setStatusId(statusId);
            IssueStatusDO res = issueStatusMapper.selectOne(issueStatusDO);
            if (res == null) {
                IssueStatusE issueStatusE = new IssueStatusE();
                issueStatusE.setCategoryCode(categoryCode);
                issueStatusE.setEnable(false);
                issueStatusE.setName(boardColumnDTO.getName());
                issueStatusE.setProjectId(projectId);
                issueStatusE.setStatusId(statusId);
                if (boardColumnDTO.getCategoryCode().equals(DONE_CODE)) {
                    issueStatusE.setCompleted(true);
                } else {
                    issueStatusE.setCompleted(false);
                }
                issueStatusRepository.create(issueStatusE);
            }
            // 创建列与状态关联关系
            if (!checkStatus) {
                ColumnStatusRelE columnStatusRelE = new ColumnStatusRelE();
                columnStatusRelE.setColumnId(boardColumnE.getColumnId());
                columnStatusRelE.setStatusId(statusId);
                columnStatusRelE.setPosition(0);
                columnStatusRelE.setProjectId(projectId);
                columnStatusRelRepository.create(columnStatusRelE);
            }
            return ConvertHelper.convert(boardColumnE, BoardColumnDTO.class);
        } else {
            setColumnColor(boardColumnDTO, true);
            return ConvertHelper.convert(boardColumnRepository.create(ConvertHelper.convert(boardColumnDTO, BoardColumnE.class)), BoardColumnDTO.class);
        }
    }

    @Override
    public BoardColumnDTO update(Long projectId, Long columnId, Long boardId, BoardColumnDTO boardColumnDTO) {
        BoardColumnValidator.checkUpdateBoardColumnDTO(projectId, boardId, boardColumnDTO);
        BoardColumnE boardColumnE = ConvertHelper.convert(boardColumnDTO, BoardColumnE.class);
        return ConvertHelper.convert(boardColumnRepository.update(boardColumnE), BoardColumnDTO.class);
    }

    @Override
    public void delete(Long projectId, Long columnId) {
        BoardColumnDO boardColumnDO = boardColumnMapper.selectByPrimaryKey(columnId);
        BoardColumnValidator.checkDeleteColumn(boardColumnDO);
        // 删除列
        boardColumnRepository.delete(columnId);
        // 取消列下的状态关联，状态归为未对应的状态
        ColumnStatusRelE columnStatusRelE = new ColumnStatusRelE();
        columnStatusRelE.setColumnId(columnId);
        columnStatusRelE.setProjectId(projectId);
        columnStatusRelRepository.delete(columnStatusRelE);
        // 调整列sequence
        boardColumnRepository.updateSequenceWhenDelete(projectId, boardColumnDO);
    }

    @Override
    public BoardColumnDTO queryBoardColumnById(Long projectId, Long columnId) {
        BoardColumnDO boardColumnDO = new BoardColumnDO();
        boardColumnDO.setProjectId(projectId);
        boardColumnDO.setColumnId(columnId);
        return ConvertHelper.convert(boardColumnMapper.selectOne(boardColumnDO), BoardColumnDTO.class);
    }

    private void initColumnWithStatus(Long projectId, Long boardId, String name, String categoryCode, Long statusId, Integer sequence) {
        BoardColumnE column = new BoardColumnE();
        column.setBoardId(boardId);
        column.setName(name);
        column.setProjectId(projectId);
        column.setCategoryCode(categoryCode);
        column.setSequence(sequence);
        switch (categoryCode) {
            case "todo":
                column.setColorCode(COLUMN_COLOR_TODO);
                break;
            case "doing":
                column.setColorCode(COLUMN_COLOR_DOING);
                break;
            case "done":
                column.setColorCode(COLUMN_COLOR_DONE);
                break;
            default:
                column.setColorCode(COLUMN_COLOR_NO_STATUS);
                break;
        }
        BoardColumnE columnE = boardColumnRepository.create(column);
        IssueStatusDO issueStatusDO = new IssueStatusDO();
        issueStatusDO.setProjectId(projectId);
        issueStatusDO.setStatusId(statusId);
        IssueStatusDO res = issueStatusMapper.selectOne(issueStatusDO);
        if (res == null) {
            IssueStatusE issueStatusE = new IssueStatusE();
            issueStatusE.setProjectId(projectId);
            issueStatusE.setStatusId(statusId);
            issueStatusE.setName(name);
            issueStatusE.setEnable(false);
            issueStatusE.setCategoryCode(categoryCode);
            if (categoryCode.equals(DONE_CODE)) {
                issueStatusE.setCompleted(true);
            } else {
                issueStatusE.setCompleted(false);
            }
            issueStatusRepository.create(issueStatusE);
        }
        ColumnStatusRelDO columnStatusRelDO = new ColumnStatusRelDO();
        columnStatusRelDO.setColumnId(columnE.getColumnId());
        columnStatusRelDO.setStatusId(statusId);
        columnStatusRelDO.setProjectId(projectId);
        if (columnStatusRelMapper.select(columnStatusRelDO).isEmpty()) {
            ColumnStatusRelE columnStatusRelE = new ColumnStatusRelE();
            columnStatusRelE.setColumnId(columnE.getColumnId());
            columnStatusRelE.setPosition(POSITION);
            columnStatusRelE.setStatusId(statusId);
            columnStatusRelE.setProjectId(projectId);
            columnStatusRelRepository.create(columnStatusRelE);
        }
    }

    @Override
    public void initBoardColumns(Long projectId, Long boardId, List<StatusPayload> statusPayloads) {
//        initColumnWithStatus(projectId, boardId, TODO, TODO_CODE, SEQUENCE_ONE, COLUMN_COLOR_TODO);
//        initColumnWithStatus(projectId, boardId, DOING, DOING_CODE, SEQUENCE_TWO, COLUMN_COLOR_DOING);
//        initColumnWithStatus(projectId, boardId, DONE, DONE_CODE, SEQUENCE_THREE, COLUMN_COLOR_DONE);
        Integer sequence = 0;
        for (StatusPayload statusPayload : statusPayloads) {
            initColumnWithStatus(projectId, boardId, statusPayload.getStatusName(), statusPayload.getType(), statusPayload.getStatusId(), sequence++);
        }
    }

    @Override
    public void columnSort(Long projectId, ColumnSortDTO columnSortDTO) {
        BoardColumnValidator.checkColumnSort(projectId, columnSortDTO);
        BoardColumnE boardColumnE = ConvertHelper.convert(columnSortDTO, BoardColumnE.class);
        boardColumnRepository.columnSort(projectId, columnSortDTO.getBoardId(), boardColumnE);
    }

    private void relate(Long projectId, Long boardId, String name, String categoryCode, Integer sequence, List<ColumnWithStatusRelDO> columnWithStatusRelDOList, String colorCode) {
        BoardColumnE column = new BoardColumnE();
        column.setBoardId(boardId);
        column.setName(name);
        column.setProjectId(projectId);
        column.setCategoryCode(categoryCode);
        column.setSequence(sequence);
        column.setColorCode(colorCode);
        BoardColumnE columnE = boardColumnRepository.create(column);
        Integer position = 0;
        for (ColumnWithStatusRelDO columnWithStatusRelDO : columnWithStatusRelDOList) {
            if (categoryCode.equals(columnWithStatusRelDO.getCategoryCode())) {
                ColumnStatusRelDO columnStatusRelDO = new ColumnStatusRelDO();
                columnStatusRelDO.setColumnId(columnE.getColumnId());
                columnStatusRelDO.setStatusId(columnWithStatusRelDO.getStatusId());
                columnStatusRelDO.setProjectId(projectId);
                if (columnStatusRelMapper.select(columnStatusRelDO).isEmpty()) {
                    ColumnStatusRelE columnStatusRelE = new ColumnStatusRelE();
                    columnStatusRelE.setColumnId(columnE.getColumnId());
                    columnStatusRelE.setPosition(position++);
                    columnStatusRelE.setStatusId(columnWithStatusRelDO.getStatusId());
                    columnStatusRelE.setProjectId(projectId);
                    columnStatusRelRepository.create(columnStatusRelE);
                }
            }
        }
    }

    @Override
    public void createColumnWithRelateStatus(BoardE boardResult) {
        List<ColumnWithStatusRelDO> columnWithStatusRelDOList = boardColumnMapper.queryColumnStatusRelByProjectId(boardResult.getProjectId());
        Long projectId = boardResult.getProjectId();
        Long boardId = boardResult.getBoardId();
        Map<Long, StatusMapDTO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
        for (ColumnWithStatusRelDO columnWithStatusRelDO : columnWithStatusRelDOList) {
            columnWithStatusRelDO.setCategoryCode(statusMapDTOMap.get(columnWithStatusRelDO.getStatusId()).getType());
        }
        relate(projectId, boardId, TODO, TODO_CODE, SEQUENCE_ONE, columnWithStatusRelDOList, COLUMN_COLOR_TODO);
        relate(projectId, boardId, DOING, DOING_CODE, SEQUENCE_TWO, columnWithStatusRelDOList, COLUMN_COLOR_DOING);
        relate(projectId, boardId, DONE, DONE_CODE, SEQUENCE_THREE, columnWithStatusRelDOList, COLUMN_COLOR_DONE);
    }

    @Override
    public BoardColumnDTO updateColumnContraint(Long projectId, Long columnId, ColumnWithMaxMinNumDTO columnWithMaxMinNumDTO) {
        BoardColumnValidator.checkUpdateColumnContraint(projectId, columnId, columnWithMaxMinNumDTO);
        return ConvertHelper.convert(boardColumnRepository.updateMaxAndMinNum(columnWithMaxMinNumDTO), BoardColumnDTO.class);
    }

}
