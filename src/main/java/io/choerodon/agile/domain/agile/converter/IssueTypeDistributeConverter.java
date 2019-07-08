package io.choerodon.agile.domain.agile.converter;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import io.choerodon.agile.api.vo.IssueTypeDistributeDTO;
import io.choerodon.agile.infra.dataobject.IssueTypeDistributeDO;
import io.choerodon.core.convertor.ConvertorI;

/**
 * Creator: ChangpingShi0213@gmail.com
 * Date:  16:31 2018/9/4
 * Description:
 */
@Component
public class IssueTypeDistributeConverter implements ConvertorI<Object, IssueTypeDistributeDO, IssueTypeDistributeDTO> {

    @Override
    public IssueTypeDistributeDTO doToDto(IssueTypeDistributeDO issueTypeDistributeDO) {
        IssueTypeDistributeDTO issueTypeDistributeDTO = new IssueTypeDistributeDTO();
        BeanUtils.copyProperties(issueTypeDistributeDO, issueTypeDistributeDTO);
        return issueTypeDistributeDTO;
    }
}