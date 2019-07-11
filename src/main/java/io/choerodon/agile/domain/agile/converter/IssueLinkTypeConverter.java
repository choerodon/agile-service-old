package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.IssueLinkTypeVO;
import io.choerodon.agile.domain.agile.entity.IssueLinkTypeE;
import io.choerodon.agile.infra.dataobject.IssueLinkTypeDTO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/14
 */
@Component
public class IssueLinkTypeConverter implements ConvertorI<IssueLinkTypeE, IssueLinkTypeDTO, IssueLinkTypeVO> {

    @Override
    public IssueLinkTypeE dtoToEntity(IssueLinkTypeVO issueLinkTypeVO) {
        IssueLinkTypeE issueLinkTypeE = new IssueLinkTypeE();
        BeanUtils.copyProperties(issueLinkTypeVO, issueLinkTypeE);
        return issueLinkTypeE;
    }

    @Override
    public IssueLinkTypeE doToEntity(IssueLinkTypeDTO issueLinkTypeDTO) {
        IssueLinkTypeE issueLinkTypeE = new IssueLinkTypeE();
        BeanUtils.copyProperties(issueLinkTypeDTO, issueLinkTypeE);
        return issueLinkTypeE;
    }

    @Override
    public IssueLinkTypeVO entityToDto(IssueLinkTypeE issueLinkTypeE) {
        IssueLinkTypeVO issueLinkTypeVO = new IssueLinkTypeVO();
        BeanUtils.copyProperties(issueLinkTypeE, issueLinkTypeVO);
        return issueLinkTypeVO;
    }

    @Override
    public IssueLinkTypeDTO entityToDo(IssueLinkTypeE issueLinkTypeE) {
        IssueLinkTypeDTO issueLinkTypeDTO = new IssueLinkTypeDTO();
        BeanUtils.copyProperties(issueLinkTypeE, issueLinkTypeDTO);
        return issueLinkTypeDTO;
    }

    @Override
    public IssueLinkTypeVO doToDto(IssueLinkTypeDTO issueLinkTypeDTO) {
        IssueLinkTypeVO issueLinkTypeVO = new IssueLinkTypeVO();
        BeanUtils.copyProperties(issueLinkTypeDTO, issueLinkTypeVO);
        return issueLinkTypeVO;
    }

    @Override
    public IssueLinkTypeDTO dtoToDo(IssueLinkTypeVO issueLinkTypeVO) {
        IssueLinkTypeDTO issueLinkTypeDTO = new IssueLinkTypeDTO();
        BeanUtils.copyProperties(issueLinkTypeVO, issueLinkTypeDTO);
        return issueLinkTypeDTO;
    }
}
