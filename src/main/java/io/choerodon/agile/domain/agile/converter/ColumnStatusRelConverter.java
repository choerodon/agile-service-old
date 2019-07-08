package io.choerodon.agile.domain.agile.converter;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.agile.api.vo.ColumnStatusRelDTO;
import io.choerodon.agile.domain.agile.entity.ColumnStatusRelE;
import io.choerodon.agile.infra.dataobject.ColumnStatusRelDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/16.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class ColumnStatusRelConverter implements ConvertorI<ColumnStatusRelE, ColumnStatusRelDO, ColumnStatusRelDTO> {

    @Override
    public ColumnStatusRelE dtoToEntity(ColumnStatusRelDTO columnStatusRelDTO) {
        ColumnStatusRelE columnStatusRelE = new ColumnStatusRelE();
        BeanUtils.copyProperties(columnStatusRelDTO, columnStatusRelE);
        return columnStatusRelE;
    }

    @Override
    public ColumnStatusRelDTO entityToDto(ColumnStatusRelE columnStatusRelE) {
        ColumnStatusRelDTO columnStatusRelDTO = new ColumnStatusRelDTO();
        BeanUtils.copyProperties(columnStatusRelE, columnStatusRelDTO);
        return columnStatusRelDTO;
    }

    @Override
    public ColumnStatusRelE doToEntity(ColumnStatusRelDO columnStatusRelDO) {
        ColumnStatusRelE columnStatusRelE = new ColumnStatusRelE();
        BeanUtils.copyProperties(columnStatusRelDO, columnStatusRelE);
        return columnStatusRelE;
    }

    @Override
    public ColumnStatusRelDO entityToDo(ColumnStatusRelE columnStatusRelE) {
        ColumnStatusRelDO columnStatusRelDO = new ColumnStatusRelDO();
        BeanUtils.copyProperties(columnStatusRelE, columnStatusRelDO);
        return columnStatusRelDO;
    }

    @Override
    public ColumnStatusRelDTO doToDto(ColumnStatusRelDO columnStatusRelDO) {
        ColumnStatusRelDTO columnStatusRelDTO = new ColumnStatusRelDTO();
        BeanUtils.copyProperties(columnStatusRelDO, columnStatusRelDTO);
        return columnStatusRelDTO;
    }

    @Override
    public ColumnStatusRelDO dtoToDo(ColumnStatusRelDTO columnStatusRelDTO) {
        ColumnStatusRelDO columnStatusRelDO = new ColumnStatusRelDO();
        BeanUtils.copyProperties(columnStatusRelDTO, columnStatusRelDO);
        return columnStatusRelDO;
    }
}
