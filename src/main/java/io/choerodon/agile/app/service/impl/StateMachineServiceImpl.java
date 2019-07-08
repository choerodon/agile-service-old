package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.api.validator.IssueValidator;
import io.choerodon.agile.app.assembler.IssueAssembler;
import io.choerodon.agile.app.service.IssueService;
import io.choerodon.agile.app.service.RankService;
import io.choerodon.agile.app.service.StateMachineService;
import io.choerodon.agile.domain.agile.entity.FeatureE;
import io.choerodon.agile.domain.agile.entity.IssueE;
import io.choerodon.agile.domain.agile.entity.PiFeatureE;
import io.choerodon.agile.domain.agile.entity.ProjectInfoE;
import io.choerodon.agile.api.vo.event.CreateIssuePayload;
import io.choerodon.agile.api.vo.event.CreateSubIssuePayload;
import io.choerodon.agile.api.vo.event.ProjectConfig;
import io.choerodon.agile.api.vo.event.StateMachineSchemeDeployCheckIssue;
import io.choerodon.agile.infra.common.enums.SchemeApplyType;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.common.utils.EnumUtil;
import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.agile.infra.dataobject.IssueDO;
import io.choerodon.agile.infra.dataobject.ProjectInfoDO;
import io.choerodon.agile.infra.dataobject.RankDO;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.feign.StateMachineFeignClient;
import io.choerodon.agile.infra.feign.UserFeignClient;
import io.choerodon.agile.infra.mapper.IssueMapper;
import io.choerodon.agile.infra.mapper.ProjectInfoMapper;
import io.choerodon.agile.infra.mapper.RankMapper;
import io.choerodon.agile.infra.repository.FeatureRepository;
import io.choerodon.agile.infra.repository.IssueRepository;
import io.choerodon.agile.infra.repository.PiFeatureRepository;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.statemachine.annotation.Condition;
import io.choerodon.statemachine.annotation.PostAction;
import io.choerodon.statemachine.annotation.UpdateStatus;
import io.choerodon.statemachine.annotation.Validator;
import io.choerodon.statemachine.client.StateMachineClient;
import io.choerodon.statemachine.dto.ExecuteResult;
import io.choerodon.statemachine.dto.InputDTO;
import io.choerodon.statemachine.dto.StateMachineConfigDTO;
import io.choerodon.statemachine.feign.InstanceFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    private static final String RANK = "rank";
    private static final String STATUS_ID = "statusId";
    private static final String STAY_DATE = "stayDate";
    private static final String UPDATE_STATUS = "updateStatus";
    private static final String UPDATE_STATUS_MOVE = "updateStatusMove";
    private static final String ISSUE_FEATURE = "feature";

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
    private ProjectInfoMapper projectInfoMapper;
    @Autowired
    private StateMachineClient stateMachineClient;
    @Autowired
    private StateMachineFeignClient stateMachineFeignClient;
    @Autowired
    private FeatureRepository featureRepository;
    @Autowired
    private PiFeatureRepository piFeatureRepository;
    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private RankMapper rankMapper;
    @Autowired
    private RankService rankService;
    @Autowired
    private IssueValidator issueValidator;

    private void insertRank(Long projectId, Long issueId, String type, RankDTO rankDTO) {
        List<RankDO> rankDOList = new ArrayList<>();
        String rank = null;
        if (rankDTO == null) {
            String minRank = rankMapper.selectMinRank(projectId, type);
            rank = (minRank == null ? RankUtil.mid() : RankUtil.genPre(minRank));
        } else {
            RankDO referenceRank = rankService.getReferenceRank(projectId, rankDTO.getType(), rankDTO.getReferenceIssueId());
            if (rankDTO.getBefore()) {
                String leftRank = rankMapper.selectLeftRank(projectId, rankDTO.getType(), referenceRank.getRank());
                rank = (leftRank == null ? RankUtil.genPre(referenceRank.getRank()) : RankUtil.between(leftRank, referenceRank.getRank()));
            } else {
                String rightRank = rankMapper.selectRightRank(projectId, rankDTO.getType(), referenceRank.getRank());
                rank = (rightRank == null ? RankUtil.genNext(referenceRank.getRank()) : RankUtil.between(referenceRank.getRank(), rightRank));
            }
        }
        RankDO rankDO = new RankDO();
        rankDO.setIssueId(issueId);
        rankDO.setRank(rank);
        rankDOList.add(rankDO);
        rankMapper.batchInsertRank(projectId, type, rankDOList);
    }

    private void initRank(IssueCreateDTO issueCreateDTO, Long issueId, String type) {
        if (issueCreateDTO.getProgramId() != null) {
            List<ProjectRelationshipDTO> projectRelationshipDTOList = userFeignClient.getProjUnderGroup(ConvertUtil.getOrganizationId(issueCreateDTO.getProgramId()), issueCreateDTO.getProgramId(), true).getBody();
            if (projectRelationshipDTOList == null || projectRelationshipDTOList.isEmpty()) {
                return;
            }
            for (ProjectRelationshipDTO projectRelationshipDTO : projectRelationshipDTOList) {
                insertRank(projectRelationshipDTO.getProjectId(), issueId, type, issueCreateDTO.getRankDTO());
            }
        } else if (issueCreateDTO.getProjectId() != null) {
            insertRank(issueCreateDTO.getProjectId(), issueId, type, issueCreateDTO.getRankDTO());
        }
    }


    /**
     * 创建issue，用于敏捷和测试
     *
     * @param issueCreateDTO
     * @param applyType
     * @return
     */
    @Override
    public IssueDTO createIssue(IssueCreateDTO issueCreateDTO, String applyType) {
        issueValidator.checkIssueCreate(issueCreateDTO, applyType);
        IssueE issueE = issueAssembler.toTarget(issueCreateDTO, IssueE.class);
        Long projectId = issueE.getProjectId();
        Long organizationId = ConvertUtil.getOrganizationId(projectId);
        //获取状态机id
        Long stateMachineId = issueFeignClient.queryStateMachineId(projectId, applyType, issueE.getIssueTypeId()).getBody();
        if (stateMachineId == null) {
            throw new CommonException(ERROR_ISSUE_STATE_MACHINE_NOT_FOUND);
        }
        //获取初始状态
        Long initStatusId = instanceFeignClient.queryInitStatusId(organizationId, stateMachineId).getBody();
        if (initStatusId == null) {
            throw new CommonException(ERROR_ISSUE_STATUS_NOT_FOUND);
        }
        //获取项目信息
        ProjectInfoDO projectInfoDO = new ProjectInfoDO();
        projectInfoDO.setProjectId(projectId);
        ProjectInfoE projectInfoE = ConvertHelper.convert(projectInfoMapper.selectOne(projectInfoDO), ProjectInfoE.class);
        if (projectInfoE == null) {
            throw new CommonException(ERROR_PROJECT_INFO_NOT_FOUND);
        }
        //创建issue
        issueE.setApplyType(applyType);
        issueService.handleInitIssue(issueE, initStatusId, projectInfoE);
        Long issueId = issueRepository.create(issueE).getIssueId();
        // 创建史诗，初始化排序
        if ("issue_epic".equals(issueCreateDTO.getTypeCode())) {
            initRank(issueCreateDTO, issueId, "epic");
        }

        // if issueType is feature, create extends table
        if (ISSUE_FEATURE.equals(issueCreateDTO.getTypeCode())) {
            FeatureDTO featureDTO = issueCreateDTO.getFeatureDTO();
            featureDTO.setIssueId(issueId);
            featureDTO.setProjectId(issueCreateDTO.getProjectId());
            if (issueCreateDTO.getProgramId() != null) {
                featureDTO.setProgramId(issueCreateDTO.getProgramId());
            }
            featureRepository.create(ConvertHelper.convert(featureDTO, FeatureE.class));
            if (issueCreateDTO.getPiId() != null && issueCreateDTO.getPiId() != 0L) {
                piFeatureRepository.create(new PiFeatureE(issueId, issueCreateDTO.getPiId(), projectId));
            }
            initRank(issueCreateDTO, issueId, "feature");
        }

        CreateIssuePayload createIssuePayload = new CreateIssuePayload(issueCreateDTO, issueE, projectInfoE);
        InputDTO inputDTO = new InputDTO(issueId, JSON.toJSONString(createIssuePayload));
        //通过状态机客户端创建实例, 反射验证/条件/后置动作
        stateMachineClient.createInstance(organizationId, stateMachineId, inputDTO);
        issueService.afterCreateIssue(issueId, issueE, issueCreateDTO, projectInfoE);
        return issueService.queryIssueCreate(issueCreateDTO.getProjectId(), issueId);
    }

    /**
     * 创建subIssue，用于敏捷
     *
     * @param issueSubCreateDTO
     * @return
     */
    @Override
    public IssueSubDTO createSubIssue(IssueSubCreateDTO issueSubCreateDTO) {
        IssueE subIssueE = issueAssembler.toTarget(issueSubCreateDTO, IssueE.class);
        Long projectId = subIssueE.getProjectId();
        Long organizationId = ConvertUtil.getOrganizationId(projectId);
        //获取状态机id
        Long stateMachineId = issueFeignClient.queryStateMachineId(projectId, SchemeApplyType.AGILE, subIssueE.getIssueTypeId()).getBody();
        if (stateMachineId == null) {
            throw new CommonException(ERROR_ISSUE_STATE_MACHINE_NOT_FOUND);
        }
        //获取初始状态
        Long initStatusId = instanceFeignClient.queryInitStatusId(organizationId, stateMachineId).getBody();
        if (initStatusId == null) {
            throw new CommonException(ERROR_ISSUE_STATUS_NOT_FOUND);
        }
        //获取项目信息
        ProjectInfoDO projectInfoDO = new ProjectInfoDO();
        projectInfoDO.setProjectId(subIssueE.getProjectId());
        ProjectInfoE projectInfoE = ConvertHelper.convert(projectInfoMapper.selectOne(projectInfoDO), ProjectInfoE.class);
        if (projectInfoE == null) {
            throw new CommonException(ERROR_PROJECT_INFO_NOT_FOUND);
        }
        //创建issue
        subIssueE.setApplyType(SchemeApplyType.AGILE);
        //初始化subIssue
        issueService.handleInitSubIssue(subIssueE, initStatusId, projectInfoE);
        Long issueId = issueRepository.create(subIssueE).getIssueId();

        CreateSubIssuePayload createSubIssuePayload = new CreateSubIssuePayload(issueSubCreateDTO, subIssueE, projectInfoE);
        InputDTO inputDTO = new InputDTO(issueId, JSON.toJSONString(createSubIssuePayload));
        //通过状态机客户端创建实例, 反射验证/条件/后置动作
        stateMachineClient.createInstance(organizationId, stateMachineId, inputDTO);
        issueService.afterCreateSubIssue(issueId, subIssueE, issueSubCreateDTO, projectInfoE);
        return issueService.queryIssueSubByCreate(subIssueE.getProjectId(), issueId);
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

    /**
     * 专用于demo的状态转换，demo创建数据不走状态机
     *
     * @param projectId
     * @param issueId
     * @param transformId
     * @param objectVersionNumber
     * @param applyType
     * @param inputDTO
     * @return
     */
    @Override
    public ExecuteResult executeTransformForDemo(Long projectId, Long issueId, Long transformId, Long objectVersionNumber, String applyType, InputDTO inputDTO) {
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
        Long targetStatusId = stateMachineFeignClient.queryDeployTransformForAgile(organizationId, transformId).getBody().getEndStatusId();
        if (UPDATE_STATUS.equals(inputDTO.getInvokeCode())) {
            updateStatus(issueId, targetStatusId, inputDTO.getInput());
        } else if (UPDATE_STATUS_MOVE.equals(inputDTO.getInvokeCode())) {
            updateStatusMove(issueId, targetStatusId, inputDTO.getInput());
        }
        return new ExecuteResult();
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

    @UpdateStatus(code = UPDATE_STATUS)
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

    @UpdateStatus(code = UPDATE_STATUS_MOVE)
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
            issueUpdateDTO.setStayDate(new Date());
            issueService.handleUpdateIssue(issueUpdateDTO, Arrays.asList(STATUS_ID, RANK, STAY_DATE), issue.getProjectId());
            logger.info("状态更新成功");
        } else {
            issueService.handleUpdateIssue(issueUpdateDTO, Collections.singletonList(RANK), issue.getProjectId());
        }
    }

}
