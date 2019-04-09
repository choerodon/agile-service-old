package io.choerodon.agile.app.assembler;

import io.choerodon.agile.api.dto.FeatureCommonDTO;
import io.choerodon.agile.api.dto.PiNameDTO;
import io.choerodon.agile.api.dto.StatusMapDTO;
import io.choerodon.agile.api.dto.VersionNameDTO;
import io.choerodon.agile.infra.dataobject.FeatureCommonDO;
import io.choerodon.core.convertor.ConvertHelper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/4/9.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class FeatureCommonAssembler {

    public List<FeatureCommonDTO> featureCommonDOToDTO(List<FeatureCommonDO> featureCommonDOList, Map<Long, StatusMapDTO> statusMapDTOMap) {
        List<FeatureCommonDTO> result = new ArrayList<>();
        featureCommonDOList.forEach(featureCommonDO -> {
            FeatureCommonDTO featureCommonDTO = ConvertHelper.convert(featureCommonDO, FeatureCommonDTO.class);
            featureCommonDTO.setPiNameDTOList(ConvertHelper.convertList(featureCommonDO.getPiNameDOList(), PiNameDTO.class));
            featureCommonDTO.setVersionNameDTOList(ConvertHelper.convertList(featureCommonDO.getVersionNameDOList(), VersionNameDTO.class));
            featureCommonDTO.setStatusMapDTO(statusMapDTOMap.get(featureCommonDO.getStatusId()));
            result.add(featureCommonDTO);
        });
        return result;
    }
}
