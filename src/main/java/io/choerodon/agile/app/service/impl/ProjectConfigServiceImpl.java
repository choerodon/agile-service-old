package io.choerodon.agile.app.service.impl;


import com.alibaba.fastjson.JSON;
import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.api.vo.event.StatusPayload;
import io.choerodon.agile.api.vo.event.TransformInfo;
import io.choerodon.agile.api.vo.event.TransformVO;
import io.choerodon.agile.app.service.*;
import io.choerodon.agile.infra.dataobject.IssueTypeDTO;
import io.choerodon.agile.infra.dataobject.ProjectConfigDTO;
import io.choerodon.agile.infra.enums.SchemeApplyType;
import io.choerodon.agile.infra.enums.SchemeType;
import io.choerodon.agile.infra.exception.RemoveStatusException;
import io.choerodon.agile.infra.mapper.IssueTypeMapper;
import io.choerodon.agile.infra.mapper.IssueTypeSchemeConfigMapper;
import io.choerodon.agile.infra.mapper.IssueTypeSchemeMapper;
import io.choerodon.agile.infra.mapper.ProjectConfigMapper;
import io.choerodon.agile.infra.utils.EnumUtil;
import io.choerodon.agile.infra.utils.ProjectUtil;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import io.choerodon.asgard.saga.feign.SagaClient;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.statemachine.feign.InstanceFeignClient;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author shinan.chen
 * @Date 2018/10/24
 */
@Service
@RefreshScope
public class ProjectConfigServiceImpl implements ProjectConfigService {

    private static final String AGILE_SERVICE = "agile-service";
    private static final String FLAG = "flag";
    private static final String MESSAGE = "message";
    private static final String STATEMACHINEID = "stateMachineId";
    private static final String ERROR_ISSUE_STATE_MACHINE_NOT_FOUND = "error.issueStateMachine.notFound";
    private static final String ERROR_ISSUE_STATUS_NOT_FOUND = "error.createIssue.issueStatusNotFound";
    private static final String ERROR_APPLYTYPE_ILLEGAL = "error.applyType.illegal";
    private static final String ERROR_STATEMACHINESCHEMEID_NULL = "error.stateMachineSchemeId.null";

    @Autowired
    private ProjectConfigMapper projectConfigMapper;
    @Autowired
    private IssueTypeMapper issueTypeMapper;
    @Autowired
    private IssueTypeSchemeMapper issueTypeSchemeMapper;
    @Autowired
    private IssueTypeSchemeConfigMapper issueTypeSchemeConfigMapper;
    @Autowired
    private StateMachineSchemeConfigService stateMachineSchemeConfigService;
    @Autowired
    private IssueTypeSchemeService issueTypeSchemeService;
    @Autowired
    private StateMachineSchemeService stateMachineSchemeService;
    @Autowired
    private ProjectUtil projectUtil;
    @Autowired
    private ProjectConfigService projectConfigService;
    @Autowired
    private InstanceFeignClient instanceFeignClient;
    @Autowired
    private SagaClient sagaClient;
    @Autowired
    private StatusService statusService;
    @Autowired
    private InstanceService instanceService;
    @Autowired
    private StateMachineTransformService transformService;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ProjectConfigDTO create(Long projectId, Long schemeId, String schemeType, String applyType) {
        if (!EnumUtil.contain(SchemeType.class, schemeType)) {
            throw new CommonException("error.schemeType.illegal");
        }
        if (!EnumUtil.contain(SchemeApplyType.class, applyType)) {
            throw new CommonException(ERROR_APPLYTYPE_ILLEGAL);
        }
        ProjectConfigDTO projectConfig = new ProjectConfigDTO(projectId, schemeId, schemeType, applyType);
        //保证幂等性
        List<ProjectConfigDTO> configs = projectConfigMapper.select(projectConfig);
        if (!configs.isEmpty()) {
            return configs.get(0);
        }
        int result = projectConfigMapper.insert(projectConfig);
        if (result != 1) {
            throw new CommonException("error.projectConfig.create");
        }

        //若是关联状态机方案，设置状态机方案、状态机为活跃
        if (schemeType.equals(SchemeType.STATE_MACHINE)) {
            stateMachineSchemeService.activeSchemeWithRefProjectConfig(schemeId);
        }
        return projectConfig;
    }

