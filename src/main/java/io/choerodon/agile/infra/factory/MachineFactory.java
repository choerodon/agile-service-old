package io.choerodon.agile.infra.factory;

import io.choerodon.agile.api.vo.ExecuteResult;
import io.choerodon.agile.api.vo.InputVO;
import io.choerodon.agile.app.service.*;
import io.choerodon.agile.infra.cache.InstanceCache;
import io.choerodon.agile.infra.dataobject.StateMachineDTO;
import io.choerodon.agile.infra.dataobject.StateMachineNodeDTO;
import io.choerodon.agile.infra.dataobject.StateMachineTransformDTO;
import io.choerodon.agile.infra.enums.TransformType;
import io.choerodon.agile.infra.mapper.StateMachineNodeMapper;
import io.choerodon.core.exception.CommonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shinan.chen
 * @date 2018/9/14
 */
@Component
public class MachineFactory {
    private static Logger logger = LoggerFactory.getLogger(MachineFactory.class);

    private static final String EXECUTE_RESULT = "executeResult";
    private static final String INPUT_VO = "inputVO";
    @Autowired
    private StateMachineClientService stateMachineClientService;
    @Autowired
    private StateMachineService stateMachineService;
    @Autowired
    private StateMachineTransformService transformService;
    @Autowired
    private StateMachineNodeService nodeService;
    @Autowired
    private StateMachineNodeMapper nodeDeployMapper;
    @Autowired
    private InstanceService instanceService;
    @Autowired
    private InstanceCache instanceCache;

    private StateMachineBuilder.Builder<String, String> getBuilder(Long organizationId, String serviceCode, Long stateMachineId) {
        StateMachineDTO stateMachine = stateMachineService.queryDeployForInstance(organizationId, stateMachineId);
        List<StateMachineNodeDTO> nodes = stateMachine.getNodes();
        List<StateMachineTransformDTO> transforms = stateMachine.getTransforms();
        Long initNodeId = nodeService.getInitNode(organizationId, stateMachineId);

        StateMachineBuilder.Builder<String, String> builder = StateMachineBuilder.builder();
        try {
            builder.configureConfiguration()
                    .withConfiguration()
                    .machineId(stateMachineId.toString());
            builder.configureStates()
                    .withStates()
                    .initial(initNodeId.toString(), initialAction(organizationId, serviceCode))
                    .states(nodes.stream().map(x -> x.getId().toString()).collect(Collectors.toSet()));
            for (StateMachineTransformDTO transform : transforms) {
                if (transform.getType().equals(TransformType.ALL)) {
                    //若配置了全部转换
                    for (StateMachineNodeDTO node : nodes) {
                        String event = transform.getId().toString();
                        String source = node.getId().toString();
                        String target = transform.getEndNodeId().toString();
                        builder.configureTransitions()
                                .withExternal()
                                .source(source).target(target)
                                .event(event)
                                .action(action(organizationId, serviceCode), errorAction(organizationId, serviceCode))
                                .guard(guard(organizationId, serviceCode));
                    }
                } else {
                    //转换都是通过id配置
                    String event = transform.getId().toString();
                    String source = transform.getStartNodeId().toString();
                    String target = transform.getEndNodeId().toString();
                    builder.configureTransitions()
                            .withExternal()
                            .source(source).target(target)
                            .event(event)
                            .action(action(organizationId, serviceCode), errorAction(organizationId, serviceCode))
                            .guard(guard(organizationId, serviceCode));
                }

            }
        } catch (Exception e) {
            logger.error("build StateMachineBuilder error,exception:{},stateMachineId:{}", e, stateMachineId);
        }
        return builder;
    }

    private StateMachine<String, String> buildInstance(Long organizationId, String serviceCode, Long stateMachineId) {
        StateMachineBuilder.Builder<String, String> builder = instanceCache.getBuilder(stateMachineId);
        if (builder == null) {
            builder = getBuilder(organizationId, serviceCode, stateMachineId);
            logger.info("build StateMachineBuilder successful,stateMachineId:{}", stateMachineId);
            instanceCache.putBuilder(stateMachineId, builder);
        }
        StateMachine<String, String> smInstance = builder.build();
        smInstance.start();
        return smInstance;
    }

    /**
     * 开始实例
     *
     * @param serviceCode
     * @param stateMachineId
     * @return
     */
    public ExecuteResult startInstance(Long organizationId, String serviceCode, Long stateMachineId, InputVO inputVO) {
        StateMachine<String, String> instance = buildInstance(organizationId, serviceCode, stateMachineId);
        //存入instanceId，以便执行guard和action
        instance.getExtendedState().getVariables().put(INPUT_VO, inputVO);
        //执行初始转换
        Long initTransformId = transformService.getInitTransform(organizationId, stateMachineId).getId();
        instance.sendEvent(initTransformId.toString());

        //缓存实例
        instanceCache.putInstance(serviceCode, stateMachineId, inputVO.getInstanceId(), instance);

        return instance.getExtendedState().getVariables().get(EXECUTE_RESULT) == null ? new ExecuteResult(false, null, "触发事件失败") : (ExecuteResult) instance.getExtendedState().getVariables().get(EXECUTE_RESULT);
    }

