package io.choerodon.agile.domain.agile.converter;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.agile.api.vo.IssueStatusDTO;
import io.choerodon.agile.domain.agile.entity.IssueStatusE;
import io.choerodon.agile.infra.dataobject.IssueStatusDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class IssueStatusConverter implements ConvertorI<IssueStatusE, IssueStatusDO, IssueStatusDTO> {
    @Override
    public IssueStatusE dtoToEntity(IssueStatusDTO issueStatusDTO) {
        IssueStatusE issueStatusE = new IssueStatusE();
        BeanUtils.copyProperties(issueStatusDTO, issueStatusE);
        return issueStatusE;
    }

    @Override
    public IssueStatusDTO entityToDto(IssueStatusE issueStatusE) {
        IssueStatusDTO issueStatusDTO = new IssueStatusDTO();
        BeanUtils.copyProperties(issueStatusE, issueStatusDTO);
        return issueStatusDTO;
    }

    @Override
    public IssueStatusE doToEntity(IssueStatusDO issueStatusDO) {
        IssueStatusE issueStatusE = new IssueStatusE();
        BeanUtils.copyProperties(issueStatusDO, issueStatusE);
        return issueStatusE;
    }

    @Override
    public IssueStatusDO entityToDo(IssueStatusE issueStatusE) {
        IssueStatusDO issueStatusDO = new IssueStatusDO();
        BeanUtils.copyProperties(issueStatusE, issueStatusDO);
        return issueStatusDO;
    }

    @Override
    public IssueStatusDTO doToDto(IssueStatusDO issueStatusDO) {
        IssueStatusDTO issueStatusDTO = new IssueStatusDTO();
        BeanUtils.copyProperties(issueStatusDO, issueStatusDTO);
        return issueStatusDTO;
    }

    @Override
    public IssueStatusDO dtoToDo(IssueStatusDTO issueStatusDTO) {
        IssueStatusDO issueStatusDO = new IssueStatusDO();
        BeanUtils.copyProperties(issueStatusDTO, issueStatusDO);
        return issueStatusDO;
    }
}
