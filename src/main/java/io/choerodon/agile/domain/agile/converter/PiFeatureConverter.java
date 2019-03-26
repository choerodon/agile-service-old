package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.domain.agile.entity.PiFeatureE;
import io.choerodon.agile.infra.dataobject.PiFeatureDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/26.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class PiFeatureConverter implements ConvertorI<PiFeatureE, PiFeatureDO, Object> {

    @Override
    public PiFeatureE doToEntity(PiFeatureDO piFeatureDO) {
        PiFeatureE piFeatureE = new PiFeatureE();
        BeanUtils.copyProperties(piFeatureDO, piFeatureE);
        return piFeatureE;
    }

    @Override
    public PiFeatureDO entityToDo(PiFeatureE piFeatureE) {
        PiFeatureDO piFeatureDO = new PiFeatureDO();
        BeanUtils.copyProperties(piFeatureE, piFeatureDO);
        return piFeatureDO;
    }
}
