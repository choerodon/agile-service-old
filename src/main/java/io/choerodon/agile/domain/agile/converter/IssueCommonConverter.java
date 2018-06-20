package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.dto.IssueCommonDTO;
import io.choerodon.agile.infra.dataobject.IssueCommonDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/20.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class IssueCommonConverter implements ConvertorI<Object, IssueCommonDO, IssueCommonDTO> {

    @Override
    public IssueCommonDTO doToDto(IssueCommonDO issueCommonDO) {
        IssueCommonDTO issueCommonDTO = new IssueCommonDTO();
        BeanUtils.copyProperties(issueCommonDO, issueCommonDTO);
        return issueCommonDTO;
    }

    @Override
    public IssueCommonDO dtoToDo(IssueCommonDTO issueCommonDTO) {
        IssueCommonDO issueCommonDO = new IssueCommonDO();
        BeanUtils.copyProperties(issueCommonDTO, issueCommonDO);
        return issueCommonDO;
    }
}
