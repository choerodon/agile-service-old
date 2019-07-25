package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.PriorityVO;

import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @since 2019/3/4
 */
public interface PriorityService {

    /**
     * 查询组织下优先级列表
     *
     * @param priorityVO 分页展示优先级对象
     * @param param      模糊分页
     * @return 优先级列表
     */
    List<PriorityVO> selectAll(PriorityVO priorityVO, String param);

    /**
     * 在组织下创建新的优先级
     *
     * @param organizationId 组织id
     * @param priorityVO     创建优先级对象
     * @return 新的优先级对象
     */
    PriorityVO create(Long organizationId, PriorityVO priorityVO);

    /**
     * 在组织下删除优先级
     *
     * @param organizationId 组织id
     * @param priorityId     优先级id
     * @return 删除是否成功 true or false
     */
    Boolean delete(Long organizationId, Long priorityId, Long changePriorityId);

    /**
     * 更新优先级
     *
     * @param priorityVO 更新优先级对象
     * @return 更新的优先级对象
     */
    PriorityVO update(PriorityVO priorityVO);

    /**
     * 检查组织下的优先级名称是否重复
     *
     * @param organizationId 组织id
     * @param name           创建或更新的优先级名称
     * @return 是否重复 true or false
     */
    Boolean checkName(Long organizationId, String name);

    /**
     * 根据id更新优先级的顺序
     *
     * @param list           优先级对象列表
     * @param organizationId 组织
     * @return 更新是否成功
     */
    List<PriorityVO> updateByList(List<PriorityVO> list, Long organizationId);

    Map<Long, PriorityVO> queryByOrganizationId(Long organizationId);

    PriorityVO queryDefaultByOrganizationId(Long organizationId);

    List<PriorityVO> queryByOrganizationIdList(Long organizationId);

    PriorityVO queryById(Long organizationId, Long id);

    Map<Long, Map<String, Long>> initProrityByOrganization(List<Long> organizationIds);

    /**
     * 生效/失效优先级
     *
     * @param organizationId
     * @param id
     * @param enable
     * @return
     */
    PriorityVO enablePriority(Long organizationId, Long id, Boolean enable);

    /**
     * 校验删除优先级
     *
     * @param organizationId
     * @param id
     * @return
     */
    Long checkDelete(Long organizationId, Long id);

    /**
     * 校验优先级能否删除
     *
     * @param organizationId
     * @param priorityId
     * @param projectIds
     * @return
     */
    Long checkPriorityDelete(Long organizationId, Long priorityId, List<Long> projectIds);

    /**
     * 批量更新issue的优先级
     *
     * @param organizationId
     * @param priorityId
     * @param changePriorityId
     * @param userId
     * @param projectIds
     */
    void batchChangeIssuePriority(Long organizationId, Long priorityId, Long changePriorityId, Long userId, List<Long> projectIds);
}
