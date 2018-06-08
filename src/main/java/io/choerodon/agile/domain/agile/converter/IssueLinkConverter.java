package io.choerodon.agile.domain.agile.converter;


import io.choerodon.core.convertor.ConvertorI;
import org.springframework.stereotype.Component;
import org.springframework.beans.BeanUtils;
import io.choerodon.agile.api.dto.IssueLinkDTO;
import io.choerodon.agile.infra.dataobject.IssueLinkDO;
import io.choerodon.agile.domain.agile.entity.IssueLinkE;

/**
 * 敏捷开发Issue链接
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:50:34
 */
@Component
public class IssueLinkConverter implements ConvertorI<IssueLinkE, IssueLinkDO, IssueLinkDTO> {

    @Override
    public IssueLinkE dtoToEntity(IssueLinkDTO issueLinkDTO) {
        IssueLinkE issueLinkE = new IssueLinkE();
        BeanUtils.copyProperties(issueLinkDTO, issueLinkE);
        return issueLinkE;
    }

    @Override
    public IssueLinkE doToEntity(IssueLinkDO issueLinkDO) {
        IssueLinkE issueLinkE = new IssueLinkE();
        BeanUtils.copyProperties(issueLinkDO, issueLinkE);
        return issueLinkE;
    }

    @Override
    public IssueLinkDTO entityToDto(IssueLinkE issueLinkE) {
        IssueLinkDTO issueLinkDTO = new IssueLinkDTO();
        BeanUtils.copyProperties(issueLinkE, issueLinkDTO);
        return issueLinkDTO;
    }

    @Override
    public IssueLinkDO entityToDo(IssueLinkE issueLinkE) {
        IssueLinkDO issueLinkDO = new IssueLinkDO();
        BeanUtils.copyProperties(issueLinkE, issueLinkDO);
        return issueLinkDO;
    }

    @Override
    public IssueLinkDTO doToDto(IssueLinkDO issueLinkDO) {
        IssueLinkDTO issueLinkDTO = new IssueLinkDTO();
        BeanUtils.copyProperties(issueLinkDO, issueLinkDTO);
        return issueLinkDTO;
    }

    @Override
    public IssueLinkDO dtoToDo(IssueLinkDTO issueLinkDTO) {
        IssueLinkDO issueLinkDO = new IssueLinkDO();
        BeanUtils.copyProperties(issueLinkDTO, issueLinkDO);
        return issueLinkDO;
    }
}