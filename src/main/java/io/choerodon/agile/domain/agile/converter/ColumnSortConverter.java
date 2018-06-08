package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.dto.ColumnSortDTO;
import io.choerodon.agile.domain.agile.entity.BoardColumnE;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/25.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class ColumnSortConverter implements ConvertorI<BoardColumnE, Object, ColumnSortDTO> {

    @Override
    public BoardColumnE dtoToEntity(ColumnSortDTO columnSortDTO) {
        BoardColumnE boardColumnE = new BoardColumnE();
        BeanUtils.copyProperties(columnSortDTO, boardColumnE);
        return boardColumnE;
    }

    @Override
    public ColumnSortDTO entityToDto(BoardColumnE boardColumnE) {
        ColumnSortDTO columnSortDTO = new ColumnSortDTO();
        BeanUtils.copyProperties(boardColumnE, columnSortDTO);
        return columnSortDTO;
    }
}
