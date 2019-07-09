package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.domain.agile.entity.PiFeatureE;
import io.choerodon.agile.infra.repository.PiFeatureRepository;
import io.choerodon.agile.infra.common.annotation.DataLog;
import io.choerodon.agile.infra.dataobject.PiFeatureDTO;
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
    @DataLog(type = "pi")
    public PiFeatureE create(PiFeatureE piFeatureE) {
        PiFeatureDTO piFeatureDTO = ConvertHelper.convert(piFeatureE, PiFeatureDTO.class);
        if (piFeatureMapper.insert(piFeatureDTO) != 1) {
            throw new CommonException("error.piFeatureDTO.insert");
        }
        return piFeatureE;
    }
}
