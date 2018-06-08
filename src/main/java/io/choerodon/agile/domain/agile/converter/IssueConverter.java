package io.choerodon.agile.domain.agile.converter;


import io.choerodon.core.convertor.ConvertorI;
import org.springframework.stereotype.Component;
import org.springframework.beans.BeanUtils;
import io.choerodon.agile.api.dto.IssueDTO;
import io.choerodon.agile.infra.dataobject.IssueDO;
import io.choerodon.agile.domain.agile.entity.IssueE;

/**
 * 敏捷开发Issue
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 20:30:48
 */
@Component
public class IssueConverter implements ConvertorI<IssueE, IssueDO, IssueDTO> {

    @Override
    public IssueE dtoToEntity(IssueDTO issueDTO) {
        IssueE issueE = new IssueE();
        BeanUtils.copyProperties(issueDTO, issueE);
        return issueE;
    }

    @Override
    public IssueE doToEntity(IssueDO issueDO) {
        IssueE issueE = new IssueE();
        BeanUtils.copyProperties(issueDO, issueE);
        return issueE;
    }

    @Override
    public IssueDTO entityToDto(IssueE issueE) {
        IssueDTO issueDTO = new IssueDTO();
        BeanUtils.copyProperties(issueE, issueDTO);
        return issueDTO;
    }

    @Override
    public IssueDO entityToDo(IssueE issueE) {
        IssueDO issueDO = new IssueDO();
        BeanUtils.copyProperties(issueE, issueDO);
        return issueDO;
    }

    @Override
    public IssueDTO doToDto(IssueDO issueDO) {
        IssueDTO issueDTO = new IssueDTO();
        BeanUtils.copyProperties(issueDO, issueDTO);
        return issueDTO;
    }

    @Override
    public IssueDO dtoToDo(IssueDTO issueDTO) {
        IssueDO issueDO = new IssueDO();
        BeanUtils.copyProperties(issueDTO, issueDO);
        return issueDO;
    }
}