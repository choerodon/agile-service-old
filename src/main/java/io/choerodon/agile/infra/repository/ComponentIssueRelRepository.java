package io.choerodon.agile.infra.repository;

import io.choerodon.agile.domain.agile.entity.ComponentIssueRelE;
import io.choerodon.agile.infra.dataobject.ComponentIssueRelDO;


/**
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 16:47:27
 */
public interface ComponentIssueRelRepository {

    /**
     * 添加一个
     *
     * @param componentIssueRelE componentIssueRelE
     * @return ComponentIssueRelE
     */
    ComponentIssueRelE create(ComponentIssueRelE componentIssueRelE);

    /**
     * 根据issueId删除
     *
     * @param issueId issueId
     * @return int
     */
    int batchComponentDelete(Long issueId);

    /**
     * 删除componentIssueRel
     *
     * @param componentIssueRelDO componentIssueRelDO
     * @return int
     */
    int delete(ComponentIssueRelDO componentIssueRelDO);

    void deleteByComponentId(Long projectId, Long componentId);

    /**
     * 根据issueId删除
     *
     * @param issueId issueId
     * @return int
     */
    int deleteByIssueId(Long issueId);
}