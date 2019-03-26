package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.domain.agile.entity.PiFeatureE;
import io.choerodon.agile.domain.agile.repository.PiFeatureRepository;
import io.choerodon.agile.infra.dataobject.PiFeatureDO;
import io.choerodon.agile.infra.mapper.PiFeatureMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/26.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class PiFeatureRepositoryImpl implements PiFeatureRepository {

    @Autowired
    private PiFeatureMapper piFeatureMapper;

    @Override
    public PiFeatureE create(PiFeatureE piFeatureE) {
        PiFeatureDO piFeatureDO = ConvertHelper.convert(piFeatureE, PiFeatureDO.class);
        if (piFeatureMapper.insert(piFeatureDO) != 1) {
            throw new CommonException("error.piFeatureDO.insert");
        }
        return piFeatureE;
    }
}
