package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.FeatureMoveVO;
import io.choerodon.agile.infra.dataobject.IssueDTO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class FeatureMoveConverter implements ConvertorI<Object, IssueDTO, FeatureMoveVO> {

    @Override
    public FeatureMoveVO doToDto(IssueDTO issueDTO) {
        FeatureMoveVO featureMoveVO = new FeatureMoveVO();
        BeanUtils.copyProperties(issueDTO, featureMoveVO);
        return featureMoveVO;
    }
}
