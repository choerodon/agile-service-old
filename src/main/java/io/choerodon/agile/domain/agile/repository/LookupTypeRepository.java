package io.choerodon.agile.domain.agile.repository;

import io.choerodon.agile.domain.agile.entity.LookupTypeE;

/**
 * 敏捷开发code键值类型
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 10:13:37
 */
public interface LookupTypeRepository {

    /**
     * 更新敏捷开发code键值类型
     *
     * @param lookupTypeE lookupTypeE
     * @return LookupTypeE
     */
    LookupTypeE update(LookupTypeE lookupTypeE);

    /**
     * 添加一个敏捷开发code键值类型
     *
     * @param lookupTypeE lookupTypeE
     * @return LookupTypeE
     */
    LookupTypeE create(LookupTypeE lookupTypeE);

    /**
     * 根据typeCode删除敏捷开发code键值类型
     *
     * @param typeCode typeCode
     * @return int
     */
    int delete(String typeCode);
}