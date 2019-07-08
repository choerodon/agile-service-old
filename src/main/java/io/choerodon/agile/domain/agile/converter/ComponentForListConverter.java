package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.ComponentForListDTO;
import io.choerodon.agile.infra.dataobject.ComponentForListDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/31.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class ComponentForListConverter implements ConvertorI<Object, ComponentForListDO, ComponentForListDTO> {

    @Override
    public ComponentForListDTO doToDto(ComponentForListDO componentForListDO) {
        ComponentForListDTO componentForListDTO = new ComponentForListDTO();
        BeanUtils.copyProperties(componentForListDO, componentForListDTO);
        return componentForListDTO;
    }

    @Override
    public ComponentForListDO dtoToDo(ComponentForListDTO componentForListDTO) {
        ComponentForListDO componentForListDO = new ComponentForListDO();
        BeanUtils.copyProperties(componentForListDTO, componentForListDO);
        return componentForListDO;
    }
}
