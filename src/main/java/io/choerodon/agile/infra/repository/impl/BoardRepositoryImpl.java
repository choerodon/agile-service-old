package io.choerodon.agile.infra.repository.impl;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.domain.agile.entity.BoardE;
import io.choerodon.agile.domain.agile.repository.BoardRepository;
import io.choerodon.agile.infra.dataobject.BoardDO;
import io.choerodon.agile.infra.mapper.BoardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class BoardRepositoryImpl implements BoardRepository {

    @Autowired
    private BoardMapper boardMapper;

    @Override
    public BoardE create(BoardE boardE) {
        BoardDO boardDO = ConvertHelper.convert(boardE, BoardDO.class);
        if (boardMapper.insert(boardDO) != 1) {
            throw new CommonException("error.board.insert");
        }
        return ConvertHelper.convert(boardMapper.selectByPrimaryKey(boardDO.getBoardId()), BoardE.class);
    }

    @Override
    public BoardE update(BoardE boardE) {
        BoardDO boardDO = ConvertHelper.convert(boardE, BoardDO.class);
        if (boardMapper.updateByPrimaryKeySelective(boardDO) != 1) {
            throw new CommonException("error.board.update");
        }
        return ConvertHelper.convert(boardMapper.selectByPrimaryKey(boardDO.getBoardId()), BoardE.class);
    }

    @Override
    public void delete(Long id) {
        BoardDO boardDO = boardMapper.selectByPrimaryKey(id);
        if (boardDO == null) {
            throw new CommonException("error.board.get");
        }
        if (boardMapper.delete(boardDO) != 1) {
            throw new CommonException("error.board.delete");
        }
    }
}
