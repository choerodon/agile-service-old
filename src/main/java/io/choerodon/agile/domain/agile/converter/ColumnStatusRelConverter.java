package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.infra.dataobject.ColumnStatusRelDTO;
import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.agile.api.vo.ColumnStatusRelVO;
import io.choerodon.agile.domain.agile.entity.ColumnStatusRelE;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/16.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class ColumnStatusRelConverter implements ConvertorI<ColumnStatusRelE, ColumnStatusRelDTO, ColumnStatusRelVO> {

    @Override
    public ColumnStatusRelE dtoToEntity(ColumnStatusRelVO columnStatusRelVO) {
        ColumnStatusRelE columnStatusRelE = new ColumnStatusRelE();
        BeanUtils.copyProperties(columnStatusRelVO, columnStatusRelE);
        return columnStatusRelE;
    }

    @Override
    public ColumnStatusRelVO entityToDto(ColumnStatusRelE columnStatusRelE) {
        ColumnStatusRelVO columnStatusRelVO = new ColumnStatusRelVO();
        BeanUtils.copyProperties(columnStatusRelE, columnStatusRelVO);
        return columnStatusRelVO;
    }

    @Override
    public ColumnStatusRelE doToEntity(ColumnStatusRelDTO columnStatusRelDTO) {
        ColumnStatusRelE columnStatusRelE = new ColumnStatusRelE();
        BeanUtils.copyProperties(columnStatusRelDTO, columnStatusRelE);
        return columnStatusRelE;
    }

    @Override
    public ColumnStatusRelDTO entityToDo(ColumnStatusRelE columnStatusRelE) {
        ColumnStatusRelDTO columnStatusRelDTO = new ColumnStatusRelDTO();
        BeanUtils.copyProperties(columnStatusRelE, columnStatusRelDTO);
        return columnStatusRelDTO;
    }

    @Override
    public ColumnStatusRelVO doToDto(ColumnStatusRelDTO columnStatusRelDTO) {
        ColumnStatusRelVO columnStatusRelVO = new ColumnStatusRelVO();
        BeanUtils.copyProperties(columnStatusRelDTO, columnStatusRelVO);
        return columnStatusRelVO;
    }

    @Override
    public ColumnStatusRelDTO dtoToDo(ColumnStatusRelVO columnStatusRelVO) {
        ColumnStatusRelDTO columnStatusRelDTO = new ColumnStatusRelDTO();
        BeanUtils.copyProperties(columnStatusRelVO, columnStatusRelDTO);
        return columnStatusRelDTO;
    }
}
