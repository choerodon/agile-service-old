package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.IssueSprintRelDTO;
import io.choerodon.agile.domain.agile.entity.IssueSprintRelE;
import io.choerodon.agile.infra.dataobject.IssueSprintRelDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/6
 */
@Component
public class IssueSprintRelConverter implements ConvertorI<IssueSprintRelE, IssueSprintRelDO, IssueSprintRelDTO> {

    @Override
    public IssueSprintRelE dtoToEntity(IssueSprintRelDTO issueSprintRelDTO) {
        IssueSprintRelE issueSprintRelE = new IssueSprintRelE();
        BeanUtils.copyProperties(issueSprintRelDTO, issueSprintRelE);
        return issueSprintRelE;
    }

    @Override
    public IssueSprintRelE doToEntity(IssueSprintRelDO issueSprintRelDO) {
        IssueSprintRelE issueSprintRelE = new IssueSprintRelE();
        BeanUtils.copyProperties(issueSprintRelDO, issueSprintRelE);
        return issueSprintRelE;
    }

    @Override
    public IssueSprintRelDTO entityToDto(IssueSprintRelE issueSprintRelE) {
        IssueSprintRelDTO issueSprintRelDTO = new IssueSprintRelDTO();
        BeanUtils.copyProperties(issueSprintRelE, issueSprintRelDTO);
        return issueSprintRelDTO;
    }

    @Override
    public IssueSprintRelDO entityToDo(IssueSprintRelE issueSprintRelE) {
        IssueSprintRelDO issueSprintRelDO = new IssueSprintRelDO();
        BeanUtils.copyProperties(issueSprintRelE, issueSprintRelDO);
        return issueSprintRelDO;
    }

    @Override
    public IssueSprintRelDTO doToDto(IssueSprintRelDO issueSprintRelDO) {
        IssueSprintRelDTO issueSprintRelDTO = new IssueSprintRelDTO();
        BeanUtils.copyProperties(issueSprintRelDO, issueSprintRelDTO);
        return issueSprintRelDTO;
    }

    @Override
    public IssueSprintRelDO dtoToDo(IssueSprintRelDTO issueSprintRelDTO) {
        IssueSprintRelDO issueSprintRelDO = new IssueSprintRelDO();
        BeanUtils.copyProperties(issueSprintRelDTO, issueSprintRelDO);
        return issueSprintRelDO;
    }
}