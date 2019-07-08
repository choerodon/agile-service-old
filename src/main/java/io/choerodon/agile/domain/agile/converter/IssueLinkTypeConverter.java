package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.IssueLinkTypeDTO;
import io.choerodon.agile.domain.agile.entity.IssueLinkTypeE;
import io.choerodon.agile.infra.dataobject.IssueLinkTypeDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/14
 */
@Component
public class IssueLinkTypeConverter implements ConvertorI<IssueLinkTypeE, IssueLinkTypeDO, IssueLinkTypeDTO> {

    @Override
    public IssueLinkTypeE dtoToEntity(IssueLinkTypeDTO issueLinkTypeDTO) {
        IssueLinkTypeE issueLinkTypeE = new IssueLinkTypeE();
        BeanUtils.copyProperties(issueLinkTypeDTO, issueLinkTypeE);
        return issueLinkTypeE;
    }

    @Override
    public IssueLinkTypeE doToEntity(IssueLinkTypeDO issueLinkTypeDO) {
        IssueLinkTypeE issueLinkTypeE = new IssueLinkTypeE();
        BeanUtils.copyProperties(issueLinkTypeDO, issueLinkTypeE);
        return issueLinkTypeE;
    }

    @Override
    public IssueLinkTypeDTO entityToDto(IssueLinkTypeE issueLinkTypeE) {
        IssueLinkTypeDTO issueLinkTypeDTO = new IssueLinkTypeDTO();
        BeanUtils.copyProperties(issueLinkTypeE, issueLinkTypeDTO);
        return issueLinkTypeDTO;
    }

    @Override
    public IssueLinkTypeDO entityToDo(IssueLinkTypeE issueLinkTypeE) {
        IssueLinkTypeDO issueLinkTypeDO = new IssueLinkTypeDO();
        BeanUtils.copyProperties(issueLinkTypeE, issueLinkTypeDO);
        return issueLinkTypeDO;
    }

    @Override
    public IssueLinkTypeDTO doToDto(IssueLinkTypeDO issueLinkTypeDO) {
        IssueLinkTypeDTO issueLinkTypeDTO = new IssueLinkTypeDTO();
        BeanUtils.copyProperties(issueLinkTypeDO, issueLinkTypeDTO);
        return issueLinkTypeDTO;
    }

    @Override
    public IssueLinkTypeDO dtoToDo(IssueLinkTypeDTO issueLinkTypeDTO) {
        IssueLinkTypeDO issueLinkTypeDO = new IssueLinkTypeDO();
        BeanUtils.copyProperties(issueLinkTypeDTO, issueLinkTypeDO);
        return issueLinkTypeDO;
    }
}
