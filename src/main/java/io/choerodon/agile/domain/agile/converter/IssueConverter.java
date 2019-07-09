package io.choerodon.agile.domain.agile.converter;


import io.choerodon.agile.api.vo.IssueVO;
import io.choerodon.agile.infra.dataobject.IssueDTO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.stereotype.Component;
import org.springframework.beans.BeanUtils;
import io.choerodon.agile.domain.agile.entity.IssueE;

/**
 * 敏捷开发Issue
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 20:30:48
 */
@Component
public class IssueConverter implements ConvertorI<IssueE, IssueDTO, IssueVO> {

    @Override
    public IssueE dtoToEntity(IssueVO issueVO) {
        IssueE issueE = new IssueE();
        BeanUtils.copyProperties(issueVO, issueE);
        return issueE;
    }

    @Override
    public IssueE doToEntity(IssueDTO issueDTO) {
        IssueE issueE = new IssueE();
        BeanUtils.copyProperties(issueDTO, issueE);
        return issueE;
    }

    @Override
    public IssueVO entityToDto(IssueE issueE) {
        IssueVO issueVO = new IssueVO();
        BeanUtils.copyProperties(issueE, issueVO);
        return issueVO;
    }

    @Override
    public IssueDTO entityToDo(IssueE issueE) {
        IssueDTO issueDTO = new IssueDTO();
        BeanUtils.copyProperties(issueE, issueDTO);
        return issueDTO;
    }

    @Override
    public IssueVO doToDto(IssueDTO issueDTO) {
        IssueVO issueVO = new IssueVO();
        BeanUtils.copyProperties(issueDTO, issueVO);
        return issueVO;
    }

    @Override
    public IssueDTO dtoToDo(IssueVO issueVO) {
        IssueDTO issueDTO = new IssueDTO();
        BeanUtils.copyProperties(issueVO, issueDTO);
        return issueDTO;
    }

}