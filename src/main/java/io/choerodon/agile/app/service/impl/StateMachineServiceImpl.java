package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.IssueCreateDTO;
import io.choerodon.agile.api.dto.IssueUpdateDTO;
import io.choerodon.agile.app.assembler.IssueAssembler;
import io.choerodon.agile.app.service.IssueService;
import io.choerodon.agile.app.service.StateMachineService;
import io.choerodon.agile.domain.agile.entity.IssueE;
import io.choerodon.agile.domain.agile.entity.ProjectInfoE;
import io.choerodon.agile.domain.agile.event.CreateIssuePayload;
import io.choerodon.agile.domain.agile.event.CreateSubIssuePayload;
import io.choerodon.agile.domain.agile.repository.IssueRepository;
import io.choerodon.agile.infra.common.enums.SchemeApplyType;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.common.utils.EnumUtil;
import io.choerodon.agile.infra.dataobject.IssueDO;
import io.choerodon.agile.infra.dataobject.IssueDetailDO;
import io.choerodon.agile.infra.dataobject.ProjectInfoDO;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.mapper.IssueMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.statemachine.annotation.*;
import io.choerodon.statemachine.dto.ExecuteResult;
import io.choerodon.statemachine.dto.InputDTO;
import io.choerodon.statemachine.dto.StateMachineConfigDTO;
import io.choerodon.statemachine.feign.InstanceFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * @author shinan.chen
 * @date 2018/10/30
 */
@Component
public class StateMachineServiceImpl implements StateMachineService {

    private static final Logger logger = LoggerFactory.getLogger(StateMachineServiceImpl.class);
    private static final String AGILE_SERVICE = "agile-service";
    private static final String ERROR_ISSUE_STATE_MACHINE_NOT_FOUND = "error.issueStateMachine.notFound";
    private static final String ERROR_ISSUE_NOT_FOUND = "error.issue.notFound";
    private static final String ERROR_INSTANCE_FEGIN_CLIENT_EXECUTE_TRANSFORM = "error.instanceFeignClient.executeTransform";

    private static final String ERROR_PROJECT_INFO_NOT_FOUND = "error.createIssue.projectInfoNotFound";
    private static final String ERROR_ISSUE_STATUS_NOT_FOUND = "error.createIssue.issueStatusNotFound";
    private static final String ERROR_CREATE_ISSUE_CREATE = "error.createIssue.create";
    private static final String ERROR_CREATE_ISSUE_HANDLE_DATA = "error.createIssue.handleData";
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
    private IssueRepository issueRepository;

    /**
     * 复制任务时，因为调用了createIssue和createSubIssue（其中执行的手动事务相当于未提交）导致查不到手动事务提交的issue，
     * 因此需要开放一个新事务设置隔离级别未读取未提交的数据即可取到数据
     *
     * @param projectId
     * @param issueId
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_UNCOMMITTED, rollbackFor = Exception.class)
    public IssueDetailDO queryIssueDetailWithUncommitted(Long projectId, Long issueId) {
        IssueDetailDO issue = issueMapper.queryIssueDetail(projectId, issueId);
        return issue;
    }

    /**
     * 执行转换要开启新事务，否则转换后查询不到最新的数据
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public ExecuteResult executeTransform(Long projectId, Long issueId, Long transformId, Long objectVersionNumber, String applyType) {
        if (!EnumUtil.contain(SchemeApplyType.class, applyType)) {
            throw new CommonException("error.applyType.illegal");
        }
        Long organizationId = ConvertUtil.getOrganizationId(projectId);
        IssueDO issue = issueMapper.selectByPrimaryKey(issueId);
        if (issue == null) {
            throw new CommonException(ERROR_ISSUE_NOT_FOUND);
        }
        //获取状态机id
        Long stateMachineId = issueFeignClient.queryStateMachineId(projectId, applyType, issue.getIssueTypeId()).getBody();
        if (stateMachineId == null) {
            throw new CommonException(ERROR_ISSUE_STATE_MACHINE_NOT_FOUND);
        }
        Long currentStatusId = issue.getStatusId();
        //执行状态转换
        InputDTO inputDTO = new InputDTO(issueId, "updateStatus", null);
        ResponseEntity<ExecuteResult> responseEntity = instanceFeignClient.executeTransform(organizationId, AGILE_SERVICE, stateMachineId, currentStatusId, transformId, inputDTO);
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

    @StartInstance(code = "createIssue")
    public void createIssue(Long instanceId, Long targetStatusId, String input) {
        CreateIssuePayload createIssuePayload = JSONObject.parseObject(input, CreateIssuePayload.class);
        issueService.afterCreateIssue(instanceId, createIssuePayload.getIssueE(), createIssuePayload.getIssueCreateDTO(), createIssuePayload.getProjectInfoE());
    }

    @StartInstance(code = "createSubIssue")
    public void createSubIssue(Long instanceId, Long targetStatusId, String input) {
        CreateSubIssuePayload createSubIssuePayload = JSONObject.parseObject(input, CreateSubIssuePayload.class);
        issueService.afterCreateSubIssue(instanceId, createSubIssuePayload.getIssueE(), createSubIssuePayload.getIssueSubCreateDTO(), createSubIssuePayload.getProjectInfoE());
    }

    @UpdateStatus(code = "updateStatus")
    public void updateStatus(Long instanceId, Long targetStatusId, String input) {
        IssueDO issue = issueMapper.selectByPrimaryKey(instanceId);
        if (issue == null) {
            throw new CommonException("error.updateStatus.instanceId.notFound");
        }
        if (targetStatusId == null) {
            throw new CommonException("error.updateStatus.targetStateId.null");
        }
        if (!issue.getStatusId().equals(targetStatusId)) {
            IssueUpdateDTO issueUpdateDTO = issueAssembler.toTarget(issue, IssueUpdateDTO.class);
            issueUpdateDTO.setStatusId(targetStatusId);
            issueService.handleUpdateIssue(issueUpdateDTO, Collections.singletonList("statusId"), issue.getProjectId());
            logger.info("状态更新成功");
        }
    }
}
