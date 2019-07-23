package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.StateMachineNodeVO;
import io.choerodon.agile.api.vo.StatusVO;

import java.util.List;
import java.util.Map;

/**
 * @author peng.jiang, dinghuang123@gmail.com
 */
public interface StateMachineNodeService {
    /**
     * 创建状态节点
     *
     * @param organizationId
     * @param nodeVO
     * @return
     */
    List<StateMachineNodeVO> create(Long organizationId, Long stateMachineId, StateMachineNodeVO nodeVO);

    /**
     * 更新节点
     *
     * @param organizationId 组织id
     * @param nodeId         节点id
     * @param nodeVO        节点对象
     * @return 更新节点
     */
    List<StateMachineNodeVO> update(Long organizationId, Long stateMachineId, Long nodeId, StateMachineNodeVO nodeVO);

    /**
     * 删除状态节点
     *
     * @param organizationId 组织id
     * @param nodeId         节点id
     * @return
     */
    List<StateMachineNodeVO> delete(Long organizationId, Long stateMachineId, Long nodeId);

    /**
     * 校验是否能删除状态节点
     *
     * @param organizationId
     * @param statusId
     * @return
     */
    Map<String, Object> checkDelete(Long organizationId, Long stateMachineId, Long statusId);

    /**
     * 获取状态机初始节点id
     *
     * @param stateMachineId
     * @return
     */
    Long getInitNode(Long organizationId, Long stateMachineId);

    /**
     * 根据id获取节点
     *
     * @param organizationId
     * @param nodeId
     * @return
     */
    StateMachineNodeVO queryById(Long organizationId, Long nodeId);

    /**
     * 根据状态机id获取所有节点
     *
     * @param organizationId
     * @param stateMachineId
     * @return
     */
    List<StateMachineNodeVO> queryByStateMachineId(Long organizationId, Long stateMachineId, Boolean isDraft);

    /**
     * 敏捷创建节点
     *
     * @param organizationId
     * @param stateMachineId
     * @param statusVO
     */
    void createNodeAndTransformForAgile(Long organizationId, Long stateMachineId, StatusVO statusVO);

}
