package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.api.dto.ColumnWithMaxMinNumDTO;
import io.choerodon.agile.domain.agile.event.RemoveStatusWithProject;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.domain.agile.entity.BoardColumnE;
import io.choerodon.agile.domain.agile.repository.BoardColumnRepository;
import io.choerodon.agile.infra.dataobject.BoardColumnDO;
import io.choerodon.agile.infra.mapper.BoardColumnMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class BoardColumnRepositoryImpl implements BoardColumnRepository {

    @Autowired
    private BoardColumnMapper boardColumnMapper;

    @Override
    public BoardColumnE create(BoardColumnE boardColumnE) {
        BoardColumnDO boardColumnDO = ConvertHelper.convert(boardColumnE, BoardColumnDO.class);
        if (boardColumnMapper.insert(boardColumnDO) != 1) {
            throw new CommonException("error.BoardColumn.insert");
        }
        return ConvertHelper.convert(boardColumnMapper.selectByPrimaryKey(boardColumnDO.getColumnId()), BoardColumnE.class);
    }

    @Override
    public BoardColumnE update(BoardColumnE boardColumnE) {
        BoardColumnDO boardColumnDO = ConvertHelper.convert(boardColumnE, BoardColumnDO.class);
        if (boardColumnMapper.updateByPrimaryKeySelective(boardColumnDO) != 1) {
            throw new CommonException("error.BoardColumn.update");
        }
        return ConvertHelper.convert(boardColumnMapper.selectByPrimaryKey(boardColumnDO.getColumnId()), BoardColumnE.class);
    }

    @Override
    public void delete(Long cloumnId) {
        BoardColumnDO boardColumnDO = boardColumnMapper.selectByPrimaryKey(cloumnId);
        if (boardColumnDO == null) {
            throw new CommonException("error.BoardColumn.get");
        }
        if (boardColumnMapper.delete(boardColumnDO) != 1) {
            throw new CommonException("error.BoardColumn.delete");
        }
    }

    @Override
    public void columnSort(Long projectId, Long boardId, BoardColumnE boardColumnE) {
        BoardColumnDO originColumn = boardColumnMapper.selectByPrimaryKey(boardColumnE.getColumnId());
        try {
            if (originColumn.getSequence() > boardColumnE.getSequence()) {
                boardColumnMapper.columnSort(boardId, boardColumnE.getSequence(), originColumn.getSequence());
            } else if (originColumn.getSequence() < boardColumnE.getSequence()) {
                boardColumnMapper.columnSortDesc(boardId, boardColumnE.getSequence(), originColumn.getSequence());
            }
            update(boardColumnE);
            BoardColumnDO boardColumnDO = new BoardColumnDO();
            boardColumnDO.setProjectId(projectId);
            boardColumnDO.setBoardId(boardId);
            Integer size = boardColumnMapper.select(boardColumnDO).size();
            boardColumnMapper.updateColumnCategory(boardId, size);
            boardColumnMapper.updateColumnColor(boardId, size);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public BoardColumnE updateMaxAndMinNum(ColumnWithMaxMinNumDTO columnWithMaxMinNumDTO) {
        try {
            boardColumnMapper.updateMaxAndMinNum(columnWithMaxMinNumDTO);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
        return ConvertHelper.convert(boardColumnMapper.selectByPrimaryKey(columnWithMaxMinNumDTO.getColumnId()), BoardColumnE.class);
    }

    @Override
    public void updateSequenceWhenDelete(Long projectId, BoardColumnDO boardColumnDO) {
        Long boardId = boardColumnDO.getBoardId();
        boardColumnMapper.updateSequenceWhenDelete(boardColumnDO.getBoardId(), boardColumnDO.getSequence());
        BoardColumnDO update = new BoardColumnDO();
        update.setProjectId(projectId);
        update.setBoardId(boardId);
        Integer size = boardColumnMapper.select(update).size();
        boardColumnMapper.updateColumnCategory(boardId, size);
        boardColumnMapper.updateColumnColor(boardId, size);
    }

    @Override
    public void batchDeleteColumnAndStatusRel(List<RemoveStatusWithProject> removeStatusWithProjects) {
        boardColumnMapper.batchDeleteColumnAndStatusRel(removeStatusWithProjects);
    }
}
