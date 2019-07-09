package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.api.validator.BoardColumnValidator;
import io.choerodon.agile.app.service.BoardColumnService;
import io.choerodon.agile.domain.agile.entity.BoardColumnE;
import io.choerodon.agile.domain.agile.entity.ColumnStatusRelE;
import io.choerodon.agile.domain.agile.entity.IssueStatusE;
import io.choerodon.agile.api.vo.event.StatusPayload;
import io.choerodon.agile.infra.common.utils.RedisUtil;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.infra.repository.BoardColumnRepository;
import io.choerodon.agile.infra.repository.ColumnStatusRelRepository;
import io.choerodon.agile.infra.repository.IssueStatusRepository;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.mapper.BoardColumnMapper;
import io.choerodon.agile.infra.mapper.ColumnStatusRelMapper;
import io.choerodon.agile.infra.mapper.IssueStatusMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
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
    private static final String PREPARE_CODE = "prepare";
    private static final Integer POSITION = 0;
    private static final Integer SEQUENCE_ONE = 0;
    private static final Integer SEQUENCE_TWO = 1;
    private static final Integer SEQUENCE_THREE = 2;
    private static final String COLUMN_COLOR_TODO = "column_color_todo";
    private static final String COLUMN_COLOR_DOING = "column_color_doing";
    private static final String COLUMN_COLOR_DONE = "column_color_done";
    private static final String COLUMN_COLOR_NO_STATUS = "column_color_no_status";
    private static final String COLUMN_COLOR_PREPARE = "column_color_prepare";
    private static final String APPLY_TYPE_PROGRAM = "program";

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

    @Autowired
    private RedisUtil redisUtil;


    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    private void updateSequence(BoardColumnVO boardColumnVO) {
        List<BoardColumnDTO> boardColumnDTOList = boardColumnMapper.selectByBoardIdOrderBySequence(boardColumnVO.getBoardId());
        BoardColumnDTO lastColumn = boardColumnDTOList.get(boardColumnDTOList.size() - 1);
        Integer lastSequence = lastColumn.getSequence();
        lastColumn.setSequence(lastSequence + 1);
//        boardColumnRepository.update(ConvertHelper.convert(lastColumn, BoardColumnE.class));
        if (boardColumnMapper.updateByPrimaryKeySelective(lastColumn) != 1) {
            throw new CommonException("error.BoardColumn.update");
        }
        boardColumnVO.setSequence(lastSequence);
    }

    private void setColumnProperties(BoardColumnVO boardColumnVO, BoardColumnDTO boardColumnDTO, String categoryDO, String categoryDTO) {
        boardColumnDTO.setCategoryCode(categoryDO);
        if (!boardColumnMapper.select(boardColumnDTO).isEmpty()) {
            boardColumnVO.setCategoryCode(categoryDTO);
            updateSequence(boardColumnVO);
        }
    }

    private void createCheck(BoardColumnVO boardColumnVO) {
        BoardColumnDTO boardColumnDTO = new BoardColumnDTO();
        boardColumnDTO.setBoardId(boardColumnVO.getBoardId());
        switch (boardColumnVO.getCategoryCode()) {
            case DONE_CODE:
                setColumnProperties(boardColumnVO, boardColumnDTO, DONE_CODE, DOING_CODE);
                break;
            case TODO_CODE:
                setColumnProperties(boardColumnVO, boardColumnDTO, TODO_CODE, DOING_CODE);
                break;
            case DOING_CODE:
                boardColumnDTO.setCategoryCode(DONE_CODE);
                if (boardColumnMapper.select(boardColumnDTO).isEmpty()) {
                    boardColumnVO.setCategoryCode(DONE_CODE);
                } else {
                    updateSequence(boardColumnVO);
                }
                break;
            default:
                break;
        }
    }

    private void setColumnColor(BoardColumnVO boardColumnVO, Boolean checkStatus) {
        if (!checkStatus) {
            switch (boardColumnVO.getCategoryCode()) {
                case PREPARE_CODE:
                    boardColumnVO.setColorCode(COLUMN_COLOR_PREPARE);
                    break;
                case TODO_CODE:
                    boardColumnVO.setColorCode(COLUMN_COLOR_TODO);
                    break;
                case DOING_CODE:
                    boardColumnVO.setColorCode(COLUMN_COLOR_DOING);
                    break;
                case DONE_CODE:
                    boardColumnVO.setColorCode(COLUMN_COLOR_DONE);
                    break;
                default:
                    break;
            }
        } else {
            boardColumnVO.setColorCode(COLUMN_COLOR_NO_STATUS);
        }
    }

    private Boolean checkColumnStatusExist(Long projectId, Long statusId) {
        ColumnStatusRelDTO columnStatusRelDTO = new ColumnStatusRelDTO();
        columnStatusRelDTO.setProjectId(projectId);
        columnStatusRelDTO.setStatusId(statusId);
        List<ColumnStatusRelDTO> columnStatusRelDTOS = columnStatusRelMapper.select(columnStatusRelDTO);
        return columnStatusRelDTOS != null && !columnStatusRelDTOS.isEmpty();
    }


    @Override
    public BoardColumnVO create(Long projectId, String categoryCode, String applyType, BoardColumnVO boardColumnVO) {
        BoardColumnValidator.checkCreateBoardColumnDTO(projectId, boardColumnVO);
        // 创建列
        if (!APPLY_TYPE_PROGRAM.equals(applyType)) {
            createCheck(boardColumnVO);
        }
        StatusInfoDTO statusInfoDTO = new StatusInfoDTO();
        statusInfoDTO.setType(categoryCode);
        statusInfoDTO.setName(boardColumnVO.getName());
        ResponseEntity<StatusInfoDTO> responseEntity = issueFeignClient.createStatusForAgile(projectId, applyType, statusInfoDTO);
        if (responseEntity.getStatusCode().value() == 200 && responseEntity.getBody() != null && responseEntity.getBody().getId() != null) {
            Long statusId = responseEntity.getBody().getId();
            Boolean checkStatus = checkColumnStatusExist(projectId, statusId);
            setColumnColor(boardColumnVO, checkStatus);
//            BoardColumnE boardColumnE = boardColumnRepository.create(ConvertHelper.convert(boardColumnVO, BoardColumnE.class));
            BoardColumnDTO boardColumnDTO = modelMapper.map(boardColumnVO, BoardColumnDTO.class);
            if (boardColumnMapper.insert(boardColumnDTO) != 1) {
                throw new CommonException("error.BoardColumn.insert");
            }
            // 创建默认状态
            IssueStatusDO issueStatusDO = new IssueStatusDO();
            issueStatusDO.setProjectId(projectId);
            issueStatusDO.setStatusId(statusId);
            IssueStatusDO res = issueStatusMapper.selectOne(issueStatusDO);
            if (res == null) {
                IssueStatusE issueStatusE = new IssueStatusE();
                issueStatusE.setCategoryCode(categoryCode);
                issueStatusE.setEnable(false);
                issueStatusE.setName(boardColumnVO.getName());
                issueStatusE.setProjectId(projectId);
                issueStatusE.setStatusId(statusId);
                if (boardColumnVO.getCategoryCode().equals(DONE_CODE)) {
                    issueStatusE.setCompleted(true);
                } else {
                    issueStatusE.setCompleted(false);
                }
                issueStatusRepository.create(issueStatusE);
            }
            // 创建列与状态关联关系
            if (!checkStatus) {
                ColumnStatusRelE columnStatusRelE = new ColumnStatusRelE();
                columnStatusRelE.setColumnId(boardColumnDTO.getColumnId());
                columnStatusRelE.setStatusId(statusId);
                columnStatusRelE.setPosition(0);
                columnStatusRelE.setProjectId(projectId);
                columnStatusRelRepository.create(columnStatusRelE);
            }
            return boardColumnVO;
        } else {
            setColumnColor(boardColumnVO, true);
            BoardColumnDTO boardColumnDTO = modelMapper.map(boardColumnVO, BoardColumnDTO.class);
            if (boardColumnMapper.insert(boardColumnDTO) != 1) {
                throw new CommonException("error.BoardColumn.insert");
            }
            return boardColumnVO;
        }
    }

    @Override
    public BoardColumnVO update(Long projectId, Long columnId, Long boardId, BoardColumnVO boardColumnVO) {
        BoardColumnValidator.checkUpdateBoardColumnDTO(projectId, boardId, boardColumnVO);
//        BoardColumnE boardColumnE = ConvertHelper.convert(boardColumnVO, BoardColumnE.class);
        BoardColumnDTO boardColumnDTO = modelMapper.map(boardColumnVO, BoardColumnDTO.class);
        if (boardColumnMapper.updateByPrimaryKeySelective(boardColumnDTO) != 1) {
            throw new CommonException("error.BoardColumn.update");
        }
        return modelMapper.map(boardColumnMapper.selectByPrimaryKey(boardColumnDTO.getColumnId()), BoardColumnVO.class);
    }

    @Override
    public void delete(Long projectId, Long columnId) {
        BoardColumnDTO boardColumnDTO = boardColumnMapper.selectByPrimaryKey(columnId);
        BoardColumnValidator.checkDeleteColumn(boardColumnDTO);
        // 删除列
//        boardColumnRepository.delete(columnId);
        if (boardColumnMapper.deleteByPrimaryKey(columnId) != 1) {
            throw new CommonException("error.BoardColumn.delete");
        }
        // 取消列下的状态关联，状态归为未对应的状态
//        ColumnStatusRelE columnStatusRelE = new ColumnStatusRelE();
//        columnStatusRelE.setColumnId(columnId);
//        columnStatusRelE.setProjectId(projectId);
//        columnStatusRelRepository.delete(columnStatusRelE);
        ColumnStatusRelDTO columnStatusRelDTO = new ColumnStatusRelDTO();
        columnStatusRelDTO.setColumnId(columnId);
        columnStatusRelDTO.setProjectId(projectId);
        if (columnStatusRelMapper.select(columnStatusRelDTO).isEmpty()) {
            return;
        }
        if (columnStatusRelMapper.delete(columnStatusRelDTO) == 0) {
            throw new CommonException("error.ColumnStatus.delete");
        }
        redisUtil.deleteRedisCache(new String[]{"Agile:CumulativeFlowDiagram" + columnStatusRelDTO.getProjectId() + ':' + "*"});
        // 调整列sequence
        updateSequenceWhenDelete(projectId, boardColumnDTO);
    }

    public void updateSequenceWhenDelete(Long projectId, BoardColumnDTO boardColumnDTO) {
        Long boardId = boardColumnDTO.getBoardId();
        boardColumnMapper.updateSequenceWhenDelete(boardColumnDTO.getBoardId(), boardColumnDTO.getSequence());
        BoardColumnDTO update = new BoardColumnDTO();
        update.setProjectId(projectId);
        update.setBoardId(boardId);
        Integer size = boardColumnMapper.select(update).size();
        boardColumnMapper.updateColumnCategory(boardId, size);
        boardColumnMapper.updateColumnColor(boardId, size);
    }

    @Override
    public void deleteProgramBoardColumn(Long projectId, Long columnId) {
        BoardColumnDTO boardColumnDTO = boardColumnMapper.selectByPrimaryKey(columnId);
        BoardColumnValidator.checkDeleteColumn(boardColumnDTO);
        // 删除列
//        boardColumnRepository.delete(columnId);
        if (boardColumnMapper.deleteByPrimaryKey(columnId) != 1) {
            throw new CommonException("error.BoardColumn.delete");
        }
        // 取消列下的状态关联，状态归为未对应的状态
//        ColumnStatusRelE columnStatusRelE = new ColumnStatusRelE();
//        columnStatusRelE.setColumnId(columnId);
//        columnStatusRelE.setProjectId(projectId);
//        columnStatusRelRepository.delete(columnStatusRelE);
        ColumnStatusRelDTO columnStatusRelDTO = new ColumnStatusRelDTO();
        columnStatusRelDTO.setColumnId(columnId);
        columnStatusRelDTO.setProjectId(projectId);
        if (columnStatusRelMapper.select(columnStatusRelDTO).isEmpty()) {
            return;
        }
        if (columnStatusRelMapper.delete(columnStatusRelDTO) == 0) {
            throw new CommonException("error.ColumnStatus.delete");
        }
    }

    @Override
    public BoardColumnVO queryBoardColumnById(Long projectId, Long columnId) {
        BoardColumnDTO boardColumnDTO = new BoardColumnDTO();
        boardColumnDTO.setProjectId(projectId);
        boardColumnDTO.setColumnId(columnId);
        return modelMapper.map(boardColumnMapper.selectOne(boardColumnDTO), BoardColumnVO.class);
    }

    private void initColumnWithStatus(Long projectId, Long boardId, String name, String categoryCode, Long statusId, Integer sequence) {
        BoardColumnE column = new BoardColumnE();
        column.setBoardId(boardId);
        column.setName(name);
        column.setProjectId(projectId);
        column.setCategoryCode(categoryCode);
        column.setSequence(sequence);
        switch (categoryCode) {
            case PREPARE_CODE:
                column.setColorCode(COLUMN_COLOR_PREPARE);
                break;
            case TODO_CODE:
                column.setColorCode(COLUMN_COLOR_TODO);
                break;
            case DOING_CODE:
                column.setColorCode(COLUMN_COLOR_DOING);
                break;
            case DONE_CODE:
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
        ColumnStatusRelDTO columnStatusRelDTO = new ColumnStatusRelDTO();
        columnStatusRelDTO.setColumnId(columnE.getColumnId());
        columnStatusRelDTO.setStatusId(statusId);
        columnStatusRelDTO.setProjectId(projectId);
        if (columnStatusRelMapper.select(columnStatusRelDTO).isEmpty()) {
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
        Integer sequence = 0;
        for (StatusPayload statusPayload : statusPayloads) {
            initColumnWithStatus(projectId, boardId, statusPayload.getStatusName(), statusPayload.getType(), statusPayload.getStatusId(), sequence++);
        }
    }

    @Override
    public void columnSort(Long projectId, ColumnSortVO columnSortVO) {
        BoardColumnValidator.checkColumnSort(projectId, columnSortVO);
//        BoardColumnE boardColumnE = ConvertHelper.convert(columnSortVO, BoardColumnE.class);
//        boardColumnRepository.columnSort(projectId, columnSortVO.getBoardId(), boardColumnE);
        BoardColumnDTO originColumn = boardColumnMapper.selectByPrimaryKey(columnSortVO.getColumnId());
        try {
            if (originColumn.getSequence() > columnSortVO.getSequence()) {
                boardColumnMapper.columnSort(columnSortVO.getBoardId(), columnSortVO.getSequence(), originColumn.getSequence());
            } else if (originColumn.getSequence() < columnSortVO.getSequence()) {
                boardColumnMapper.columnSortDesc(columnSortVO.getBoardId(), columnSortVO.getSequence(), originColumn.getSequence());
            }
//            update(boardColumnE);
            if (boardColumnMapper.updateByPrimaryKeySelective(modelMapper.map(columnSortVO, BoardColumnDTO.class)) != 1) {
                throw new CommonException("error.BoardColumn.update");
            }
            BoardColumnDTO boardColumnDTO = new BoardColumnDTO();
            boardColumnDTO.setProjectId(projectId);
            boardColumnDTO.setBoardId(columnSortVO.getBoardId());
            Integer size = boardColumnMapper.select(boardColumnDTO).size();
            boardColumnMapper.updateColumnCategory(columnSortVO.getBoardId(), size);
            boardColumnMapper.updateColumnColor(columnSortVO.getBoardId(), size);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public void columnSortByProgram(Long projectId, ColumnSortVO columnSortVO) {
        BoardColumnValidator.checkColumnSort(projectId, columnSortVO);
        BoardColumnDTO boardColumnDTO = modelMapper.map(columnSortVO, BoardColumnDTO.class);
        columnSortByProgram(columnSortVO.getBoardId(), boardColumnDTO);
    }

    public void columnSortByProgram(Long boardId, BoardColumnDTO boardColumnDTO) {
        BoardColumnDTO originColumn = boardColumnMapper.selectByPrimaryKey(boardColumnDTO.getColumnId());
        try {
            if (originColumn.getSequence() > boardColumnDTO.getSequence()) {
                boardColumnMapper.columnSort(boardId, boardColumnDTO.getSequence(), originColumn.getSequence());
            } else if (originColumn.getSequence() < boardColumnDTO.getSequence()) {
                boardColumnMapper.columnSortDesc(boardId, boardColumnDTO.getSequence(), originColumn.getSequence());
            }
            if (boardColumnMapper.updateByPrimaryKeySelective(boardColumnDTO) != 1) {
                throw new CommonException("error.BoardColumn.update");
            }
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    private void relate(Long projectId, Long boardId, String name, String categoryCode, Integer sequence, List<ColumnWithStatusRelDTO> columnWithStatusRelDTOList, String colorCode) {
        BoardColumnDTO column = new BoardColumnDTO();
        column.setBoardId(boardId);
        column.setName(name);
        column.setProjectId(projectId);
        column.setCategoryCode(categoryCode);
        column.setSequence(sequence);
        column.setColorCode(colorCode);
//        BoardColumnE columnE = boardColumnRepository.create(column);
        if (boardColumnMapper.insert(column) != 1) {
            throw new CommonException("error.BoardColumn.insert");
        }
        BoardColumnDTO boardColumnDTO = boardColumnMapper.selectByPrimaryKey(column.getColumnId());
        Integer position = 0;
        for (ColumnWithStatusRelDTO columnWithStatusRelDTO : columnWithStatusRelDTOList) {
            if (categoryCode.equals(columnWithStatusRelDTO.getCategoryCode())) {
                ColumnStatusRelDTO columnStatusRelDTO = new ColumnStatusRelDTO();
                columnStatusRelDTO.setColumnId(boardColumnDTO.getColumnId());
                columnStatusRelDTO.setStatusId(columnWithStatusRelDTO.getStatusId());
                columnStatusRelDTO.setProjectId(projectId);
                if (columnStatusRelMapper.select(columnStatusRelDTO).isEmpty()) {
                    ColumnStatusRelDTO columnStatusRel = new ColumnStatusRelDTO();
                    columnStatusRel.setColumnId(boardColumnDTO.getColumnId());
                    columnStatusRel.setPosition(position++);
                    columnStatusRel.setStatusId(columnWithStatusRelDTO.getStatusId());
                    columnStatusRel.setProjectId(projectId);
//                    columnStatusRelRepository.create(columnStatusRelE);
                    if (columnStatusRelMapper.insert(columnStatusRel) != 1) {
                        throw new CommonException("error.ColumnStatus.insert");
                    }
                }
            }
        }
    }

    @Override
    public void createColumnWithRelateStatus(BoardDTO boardResult) {
        List<ColumnWithStatusRelDTO> columnWithStatusRelDTOList = boardColumnMapper.queryColumnStatusRelByProjectId(boardResult.getProjectId());
        Long projectId = boardResult.getProjectId();
        Long boardId = boardResult.getBoardId();
        Map<Long, StatusMapDTO> statusMapDTOMap = ConvertUtil.getIssueStatusMap(projectId);
        for (ColumnWithStatusRelDTO columnWithStatusRelDTO : columnWithStatusRelDTOList) {
            columnWithStatusRelDTO.setCategoryCode(statusMapDTOMap.get(columnWithStatusRelDTO.getStatusId()).getType());
        }
        relate(projectId, boardId, TODO, TODO_CODE, SEQUENCE_ONE, columnWithStatusRelDTOList, COLUMN_COLOR_TODO);
        relate(projectId, boardId, DOING, DOING_CODE, SEQUENCE_TWO, columnWithStatusRelDTOList, COLUMN_COLOR_DOING);
        relate(projectId, boardId, DONE, DONE_CODE, SEQUENCE_THREE, columnWithStatusRelDTOList, COLUMN_COLOR_DONE);
    }

    @Override
    public BoardColumnVO updateColumnContraint(Long projectId, Long columnId, ColumnWithMaxMinNumVO columnWithMaxMinNumVO) {
        BoardColumnValidator.checkUpdateColumnContraint(projectId, columnId, columnWithMaxMinNumVO);
        try {
            boardColumnMapper.updateMaxAndMinNum(columnWithMaxMinNumVO);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
        return modelMapper.map(boardColumnMapper.selectByPrimaryKey(columnWithMaxMinNumVO.getColumnId()), BoardColumnVO.class);
    }

    @Override
    public void initBoardColumnsByProgram(Long projectId, Long boardId, List<StatusPayload> statusPayloads) {
        Integer sequence = 0;
        for (StatusPayload statusPayload : statusPayloads) {
            initColumnWithStatus(projectId, boardId, statusPayload.getStatusName(), statusPayload.getType(), statusPayload.getStatusId(), sequence++);
        }
    }

}
