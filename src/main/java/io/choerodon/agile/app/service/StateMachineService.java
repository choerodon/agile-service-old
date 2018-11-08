package io.choerodon.agile.app.service;

import io.choerodon.agile.infra.dataobject.IssueDetailDO;
import io.choerodon.statemachine.dto.ExecuteResult;

/**
 * @author shinan.chen
 * @date 2018/10/30
 */
public interface StateMachineService {

    IssueDetailDO queryIssueDetailWithUncommitted(Long projectId, Long issueId);

    /**
     * 执行状态转换
     *
     * @param projectId
     * @param issueId
     * @param transformId
     * @return
     */
    ExecuteResult executeTransform(Long projectId, Long issueId, Long transformId, Long objectVersionNumber, String applyType);
}
