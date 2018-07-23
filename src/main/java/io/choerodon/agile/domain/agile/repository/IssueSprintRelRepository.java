package io.choerodon.agile.domain.agile.repository;

import io.choerodon.agile.domain.agile.entity.IssueSprintRelE;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/6
 */

public interface IssueSprintRelRepository {

    /**
     * 创建冲刺issue关联
     *
     * @param issueSprintRelE issueSprintRelE
     * @return IssueSprintRelE
     */
    IssueSprintRelE createIssueSprintRel(IssueSprintRelE issueSprintRelE);
}
