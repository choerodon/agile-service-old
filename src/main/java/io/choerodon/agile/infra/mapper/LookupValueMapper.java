package io.choerodon.agile.infra.mapper;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.agile.infra.dataobject.*;


/**
 * 敏捷开发code键值
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 09:40:27
 */
public interface LookupValueMapper extends Mapper<LookupValueDO> {

    LookupTypeWithValuesDO queryLookupValueByCode(String typeCode);

    String selectNameByValueCode(String valueCode);
}