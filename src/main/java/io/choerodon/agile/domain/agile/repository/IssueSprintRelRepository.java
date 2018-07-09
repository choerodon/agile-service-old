package io.choerodon.agile.domain.agile.repository;

import io.choerodon.agile.infra.dataobject.IssueSprintRelDO;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/6
 */

public interface IssueSprintRelRepository {

    /**
     * 批量创建冲刺issue关联
     *
     * @param issueSprintRelDO issueSprintRelDO
     * @return IssueSprintRelDO
     */
    IssueSprintRelDO createIssueSprintRel(IssueSprintRelDO issueSprintRelDO);
}
