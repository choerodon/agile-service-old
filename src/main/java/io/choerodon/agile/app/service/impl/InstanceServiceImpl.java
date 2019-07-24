package io.choerodon.agile.app.service.impl;

import io.choerodon.statemachine.dto.ExecuteResult;
import io.choerodon.agile.api.vo.StateMachineConfigVO;
import io.choerodon.agile.api.vo.StateMachineTransformVO;
import io.choerodon.agile.api.vo.event.TransformInfo;
import io.choerodon.agile.app.service.InstanceService;
import io.choerodon.agile.app.service.StateMachineConfigService;
import io.choerodon.agile.app.service.StateMachineTransformService;
import io.choerodon.agile.infra.dataobject.StateMachineDTO;
import io.choerodon.agile.infra.dataobject.StateMachineNodeDTO;
import io.choerodon.agile.infra.dataobject.StateMachineTransformDTO;
import io.choerodon.agile.infra.enums.ConfigType;
import io.choerodon.agile.infra.enums.NodeType;
import io.choerodon.agile.infra.factory.MachineFactory;
import io.choerodon.agile.infra.mapper.StateMachineMapper;
import io.choerodon.agile.infra.mapper.StateMachineNodeMapper;
import io.choerodon.agile.infra.mapper.StateMachineTransformMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.statemachine.dto.InputDTO;
import io.choerodon.statemachine.service.ClientService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author shinan.chen
 * @date 2018/9/18
 */
