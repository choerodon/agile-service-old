package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.dto.VersionNameDTO;
import io.choerodon.agile.infra.dataobject.VersionNameDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/4/9.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class VersionNameConverter implements ConvertorI<Object, VersionNameDO, VersionNameDTO> {

    @Override
    public VersionNameDTO doToDto(VersionNameDO versionNameDO) {
        VersionNameDTO versionNameDTO = new VersionNameDTO();
        BeanUtils.copyProperties(versionNameDO, versionNameDTO);
        return versionNameDTO;
    }
}
