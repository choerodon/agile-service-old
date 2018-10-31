package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.IssueUpdateDTO;
import io.choerodon.agile.app.assembler.IssueAssembler;
import io.choerodon.agile.app.service.IssueService;
import io.choerodon.agile.app.service.StateMachineService;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.common.utils.ProjectUtil;
import io.choerodon.agile.infra.dataobject.IssueDO;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.mapper.IssueMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.statemachine.annotation.Condition;
import io.choerodon.statemachine.annotation.PostAction;
import io.choerodon.statemachine.annotation.UpdateStatus;
import io.choerodon.statemachine.annotation.Validator;
import io.choerodon.statemachine.dto.ExecuteResult;
import io.choerodon.statemachine.dto.StateMachineConfigDTO;
import io.choerodon.statemachine.feign.InstanceFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * @author shinan.chen
 * @date 2018/10/30
 */
@Component
public class StateMachineServiceImpl implements StateMachineService {

    private static final Logger logger = LoggerFactory.getLogger(StateMachineServiceImpl.class);
    private static final String AGILE = "agile";
    private static final String AGILE_SERVICE = "agile-service";
    private static final String ERROR_ISSUE_STATE_MACHINE_NOT_FOUND = "error.issueStateMachine.notFound";
    private static final String ERROR_ISSUE_NOT_FOUND = "error.issue.notFound";
    private static final String ERROR_INSTANCE_FEGIN_CLIENT_EXECUTE_TRANSFORM = "error.instanceFeignClient.executeTransform";

    @Autowired
    private IssueMapper issueMapper;
    @Autowired
    private IssueService issueService;
    @Autowired
    private IssueAssembler issueAssembler;
    @Autowired
    private InstanceFeignClient instanceFeignClient;
    @Autowired
    private IssueFeignClient issueFeignClient;
    @Autowired
    private ProjectUtil projectUtil;

    @Override
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public ExecuteResult executeTransform(Long projectId, Long issueId, Long transformId) {
        Long organizationId = ConvertUtil.getOrganizationId(projectId);
        IssueDO issue = issueMapper.selectByPrimaryKey(issueId);
        if (issue == null) {
            throw new CommonException(ERROR_ISSUE_NOT_FOUND);
        }
        //获取状态机id
        Long stateMachineId = issueFeignClient.queryStateMachineId(projectId, AGILE, issue.getIssueTypeId()).getBody();
        if (stateMachineId == null) {
            throw new CommonException(ERROR_ISSUE_STATE_MACHINE_NOT_FOUND);
        }
        Long currentStatusId = issue.getStatusId();
        //执行状态转换
        ResponseEntity<ExecuteResult> responseEntity = instanceFeignClient.executeTransform(organizationId, AGILE_SERVICE, stateMachineId, issueId, currentStatusId, transformId);
        if (!responseEntity.getBody().getSuccess()) {
            throw new CommonException(ERROR_INSTANCE_FEGIN_CLIENT_EXECUTE_TRANSFORM);
        }
        return responseEntity.getBody();
    }

    @Condition(code = "just_reporter", name = "仅允许报告人", description = "只有该报告人才能执行转换")
    public Boolean justReporter(Long instanceId, StateMachineConfigDTO configDTO) {
        IssueDO issue = issueMapper.selectByPrimaryKey(instanceId);
        Long currentUserId = DetailsHelper.getUserDetails().getUserId();
        return issue != null && issue.getReporterId() != null && issue.getReporterId().equals(currentUserId);
    }

    @Condition(code = "just_admin", name = "仅允许管理员", description = "只有该管理员才能执行转换")
    public Boolean justAdmin(Long instanceId, StateMachineConfigDTO configDTO) {
        //todo
        return true;
    }

    @Validator(code = "permission_validator", name = "权限校验", description = "校验操作的用户权限")
    public Boolean permissionValidator(Long instanceId, StateMachineConfigDTO configDTO) {
        //todo
        return true;
    }

    @Validator(code = "time_validator", name = "时间校验", description = "根据时间校验权限")
    public Boolean timeValidator(Long instanceId, StateMachineConfigDTO configDTO) {
        //todo
        return true;
    }

    @PostAction(code = "assign_current_user", name = "分派给当前用户", description = "分派给当前用户")
    public void assignCurrentUser(Long instanceId, StateMachineConfigDTO configDTO) {
        //todo
    }

    @PostAction(code = "create_change_log", name = "创建日志", description = "创建日志")
    public void createChangeLog(Long instanceId, StateMachineConfigDTO configDTO) {
        //todo
    }

    @UpdateStatus
    public void updateStatus(Long instanceId, Long targetStatusId) {
        IssueDO issue = issueMapper.selectByPrimaryKey(instanceId);
        if (issue == null) {
            throw new CommonException("error.updateStatus.instanceId.notFound");
        }
        if (targetStatusId == null) {
            throw new CommonException("error.updateStatus.targetStateId.null");
        }
        if(!issue.getStatusId().equals(targetStatusId)){
            IssueUpdateDTO issueUpdateDTO = issueAssembler.toTarget(issue, IssueUpdateDTO.class);
            issueUpdateDTO.setStatusId(targetStatusId);
            issueService.handleUpdateIssue(issueUpdateDTO, Collections.singletonList("statusId"), issue.getProjectId());
            logger.info("状态更新成功");
        }
    }
}
