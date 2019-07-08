package io.choerodon.agile.domain.agile.converter;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.agile.api.vo.BoardColumnDTO;
import io.choerodon.agile.domain.agile.entity.BoardColumnE;
import io.choerodon.agile.infra.dataobject.BoardColumnDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class BoardColumnConverter implements ConvertorI<BoardColumnE, BoardColumnDO, BoardColumnDTO> {

    @Override
    public BoardColumnE dtoToEntity(BoardColumnDTO boardColumnDTO) {
        BoardColumnE boardColumnE = new BoardColumnE();
        BeanUtils.copyProperties(boardColumnDTO, boardColumnE);
        return boardColumnE;
    }

    @Override
    public BoardColumnDTO entityToDto(BoardColumnE boardColumnE) {
        BoardColumnDTO boardColumnDTO = new BoardColumnDTO();
        BeanUtils.copyProperties(boardColumnE, boardColumnDTO);
        return boardColumnDTO;
    }

    @Override
    public BoardColumnE doToEntity(BoardColumnDO boardColumnDO) {
        BoardColumnE boardColumnE = new BoardColumnE();
        BeanUtils.copyProperties(boardColumnDO, boardColumnE);
        return boardColumnE;
    }

    @Override
    public BoardColumnDO entityToDo(BoardColumnE boardColumnE) {
        BoardColumnDO boardColumnDO = new BoardColumnDO();
        BeanUtils.copyProperties(boardColumnE, boardColumnDO);
        return boardColumnDO;
    }

    @Override
    public BoardColumnDTO doToDto(BoardColumnDO boardColumnDO) {
        BoardColumnDTO boardColumnDTO = new BoardColumnDTO();
        BeanUtils.copyProperties(boardColumnDO, boardColumnDTO);
        return boardColumnDTO;
    }

    @Override
    public BoardColumnDO dtoToDo(BoardColumnDTO boardColumnDTO) {
        BoardColumnDO boardColumnDO = new BoardColumnDO();
        BeanUtils.copyProperties(boardColumnDTO, boardColumnDO);
        return boardColumnDO;
    }
}