@Service("instanceService")
@Transactional(rollbackFor = Exception.class)
public class InstanceServiceImpl implements InstanceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceServiceImpl.class);
    private static final String EXCEPTION = "Exception:{}";
    @Autowired
    private StateMachineNodeMapper nodeDeployMapper;
    @Autowired
    private StateMachineConfigService configService;
    @Autowired
    private StateMachineTransformService transformService;
    @Autowired
    private StateMachineTransformMapper transformMapper;
    @Autowired
    private MachineFactory machineFactory;
    @Autowired
    private StateMachineMapper stateMachineMapper;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    @Qualifier("clientService")
    private ClientService stateMachineClientService;

    @Override
    public ExecuteResult startInstance(Long organizationId, String serviceCode, Long stateMachineId, InputDTO inputDTO) {
        StateMachineDTO stateMachine = stateMachineMapper.queryById(organizationId, stateMachineId);
        if (stateMachine == null) {
            throw new CommonException("error.stateMachine.notFound");
        }
        ExecuteResult executeResult;
        try {
            executeResult = machineFactory.startInstance(organizationId, serviceCode, stateMachineId, inputDTO);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            executeResult = new ExecuteResult();
            executeResult.setSuccess(false);
            executeResult.setResultStatusId(null);
            executeResult.setErrorMessage("创建状态机实例失败");
        }
        return executeResult;
    }

    @Override
    public Long queryInitStatusId(Long organizationId, Long stateMachineId) {
        StateMachineNodeDTO select = new StateMachineNodeDTO();
        select.setOrganizationId(organizationId);
        select.setStateMachineId(stateMachineId);
        select.setType(NodeType.INIT);
        List<StateMachineNodeDTO> nodes = nodeDeployMapper.select(select);
        if (nodes.isEmpty()) {
            throw new CommonException("error.queryInitStatusId.notFound");
        }
        return nodes.get(0).getStatusId();
    }

    @Override
    public ExecuteResult executeTransform(Long organizationId, String serviceCode, Long stateMachineId, Long currentStatusId, Long transformId, InputDTO inputDTO) {
        return machineFactory.executeTransform(organizationId, serviceCode, stateMachineId, currentStatusId, transformId, inputDTO);
    }

    @Override
    public List<TransformInfo> queryListTransform(Long organizationId, String serviceCode, Long stateMachineId, Long instanceId, Long statusId) {
        Boolean isNeedFilter = false;
        List<StateMachineTransformDTO> stateMachineTransforms = transformService.queryListByStatusIdByDeploy(organizationId, stateMachineId, statusId);
        //获取节点信息
        List<StateMachineNodeDTO> nodes = nodeDeployMapper.selectByStateMachineId(stateMachineId);
        List<StateMachineConfigVO> configs = configService.queryDeployByTransformIds(organizationId, ConfigType.CONDITION, stateMachineTransforms.stream().map(StateMachineTransformDTO::getId).collect(Collectors.toList()));
        Map<Long, Long> nodeMap = nodes.stream().collect(Collectors.toMap(StateMachineNodeDTO::getId, StateMachineNodeDTO::getStatusId));
        Map<Long, List<StateMachineConfigVO>> configMaps = configs.stream().collect(Collectors.groupingBy(StateMachineConfigVO::getTransformId));
        List<TransformInfo> transformInfos = new ArrayList<>(stateMachineTransforms.size());
        for (StateMachineTransformDTO transform : stateMachineTransforms) {
            TransformInfo transformInfo = modelMapper.map(transform, TransformInfo.class);
            transformInfo.setStartStatusId(nodeMap.get(transform.getStartNodeId()));
            transformInfo.setEndStatusId(nodeMap.get(transform.getEndNodeId()));
            //获取转换的条件配置
            List<StateMachineConfigVO> conditionConfigs = configMaps.get(transform.getId());
            if (conditionConfigs == null) {
                transformInfo.setConditions(Collections.emptyList());
            } else {
                transformInfo.setConditions(conditionConfigs);
                isNeedFilter = true;
            }
            transformInfos.add(transformInfo);
        }
        //调用对应服务，根据条件校验转换，过滤掉不可用的转换
        if (isNeedFilter) {
            try {
                transformInfos = modelMapper.map(stateMachineClientService.conditionFilter(instanceId, modelMapper.map(transformInfos, new TypeToken<List<io.choerodon.statemachine.dto.TransformInfo>>() {
                }.getType())), new TypeToken<List<TransformInfo>>() {
                }.getType());
            } catch (Exception e) {
                LOGGER.error(EXCEPTION, e);
                transformInfos = Collections.emptyList();
            }
        }
        return transformInfos;
    }

    @Override
    public Boolean validatorGuard(Long organizationId, String serviceCode, Long transformId, InputDTO inputDTO, StateContext<String, String> context) {
        StateMachineTransformDTO transform = transformMapper.queryById(organizationId, transformId);
        List<StateMachineConfigVO> conditionConfigs = condition(organizationId, transformId);
        List<StateMachineConfigVO> validatorConfigs = validator(organizationId, transformId);
        ExecuteResult executeResult = new ExecuteResult();
        executeResult.setSuccess(true);
        //调用对应服务，执行条件和验证，返回是否成功
        try {
            if (!conditionConfigs.isEmpty()) {
                inputDTO.setConfigs(modelMapper.map(conditionConfigs, new TypeToken<List<StateMachineConfigVO>>() {
                }.getType()));
                executeResult = modelMapper.map(stateMachineClientService.configExecuteCondition(null, transform.getConditionStrategy(), modelMapper.map(inputDTO, InputDTO.class)), ExecuteResult.class);
            }
            if (executeResult.getSuccess() && !validatorConfigs.isEmpty()) {
                inputDTO.setConfigs(modelMapper.map(validatorConfigs, new TypeToken<List<StateMachineConfigVO>>() {
                }.getType()));
                executeResult = modelMapper.map(stateMachineClientService.configExecuteValidator(null, modelMapper.map(inputDTO, InputDTO.class)), ExecuteResult.class);
            }
        } catch (Exception e) {
            LOGGER.error(EXCEPTION, e);
            executeResult = new ExecuteResult();
            executeResult.setSuccess(false);
            executeResult.setResultStatusId(null);
            executeResult.setErrorMessage("验证调用失败");
        }

        Map<Object, Object> variables = context.getExtendedState().getVariables();
        variables.put("executeResult", executeResult);
        return executeResult.getSuccess();
    }

    @Override
    public Boolean postAction(Long organizationId, String serviceCode, Long transformId, InputDTO inputDTO, StateContext<String, String> context) {
        List<StateMachineConfigVO> configs = action(organizationId, transformId);
        inputDTO.setConfigs(modelMapper.map(configs, new TypeToken<List<StateMachineConfigVO>>() {
        }.getType()));
        StateMachineTransformDTO transform = transformMapper.queryById(organizationId, transformId);
        //节点转状态
        Long targetStatusId = nodeDeployMapper.getNodeDeployById(Long.parseLong(context.getTarget().getId())).getStatusId();
        if (targetStatusId == null) {
            throw new CommonException("error.postAction.targetStatusId.notNull");
        }
        ExecuteResult executeResult;
        //调用对应服务，执行动作，返回是否成功
        try {
            executeResult = modelMapper.map(stateMachineClientService.configExecutePostAction(targetStatusId, transform.getType(), modelMapper.map(inputDTO, InputDTO.class)), ExecuteResult.class);
            //返回为空则调用对应服务，对应服务方法报错
            if (executeResult.getSuccess() != null) {
                executeResult = new ExecuteResult();
                executeResult.setSuccess(false);
                executeResult.setResultStatusId(null);
                executeResult.setErrorMessage("后置动作调用失败");
            }
        } catch (Exception e) {
            LOGGER.error(EXCEPTION, e);
            executeResult = new ExecuteResult();
            executeResult.setSuccess(false);
            executeResult.setResultStatusId(null);
            executeResult.setErrorMessage("后置动作调用失败");
        }
        Map<Object, Object> variables = context.getExtendedState().getVariables();
        variables.put("executeResult", executeResult);
        return executeResult.getSuccess();
    }

    @Override
    public List<StateMachineConfigVO> condition(Long organizationId, Long transformId) {
        List<StateMachineConfigVO> configs = configService.queryByTransformId(organizationId, transformId, ConfigType.CONDITION, false);
        return configs == null ? Collections.emptyList() : configs;
    }

    @Override
    public List<StateMachineConfigVO> validator(Long organizationId, Long transformId) {
        List<StateMachineConfigVO> configs = configService.queryByTransformId(organizationId, transformId, ConfigType.VALIDATOR, false);
        return configs == null ? Collections.emptyList() : configs;
    }

    @Override
    public List<StateMachineConfigVO> trigger(Long organizationId, Long transformId) {
        List<StateMachineConfigVO> configs = configService.queryByTransformId(organizationId, transformId, ConfigType.TRIGGER, false);
        return configs == null ? Collections.emptyList() : configs;
    }

    @Override
    public List<StateMachineConfigVO> action(Long organizationId, Long transformId) {
        List<StateMachineConfigVO> configs = configService.queryByTransformId(organizationId, transformId, ConfigType.ACTION, false);
        return configs == null ? Collections.emptyList() : configs;
    }

    @Override
    public Map<Long, Long> queryInitStatusIds(Long organizationId, List<Long> stateMachineIds) {
        if (!stateMachineIds.isEmpty()) {
            return nodeDeployMapper.queryInitByStateMachineIds(stateMachineIds, organizationId).stream()
                    .collect(Collectors.toMap(StateMachineNodeDTO::getStateMachineId, StateMachineNodeDTO::getStatusId));
        } else {
            return new HashMap<>();
        }
    }

    /**
     * 创建实例时，获取状态机的初始转换
     *
     * @param organizationId
     * @param stateMachineId
     * @return
     */
    @Override
    public StateMachineTransformVO queryInitTransform(Long organizationId, Long stateMachineId) {
        //获取初始转换
        StateMachineTransformDTO initTransform = transformService.getInitTransform(organizationId, stateMachineId);
        StateMachineTransformVO stateMachineTransformVO = modelMapper.map(initTransform, StateMachineTransformVO.class);
        //获取转换配置
        List<StateMachineConfigVO> configDTOS = configService.queryByTransformId(organizationId, initTransform.getId(), null, false);
        Map<String, List<StateMachineConfigVO>> configMap = configDTOS.stream().collect(Collectors.groupingBy(StateMachineConfigVO::getType));
        stateMachineTransformVO.setConditions(configMap.get(ConfigType.CONDITION));
        stateMachineTransformVO.setTriggers(configMap.get(ConfigType.TRIGGER));
        stateMachineTransformVO.setValidators(configMap.get(ConfigType.VALIDATOR));
        stateMachineTransformVO.setPostpositions(configMap.get(ConfigType.ACTION));
        return stateMachineTransformVO;
    }
}
