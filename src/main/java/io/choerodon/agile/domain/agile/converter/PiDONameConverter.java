package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.PiNameDTO;
import io.choerodon.agile.infra.dataobject.PiDTO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/4/18.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class PiDONameConverter implements ConvertorI<Object, PiDTO, PiNameDTO> {

    @Override
    public PiNameDTO doToDto(PiDTO piDTO) {
        PiNameDTO piNameDTO = new PiNameDTO();
        BeanUtils.copyProperties(piDTO, piNameDTO);
        return piNameDTO;
    }
}
