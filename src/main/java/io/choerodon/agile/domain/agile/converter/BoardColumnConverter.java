package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.BoardColumnVO;
import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.agile.domain.agile.entity.BoardColumnE;
import io.choerodon.agile.infra.dataobject.BoardColumnDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class BoardColumnConverter implements ConvertorI<BoardColumnE, BoardColumnDTO, BoardColumnVO> {

    @Override
    public BoardColumnE dtoToEntity(BoardColumnVO boardColumnVO) {
        BoardColumnE boardColumnE = new BoardColumnE();
        BeanUtils.copyProperties(boardColumnVO, boardColumnE);
        return boardColumnE;
    }

    @Override
    public BoardColumnVO entityToDto(BoardColumnE boardColumnE) {
        BoardColumnVO boardColumnVO = new BoardColumnVO();
        BeanUtils.copyProperties(boardColumnE, boardColumnVO);
        return boardColumnVO;
    }

    @Override
    public BoardColumnE doToEntity(BoardColumnDTO boardColumnDTO) {
        BoardColumnE boardColumnE = new BoardColumnE();
        BeanUtils.copyProperties(boardColumnDTO, boardColumnE);
        return boardColumnE;
    }

    @Override
    public BoardColumnDTO entityToDo(BoardColumnE boardColumnE) {
        BoardColumnDTO boardColumnDTO = new BoardColumnDTO();
        BeanUtils.copyProperties(boardColumnE, boardColumnDTO);
        return boardColumnDTO;
    }

    @Override
    public BoardColumnVO doToDto(BoardColumnDTO boardColumnDTO) {
        BoardColumnVO boardColumnVO = new BoardColumnVO();
        BeanUtils.copyProperties(boardColumnDTO, boardColumnVO);
        return boardColumnVO;
    }

    @Override
    public BoardColumnDTO dtoToDo(BoardColumnVO boardColumnVO) {
        BoardColumnDTO boardColumnDTO = new BoardColumnDTO();
        BeanUtils.copyProperties(boardColumnVO, boardColumnDTO);
        return boardColumnDTO;
    }
}
