package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.FeatureExportDTO;
import io.choerodon.agile.infra.dataobject.FeatureExportDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/4/23.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class FeatureExportConverter implements ConvertorI<Object, FeatureExportDO, FeatureExportDTO> {

    @Override
    public FeatureExportDTO doToDto(FeatureExportDO featureExportDO) {
        FeatureExportDTO featureExportDTO = new FeatureExportDTO();
        BeanUtils.copyProperties(featureExportDO, featureExportDTO);
        return featureExportDTO;
    }
}
