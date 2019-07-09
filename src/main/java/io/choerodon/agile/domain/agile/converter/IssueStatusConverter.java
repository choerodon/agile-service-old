package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.IssueStatusVO;
import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.agile.domain.agile.entity.IssueStatusE;
import io.choerodon.agile.infra.dataobject.IssueStatusDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class IssueStatusConverter implements ConvertorI<IssueStatusE, IssueStatusDTO, IssueStatusVO> {
    @Override
    public IssueStatusE dtoToEntity(IssueStatusVO issueStatusVO) {
        IssueStatusE issueStatusE = new IssueStatusE();
        BeanUtils.copyProperties(issueStatusVO, issueStatusE);
        return issueStatusE;
    }

    @Override
    public IssueStatusVO entityToDto(IssueStatusE issueStatusE) {
        IssueStatusVO issueStatusVO = new IssueStatusVO();
        BeanUtils.copyProperties(issueStatusE, issueStatusVO);
        return issueStatusVO;
    }

    @Override
    public IssueStatusE doToEntity(IssueStatusDTO issueStatusDTO) {
        IssueStatusE issueStatusE = new IssueStatusE();
        BeanUtils.copyProperties(issueStatusDTO, issueStatusE);
        return issueStatusE;
    }

    @Override
    public IssueStatusDTO entityToDo(IssueStatusE issueStatusE) {
        IssueStatusDTO issueStatusDTO = new IssueStatusDTO();
        BeanUtils.copyProperties(issueStatusE, issueStatusDTO);
        return issueStatusDTO;
    }

    @Override
    public IssueStatusVO doToDto(IssueStatusDTO issueStatusDTO) {
        IssueStatusVO issueStatusVO = new IssueStatusVO();
        BeanUtils.copyProperties(issueStatusDTO, issueStatusVO);
        return issueStatusVO;
    }

    @Override
    public IssueStatusDTO dtoToDo(IssueStatusVO issueStatusVO) {
        IssueStatusDTO issueStatusDTO = new IssueStatusDTO();
        BeanUtils.copyProperties(issueStatusVO, issueStatusDTO);
        return issueStatusDTO;
    }
}
