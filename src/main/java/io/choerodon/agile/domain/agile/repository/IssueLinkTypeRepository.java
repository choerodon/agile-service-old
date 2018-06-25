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
     * @param projectId  projectId
     * @return int
     */
    int delete(Long linkTypeId, Long projectId);

    /**
     * 删除issue下的link关系
     *
     * @param issueLinkTypeId issueLinkTypeId
     * @return int
     */
    int deleteIssueLinkTypeRel(Long issueLinkTypeId);

    /**
     * 批量修改issue链接关系的类型到新的类型
     *
     * @param issueLinkTypeId   issueLinkTypeId
     * @param toIssueLinkTypeId toIssueLinkTypeId
     * @return int
     */
    int batchUpdateRelToIssueLinkType(Long issueLinkTypeId, Long toIssueLinkTypeId);
}
