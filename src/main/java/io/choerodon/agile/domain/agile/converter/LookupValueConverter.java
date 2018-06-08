package io.choerodon.agile.domain.agile.converter;


import io.choerodon.core.convertor.ConvertorI;
import org.springframework.stereotype.Component;
import org.springframework.beans.BeanUtils;
import io.choerodon.agile.api.dto.LookupValueDTO;
import io.choerodon.agile.infra.dataobject.LookupValueDO;
import io.choerodon.agile.domain.agile.entity.LookupValueE;

/**
 * 敏捷开发code键值
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 09:40:27
 */
@Component
public class LookupValueConverter implements ConvertorI<LookupValueE, LookupValueDO, LookupValueDTO> {

    @Override
    public LookupValueE dtoToEntity(LookupValueDTO lookupValueDTO) {
        LookupValueE lookupValueE = new LookupValueE();
        BeanUtils.copyProperties(lookupValueDTO, lookupValueE);
        return lookupValueE;
    }

    @Override
    public LookupValueE doToEntity(LookupValueDO lookupValueDO) {
        LookupValueE lookupValueE = new LookupValueE();
        BeanUtils.copyProperties(lookupValueDO, lookupValueE);
        return lookupValueE;
    }

    @Override
    public LookupValueDTO entityToDto(LookupValueE lookupValueE) {
        LookupValueDTO lookupValueDTO = new LookupValueDTO();
        BeanUtils.copyProperties(lookupValueE, lookupValueDTO);
        return lookupValueDTO;
    }

    @Override
    public LookupValueDO entityToDo(LookupValueE lookupValueE) {
        LookupValueDO lookupValueDO = new LookupValueDO();
        BeanUtils.copyProperties(lookupValueE, lookupValueDO);
        return lookupValueDO;
    }

    @Override
    public LookupValueDTO doToDto(LookupValueDO lookupValueDO) {
        LookupValueDTO lookupValueDTO = new LookupValueDTO();
        BeanUtils.copyProperties(lookupValueDO, lookupValueDTO);
        return lookupValueDTO;
    }

    @Override
    public LookupValueDO dtoToDo(LookupValueDTO lookupValueDTO) {
        LookupValueDO lookupValueDO = new LookupValueDO();
        BeanUtils.copyProperties(lookupValueDTO, lookupValueDO);
        return lookupValueDO;
    }
}