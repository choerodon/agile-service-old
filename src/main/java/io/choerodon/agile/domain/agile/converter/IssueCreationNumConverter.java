package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.IssueCreationNumVO;
import io.choerodon.agile.infra.dataobject.IssueCreationNumDTO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/7/16.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class IssueCreationNumConverter implements ConvertorI<Object, IssueCreationNumDTO, IssueCreationNumVO> {

    @Override
    public IssueCreationNumVO doToDto(IssueCreationNumDTO issueCreationNumDTO) {
        IssueCreationNumVO issueCreationNumVO = new IssueCreationNumVO();
        BeanUtils.copyProperties(issueCreationNumDTO, issueCreationNumVO);
        return issueCreationNumVO;
    }
}
