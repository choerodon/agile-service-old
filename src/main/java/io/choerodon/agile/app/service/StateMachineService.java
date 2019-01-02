package io.choerodon.agile.app.service;

import io.choerodon.agile.api.dto.IssueCreateDTO;
import io.choerodon.agile.api.dto.IssueDTO;
import io.choerodon.agile.api.dto.IssueSubCreateDTO;
import io.choerodon.agile.api.dto.IssueSubDTO;
import io.choerodon.agile.domain.agile.event.ProjectConfig;
import io.choerodon.agile.domain.agile.event.StateMachineSchemeDeployCheckIssue;
import io.choerodon.agile.infra.dataobject.IssueDetailDO;
import io.choerodon.statemachine.dto.ExecuteResult;
import io.choerodon.statemachine.dto.InputDTO;

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
     * @param inputDTO
     * @return
     */
    ExecuteResult executeTransform(Long projectId, Long issueId, Long transformId, Long objectVersionNumber, String applyType, InputDTO inputDTO);

    /**
     * 【内部调用】校验是否可以删除状态机的节点
     *
     * @param organizationId
     * @param statusId
     * @param projectConfigs
     * @return
     */
    Map<String, Object> checkDeleteNode(Long organizationId, Long statusId, List<ProjectConfig> projectConfigs);

    /**
     * 【内部调用】issue服务修改状态机方案时，校验变更的问题类型影响的issue数量
     *
     * @param organizationId
     * @param deployCheckIssue
     * @return
     */
    Map<Long, Long> checkStateMachineSchemeChange(Long organizationId, StateMachineSchemeDeployCheckIssue deployCheckIssue);

}
