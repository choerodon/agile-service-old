package io.choerodon.agile.app.service;

import io.choerodon.agile.domain.agile.entity.ComponentIssueRelE;
import io.choerodon.agile.infra.dataobject.ComponentIssueRelDTO;


/**
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 16:47:27
 */
public interface ComponentIssueRelService {

    /**
     * 添加一个
     *
     * @param componentIssueRelDTO componentIssueRelDTO
     * @return ComponentIssueRelE
     */
    ComponentIssueRelDTO create(ComponentIssueRelDTO componentIssueRelDTO);

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
     * @param componentIssueRelDTO componentIssueRelDTO
     * @return int
     */
    int delete(ComponentIssueRelDTO componentIssueRelDTO);

    void deleteByComponentId(Long projectId, Long componentId);

    /**
     * 根据issueId删除
     *
     * @param issueId issueId
     * @return int
     */
    int deleteByIssueId(Long issueId);
}