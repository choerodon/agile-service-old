package io.choerodon.agile.domain.agile.converter;


import io.choerodon.core.convertor.ConvertorI;
import org.springframework.stereotype.Component;
import org.springframework.beans.BeanUtils;
import io.choerodon.agile.api.dto.IssueLabelDTO;
import io.choerodon.agile.infra.dataobject.IssueLabelDO;
import io.choerodon.agile.domain.agile.entity.IssueLabelE;

/**
 * 敏捷开发Issue标签
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:04:00
 */
@Component
public class IssueLabelConverter implements ConvertorI<IssueLabelE, IssueLabelDO, IssueLabelDTO> {

    @Override
    public IssueLabelE dtoToEntity(IssueLabelDTO issueLabelDTO) {
        IssueLabelE issueLabelE = new IssueLabelE();
        BeanUtils.copyProperties(issueLabelDTO, issueLabelE);
        return issueLabelE;
    }

    @Override
    public IssueLabelE doToEntity(IssueLabelDO issueLabelDO) {
        IssueLabelE issueLabelE = new IssueLabelE();
        BeanUtils.copyProperties(issueLabelDO, issueLabelE);
        return issueLabelE;
    }

    @Override
    public IssueLabelDTO entityToDto(IssueLabelE issueLabelE) {
        IssueLabelDTO issueLabelDTO = new IssueLabelDTO();
        BeanUtils.copyProperties(issueLabelE, issueLabelDTO);
        return issueLabelDTO;
    }

    @Override
    public IssueLabelDO entityToDo(IssueLabelE issueLabelE) {
        IssueLabelDO issueLabelDO = new IssueLabelDO();
        BeanUtils.copyProperties(issueLabelE, issueLabelDO);
        return issueLabelDO;
    }

    @Override
    public IssueLabelDTO doToDto(IssueLabelDO issueLabelDO) {
        IssueLabelDTO issueLabelDTO = new IssueLabelDTO();
        BeanUtils.copyProperties(issueLabelDO, issueLabelDTO);
        return issueLabelDTO;
    }

    @Override
    public IssueLabelDO dtoToDo(IssueLabelDTO issueLabelDTO) {
        IssueLabelDO issueLabelDO = new IssueLabelDO();
        BeanUtils.copyProperties(issueLabelDTO, issueLabelDO);
        return issueLabelDO;
    }
}