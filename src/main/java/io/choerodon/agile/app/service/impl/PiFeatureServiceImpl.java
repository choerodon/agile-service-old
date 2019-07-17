package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.app.service.PiFeatureService;
import io.choerodon.agile.infra.common.annotation.DataLog;
import io.choerodon.agile.infra.dataobject.PiFeatureDTO;
import io.choerodon.agile.infra.mapper.PiFeatureMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/26.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class PiFeatureServiceImpl implements PiFeatureService {

    @Autowired
    private PiFeatureMapper piFeatureMapper;

    @Override
    @DataLog(type = "pi")
    public PiFeatureDTO create(PiFeatureDTO piFeatureDTO) {
        if (piFeatureMapper.insert(piFeatureDTO) != 1) {
            throw new CommonException("error.piFeatureDTO.insert");
        }
        return piFeatureDTO;
    }
}
