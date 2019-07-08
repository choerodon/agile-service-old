package io.choerodon.agile.app.service.impl;


import io.choerodon.agile.api.vo.LookupTypeWithValuesDTO;
import io.choerodon.agile.api.vo.LookupValueDTO;
import io.choerodon.agile.app.service.LookupValueService;
import io.choerodon.agile.infra.dataobject.LookupTypeWithValuesDO;
import io.choerodon.agile.infra.mapper.LookupValueMapper;
import io.choerodon.core.convertor.ConvertHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 敏捷开发code键值
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 09:40:27
 */
@Service
public class LookupValueServiceImpl implements LookupValueService {

    @Autowired
    private LookupValueMapper lookupValueMapper;

    @Override
    public LookupTypeWithValuesDTO queryLookupValueByCode(Long projectId, String typeCode) {
        LookupTypeWithValuesDO typeWithValues = lookupValueMapper.queryLookupValueByCode(typeCode);
        LookupTypeWithValuesDTO result = ConvertHelper.convert(typeWithValues, LookupTypeWithValuesDTO.class);
        result.setLookupValues(ConvertHelper.convertList(typeWithValues.getLookupValues(), LookupValueDTO.class));
        return result;
    }

    @Override
    public LookupTypeWithValuesDTO queryConstraintLookupValue(Long projectId) {
        LookupTypeWithValuesDO typeWithValues = lookupValueMapper.queryLookupValueByCode("constraint");
        LookupTypeWithValuesDTO result = ConvertHelper.convert(typeWithValues, LookupTypeWithValuesDTO.class);
        result.setLookupValues(ConvertHelper.convertList(typeWithValues.getLookupValues(), LookupValueDTO.class));
        return result;
    }
}