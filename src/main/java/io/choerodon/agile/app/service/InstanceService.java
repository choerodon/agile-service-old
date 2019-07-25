package io.choerodon.agile.app.service;

import io.choerodon.statemachine.dto.ExecuteResult;
import io.choerodon.agile.api.vo.StateMachineConfigVO;
import io.choerodon.agile.api.vo.StateMachineTransformVO;
import io.choerodon.agile.api.vo.event.TransformInfo;
import io.choerodon.statemachine.dto.InputDTO;
import org.springframework.statemachine.StateContext;

import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @date 2018/9/18
 */
public interface InstanceService {

    /**
     * 创建状态机实例，并返回初始状态
     *
     * @param serviceCode
     * @param stateMachineId
     * @return
     */
    ExecuteResult startInstance(Long organizationId, String serviceCode, Long stateMachineId, InputDTO inputDTO);

    /**
     * 查询状态机的初始状态id
     *
     * @param organizationId
     * @param stateMachineId
     * @return
     */
    Long queryInitStatusId(Long organizationId, Long stateMachineId);

    /**
     * 执行状态转换，并返回转换后的状态
     *
     * @param stateMachineId  状态机Id
     * @param transformId     转换Id
     * @param currentStatusId 当前状态Id
     * @param serviceCode     请求服务code
     * @return
     */
    ExecuteResult executeTransform(Long organizationId, String serviceCode, Long stateMachineId, Long currentStatusId, Long transformId, InputDTO inputDTO);

    /**
     * 获取当前状态拥有的转换列表，feign调用对应服务的条件验证
     *
     * @param organizationId
     * @param statusId
     * @return
     */
    List<TransformInfo> queryListTransform(Long organizationId, String serviceCode, Long stateMachineId, Long instanceId, Long statusId);

    /**
     * 调用相应服务，验证转换
     *
     * @param organizationId
     * @param serviceCode
     * @param transformId
     * @param inputDTO
     * @param context        状态机上下文，传递参数
     * @return
     */
    Boolean validatorGuard(Long organizationId, String serviceCode, Long transformId, InputDTO inputDTO, StateContext<String, String> context);

    /**
     * 调用相应服务，执行后置动作
     *
     * @param organizationId
     * @param serviceCode
     * @param transformId
     * @param inputDTO
     * @param context        状态机上下文，传递参数
     * @return
     */
    Boolean postAction(Long organizationId, String serviceCode, Long transformId, InputDTO inputDTO, StateContext<String, String> context);

    /**
     * 条件
     *
     * @param transformId 转换id
     * @return
     */
    List<StateMachineConfigVO> condition(Long organizationId, Long transformId);

    /**
     * 验证器
     *
     * @param transformId 转换id
     * @return
     */
    List<StateMachineConfigVO> validator(Long organizationId, Long transformId);

    /**
     * 触发器
     *
     * @param transformId 转换id
     * @return
     */
    List<StateMachineConfigVO> trigger(Long organizationId, Long transformId);

    /**
     * 后置功能
     *
     * @param transformId 转换id
     * @return
     */
    List<StateMachineConfigVO> action(Long organizationId, Long transformId);

    /**
     * 获取状态机列表对应的状态机初始状态map
     *
     * @param organizationId  organizationId
     * @param stateMachineIds stateMachineIds
     * @return Map
     */
    Map<Long, Long> queryInitStatusIds(Long organizationId, List<Long> stateMachineIds);

    /**
     * 创建实例时，获取状态机的初始转换
     *
     * @param organizationId
     * @param stateMachineId
     * @return
     */
    StateMachineTransformVO queryInitTransform(Long organizationId, Long stateMachineId);
}
