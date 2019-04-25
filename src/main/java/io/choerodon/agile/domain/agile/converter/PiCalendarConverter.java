package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.dto.PiCalendarDTO;
import io.choerodon.agile.infra.dataobject.PiCalendarDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/4/25.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class PiCalendarConverter implements ConvertorI<Object, PiCalendarDO, PiCalendarDTO> {

    @Override
    public PiCalendarDTO doToDto(PiCalendarDO piCalendarDO) {
        PiCalendarDTO piCalendarDTO = new PiCalendarDTO();
        BeanUtils.copyProperties(piCalendarDO, piCalendarDTO);
        return piCalendarDTO;
    }
}
