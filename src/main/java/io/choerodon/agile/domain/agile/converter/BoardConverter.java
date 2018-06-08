package io.choerodon.agile.domain.agile.converter;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.agile.api.dto.BoardDTO;
import io.choerodon.agile.domain.agile.entity.BoardE;
import io.choerodon.agile.infra.dataobject.BoardDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class BoardConverter implements ConvertorI<BoardE, BoardDO, BoardDTO> {

    @Override
    public BoardE dtoToEntity(BoardDTO boardDTO) {
        BoardE boardE = new BoardE();
        BeanUtils.copyProperties(boardDTO, boardE);
        return boardE;
    }

    @Override
    public BoardDTO entityToDto(BoardE boardE) {
        BoardDTO boardDTO = new BoardDTO();
        BeanUtils.copyProperties(boardE, boardDTO);
        return boardDTO;
    }

    @Override
    public BoardE doToEntity(BoardDO boardDO) {
        BoardE boardE = new BoardE();
        BeanUtils.copyProperties(boardDO, boardE);
        return boardE;
    }

    @Override
    public BoardDO entityToDo(BoardE boardE) {
        BoardDO boardDO = new BoardDO();
        BeanUtils.copyProperties(boardE, boardDO);
        return boardDO;
    }

    @Override
    public BoardDTO doToDto(BoardDO boardDO) {
        BoardDTO boardDTO = new BoardDTO();
        BeanUtils.copyProperties(boardDO, boardDTO);
        return boardDTO;
    }

    @Override
    public BoardDO dtoToDo(BoardDTO boardDTO) {
        BoardDO boardDO = new BoardDO();
        BeanUtils.copyProperties(boardDTO, boardDO);
        return boardDO;
    }
}
