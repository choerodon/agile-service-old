package io.choerodon.agile.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.agile.api.vo.StateMachineSchemeVO;
import io.choerodon.agile.api.vo.event.ProjectEvent;

import java.util.List;

/**
 * @author shinan.chen
 * @Date 2018/8/2
 */
public interface StateMachineSchemeService {

    /**
     * 分页查询状态机方案
     *
     * @param pageRequest 分页对象
     * @param schemeVO   查询参数
     * @param params      模糊查询参数
     * @return 状态机方案列表
     */
    PageInfo<StateMachineSchemeVO> pageQuery(Long organizationId, PageRequest pageRequest, StateMachineSchemeVO schemeVO, String params);

    /**
     * 创建状态机方案
     *
     * @param organizationId 组织id
     * @param schemeVO      状态机方案对象
     * @return 状态机方案对象
     */
    StateMachineSchemeVO create(Long organizationId, StateMachineSchemeVO schemeVO);

    /**
     * 更新状态机方案
     *
     * @param organizationId 组织id
     * @param schemeId       方案id
     * @param schemeVO      方案对象
     * @return 方案对象
     */
    StateMachineSchemeVO update(Long organizationId, Long schemeId, StateMachineSchemeVO schemeVO);

    /**
     * 删除状态机方案
     *
     * @param organizationId 组织id
     * @param schemeId       方案id
     * @return
     */
    Boolean delete(Long organizationId, Long schemeId);

    /**
     * 获取状态机方案及其配置
     *
     * @param organizationId 组织id
     * @param schemeId       方案id
     * @return 状态机方案及配置
     */
    StateMachineSchemeVO querySchemeWithConfigById(Boolean isDraft, Long organizationId, Long schemeId);

    /**
     * 校验名字是否未被使用
     *
     * @param organizationId 组织id
     * @param name           名称
     * @return
     */
    Boolean checkName(Long organizationId, String name);

    /**
     * 根据状态机id查询所使用到该状态机的方案，包含发布使用与草稿使用
     *
     * @param organizationId 组织id
     * @param stateMachineId 状态机id
     * @return 方案列表
     */
    List<StateMachineSchemeVO> querySchemeByStateMachineId(Long organizationId, Long stateMachineId);

    /**
     * 创建项目时，初始化敏捷/测试方案
     *
     * @param projectEvent
     */
    void initByConsumeCreateProject(ProjectEvent projectEvent);

    /**
     * 创建项目时，初始化项目群方案
     *
     * @param projectEvent
     */
    void initByConsumeCreateProgram(ProjectEvent projectEvent);

    /**
     * 若项目关联状态机方案，设置状态机方案、状态机为活跃
     *
     * @param schemeId
     */
    void activeSchemeWithRefProjectConfig(Long schemeId);

    /**
     * 更新发布进度
     *
     * @param organizationId organizationId
     * @param schemeId       schemeId
     * @param deployProgress deployProgress
     * @return Boolean
     */
    Boolean updateDeployProgress(Long organizationId, Long schemeId, Integer deployProgress);
}
