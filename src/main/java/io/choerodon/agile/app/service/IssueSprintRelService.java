package io.choerodon.agile.app.service;

import io.choerodon.agile.infra.dataobject.IssueSprintRelDTO;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/6
 */

public interface IssueSprintRelService {

    /**
     * 创建冲刺issue关联
     *
     * @param issueSprintRelDTO issueSprintRelDTO
     * @return IssueSprintRelDTO
     */
    IssueSprintRelDTO createIssueSprintRel(IssueSprintRelDTO issueSprintRelDTO);
}
