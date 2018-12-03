package io.choerodon.agile.app.service;

import io.choerodon.agile.api.dto.LookupTypeWithValuesDTO;

/**
 * 敏捷开发code键值
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 09:40:27
 */
public interface LookupValueService {

    LookupTypeWithValuesDTO queryLookupValueByCode(Long projectId, String typeCode);

    LookupTypeWithValuesDTO queryConstraintLookupValue(Long projectId);

}