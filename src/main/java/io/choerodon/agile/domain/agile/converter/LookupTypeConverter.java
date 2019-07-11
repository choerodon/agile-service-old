package io.choerodon.agile.domain.agile.converter;


import io.choerodon.agile.api.vo.LookupTypeVO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.stereotype.Component;
import org.springframework.beans.BeanUtils;
import io.choerodon.agile.infra.dataobject.LookupTypeDTO;
import io.choerodon.agile.domain.agile.entity.LookupTypeE;

/**
 * 敏捷开发code键值类型
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 10:13:37
 */
@Component
public class LookupTypeConverter implements ConvertorI<LookupTypeE, LookupTypeDTO, LookupTypeVO> {

    @Override
    public LookupTypeE dtoToEntity(LookupTypeVO lookupTypeVO) {
        LookupTypeE lookupTypeE = new LookupTypeE();
        BeanUtils.copyProperties(lookupTypeVO, lookupTypeE);
        return lookupTypeE;
    }

    @Override
    public LookupTypeE doToEntity(LookupTypeDTO lookupTypeDTO) {
        LookupTypeE lookupTypeE = new LookupTypeE();
        BeanUtils.copyProperties(lookupTypeDTO, lookupTypeE);
        return lookupTypeE;
    }

    @Override
    public LookupTypeVO entityToDto(LookupTypeE lookupTypeE) {
        LookupTypeVO lookupTypeVO = new LookupTypeVO();
        BeanUtils.copyProperties(lookupTypeE, lookupTypeVO);
        return lookupTypeVO;
    }

    @Override
    public LookupTypeDTO entityToDo(LookupTypeE lookupTypeE) {
        LookupTypeDTO lookupTypeDTO = new LookupTypeDTO();
        BeanUtils.copyProperties(lookupTypeE, lookupTypeDTO);
        return lookupTypeDTO;
    }

    @Override
    public LookupTypeVO doToDto(LookupTypeDTO lookupTypeDTO) {
        LookupTypeVO lookupTypeVO = new LookupTypeVO();
        BeanUtils.copyProperties(lookupTypeDTO, lookupTypeVO);
        return lookupTypeVO;
    }

    @Override
    public LookupTypeDTO dtoToDo(LookupTypeVO lookupTypeVO) {
        LookupTypeDTO lookupTypeDTO = new LookupTypeDTO();
        BeanUtils.copyProperties(lookupTypeVO, lookupTypeDTO);
        return lookupTypeDTO;
    }
}