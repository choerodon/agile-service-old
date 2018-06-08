package io.choerodon.agile.domain.agile.repository;

import io.choerodon.agile.domain.agile.entity.ComponentIssueRelE;

import java.util.List;


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
    List<ComponentIssueRelE> create(ComponentIssueRelE componentIssueRelE);

    /**
     * 根据issueId删除
     *
     * @param issueId issueId
     * @return int
     */
    int deleteByIssueId(Long issueId);

    void deleteByComponentId(Long projectId, Long componentId);
}