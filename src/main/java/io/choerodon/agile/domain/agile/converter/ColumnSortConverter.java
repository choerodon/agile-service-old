package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.ColumnSortVO;
import io.choerodon.agile.domain.agile.entity.BoardColumnE;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/25.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class ColumnSortConverter implements ConvertorI<BoardColumnE, Object, ColumnSortVO> {

    @Override
    public BoardColumnE dtoToEntity(ColumnSortVO columnSortVO) {
        BoardColumnE boardColumnE = new BoardColumnE();
        BeanUtils.copyProperties(columnSortVO, boardColumnE);
        return boardColumnE;
    }

    @Override
    public ColumnSortVO entityToDto(BoardColumnE boardColumnE) {
        ColumnSortVO columnSortVO = new ColumnSortVO();
        BeanUtils.copyProperties(boardColumnE, columnSortVO);
        return columnSortVO;
    }
}
