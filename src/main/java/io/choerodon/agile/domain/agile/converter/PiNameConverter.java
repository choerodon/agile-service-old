package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.PiNameDTO;
import io.choerodon.agile.infra.dataobject.PiNameDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/4/9.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class PiNameConverter implements ConvertorI<Object, PiNameDO, PiNameDTO> {

    @Override
    public PiNameDTO doToDto(PiNameDO piNameDO) {
        PiNameDTO piNameDTO = new PiNameDTO();
        BeanUtils.copyProperties(piNameDO, piNameDTO);
        return piNameDTO;
    }
}
