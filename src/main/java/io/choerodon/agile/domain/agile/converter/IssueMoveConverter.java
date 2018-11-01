package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.dto.IssueMoveDTO;
import io.choerodon.agile.domain.agile.entity.IssueE;
import io.choerodon.agile.infra.dataobject.IssueDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/16.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class IssueMoveConverter implements ConvertorI<IssueE, IssueDO, IssueMoveDTO> {

    @Override
    public IssueE dtoToEntity(IssueMoveDTO issueMoveDTO) {
        IssueE issueE = new IssueE();
        BeanUtils.copyProperties(issueMoveDTO, issueE);
        return issueE;
    }

    @Override
    public IssueMoveDTO entityToDto(IssueE issueE) {
        IssueMoveDTO issueMoveDTO = new IssueMoveDTO();
        BeanUtils.copyProperties(issueE, issueMoveDTO);
        return issueMoveDTO;
    }

    @Override
    public IssueMoveDTO doToDto(IssueDO issueDO) {
        IssueMoveDTO issueMoveDTO = new IssueMoveDTO();
        BeanUtils.copyProperties(issueDO, issueMoveDTO);
        return issueMoveDTO;
    }
}
