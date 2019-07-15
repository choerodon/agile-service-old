//package io.choerodon.agile.infra.repository.impl;
//
//import io.choerodon.agile.api.vo.ColumnWithMaxMinNumVO;
//import io.choerodon.agile.api.vo.event.RemoveStatusWithProject;
//import io.choerodon.core.convertor.ConvertHelper;
//import io.choerodon.core.exception.CommonException;
//import io.choerodon.agile.domain.agile.entity.BoardColumnE;
//import io.choerodon.agile.infra.repository.BoardColumnRepository;
//import io.choerodon.agile.infra.dataobject.BoardColumnDTO;
//import io.choerodon.agile.infra.mapper.BoardColumnMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
///**
// * Created by HuangFuqiang@choerodon.io on 2018/5/14.
// * Email: fuqianghuang01@gmail.com
// */
//@Component
//public class BoardColumnRepositoryImpl implements BoardColumnRepository {
//
//    @Autowired
//    private BoardColumnMapper boardColumnMapper;
//
//    @Override
//    public BoardColumnE create(BoardColumnE boardColumnE) {
//        BoardColumnDTO boardColumnDTO = ConvertHelper.convert(boardColumnE, BoardColumnDTO.class);
//        if (boardColumnMapper.insert(boardColumnDTO) != 1) {
//            throw new CommonException("error.BoardColumn.insert");
//        }
//        return ConvertHelper.convert(boardColumnMapper.selectByPrimaryKey(boardColumnDTO.getColumnId()), BoardColumnE.class);
//    }
//
//    @Override
//    public BoardColumnE update(BoardColumnE boardColumnE) {
//        BoardColumnDTO boardColumnDTO = ConvertHelper.convert(boardColumnE, BoardColumnDTO.class);
//        if (boardColumnMapper.updateByPrimaryKeySelective(boardColumnDTO) != 1) {
//            throw new CommonException("error.BoardColumn.update");
//        }
//        return ConvertHelper.convert(boardColumnMapper.selectByPrimaryKey(boardColumnDTO.getColumnId()), BoardColumnE.class);
//    }
//
//    @Override
//    public void delete(Long cloumnId) {
//        BoardColumnDTO boardColumnDTO = boardColumnMapper.selectByPrimaryKey(cloumnId);
//        if (boardColumnDTO == null) {
//            throw new CommonException("error.BoardColumn.get");
//        }
//        if (boardColumnMapper.delete(boardColumnDTO) != 1) {
//            throw new CommonException("error.BoardColumn.delete");
//        }
//    }
//
//    @Override
//    public void columnSort(Long projectId, Long boardId, BoardColumnE boardColumnE) {
//        BoardColumnDTO originColumn = boardColumnMapper.selectByPrimaryKey(boardColumnE.getColumnId());
//        try {
//            if (originColumn.getSequence() > boardColumnE.getSequence()) {
//                boardColumnMapper.columnSort(boardId, boardColumnE.getSequence(), originColumn.getSequence());
//            } else if (originColumn.getSequence() < boardColumnE.getSequence()) {
//                boardColumnMapper.columnSortDesc(boardId, boardColumnE.getSequence(), originColumn.getSequence());
//            }
//            update(boardColumnE);
//            BoardColumnDTO boardColumnDTO = new BoardColumnDTO();
//            boardColumnDTO.setProjectId(projectId);
//            boardColumnDTO.setBoardId(boardId);
//            Integer size = boardColumnMapper.select(boardColumnDTO).size();
//            boardColumnMapper.updateColumnCategory(boardId, size);
//            boardColumnMapper.updateColumnColor(boardId, size);
//        } catch (Exception e) {
//            throw new CommonException(e.getMessage());
//        }
//    }
//
//    @Override
//    public void columnSortByProgram(Long projectId, Long boardId, BoardColumnE boardColumnE) {
//        BoardColumnDTO originColumn = boardColumnMapper.selectByPrimaryKey(boardColumnE.getColumnId());
//        try {
//            if (originColumn.getSequence() > boardColumnE.getSequence()) {
//                boardColumnMapper.columnSort(boardId, boardColumnE.getSequence(), originColumn.getSequence());
//            } else if (originColumn.getSequence() < boardColumnE.getSequence()) {
//                boardColumnMapper.columnSortDesc(boardId, boardColumnE.getSequence(), originColumn.getSequence());
//            }
//            update(boardColumnE);
//        } catch (Exception e) {
//            throw new CommonException(e.getMessage());
//        }
//    }
//
//    @Override
//    public BoardColumnE updateMaxAndMinNum(ColumnWithMaxMinNumVO columnWithMaxMinNumVO) {
//        try {
//            boardColumnMapper.updateMaxAndMinNum(columnWithMaxMinNumVO);
//        } catch (Exception e) {
//            throw new CommonException(e.getMessage());
//        }
//        return ConvertHelper.convert(boardColumnMapper.selectByPrimaryKey(columnWithMaxMinNumVO.getColumnId()), BoardColumnE.class);
//    }
//
//    @Override
//    public void updateSequenceWhenDelete(Long projectId, BoardColumnDTO boardColumnDTO) {
//        Long boardId = boardColumnDTO.getBoardId();
//        boardColumnMapper.updateSequenceWhenDelete(boardColumnDTO.getBoardId(), boardColumnDTO.getSequence());
//        BoardColumnDTO update = new BoardColumnDTO();
//        update.setProjectId(projectId);
//        update.setBoardId(boardId);
//        Integer size = boardColumnMapper.select(update).size();
//        boardColumnMapper.updateColumnCategory(boardId, size);
//        boardColumnMapper.updateColumnColor(boardId, size);
//    }
//
//    @Override
//    public void batchDeleteColumnAndStatusRel(List<RemoveStatusWithProject> removeStatusWithProjects) {
//        boardColumnMapper.batchDeleteColumnAndStatusRel(removeStatusWithProjects);
//    }
//}
