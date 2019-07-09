package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.IssueMoveVO;
import io.choerodon.agile.domain.agile.entity.IssueE;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/16.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class IssueMoveConverter implements ConvertorI<IssueE, Object, IssueMoveVO> {

    @Override
    public IssueE dtoToEntity(IssueMoveVO issueMoveVO) {
        IssueE issueE = new IssueE();
        BeanUtils.copyProperties(issueMoveVO, issueE);
        return issueE;
    }

    @Override
    public IssueMoveVO entityToDto(IssueE issueE) {
        IssueMoveVO issueMoveVO = new IssueMoveVO();
        BeanUtils.copyProperties(issueE, issueMoveVO);
        return issueMoveVO;
    }

}
