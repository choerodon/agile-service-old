package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.app.assembler.IssueAssembler;
import io.choerodon.agile.app.service.IssueService;
import io.choerodon.agile.app.service.StateMachineService;
import io.choerodon.agile.domain.agile.entity.IssueE;
import io.choerodon.agile.domain.agile.entity.ProjectInfoE;
import io.choerodon.agile.domain.agile.event.CreateIssuePayload;
import io.choerodon.agile.domain.agile.event.CreateSubIssuePayload;
import io.choerodon.agile.domain.agile.event.ProjectConfig;
import io.choerodon.agile.domain.agile.event.StateMachineSchemeDeployCheckIssue;
import io.choerodon.agile.domain.agile.repository.IssueRepository;
import io.choerodon.agile.infra.common.enums.SchemeApplyType;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.common.utils.EnumUtil;
import io.choerodon.agile.infra.dataobject.IssueDO;
import io.choerodon.agile.infra.dataobject.IssueDetailDO;
import io.choerodon.agile.infra.dataobject.ProjectInfoDO;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.mapper.IssueMapper;
import io.choerodon.agile.infra.mapper.ProjectInfoMapper;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.*;
import java.util.stream.Collectors;

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
    private static final String FIELD_RANK = "Rank";
    private static final String PROJECT_ID = "projectId";
    private static final String RANK = "rank";
    private static final String STATUS_ID = "statusId";
    private static final String RANK_HIGHER = "评级更高";
    private static final String RANK_LOWER = "评级更低";

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
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private ProjectInfoMapper projectInfoMapper;

    /**
     * 创建issue，由于状态机需要回调，采用手动提交事务
     *
     * @param issueCreateDTO
     * @param applyType
     * @return
     */
    @Override
    public synchronized IssueDTO createIssue(IssueCreateDTO issueCreateDTO, String applyType) {
        if (!EnumUtil.contain(SchemeApplyType.class, applyType)) {
            throw new CommonException("error.applyType.illegal");
        }
        if ("agile".equals(applyType) && issueCreateDTO.getEpicName() != null && issueService.checkEpicName(issueCreateDTO.getProjectId(), issueCreateDTO.getEpicName())) {
            throw new CommonException("error.epicName.exist");
        }
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //事物隔离级别：开启新事务
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        //获得事务状态
        TransactionStatus status = transactionManager.getTransaction(def);
        IssueE issueE = issueAssembler.toTarget(issueCreateDTO, IssueE.class);
        Long projectId = issueE.getProjectId();
        Long organizationId = ConvertUtil.getOrganizationId(projectId);
        Long issueId;
        ProjectInfoE projectInfoE;
        Long stateMachineId;
        try {
            //获取状态机id
            stateMachineId = issueFeignClient.queryStateMachineId(projectId, applyType, issueE.getIssueTypeId()).getBody();
            if (stateMachineId == null) {
                throw new CommonException(ERROR_ISSUE_STATE_MACHINE_NOT_FOUND);
            }
            Long initStatusId = instanceFeignClient.queryInitStatusId(organizationId, stateMachineId).getBody();
            if (initStatusId == null) {
                throw new CommonException(ERROR_ISSUE_STATUS_NOT_FOUND);
            }
            //处理编号
            ProjectInfoDO projectInfoDO = new ProjectInfoDO();
            projectInfoDO.setProjectId(projectId);
            projectInfoE = ConvertHelper.convert(projectInfoMapper.selectOne(projectInfoDO), ProjectInfoE.class);
            if (projectInfoE == null) {
                throw new CommonException(ERROR_PROJECT_INFO_NOT_FOUND);
            }
            //创建issue
            issueE.setApplyType(applyType);
            issueService.handleInitIssue(issueE, initStatusId, projectInfoE);
            issueId = issueRepository.create(issueE).getIssueId();
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new CommonException(ERROR_CREATE_ISSUE_CREATE, e);
        }

        CreateIssuePayload createIssuePayload = new CreateIssuePayload(issueCreateDTO, issueE, projectInfoE);
        InputDTO inputDTO = new InputDTO(issueId, "createIssue", JSON.toJSONString(createIssuePayload));
        ResponseEntity<ExecuteResult> executeResult = instanceFeignClient.startInstance(organizationId, AGILE_SERVICE, stateMachineId, inputDTO);
        //feign调用执行失败，抛出异常回滚
        if (!executeResult.getBody().getSuccess()) {
            //手动回滚数据
            issueMapper.batchDeleteIssues(issueE.getProjectId(), Collections.singletonList(issueId));
            logger.error(executeResult.getBody().getErrorMessage());
            return null;
        }
        return issueService.queryIssueCreate(issueCreateDTO.getProjectId(), issueId);
    }

    /**
     * 创建subIssue，由于状态机需要回调，采用手动提交事务
     *
     * @param issueSubCreateDTO
     * @return
     */
    @Override
    public IssueSubDTO createSubIssue(IssueSubCreateDTO issueSubCreateDTO) {
        IssueE subIssueE = issueAssembler.toTarget(issueSubCreateDTO, IssueE.class);
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //事物隔离级别，开启新事务，这样会比较安全些
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        //获得事务状态
        TransactionStatus status = transactionManager.getTransaction(def);
        Long projectId = subIssueE.getProjectId();
        Long organizationId = ConvertUtil.getOrganizationId(projectId);
        Long issueId;
        ProjectInfoE projectInfoE;
        Long stateMachineId;
        try {
            //获取状态机id
            stateMachineId = issueFeignClient.queryStateMachineId(projectId, SchemeApplyType.AGILE, subIssueE.getIssueTypeId()).getBody();
            if (stateMachineId == null) {
                throw new CommonException(ERROR_ISSUE_STATE_MACHINE_NOT_FOUND);
            }
            Long initStatusId = instanceFeignClient.queryInitStatusId(organizationId, stateMachineId).getBody();
            if (initStatusId == null) {
                throw new CommonException(ERROR_ISSUE_STATUS_NOT_FOUND);
            }
            //处理编号
            ProjectInfoDO projectInfoDO = new ProjectInfoDO();
            projectInfoDO.setProjectId(subIssueE.getProjectId());
            projectInfoE = ConvertHelper.convert(projectInfoMapper.selectOne(projectInfoDO), ProjectInfoE.class);
            if (projectInfoE == null) {
                throw new CommonException(ERROR_PROJECT_INFO_NOT_FOUND);
            }
            //创建issue
            subIssueE.setApplyType(SchemeApplyType.AGILE);
            //初始化subIssue
            issueService.handleInitSubIssue(subIssueE, initStatusId, projectInfoE);
            issueId = issueRepository.create(subIssueE).getIssueId();

            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new CommonException(ERROR_CREATE_ISSUE_CREATE, e);
        }

        CreateSubIssuePayload createSubIssuePayload = new CreateSubIssuePayload(issueSubCreateDTO, subIssueE, projectInfoE);
        InputDTO inputDTO = new InputDTO(issueId, "createSubIssue", JSON.toJSONString(createSubIssuePayload));
        ResponseEntity<ExecuteResult> executeResult = instanceFeignClient.startInstance(organizationId, AGILE_SERVICE, stateMachineId, inputDTO);
        //feign调用执行失败，抛出异常回滚
        if (!executeResult.getBody().getSuccess()) {
            //手动回滚数据
            issueMapper.batchDeleteIssues(subIssueE.getProjectId(), Collections.singletonList(issueId));
            logger.error(executeResult.getBody().getErrorMessage());
            return null;
        }
        return issueService.queryIssueSubByCreate(subIssueE.getProjectId(), issueId);
    }

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
    public ExecuteResult executeTransform(Long projectId, Long issueId, Long transformId, Long objectVersionNumber, String applyType, InputDTO inputDTO) {
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
        ResponseEntity<ExecuteResult> responseEntity = instanceFeignClient.executeTransform(organizationId, AGILE_SERVICE, stateMachineId, currentStatusId, transformId, inputDTO);
        if (!responseEntity.getBody().getSuccess()) {
            throw new CommonException(ERROR_INSTANCE_FEGIN_CLIENT_EXECUTE_TRANSFORM);
        }
        return responseEntity.getBody();
    }

    @Override
    public Map<String, Object> checkDeleteNode(Long organizationId, Long statusId, List<ProjectConfig> projectConfigs) {
        Map<String, Object> result = new HashMap<>(2);
        Long count = 0L;
        for (ProjectConfig projectConfig : projectConfigs) {
            Long projectId = projectConfig.getProjectId();
            String applyType = projectConfig.getApplyType();
            count = count + issueMapper.querySizeByApplyTypeAndStatusId(projectId, applyType, statusId);
        }
        if (count.equals(0L)) {
            result.put("canDelete", true);
        } else {
            result.put("canDelete", false);
            result.put("count", count);
        }
        return result;
    }

    @Override
    public Map<Long, Long> checkStateMachineSchemeChange(Long organizationId, StateMachineSchemeDeployCheckIssue deployCheckIssue) {
        List<ProjectConfig> projectConfigs = deployCheckIssue.getProjectConfigs();
        List<Long> issueTypeIds = deployCheckIssue.getIssueTypeIds();
        Map<Long, Long> result = new HashMap<>(issueTypeIds.size());
        if (!issueTypeIds.isEmpty()) {
            issueTypeIds.forEach(issueTypeId -> result.put(issueTypeId, 0L));
            //计算出所有有影响的issue数量，根据issueTypeId分类
            projectConfigs.forEach(projectConfig -> {
                List<IssueDO> issueDOs = issueMapper.queryByIssueTypeIdsAndApplyType(projectConfig.getProjectId(), projectConfig.getApplyType(), issueTypeIds);
                Map<Long, Long> issueCounts = issueDOs.stream().collect(Collectors.groupingBy(IssueDO::getIssueTypeId, Collectors.counting()));
                for (Map.Entry<Long, Long> entry : issueCounts.entrySet()) {
                    Long issueTypeId = entry.getKey();
                    Long count = entry.getValue();
                    result.put(issueTypeId, result.get(issueTypeId) + count);
                }
            });
        }

        return result;
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
            issueService.handleUpdateIssue(issueUpdateDTO, Collections.singletonList(STATUS_ID), issue.getProjectId());
            logger.info("状态更新成功");
        }
    }

    @UpdateStatus(code = "updateStatusMove")
    public void updateStatusMove(Long instanceId, Long targetStatusId, String input) {
        IssueDO issue = issueMapper.selectByPrimaryKey(instanceId);
        if (issue == null) {
            throw new CommonException("error.updateStatus.instanceId.notFound");
        }
        if (targetStatusId == null) {
            throw new CommonException("error.updateStatus.targetStateId.null");
        }
        IssueUpdateDTO issueUpdateDTO = issueAssembler.toTarget(issue, IssueUpdateDTO.class);
        if (input != null && !Objects.equals(input, "null")) {
            JSONObject jsonObject = JSON.parseObject(input, JSONObject.class);
            issueUpdateDTO.setRank(jsonObject.getString(RANK));
        }
        if (!issue.getStatusId().equals(targetStatusId)) {
            issueUpdateDTO.setStatusId(targetStatusId);
            issueService.handleUpdateIssue(issueUpdateDTO, Arrays.asList(STATUS_ID, RANK), issue.getProjectId());
            logger.info("状态更新成功");
        } else {
            issueService.handleUpdateIssue(issueUpdateDTO, Collections.singletonList(RANK), issue.getProjectId());
        }
    }

}
