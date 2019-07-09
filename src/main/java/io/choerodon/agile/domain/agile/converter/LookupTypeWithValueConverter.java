package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.LookupTypeWithValuesVO;
import io.choerodon.agile.infra.dataobject.LookupTypeWithValuesDTO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/19.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class LookupTypeWithValueConverter implements ConvertorI<Object, LookupTypeWithValuesDTO, LookupTypeWithValuesVO> {
    @Override
    public LookupTypeWithValuesVO doToDto(LookupTypeWithValuesDTO lookupTypeWithValuesDTO) {
        LookupTypeWithValuesVO lookupTypeWithValuesVO = new LookupTypeWithValuesVO();
        BeanUtils.copyProperties(lookupTypeWithValuesDTO, lookupTypeWithValuesVO);
        return lookupTypeWithValuesVO;
    }

    @Override
    public LookupTypeWithValuesDTO dtoToDo(LookupTypeWithValuesVO lookupTypeWithValuesVO) {
        LookupTypeWithValuesDTO lookupTypeWithValuesDTO = new LookupTypeWithValuesDTO();
        BeanUtils.copyProperties(lookupTypeWithValuesVO, lookupTypeWithValuesDTO);
        return lookupTypeWithValuesDTO;
    }
}