    /**
     * 状态转换
     *
     * @param serviceCode
     * @param stateMachineId
     * @param currentStatusId
     * @param transformId
     * @return
     */
    public ExecuteResult executeTransform(Long organizationId, String serviceCode, Long stateMachineId, Long currentStatusId, Long transformId, InputVO inputVO) {
        try {
            Long instanceId = inputVO.getInstanceId();
            //校验transformId是否合法
            List<StateMachineTransformDTO> transforms = transformService.queryListByStatusIdByDeploy(organizationId, stateMachineId, currentStatusId);
            if (transforms.stream().noneMatch(x -> x.getId().equals(transformId))) {
                throw new CommonException("error.executeTransform.transformId.illegal");
            }
            //状态转节点
            Long currentNodeId = nodeDeployMapper.getNodeDeployByStatusId(stateMachineId, currentStatusId).getId();

            StateMachine<String, String> instance = instanceCache.getInstance(serviceCode, stateMachineId, instanceId);
            if (instance == null) {
                instance = buildInstance(organizationId, serviceCode, stateMachineId);
                //恢复节点
                String id = instance.getId();
                instance.getStateMachineAccessor()
                        .doWithAllRegions(access ->
                                access.resetStateMachine(new DefaultStateMachineContext<>(currentNodeId.toString(), null, null, null, null, id)));
                logger.info("restore stateMachine instance successful, stateMachineId:{}", stateMachineId);
                instanceCache.putInstance(serviceCode, stateMachineId, instanceId, instance);
            }
            //存入instanceId，以便执行guard和action
            instance.getExtendedState().getVariables().put(INPUT_VO, inputVO);
            //触发事件
            instance.sendEvent(transformId.toString());

            //节点转状态
            Long statusId = nodeDeployMapper.getNodeDeployById(Long.parseLong(instance.getState().getId())).getStatusId();
            Object executeResult = instance.getExtendedState().getVariables().get(EXECUTE_RESULT);
            if (executeResult == null) {
                executeResult = new ExecuteResult(false, statusId, "触发事件失败");
            }
            return (ExecuteResult) executeResult;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ExecuteResult(false, null, "执行转换失败");
        }

    }

    /**
     * 初始化动作
     *
     * @param serviceCode
     * @return
     */
    private Action<String, String> initialAction(Long organizationId, String serviceCode) {
        return context ->
                logger.info("stateMachine instance execute initialAction,organizationId:{},serviceCode:{}", organizationId, serviceCode);
//                instanceService.postAction()

    }

    /**
     * 转换动作
     *
     * @param serviceCode
     * @return
     */
    private Action<String, String> action(Long organizationId, String serviceCode) {
        return context -> {
            Long transformId = Long.parseLong(context.getEvent());
            InputVO inputVO = (InputVO) context.getExtendedState().getVariables().get(INPUT_VO);
            logger.info("stateMachine instance execute transform action,instanceId:{},transformId:{}", inputVO.getInstanceId(), transformId);
            Boolean result = instanceService.postAction(organizationId, serviceCode, transformId, inputVO, context);
            if (!result) {
                throw new CommonException("error.stateMachine.action");
            }
        };
    }

    /**
     * 转换出错动作
     *
     * @param serviceCode
     * @return
     */
    private Action<String, String> errorAction(Long organizationId, String serviceCode) {
        return context -> {
            Long transformId = Long.parseLong(context.getEvent());
            InputVO inputVO = (InputVO) context.getExtendedState().getVariables().get(INPUT_VO);
            logger.error("stateMachine instance execute transform error,organizationId:{},serviceCode:{},instanceId:{},transformId:{}", organizationId, serviceCode, inputVO.getInstanceId(), transformId);
            // do something
        };
    }

    /**
     * 条件验证是否转换
     *
     * @param serviceCode
     * @return
     */
    private Guard<String, String> guard(Long organizationId, String serviceCode) {
        return context -> {
            Long transformId = Long.parseLong(context.getEvent());
            InputVO inputVO = (InputVO) context.getExtendedState().getVariables().get(INPUT_VO);
            logger.info("stateMachine instance execute transform guard,instanceId:{},transformId:{}", inputVO.getInstanceId(), transformId);
            return instanceService.validatorGuard(organizationId, serviceCode, transformId, inputVO, context);
        };
    }
}
