package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.FeatureCommonDTO;
import io.choerodon.agile.infra.dataobject.FeatureCommonDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/4/9.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class FeatureCommonConverter implements ConvertorI<Object, FeatureCommonDO, FeatureCommonDTO> {

    @Override
    public FeatureCommonDTO doToDto(FeatureCommonDO featureCommonDO) {
        FeatureCommonDTO featureCommonDTO = new FeatureCommonDTO();
        BeanUtils.copyProperties(featureCommonDO, featureCommonDTO);
        return featureCommonDTO;
    }
}
