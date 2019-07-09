package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.IssueMoveVO;
import io.choerodon.agile.infra.dataobject.IssueDTO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/16.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class IssueDOToMoveDTOConverter implements ConvertorI<Object, IssueDTO, IssueMoveVO> {

    @Override
    public IssueMoveVO doToDto(IssueDTO issueDTO) {
        IssueMoveVO issueMoveVO = new IssueMoveVO();
        BeanUtils.copyProperties(issueDTO, issueMoveVO);
        return issueMoveVO;
    }
}
