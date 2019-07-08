package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.FeatureDTO;
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
public class FeatureConverter implements ConvertorI<FeatureE, FeatureDTO, FeatureDO> {

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
    public FeatureE doToEntity(FeatureDTO featureDTO) {
        FeatureE featureE = new FeatureE();
        BeanUtils.copyProperties(featureDTO, featureE);
        return featureE;
    }

    @Override
    public FeatureDTO entityToDo(FeatureE featureE) {
        FeatureDTO featureDTO = new FeatureDTO();
        BeanUtils.copyProperties(featureE, featureDTO);
        return featureDTO;
    }

    @Override
    public FeatureDO doToDto(FeatureDTO featureDTO) {
        FeatureDO featureDO = new FeatureDO();
        BeanUtils.copyProperties(featureDTO, featureDO);
        return featureDO;
    }

    @Override
    public FeatureDTO dtoToDo(FeatureDO featureDO) {
        FeatureDTO featureDTO = new FeatureDTO();
        BeanUtils.copyProperties(featureDO, featureDTO);
        return featureDTO;
    }
}
