package io.choerodon.agile.app.service;

import io.choerodon.statemachine.dto.ExecuteResult;

/**
 * @author shinan.chen
 * @date 2018/10/30
 */
public interface StateMachineService {
    /**
     * 执行状态转换
     * @param projectId
     * @param issueId
     * @param transformId
     * @return
     */
    ExecuteResult executeTransform(Long projectId, Long issueId, Long transformId);
}
