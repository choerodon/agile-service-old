package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.FeatureVO;
import io.choerodon.agile.domain.agile.entity.FeatureE;
import io.choerodon.agile.infra.dataobject.FeatureDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/13.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class FeatureConverter implements ConvertorI<FeatureE, FeatureVO, FeatureDO> {

    @Override
    public FeatureE dtoToEntity(FeatureDO featureDO) {
        FeatureE featureE = new FeatureE();
        BeanUtils.copyProperties(featureDO, featureE);
        return featureE;
    }

    @Override
    public FeatureDO entityToDto(FeatureE featureE) {
        FeatureDO featureDO = new FeatureDO();
        BeanUtils.copyProperties(featureE, featureDO);
        return featureDO;
    }

    @Override
    public FeatureE doToEntity(FeatureVO featureVO) {
        FeatureE featureE = new FeatureE();
        BeanUtils.copyProperties(featureVO, featureE);
        return featureE;
    }

    @Override
    public FeatureVO entityToDo(FeatureE featureE) {
        FeatureVO featureVO = new FeatureVO();
        BeanUtils.copyProperties(featureE, featureVO);
        return featureVO;
    }

    @Override
    public FeatureDO doToDto(FeatureVO featureVO) {
        FeatureDO featureDO = new FeatureDO();
        BeanUtils.copyProperties(featureVO, featureDO);
        return featureDO;
    }

    @Override
    public FeatureVO dtoToDo(FeatureDO featureDO) {
        FeatureVO featureVO = new FeatureVO();
        BeanUtils.copyProperties(featureDO, featureVO);
        return featureVO;
    }
}
