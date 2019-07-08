package io.choerodon.agile.domain.agile.converter;


import io.choerodon.core.convertor.ConvertorI;
import org.springframework.stereotype.Component;
import org.springframework.beans.BeanUtils;
import io.choerodon.agile.api.vo.LookupTypeDTO;
import io.choerodon.agile.infra.dataobject.LookupTypeDO;
import io.choerodon.agile.domain.agile.entity.LookupTypeE;

/**
 * 敏捷开发code键值类型
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 10:13:37
 */
@Component
public class LookupTypeConverter implements ConvertorI<LookupTypeE, LookupTypeDO, LookupTypeDTO> {

    @Override
    public LookupTypeE dtoToEntity(LookupTypeDTO lookupTypeDTO) {
        LookupTypeE lookupTypeE = new LookupTypeE();
        BeanUtils.copyProperties(lookupTypeDTO, lookupTypeE);
        return lookupTypeE;
    }

    @Override
    public LookupTypeE doToEntity(LookupTypeDO lookupTypeDO) {
        LookupTypeE lookupTypeE = new LookupTypeE();
        BeanUtils.copyProperties(lookupTypeDO, lookupTypeE);
        return lookupTypeE;
    }

    @Override
    public LookupTypeDTO entityToDto(LookupTypeE lookupTypeE) {
        LookupTypeDTO lookupTypeDTO = new LookupTypeDTO();
        BeanUtils.copyProperties(lookupTypeE, lookupTypeDTO);
        return lookupTypeDTO;
    }

    @Override
    public LookupTypeDO entityToDo(LookupTypeE lookupTypeE) {
        LookupTypeDO lookupTypeDO = new LookupTypeDO();
        BeanUtils.copyProperties(lookupTypeE, lookupTypeDO);
        return lookupTypeDO;
    }

    @Override
    public LookupTypeDTO doToDto(LookupTypeDO lookupTypeDO) {
        LookupTypeDTO lookupTypeDTO = new LookupTypeDTO();
        BeanUtils.copyProperties(lookupTypeDO, lookupTypeDTO);
        return lookupTypeDTO;
    }

    @Override
    public LookupTypeDO dtoToDo(LookupTypeDTO lookupTypeDTO) {
        LookupTypeDO lookupTypeDO = new LookupTypeDO();
        BeanUtils.copyProperties(lookupTypeDTO, lookupTypeDO);
        return lookupTypeDO;
    }
}