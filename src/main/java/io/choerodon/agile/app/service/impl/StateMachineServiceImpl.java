package io.choerodon.agile.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.api.validator.IssueValidator;
import io.choerodon.agile.app.assembler.IssueAssembler;
import io.choerodon.agile.app.service.IssueAccessDataService;
import io.choerodon.agile.app.service.IssueService;
import io.choerodon.agile.app.service.RankService;
import io.choerodon.agile.app.service.StateMachineService;
import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.agile.api.vo.event.CreateIssuePayload;
import io.choerodon.agile.api.vo.event.CreateSubIssuePayload;
import io.choerodon.agile.api.vo.event.ProjectConfig;
import io.choerodon.agile.api.vo.event.StateMachineSchemeDeployCheckIssue;
import io.choerodon.agile.infra.common.enums.SchemeApplyType;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.common.utils.EnumUtil;
import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.feign.UserFeignClient;
import io.choerodon.agile.infra.mapper.IssueMapper;
import io.choerodon.agile.infra.mapper.ProjectInfoMapper;
import io.choerodon.agile.infra.mapper.RankMapper;
import io.choerodon.agile.app.service.FeatureService;
import io.choerodon.agile.app.service.PiFeatureService;
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
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
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
    private IssueAccessDataService issueAccessDataService;
    @Autowired
    private ProjectInfoMapper projectInfoMapper;
    @Autowired
    private StateMachineClient stateMachineClient;
    @Autowired
    private FeatureService featureService;
    @Autowired
    private PiFeatureService piFeatureService;
    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private RankMapper rankMapper;
    @Autowired
    private RankService rankService;
    @Autowired
    private IssueValidator issueValidator;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    private void insertRank(Long projectId, Long issueId, String type, RankVO rankVO) {
        List<RankDTO> rankDTOList = new ArrayList<>();
        String rank = null;
        if (rankVO == null) {
            String minRank = rankMapper.selectMinRank(projectId, type);
            rank = (minRank == null ? RankUtil.mid() : RankUtil.genPre(minRank));
        } else {
            RankDTO referenceRank = rankService.getReferenceRank(projectId, rankVO.getType(), rankVO.getReferenceIssueId());
            if (rankVO.getBefore()) {
                String leftRank = rankMapper.selectLeftRank(projectId, rankVO.getType(), referenceRank.getRank());
                rank = (leftRank == null ? RankUtil.genPre(referenceRank.getRank()) : RankUtil.between(leftRank, referenceRank.getRank()));
            } else {
                String rightRank = rankMapper.selectRightRank(projectId, rankVO.getType(), referenceRank.getRank());
                rank = (rightRank == null ? RankUtil.genNext(referenceRank.getRank()) : RankUtil.between(referenceRank.getRank(), rightRank));
            }
        }
        RankDTO rankDTO = new RankDTO();
        rankDTO.setIssueId(issueId);
        rankDTO.setRank(rank);
        rankDTOList.add(rankDTO);
        rankMapper.batchInsertRank(projectId, type, rankDTOList);
    }

    private void initRank(IssueCreateVO issueCreateVO, Long issueId, String type) {
        if (issueCreateVO.getProgramId() != null) {
            List<ProjectRelationshipVO> projectRelationshipVOList = userFeignClient.getProjUnderGroup(ConvertUtil.getOrganizationId(issueCreateVO.getProgramId()), issueCreateVO.getProgramId(), true).getBody();
            if (projectRelationshipVOList == null || projectRelationshipVOList.isEmpty()) {
                return;
            }
            for (ProjectRelationshipVO projectRelationshipVO : projectRelationshipVOList) {
                insertRank(projectRelationshipVO.getProjectId(), issueId, type, issueCreateVO.getRankVO());
            }
        } else if (issueCreateVO.getProjectId() != null) {
            insertRank(issueCreateVO.getProjectId(), issueId, type, issueCreateVO.getRankVO());
        }
    }


    /**
     * 创建issue，用于敏捷和测试
     *
     * @param issueCreateVO
     * @param applyType
     * @return
     */
    @Override
    public IssueVO createIssue(IssueCreateVO issueCreateVO, String applyType) {
        issueValidator.checkIssueCreate(issueCreateVO, applyType);
        IssueConvertDTO issueConvertDTO = issueAssembler.toTarget(issueCreateVO, IssueConvertDTO.class);
        Long projectId = issueConvertDTO.getProjectId();
        Long organizationId = ConvertUtil.getOrganizationId(projectId);
        //获取状态机id
        Long stateMachineId = issueFeignClient.queryStateMachineId(projectId, applyType, issueConvertDTO.getIssueTypeId()).getBody();
        if (stateMachineId == null) {
            throw new CommonException(ERROR_ISSUE_STATE_MACHINE_NOT_FOUND);
        }
        //获取初始状态
        Long initStatusId = instanceFeignClient.queryInitStatusId(organizationId, stateMachineId).getBody();
        if (initStatusId == null) {
            throw new CommonException(ERROR_ISSUE_STATUS_NOT_FOUND);
        }
        //获取项目信息
        ProjectInfoDTO projectInfoDTO = new ProjectInfoDTO();
        projectInfoDTO.setProjectId(projectId);
        ProjectInfoDTO projectInfo = modelMapper.map(projectInfoMapper.selectOne(projectInfoDTO), ProjectInfoDTO.class);
        if (projectInfo == null) {
            throw new CommonException(ERROR_PROJECT_INFO_NOT_FOUND);
        }
        //创建issue
        issueConvertDTO.setApplyType(applyType);
        issueService.handleInitIssue(issueConvertDTO, initStatusId, projectInfo);
        Long issueId = issueAccessDataService.create(issueConvertDTO).getIssueId();
        // 创建史诗，初始化排序
        if ("issue_epic".equals(issueCreateVO.getTypeCode())) {
            initRank(issueCreateVO, issueId, "epic");
        }

        // if issueType is feature, create extends table
        if (ISSUE_FEATURE.equals(issueCreateVO.getTypeCode())) {
            FeatureVO featureVO = issueCreateVO.getFeatureVO();
            featureVO.setIssueId(issueId);
            featureVO.setProjectId(issueCreateVO.getProjectId());
            if (issueCreateVO.getProgramId() != null) {
                featureVO.setProgramId(issueCreateVO.getProgramId());
            }
            featureService.create(modelMapper.map(featureVO, FeatureDTO.class));
            if (issueCreateVO.getPiId() != null && issueCreateVO.getPiId() != 0L) {
                piFeatureService.create(new PiFeatureDTO(issueId, issueCreateVO.getPiId(), projectId));
            }
            initRank(issueCreateVO, issueId, "feature");
        }

        CreateIssuePayload createIssuePayload = new CreateIssuePayload(issueCreateVO, issueConvertDTO, projectInfo);
        InputDTO inputDTO = new InputDTO(issueId, JSON.toJSONString(createIssuePayload));
        //通过状态机客户端创建实例, 反射验证/条件/后置动作
        stateMachineClient.createInstance(organizationId, stateMachineId, inputDTO);
        issueService.afterCreateIssue(issueId, issueConvertDTO, issueCreateVO, projectInfo);
        return issueService.queryIssueCreate(issueCreateVO.getProjectId(), issueId);
    }

    /**
     * 创建subIssue，用于敏捷
     *
     * @param issueSubCreateVO
     * @return
     */
    @Override
    public IssueSubVO createSubIssue(IssueSubCreateVO issueSubCreateVO) {
        IssueConvertDTO subIssueConvertDTO = issueAssembler.toTarget(issueSubCreateVO, IssueConvertDTO.class);
        Long projectId = subIssueConvertDTO.getProjectId();
        Long organizationId = ConvertUtil.getOrganizationId(projectId);
        //获取状态机id
        Long stateMachineId = issueFeignClient.queryStateMachineId(projectId, SchemeApplyType.AGILE, subIssueConvertDTO.getIssueTypeId()).getBody();
        if (stateMachineId == null) {
            throw new CommonException(ERROR_ISSUE_STATE_MACHINE_NOT_FOUND);
        }
        //获取初始状态
        Long initStatusId = instanceFeignClient.queryInitStatusId(organizationId, stateMachineId).getBody();
        if (initStatusId == null) {
            throw new CommonException(ERROR_ISSUE_STATUS_NOT_FOUND);
        }
        //获取项目信息
        ProjectInfoDTO projectInfoDTO = new ProjectInfoDTO();
        projectInfoDTO.setProjectId(subIssueConvertDTO.getProjectId());
        ProjectInfoDTO projectInfo = modelMapper.map(projectInfoMapper.selectOne(projectInfoDTO), ProjectInfoDTO.class);
        if (projectInfo == null) {
            throw new CommonException(ERROR_PROJECT_INFO_NOT_FOUND);
        }
        //创建issue
        subIssueConvertDTO.setApplyType(SchemeApplyType.AGILE);
        //初始化subIssue
        issueService.handleInitSubIssue(subIssueConvertDTO, initStatusId, projectInfo);
        Long issueId = issueAccessDataService.create(subIssueConvertDTO).getIssueId();

        CreateSubIssuePayload createSubIssuePayload = new CreateSubIssuePayload(issueSubCreateVO, subIssueConvertDTO, projectInfo);
        InputDTO inputDTO = new InputDTO(issueId, JSON.toJSONString(createSubIssuePayload));
        //通过状态机客户端创建实例, 反射验证/条件/后置动作
        stateMachineClient.createInstance(organizationId, stateMachineId, inputDTO);
        issueService.afterCreateSubIssue(issueId, subIssueConvertDTO, issueSubCreateVO, projectInfo);
        return issueService.queryIssueSubByCreate(subIssueConvertDTO.getProjectId(), issueId);
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
        IssueDTO issue = issueMapper.selectByPrimaryKey(issueId);
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
        IssueDTO issue = issueMapper.selectByPrimaryKey(issueId);
        if (issue == null) {
            throw new CommonException(ERROR_ISSUE_NOT_FOUND);
        }
        //获取状态机id
        Long stateMachineId = issueFeignClient.queryStateMachineId(projectId, applyType, issue.getIssueTypeId()).getBody();
        if (stateMachineId == null) {
            throw new CommonException(ERROR_ISSUE_STATE_MACHINE_NOT_FOUND);
        }
        Long targetStatusId = issueFeignClient.queryDeployTransformForAgile(organizationId, transformId).getBody().getEndStatusId();
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
                List<IssueDTO> issueDTOS = issueMapper.queryByIssueTypeIdsAndApplyType(projectConfig.getProjectId(), projectConfig.getApplyType(), issueTypeIds);
                Map<Long, Long> issueCounts = issueDTOS.stream().collect(Collectors.groupingBy(IssueDTO::getIssueTypeId, Collectors.counting()));
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
        IssueDTO issue = issueMapper.selectByPrimaryKey(instanceId);
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
        IssueDTO issue = issueMapper.selectByPrimaryKey(instanceId);
        if (issue == null) {
            throw new CommonException("error.updateStatus.instanceId.notFound");
        }
        if (targetStatusId == null) {
            throw new CommonException("error.updateStatus.targetStateId.null");
        }
        if (!issue.getStatusId().equals(targetStatusId)) {
            IssueUpdateVO issueUpdateVO = issueAssembler.toTarget(issue, IssueUpdateVO.class);
            issueUpdateVO.setStatusId(targetStatusId);
            issueService.handleUpdateIssue(issueUpdateVO, Collections.singletonList(STATUS_ID), issue.getProjectId());
            logger.info("状态更新成功");
        }
    }

    @UpdateStatus(code = UPDATE_STATUS_MOVE)
    public void updateStatusMove(Long instanceId, Long targetStatusId, String input) {
        IssueDTO issue = issueMapper.selectByPrimaryKey(instanceId);
        if (issue == null) {
            throw new CommonException("error.updateStatus.instanceId.notFound");
        }
        if (targetStatusId == null) {
            throw new CommonException("error.updateStatus.targetStateId.null");
        }
        IssueUpdateVO issueUpdateVO = issueAssembler.toTarget(issue, IssueUpdateVO.class);
        if (input != null && !Objects.equals(input, "null")) {
            JSONObject jsonObject = JSON.parseObject(input, JSONObject.class);
            issueUpdateVO.setRank(jsonObject.getString(RANK));
        }
        if (!issue.getStatusId().equals(targetStatusId)) {
            issueUpdateVO.setStatusId(targetStatusId);
            issueUpdateVO.setStayDate(new Date());
            issueService.handleUpdateIssue(issueUpdateVO, Arrays.asList(STATUS_ID, RANK, STAY_DATE), issue.getProjectId());
            logger.info("状态更新成功");
        } else {
            issueService.handleUpdateIssue(issueUpdateVO, Collections.singletonList(RANK), issue.getProjectId());
        }
    }

}
