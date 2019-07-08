package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.IssueInfoDTO;
import io.choerodon.agile.infra.dataobject.IssueInfoDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/7/11.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class IssueInfoConverter implements ConvertorI<Object, IssueInfoDO, IssueInfoDTO> {

    @Override
    public IssueInfoDTO doToDto(IssueInfoDO issueInfoDO) {
        IssueInfoDTO issueInfoDTO = new IssueInfoDTO();
        BeanUtils.copyProperties(issueInfoDO, issueInfoDTO);
        return issueInfoDTO;
    }

}
