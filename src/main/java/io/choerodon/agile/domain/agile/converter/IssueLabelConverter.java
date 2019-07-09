package io.choerodon.agile.domain.agile.converter;


import io.choerodon.agile.api.vo.IssueLabelVO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.stereotype.Component;
import org.springframework.beans.BeanUtils;
import io.choerodon.agile.infra.dataobject.IssueLabelDTO;
import io.choerodon.agile.domain.agile.entity.IssueLabelE;

/**
 * 敏捷开发Issue标签
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:04:00
 */
@Component
public class IssueLabelConverter implements ConvertorI<IssueLabelE, IssueLabelDTO, IssueLabelVO> {

    @Override
    public IssueLabelE dtoToEntity(IssueLabelVO issueLabelVO) {
        IssueLabelE issueLabelE = new IssueLabelE();
        BeanUtils.copyProperties(issueLabelVO, issueLabelE);
        return issueLabelE;
    }

    @Override
    public IssueLabelE doToEntity(IssueLabelDTO issueLabelDTO) {
        IssueLabelE issueLabelE = new IssueLabelE();
        BeanUtils.copyProperties(issueLabelDTO, issueLabelE);
        return issueLabelE;
    }

    @Override
    public IssueLabelVO entityToDto(IssueLabelE issueLabelE) {
        IssueLabelVO issueLabelVO = new IssueLabelVO();
        BeanUtils.copyProperties(issueLabelE, issueLabelVO);
        return issueLabelVO;
    }

    @Override
    public IssueLabelDTO entityToDo(IssueLabelE issueLabelE) {
        IssueLabelDTO issueLabelDTO = new IssueLabelDTO();
        BeanUtils.copyProperties(issueLabelE, issueLabelDTO);
        return issueLabelDTO;
    }

    @Override
    public IssueLabelVO doToDto(IssueLabelDTO issueLabelDTO) {
        IssueLabelVO issueLabelVO = new IssueLabelVO();
        BeanUtils.copyProperties(issueLabelDTO, issueLabelVO);
        return issueLabelVO;
    }

    @Override
    public IssueLabelDTO dtoToDo(IssueLabelVO issueLabelVO) {
        IssueLabelDTO issueLabelDTO = new IssueLabelDTO();
        BeanUtils.copyProperties(issueLabelVO, issueLabelDTO);
        return issueLabelDTO;
    }
}