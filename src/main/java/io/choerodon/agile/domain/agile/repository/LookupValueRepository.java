package io.choerodon.agile.domain.agile.repository;

import io.choerodon.agile.domain.agile.entity.LookupValueE;


/**
 * 敏捷开发code键值
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 09:40:27
 */
public interface LookupValueRepository {

    /**
     * 更新敏捷开发code键值
     *
     * @param lookupValueE lookupValueE
     * @return LookupValueE
     */
    LookupValueE update(LookupValueE lookupValueE);

    /**
     * 添加一个敏捷开发code键值
     *
     * @param lookupValueE lookupValueE
     * @return LookupValueE
     */
    LookupValueE create(LookupValueE lookupValueE);

    /**
     * 根据valueCode删除敏捷开发code键值
     *
     * @param valueCode valueCode
     * @return int
     */
    int delete(String valueCode);
}