    @Override
    public ProjectConfigDetailVO queryById(Long projectId) {
        Long organizationId = projectUtil.getOrganizationId(projectId);
        List<ProjectConfigDTO> projectConfigs = projectConfigMapper.queryByProjectId(projectId);
        Map<String, List<ProjectConfigDTO>> configMap = projectConfigs.stream().collect(Collectors.groupingBy(ProjectConfigDTO::getSchemeType));
        ProjectConfigDetailVO projectConfigDetailVO = new ProjectConfigDetailVO();
        projectConfigDetailVO.setProjectId(projectId);
        //获取问题类型方案
        List<ProjectConfigDTO> issueTypeSchemeConfigs = configMap.get(SchemeType.ISSUE_TYPE);
        if (issueTypeSchemeConfigs != null && !issueTypeSchemeConfigs.isEmpty()) {
            Map<String, IssueTypeSchemeVO> issueTypeSchemeMap = new HashMap<>(issueTypeSchemeConfigs.size());
            for (ProjectConfigDTO projectConfig : issueTypeSchemeConfigs) {
                IssueTypeSchemeVO issueTypeSchemeVO = issueTypeSchemeService.queryById(organizationId, projectConfig.getSchemeId());
                issueTypeSchemeMap.put(projectConfig.getApplyType(), issueTypeSchemeVO);
            }
            projectConfigDetailVO.setIssueTypeSchemeMap(issueTypeSchemeMap);
        }
        //获取状态机方案
        List<ProjectConfigDTO> stateMachineSchemeConfigs = configMap.get(SchemeType.STATE_MACHINE);
        if (stateMachineSchemeConfigs != null && !stateMachineSchemeConfigs.isEmpty()) {
            Map<String, StateMachineSchemeVO> stateMachineSchemeMap = new HashMap<>(stateMachineSchemeConfigs.size());
            for (ProjectConfigDTO projectConfig : stateMachineSchemeConfigs) {
                StateMachineSchemeVO stateMachineSchemeVO = stateMachineSchemeService.querySchemeWithConfigById(false, organizationId, projectConfig.getSchemeId());
                stateMachineSchemeMap.put(projectConfig.getApplyType(), stateMachineSchemeVO);
            }
            projectConfigDetailVO.setStateMachineSchemeMap(stateMachineSchemeMap);
        }
        return projectConfigDetailVO;
    }

