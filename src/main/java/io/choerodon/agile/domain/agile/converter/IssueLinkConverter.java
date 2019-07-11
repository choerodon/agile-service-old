package io.choerodon.agile.domain.agile.converter;


import io.choerodon.agile.api.vo.IssueLinkVO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.stereotype.Component;
import org.springframework.beans.BeanUtils;
import io.choerodon.agile.infra.dataobject.IssueLinkDTO;
import io.choerodon.agile.domain.agile.entity.IssueLinkE;

/**
 * 敏捷开发Issue链接
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:50:34
 */
@Component
public class IssueLinkConverter implements ConvertorI<IssueLinkE, IssueLinkDTO, IssueLinkVO> {

    @Override
    public IssueLinkE dtoToEntity(IssueLinkVO issueLinkVO) {
        IssueLinkE issueLinkE = new IssueLinkE();
        BeanUtils.copyProperties(issueLinkVO, issueLinkE);
        return issueLinkE;
    }

    @Override
    public IssueLinkE doToEntity(IssueLinkDTO issueLinkDTO) {
        IssueLinkE issueLinkE = new IssueLinkE();
        BeanUtils.copyProperties(issueLinkDTO, issueLinkE);
        return issueLinkE;
    }

    @Override
    public IssueLinkVO entityToDto(IssueLinkE issueLinkE) {
        IssueLinkVO issueLinkVO = new IssueLinkVO();
        BeanUtils.copyProperties(issueLinkE, issueLinkVO);
        return issueLinkVO;
    }

    @Override
    public IssueLinkDTO entityToDo(IssueLinkE issueLinkE) {
        IssueLinkDTO issueLinkDTO = new IssueLinkDTO();
        BeanUtils.copyProperties(issueLinkE, issueLinkDTO);
        return issueLinkDTO;
    }

    @Override
    public IssueLinkVO doToDto(IssueLinkDTO issueLinkDTO) {
        IssueLinkVO issueLinkVO = new IssueLinkVO();
        BeanUtils.copyProperties(issueLinkDTO, issueLinkVO);
        return issueLinkVO;
    }

    @Override
    public IssueLinkDTO dtoToDo(IssueLinkVO issueLinkVO) {
        IssueLinkDTO issueLinkDTO = new IssueLinkDTO();
        BeanUtils.copyProperties(issueLinkVO, issueLinkDTO);
        return issueLinkDTO;
    }
}