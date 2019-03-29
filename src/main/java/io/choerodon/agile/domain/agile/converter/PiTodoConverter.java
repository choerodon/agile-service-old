package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.dto.PiTodoDTO;
import io.choerodon.agile.infra.dataobject.PiTodoDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/29.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class PiTodoConverter implements ConvertorI<Object, PiTodoDO, PiTodoDTO> {

    @Override
    public PiTodoDTO doToDto(PiTodoDO piTodoDO) {
        PiTodoDTO piTodoDTO = new PiTodoDTO();
        BeanUtils.copyProperties(piTodoDO, piTodoDTO);
        return piTodoDTO;
    }
}
