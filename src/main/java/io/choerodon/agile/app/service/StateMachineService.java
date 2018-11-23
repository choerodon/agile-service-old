package io.choerodon.agile.app.service;

import io.choerodon.agile.api.dto.IssueCreateDTO;
import io.choerodon.agile.api.dto.IssueDTO;
import io.choerodon.agile.api.dto.IssueSubCreateDTO;
import io.choerodon.agile.api.dto.IssueSubDTO;
import io.choerodon.agile.domain.agile.event.StateMachineSchemeDeployCheckIssue;
import io.choerodon.agile.domain.agile.event.StateMachineSchemeDeployUpdateIssue;
import io.choerodon.agile.infra.dataobject.IssueDetailDO;
import io.choerodon.statemachine.dto.ExecuteResult;

import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @date 2018/10/30
 */
public interface StateMachineService {

    IssueDTO createIssue(IssueCreateDTO issueCreateDTO, String applyType);

    IssueSubDTO createSubIssue(IssueSubCreateDTO issueSubCreateDTO);

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

    /**
     * 【内部调用】校验是否可以删除状态机的节点
     *
     * @param organizationId
     * @param statusId
     * @param issueTypeIdsMap
     * @return
     */
    Map<String, Object> checkDeleteNode(Long organizationId, Long statusId, Map<Long, List<Long>> issueTypeIdsMap);

    /**
     * 【内部调用】issue服务修改状态机方案时，校验变更的问题类型影响的issue数量
     *
     * @param organizationId
     * @param deployCheckIssue
     * @return
     */
    Map<Long, Long> checkStateMachineSchemeChange(Long organizationId, StateMachineSchemeDeployCheckIssue deployCheckIssue);

    /**
     * 【内部调用】issue服务修改状态机方案时，对问题类型的状态进行重新匹配后，对issue的批量更新
     *
     * @param organizationId
     * @param deployUpdateIssue
     * @return
     */
    Boolean updateStateMachineSchemeChange(Long organizationId, StateMachineSchemeDeployUpdateIssue deployUpdateIssue);
}
