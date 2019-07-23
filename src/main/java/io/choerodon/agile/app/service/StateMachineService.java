package io.choerodon.agile.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.agile.api.vo.StateMachineListVO;
import io.choerodon.agile.api.vo.StateMachineVO;
import io.choerodon.agile.api.vo.StateMachineWithStatusVO;
import io.choerodon.agile.api.vo.event.ChangeStatus;
import io.choerodon.agile.api.vo.event.DeployStateMachinePayload;
import io.choerodon.agile.api.vo.event.StateMachineSchemeDeployCheckIssue;
import io.choerodon.agile.infra.dataobject.StateMachineDTO;
import io.choerodon.base.domain.PageRequest;

import java.util.List;
import java.util.Map;


/**
 * @author peng.jiang@hand-china.com
 */
public interface StateMachineService {

    /**
     * 分页查询状态机
     *
     * @param organizationId 组织id
     * @param name           名称
     * @param description    描述
     * @param param          模糊查询参数
     * @return 状态机列表
     */
    PageInfo<StateMachineListVO> pageQuery(Long organizationId, PageRequest pageRequest, String name, String description, String param);

    /**
     * 创建状态机及配置
     *
     * @param organizationId 组织id
     * @param stateMachineVO 状态机及配置对象
     * @return 状态机
     */
    StateMachineVO create(Long organizationId, StateMachineVO stateMachineVO);

    /**
     * 更新状态机
     *
     * @param organizationId 组织id
     * @param stateMachineId 状态机id
     * @param stateMachineVO 状态机对象
     * @return 更新状态机
     */
    StateMachineVO update(Long organizationId, Long stateMachineId, StateMachineVO stateMachineVO);

    /**
     * 发布状态机
     *
     * @param organizationId 组织id
     * @param stateMachineId 状态机id
     * @return 发布状态机对象
     */
    Boolean deploy(Long organizationId, Long stateMachineId, Boolean isStartSaga);

    /**
     * 删除状态机
     *
     * @param organizationId 组织id
     * @param stateMachineId 状态机id
     * @return
     */
    void delete(Long organizationId, Long stateMachineId);

    /**
     * 删除校验
     *
     * @param organizationId 组织id
     * @param stateMachineId 状态机id
     * @return
     */
    Map<String, Object> checkDelete(Long organizationId, Long stateMachineId);

    /**
     * 删除节点校验
     *
     * @param organizationId
     * @param stateMachineId
     * @return
     */
    Map<String, Object> checkDeleteNode(Long organizationId, Long stateMachineId, Long statusId);

    /**
     * 使状态机变成非活跃状态
     *
     * @param organizationId
     * @param stateMachineIds
     */
    void notActiveStateMachine(Long organizationId, List<Long> stateMachineIds);

    /**
     * 发布状态机时对增加与减少的状态进行处理，影响到的项目是否需要增加与减少相应的状态
     *
     * @param organizationId
     * @param ignoreStateMachineId 忽略当前修改的状态机
     * @param ignoreSchemeId       忽略当前修改的状态机方案
     * @param schemeIds
     * @param changeStatus
     * @return
     */
    DeployStateMachinePayload handleStateMachineChangeStatusBySchemeIds(Long organizationId, Long ignoreStateMachineId, Long ignoreSchemeId, List<Long> schemeIds, ChangeStatus changeStatus);

    /**
     * 获取状态机及配置（草稿、活跃）
     *
     * @param organizationId
     * @param stateMachineId
     * @param isDraft        是否为草稿
     * @return
     */
    StateMachineVO queryStateMachineWithConfigById(Long organizationId, Long stateMachineId, Boolean isDraft);

    /**
     * 获取状态机及配置，用于内部状态机实例构建
     *
     * @param stateMachineId 状态机id
     * @return
     */
    StateMachineDTO queryDeployForInstance(Long organizationId, Long stateMachineId);

    /**
     * 删除草稿
     *
     * @param stateMachineId 状态机Id
     * @return 状态机对象
     */
    StateMachineVO deleteDraft(Long organizationId, Long stateMachineId);

    /**
     * 获取状态机
     *
     * @param stateMachineId 状态机id
     * @return
     */
    StateMachineVO queryStateMachineById(Long organizationId, Long stateMachineId);

    /**
     * 获取组织默认状态机
     *
     * @param organizationId
     * @return
     */
    StateMachineVO queryDefaultStateMachine(Long organizationId);

    /**
     * 校验问题状态机名字是否未被使用
     *
     * @param organizationId 组织id
     * @param name           名称
     * @return
     */
    Boolean checkName(Long organizationId, String name);

    /**
     * 获取所有状态机
     *
     * @param organizationId 组织id
     * @return 状态机列表
     */
    List<StateMachineVO> queryAll(Long organizationId);

    /**
     * 修改状态机状态
     * 活跃 -> 草稿
     *
     * @param organizationId organizationId
     * @param stateMachineId stateMachineId
     */
    void updateStateMachineStatus(Long organizationId, Long stateMachineId);

    /**
     * 批量活跃状态机
     *
     * @param organizationId
     * @param stateMachineIds
     * @return
     */
    Boolean activeStateMachines(Long organizationId, List<Long> stateMachineIds);

    /**
     * 批量使活跃状态机变成未活跃
     *
     * @param organizationId
     * @param stateMachineIds
     * @return
     */
    Boolean notActiveStateMachines(Long organizationId, List<Long> stateMachineIds);

    /**
     * 获取组织下所有状态机，附带状态
     *
     * @param organizationId 组织id
     * @return 状态机列表
     */
    List<StateMachineWithStatusVO> queryAllWithStatus(Long organizationId);

    /**
     * 获取组织下所有状态机
     *
     * @param organizationId
     * @return
     */
    List<StateMachineVO> queryByOrgId(Long organizationId);

    /**
     * issue服务修改状态机方案时，校验变更的问题类型影响的issue数量
     *
     * @param organizationId
     * @param deployCheckIssue
     * @return
     */
    Map<Long, Long> checkStateMachineSchemeChange(Long organizationId, StateMachineSchemeDeployCheckIssue deployCheckIssue);

    DeployStateMachinePayload handleStateMachineChangeStatusByStateMachineId(Long organizationId, Long stateMachineId, ChangeStatus changeStatus);

}
