package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.IssueCreateVO;
import io.choerodon.agile.api.vo.IssueSubVO;
import io.choerodon.agile.api.vo.IssueVO;
import io.choerodon.agile.api.vo.IssueSubCreateVO;
import io.choerodon.agile.api.vo.event.ProjectConfig;
import io.choerodon.agile.api.vo.event.StateMachineSchemeDeployCheckIssue;
import io.choerodon.statemachine.dto.ExecuteResult;
import io.choerodon.statemachine.dto.InputDTO;

import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @date 2018/10/30
 */
public interface StateMachineClientService {

    IssueVO createIssue(IssueCreateVO issueCreateVO, String applyType);

    IssueSubVO createSubIssue(IssueSubCreateVO issueSubCreateVO);

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

    ExecuteResult executeTransformForDemo(Long projectId, Long issueId, Long transformId, Long objectVersionNumber, String applyType, InputDTO inputDTO);
}
