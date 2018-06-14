package io.choerodon.agile.domain.agile.repository;

import io.choerodon.agile.domain.agile.entity.IssueLinkTypeE;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/14
 */
public interface IssueLinkTypeRepository {

    /**
     * 更新
     *
     * @param issueLinkTypeE issueLinkTypeE
     * @return IssueLinkTypeE
     */
    IssueLinkTypeE update(IssueLinkTypeE issueLinkTypeE);

    /**
     * 创建
     *
     * @param issueLinkTypeE issueLinkTypeE
     * @return IssueLinkTypeE
     */
    IssueLinkTypeE create(IssueLinkTypeE issueLinkTypeE);

    /**
     * 删除
     *
     * @param linkTypeId linkTypeId
     * @return int
     */
    int delete(Long linkTypeId);
}
