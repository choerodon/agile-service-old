package io.choerodon.agile.domain.agile.converter;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.agile.api.vo.IssueComponentDTO;
import io.choerodon.agile.domain.agile.entity.IssueComponentE;
import io.choerodon.agile.infra.dataobject.IssueComponentDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class IssueComponentConverter implements ConvertorI<IssueComponentE, IssueComponentDO, IssueComponentDTO> {

    @Override
    public IssueComponentE dtoToEntity(IssueComponentDTO issueComponentDTO) {
        IssueComponentE issueComponentE = new IssueComponentE();
        BeanUtils.copyProperties(issueComponentDTO, issueComponentE);
        return issueComponentE;
    }

    @Override
    public IssueComponentDTO entityToDto(IssueComponentE issueComponentE) {
        IssueComponentDTO issueComponentDTO = new IssueComponentDTO();
        BeanUtils.copyProperties(issueComponentE, issueComponentDTO);
        return issueComponentDTO;
    }

    @Override
    public IssueComponentE doToEntity(IssueComponentDO issueComponentDO) {
        IssueComponentE issueComponentE = new IssueComponentE();
        BeanUtils.copyProperties(issueComponentDO, issueComponentE);
        return issueComponentE;
    }

    @Override
    public IssueComponentDO entityToDo(IssueComponentE issueComponentE) {
        IssueComponentDO issueComponentDO = new IssueComponentDO();
        BeanUtils.copyProperties(issueComponentE, issueComponentDO);
        return issueComponentDO;
    }

    @Override
    public IssueComponentDTO doToDto(IssueComponentDO issueComponentDO) {
        IssueComponentDTO issueComponentDTO = new IssueComponentDTO();
        BeanUtils.copyProperties(issueComponentDO, issueComponentDTO);
        return issueComponentDTO;
    }

    @Override
    public IssueComponentDO dtoToDo(IssueComponentDTO issueComponentDTO) {
        IssueComponentDO issueComponentDO = new IssueComponentDO();
        BeanUtils.copyProperties(issueComponentDTO, issueComponentDO);
        return issueComponentDO;
    }
}
