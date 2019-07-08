package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.FeatureMoveDTO;
import io.choerodon.agile.infra.dataobject.IssueDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class FeatureMoveConverter implements ConvertorI<Object, IssueDO, FeatureMoveDTO> {

    @Override
    public FeatureMoveDTO doToDto(IssueDO issueDO) {
        FeatureMoveDTO featureMoveDTO = new FeatureMoveDTO();
        BeanUtils.copyProperties(issueDO, featureMoveDTO);
        return featureMoveDTO;
    }
}