    @Override
    public List<IssueTypeVO> queryIssueTypesByProjectId(Long projectId, String applyType) {
        if (!EnumUtil.contain(SchemeApplyType.class, applyType)) {
            throw new CommonException(ERROR_APPLYTYPE_ILLEGAL);
        }
        Long organizationId = projectUtil.getOrganizationId(projectId);
        ProjectConfigDTO projectConfig = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.ISSUE_TYPE, applyType);
        //获取问题类型方案
        if (projectConfig.getSchemeId() != null) {
            //根据方案配置表获取 问题类型
            List<IssueTypeDTO> issueTypes = issueTypeMapper.queryBySchemeId(organizationId, projectConfig.getSchemeId());
            return modelMapper.map(issueTypes, new TypeToken<List<IssueTypeVO>>() {
            }.getType());
        } else {
            throw new CommonException("error.queryIssueTypesByProjectId.issueTypeSchemeId.null");
        }
    }

    @Override
    public List<IssueTypeWithStateMachineIdVO> queryIssueTypesWithStateMachineIdByProjectId(Long projectId, String applyType) {
        if (!EnumUtil.contain(SchemeApplyType.class, applyType)) {
            throw new CommonException(ERROR_APPLYTYPE_ILLEGAL);
        }
        Long organizationId = projectUtil.getOrganizationId(projectId);
        Long issueTypeSchemeId = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.ISSUE_TYPE, applyType).getSchemeId();
        Long stateMachineSchemeId = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.STATE_MACHINE, applyType).getSchemeId();
        if (issueTypeSchemeId == null) {
            throw new CommonException("error.issueTypeSchemeId.null");
        }
        if (stateMachineSchemeId == null) {
            throw new CommonException(ERROR_STATEMACHINESCHEMEID_NULL);
        }
        //根据方案配置表获取 问题类型
        List<IssueTypeDTO> issueTypes = issueTypeMapper.queryBySchemeId(organizationId, issueTypeSchemeId);
        //根据方案配置表获取 状态机与问题类型的对应关系
        List<StateMachineSchemeConfigVO> configs = stateMachineSchemeConfigService.queryBySchemeId(false, organizationId, stateMachineSchemeId);
        Map<Long, Long> map = configs.stream().collect(Collectors.toMap(StateMachineSchemeConfigVO::getIssueTypeId, StateMachineSchemeConfigVO::getStateMachineId));
        Long defaultStateMachineId = stateMachineSchemeConfigService.selectDefault(false, organizationId, stateMachineSchemeId).getStateMachineId();
        List<IssueTypeWithStateMachineIdVO> issueTypeWithStateMachineIds = modelMapper.map(issueTypes, new TypeToken<List<IssueTypeWithStateMachineIdVO>>() {
        }.getType());
        issueTypeWithStateMachineIds.forEach(x -> {
            Long stateMachineId = map.get(x.getId());
            if (stateMachineId != null) {
                x.setStateMachineId(stateMachineId);
            } else {
                x.setStateMachineId(defaultStateMachineId);
            }
        });
        return issueTypeWithStateMachineIds;
    }

    @Override
    public List<StatusVO> queryStatusByIssueTypeId(Long projectId, Long issueTypeId, String applyType) {
        Long organizationId = projectUtil.getOrganizationId(projectId);
        Long stateMachineSchemeId = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.STATE_MACHINE, applyType).getSchemeId();
        if (stateMachineSchemeId == null) {
            throw new CommonException(ERROR_STATEMACHINESCHEMEID_NULL);
        }
        //获取状态机
        Long stateMachineId = stateMachineSchemeConfigService.queryStateMachineIdBySchemeIdAndIssueTypeId(false, organizationId, stateMachineSchemeId, issueTypeId);
        return statusService.queryByStateMachineIds(organizationId, Collections.singletonList(stateMachineId));
    }

    @Override
    public List<StatusVO> queryStatusByProjectId(Long projectId, String applyType) {
        Long organizationId = projectUtil.getOrganizationId(projectId);
        Long stateMachineSchemeId = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.STATE_MACHINE, applyType).getSchemeId();
        if (stateMachineSchemeId == null) {
            throw new CommonException(ERROR_STATEMACHINESCHEMEID_NULL);
        }
        //获取状态机ids
        List<Long> stateMachineIds = stateMachineSchemeConfigService.queryBySchemeId(false, organizationId, stateMachineSchemeId)
                .stream().map(StateMachineSchemeConfigVO::getStateMachineId).collect(Collectors.toList());
        return statusService.queryByStateMachineIds(organizationId, stateMachineIds);
    }

    @Override
    public List<TransformVO> queryTransformsByProjectId(Long projectId, Long currentStatusId, Long issueId, Long issueTypeId, String applyType) {
        if (!EnumUtil.contain(SchemeApplyType.class, applyType)) {
            throw new CommonException(ERROR_APPLYTYPE_ILLEGAL);
        }
        Long organizationId = projectUtil.getOrganizationId(projectId);
        ProjectConfigDTO projectConfig = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.STATE_MACHINE, applyType);
        //获取状态机方案
        if (projectConfig.getSchemeId() != null) {
            //获取状态机
            Long stateMachineId = stateMachineSchemeConfigService.queryStateMachineIdBySchemeIdAndIssueTypeId(false, organizationId, projectConfig.getSchemeId(), issueTypeId);
            //获取当前状态拥有的转换
            List<TransformInfo> transformInfos = instanceService.queryListTransform(organizationId, AGILE_SERVICE, stateMachineId, issueId, currentStatusId);
            List<TransformVO> transformVOS = modelMapper.map(transformInfos, new TypeToken<List<TransformVO>>() {
            }.getType());
            //获取组织中所有状态
            List<StatusVO> statusVOS = statusService.queryAllStatus(organizationId);
            Map<Long, StatusVO> statusMap = statusVOS.stream().collect(Collectors.toMap(StatusVO::getId, x -> x));
            transformVOS.forEach(transformVO -> {
                StatusVO statusVO = statusMap.get(transformVO.getEndStatusId());
                transformVO.setStatusVO(statusVO);
            });
            //如果转换中不包含当前状态，则添加一个self
            if (transformVOS.stream().noneMatch(transformVO -> currentStatusId.equals(transformVO.getEndStatusId()))) {
                TransformVO self = new TransformVO();
                self.setEndStatusId(currentStatusId);
                self.setStatusVO(statusMap.get(currentStatusId));
                transformVOS.add(self);
            }
            return transformVOS;
        } else {
            throw new CommonException("error.queryIssueTypesByProjectId.stateMachineSchemeId.null");
        }
    }

    @Override
    public Map<Long, Map<Long, List<TransformVO>>> queryTransformsMapByProjectId(Long projectId, String applyType) {
        if (!EnumUtil.contain(SchemeApplyType.class, applyType)) {
            throw new CommonException(ERROR_APPLYTYPE_ILLEGAL);
        }
        //获取状态机方案
        Long organizationId = projectUtil.getOrganizationId(projectId);
        ProjectConfigDTO smProjectConfig = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.STATE_MACHINE, applyType);
        if (smProjectConfig.getSchemeId() == null) {
            throw new CommonException("error.queryTransformsMapByProjectId.stateMachineSchemeId.null");
        }
        List<StateMachineSchemeConfigVO> smsConfigVO = stateMachineSchemeConfigService.queryBySchemeId(false, organizationId, smProjectConfig.getSchemeId());
        //获取问题类型方案
        ProjectConfigDTO itProjectConfig = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.ISSUE_TYPE, applyType);
        if (itProjectConfig.getSchemeId() == null) {
            throw new CommonException("error.queryTransformsMapByProjectId.issueTypeSchemeId.null");
        }
        List<IssueTypeDTO> issueTypes = issueTypeMapper.queryBySchemeId(organizationId, itProjectConfig.getSchemeId());
        List<Long> stateMachineIds = smsConfigVO.stream().map(StateMachineSchemeConfigVO::getStateMachineId).collect(Collectors.toList());
        //状态机id->状态id->转换列表
        Map<Long, Map<Long, List<TransformVO>>> statusMap = transformService.queryStatusTransformsMap(organizationId, stateMachineIds);
        Map<Long, Long> idMap = smsConfigVO.stream().collect(Collectors.toMap(StateMachineSchemeConfigVO::getIssueTypeId, StateMachineSchemeConfigVO::getStateMachineId));
        //问题类型id->状态id->转换列表
        Map<Long, Map<Long, List<TransformVO>>> resultMap = new HashMap<>(issueTypes.size());
        //获取组织所有状态
        List<StatusVO> statusVOS = statusService.queryAllStatus(organizationId);
        Map<Long, StatusVO> sMap = statusVOS.stream().collect(Collectors.toMap(StatusVO::getId, x -> x));
        statusMap.entrySet().forEach(x -> x.getValue().entrySet().forEach(y -> y.getValue().forEach(transformVO -> {
            StatusVO statusVO = sMap.get(transformVO.getEndStatusId());
            if (statusVO != null) {
                transformVO.setStatusType(statusVO.getType());
            }
        })));
        //匹配默认状态机的问题类型映射
        Long defaultStateMachineId = idMap.get(0L);
        resultMap.put(0L, statusMap.get(defaultStateMachineId));
        //匹配状态机的问题类型映射
        for (IssueTypeDTO issueType : issueTypes) {
            Long stateMachineId = idMap.get(issueType.getId());
            if (stateMachineId != null) {
                resultMap.put(issueType.getId(), statusMap.get(stateMachineId));
            }
        }
        return resultMap;
    }

    @Override
    public Long queryStateMachineId(Long projectId, String applyType, Long issueTypeId) {
        if (!EnumUtil.contain(SchemeApplyType.class, applyType)) {
            throw new CommonException(ERROR_APPLYTYPE_ILLEGAL);
        }
        Long organizationId = projectUtil.getOrganizationId(projectId);
        Long issueTypeSchemeId = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.ISSUE_TYPE, applyType).getSchemeId();
        Long stateMachineSchemeId = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.STATE_MACHINE, applyType).getSchemeId();
        if (issueTypeSchemeId == null) {
            throw new CommonException("error.queryStateMachineId.issueTypeSchemeId.null");
        }
        if (stateMachineSchemeId == null) {
            throw new CommonException("error.queryStateMachineId.getStateMachineSchemeId.null");
        }
        return stateMachineSchemeConfigService.queryStateMachineIdBySchemeIdAndIssueTypeId(false, organizationId, stateMachineSchemeId, issueTypeId);
    }

    @Override
    public StatusVO createStatusForAgile(Long projectId, String applyType, StatusVO statusVO) {
        Long organizationId = projectUtil.getOrganizationId(projectId);
        statusVO.setOrganizationId(organizationId);
        Map<String, Object> result = checkCreateStatusForAgile(projectId, applyType);
        if ((Boolean) result.get(FLAG)) {
            Long stateMachineId = (Long) result.get(STATEMACHINEID);
            statusVO = statusService.createStatusForAgile(organizationId, stateMachineId, statusVO);
        } else {
            throw new CommonException((String) result.get(MESSAGE));
        }
        return statusVO;
    }

    @Override
    public Map<String, Object> checkCreateStatusForAgile(Long projectId, String applyType) {
        Map<String, Object> result = new HashMap<>(3);
        result.put(FLAG, true);
        Long organizationId = projectUtil.getOrganizationId(projectId);
        Long stateMachineSchemeId = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.STATE_MACHINE, applyType).getSchemeId();
        //校验状态机方案是否只关联一个项目
        ProjectConfigDTO select = new ProjectConfigDTO();
        select.setSchemeId(stateMachineSchemeId);
        select.setSchemeType(SchemeType.STATE_MACHINE);
        select.setApplyType(SchemeApplyType.AGILE);
        if (projectConfigMapper.select(select).size() > 1) {
            result.put(FLAG, false);
            result.put(MESSAGE, "error.stateMachineScheme.multiScheme");
            return result;
        }
        //校验状态机方案是否只有一个状态机
        if (stateMachineSchemeConfigService.queryBySchemeId(false, organizationId, stateMachineSchemeId).size() > 1) {
            result.put(FLAG, false);
            result.put(MESSAGE, "error.stateMachineScheme.multiStateMachine");
            return result;
        }
        Long stateMachineId = stateMachineSchemeConfigService.selectDefault(false, organizationId, stateMachineSchemeId).getStateMachineId();
        if (stateMachineId == null) {
            result.put(FLAG, false);
            result.put(MESSAGE, "error.stateMachineScheme.defaultStateMachineId.notNull");
            return result;
        }
        //校验这个状态机是否只关联一个方案
        List<Long> schemeIds = stateMachineSchemeConfigService.querySchemeIdsByStateMachineId(false, organizationId, stateMachineId);
        if (schemeIds.size() > 1) {
            result.put(FLAG, false);
            result.put(MESSAGE, "error.stateMachineScheme.stateMachineInMoreThanOneScheme");
            return result;
        }
        result.put(STATEMACHINEID, stateMachineId);
        return result;
    }

    @Saga(code = "agile-remove-status", description = "移除状态", inputSchemaClass = StatusPayload.class)
    @Override
    public void removeStatusForAgile(Long projectId, Long statusId, String applyType) {
        Map<String, Object> result = checkCreateStatusForAgile(projectId, applyType);
        Boolean flag = (Boolean) result.get(FLAG);
        if (flag) {
            Long stateMachineId = (Long) result.get(STATEMACHINEID);
            Long organizationId = projectUtil.getOrganizationId(projectId);
            Long initStatusId = instanceService.queryInitStatusId(organizationId, stateMachineId);
            if (statusId.equals(initStatusId)) {
                throw new CommonException("error.initStatus.illegal");
            }
            try {
                statusService.removeStatusForAgile(organizationId, stateMachineId, statusId);
                StatusPayload statusPayload = new StatusPayload();
                statusPayload.setProjectId(projectId);
                statusPayload.setStatusId(statusId);
                sagaClient.startSaga("agile-remove-status", new StartInstanceDTO(JSON.toJSONString(statusPayload), "", "", ResourceLevel.PROJECT.value(), projectId));
            } catch (Exception e) {
                throw new RemoveStatusException("error.status.remove");
            }
        } else {
            throw new RemoveStatusException((String) result.get(MESSAGE));
        }
    }

    @Override
    public Boolean checkRemoveStatusForAgile(Long projectId, Long statusId, String applyType) {
        Map<String, Object> result = checkCreateStatusForAgile(projectId, applyType);
        Boolean flag = (Boolean) result.get(FLAG);
        if (flag) {
            Long stateMachineId = (Long) result.get(STATEMACHINEID);
            Long organizationId = projectUtil.getOrganizationId(projectId);
            Long initStatusId = instanceService.queryInitStatusId(organizationId, stateMachineId);
            return !statusId.equals(initStatusId);
        } else {
            throw new RemoveStatusException((String) result.get(MESSAGE));
        }
    }

    @Override
    public Map<String, List<Long>> queryProjectIdsMap(Long organizationId, Long stateMachineId) {
        //查询状态机方案中的配置
        List<Long> schemeIds = stateMachineSchemeConfigService.querySchemeIdsByStateMachineId(false, organizationId, stateMachineId);

        if (!schemeIds.isEmpty()) {
            List<ProjectConfigDTO> projectConfigs = projectConfigMapper.queryBySchemeIds(schemeIds, SchemeType.STATE_MACHINE);
            return projectConfigs.stream().collect(Collectors.groupingBy(ProjectConfigDTO::getApplyType, Collectors.mapping(ProjectConfigDTO::getProjectId, Collectors.toList())));
        }
        return Collections.emptyMap();
    }

    @Override
    public Long queryWorkFlowFirstStatus(Long projectId, String applyType, Long issueTypeId, Long organizationId) {
        Long statusMachineId = projectConfigService.queryStateMachineId(projectId, applyType, issueTypeId);
        if (statusMachineId == null) {
            throw new CommonException(ERROR_ISSUE_STATE_MACHINE_NOT_FOUND);
        }
        Long initStatusId = instanceFeignClient.queryInitStatusId(organizationId, statusMachineId).getBody();
        if (initStatusId == null) {
            throw new CommonException(ERROR_ISSUE_STATUS_NOT_FOUND);
        }
        return initStatusId;
    }
}
