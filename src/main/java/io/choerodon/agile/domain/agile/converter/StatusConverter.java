package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.dto.StatusDTO;
import io.choerodon.agile.infra.dataobject.StatusDO;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/26.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class StatusConverter implements ConvertorI<Object, StatusDO, StatusDTO> {

    @Override
    public StatusDTO doToDto(StatusDO statusDO) {
        StatusDTO statusDTO = new StatusDTO();
        BeanUtils.copyProperties(statusDO, statusDTO);
        return statusDTO;
    }
}
