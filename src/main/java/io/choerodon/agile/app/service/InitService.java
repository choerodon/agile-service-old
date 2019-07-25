package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.event.ProjectEvent;
import io.choerodon.agile.infra.dataobject.StatusDTO;

import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/10/15
 */
public interface InitService {
    /**
     * 初始化状态
     *
     * @param organizationId
     */
    List<StatusDTO> initStatus(Long organizationId);

    /**
     * 创建项目时初始化状态机
     *
     * @param organizationId
     * @param applyType
     * @param projectEvent
     * @return
     */
    Long createStateMachineWithCreateProject(Long organizationId, String applyType, ProjectEvent projectEvent);

    /**
     * 初始化默认状态机
     *
     * @param organizationId
     * @return
     */
    Long initDefaultStateMachine(Long organizationId);

    /**
     * 初始化敏捷状态机
     *
     * @param organizationId
     * @param projectEvent
     * @return
     */
    Long initAGStateMachine(Long organizationId, ProjectEvent projectEvent);

    /**
     * 初始化测试状态机
     *
     * @param organizationId
     * @param projectEvent
     * @return
     */
    Long initTEStateMachine(Long organizationId, ProjectEvent projectEvent);

    /**
     * 初始化项目群状态机
     *
     * @param organizationId
     * @param projectEvent
     * @return
     */
    Long initPRStateMachine(Long organizationId, ProjectEvent projectEvent);

    void createStateMachineDetail(Long organizationId, Long stateMachineId, String applyType);
}

