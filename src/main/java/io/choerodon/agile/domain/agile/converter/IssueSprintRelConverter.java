package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.IssueSprintRelVO;
import io.choerodon.agile.domain.agile.entity.IssueSprintRelE;
import io.choerodon.agile.infra.dataobject.IssueSprintRelDTO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/6
 */
@Component
public class IssueSprintRelConverter implements ConvertorI<IssueSprintRelE, IssueSprintRelDTO, IssueSprintRelVO> {

    @Override
    public IssueSprintRelE dtoToEntity(IssueSprintRelVO issueSprintRelVO) {
        IssueSprintRelE issueSprintRelE = new IssueSprintRelE();
        BeanUtils.copyProperties(issueSprintRelVO, issueSprintRelE);
        return issueSprintRelE;
    }

    @Override
    public IssueSprintRelE doToEntity(IssueSprintRelDTO issueSprintRelDTO) {
        IssueSprintRelE issueSprintRelE = new IssueSprintRelE();
        BeanUtils.copyProperties(issueSprintRelDTO, issueSprintRelE);
        return issueSprintRelE;
    }

    @Override
    public IssueSprintRelVO entityToDto(IssueSprintRelE issueSprintRelE) {
        IssueSprintRelVO issueSprintRelVO = new IssueSprintRelVO();
        BeanUtils.copyProperties(issueSprintRelE, issueSprintRelVO);
        return issueSprintRelVO;
    }

    @Override
    public IssueSprintRelDTO entityToDo(IssueSprintRelE issueSprintRelE) {
        IssueSprintRelDTO issueSprintRelDTO = new IssueSprintRelDTO();
        BeanUtils.copyProperties(issueSprintRelE, issueSprintRelDTO);
        return issueSprintRelDTO;
    }

    @Override
    public IssueSprintRelVO doToDto(IssueSprintRelDTO issueSprintRelDTO) {
        IssueSprintRelVO issueSprintRelVO = new IssueSprintRelVO();
        BeanUtils.copyProperties(issueSprintRelDTO, issueSprintRelVO);
        return issueSprintRelVO;
    }

    @Override
    public IssueSprintRelDTO dtoToDo(IssueSprintRelVO issueSprintRelVO) {
        IssueSprintRelDTO issueSprintRelDTO = new IssueSprintRelDTO();
        BeanUtils.copyProperties(issueSprintRelVO, issueSprintRelDTO);
        return issueSprintRelDTO;
    }
}