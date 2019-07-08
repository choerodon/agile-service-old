package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.LookupTypeWithValuesDTO;
import io.choerodon.agile.infra.dataobject.LookupTypeWithValuesDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/19.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class LookupTypeWithValueConverter implements ConvertorI<Object, LookupTypeWithValuesDO, LookupTypeWithValuesDTO> {
    @Override
    public LookupTypeWithValuesDTO doToDto(LookupTypeWithValuesDO lookupTypeWithValuesDO) {
        LookupTypeWithValuesDTO lookupTypeWithValuesDTO = new LookupTypeWithValuesDTO();
        BeanUtils.copyProperties(lookupTypeWithValuesDO, lookupTypeWithValuesDTO);
        return lookupTypeWithValuesDTO;
    }

    @Override
    public LookupTypeWithValuesDO dtoToDo(LookupTypeWithValuesDTO lookupTypeWithValuesDTO) {
        LookupTypeWithValuesDO lookupTypeWithValuesDO = new LookupTypeWithValuesDO();
        BeanUtils.copyProperties(lookupTypeWithValuesDTO, lookupTypeWithValuesDO);
        return lookupTypeWithValuesDO;
    }
}
