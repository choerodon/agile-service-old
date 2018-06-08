package io.choerodon.agile.domain.agile.repository;

import io.choerodon.agile.domain.agile.entity.IssueLabelE;

/**
 * 敏捷开发Issue标签
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:04:00
 */
public interface IssueLabelRepository {

    /**
     * 更新敏捷开发Issue标签
     *
     * @param issueLabelE issueLabelE
     * @return IssueLabelE
     */
    IssueLabelE update(IssueLabelE issueLabelE);

    /**
     * 添加一个敏捷开发Issue标签
     *
     * @param issueLabelE issueLabelE
     * @return IssueLabelE
     */
    IssueLabelE create(IssueLabelE issueLabelE);

    /**
     * 根据id删除敏捷开发Issue标签
     *
     * @param id id
     * @return int
     */
    int delete(Long id);

    /**
     * 不是使用中的issue标签垃圾回收
     *
     * @return int
     */
    int labelGarbageCollection();

}