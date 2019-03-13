package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.domain.agile.entity.FeatureE;
import io.choerodon.agile.domain.agile.repository.FeatureRepository;
import io.choerodon.agile.infra.dataobject.FeatureDO;
import io.choerodon.agile.infra.mapper.FeatureMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/13.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class FeatureRepositoryImpl implements FeatureRepository {

    @Autowired
    private FeatureMapper featureMapper;

    @Override
    public FeatureE create(FeatureE featureE) {
        FeatureDO featureDO = ConvertHelper.convert(featureE, FeatureDO.class);
        if (featureMapper.insert(featureDO) != 1) {
            throw new CommonException("error.feature.insert");
        }
        return ConvertHelper.convert(featureMapper.selectByPrimaryKey(featureDO.getId()), FeatureE.class);
    }
}
