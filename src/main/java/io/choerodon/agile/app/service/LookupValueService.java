package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.LookupTypeWithValuesVO;

/**
 * 敏捷开发code键值
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 09:40:27
 */
public interface LookupValueService {

    LookupTypeWithValuesVO queryLookupValueByCode(Long projectId, String typeCode);

    LookupTypeWithValuesVO queryConstraintLookupValue(Long projectId);

}