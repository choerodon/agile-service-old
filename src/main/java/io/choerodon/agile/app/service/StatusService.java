package io.choerodon.agile.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.infra.dataobject.StatusDTO;

import java.util.List;
import java.util.Map;

/**
 * @author peng.jiang, dinghuang123@gmail.com
 */
public interface StatusService {

    PageInfo<StatusWithInfoVO> queryStatusList(PageRequest pageRequest, Long organizationId, StatusSearchVO statusSearchVO);

    /**
     * 创建状态
     *
     * @param organizationId 组织id
     * @param statusVO      状态对象
     * @return 状态对象
     */
    StatusVO create(Long organizationId, StatusVO statusVO);

    /**
     * 更新状态
     *
     * @param statusVO 更新对象
     * @return 更新对象
     */
    StatusVO update(StatusVO statusVO);

    /**
     * 删除状态
     *
     * @param organizationId 组织id
     * @param statusId       状态机id
     * @return
     */
    Boolean delete(Long organizationId, Long statusId);

    /**
     * 根据id获取状态对象
     *
     * @param organizationId 组织id
     * @param statusId       状态id
     * @return
     */
    StatusInfoVO queryStatusById(Long organizationId, Long statusId);

    /**
     * 获取所有
     *
     * @param organizationId 组织id
     * @return
     */
    List<StatusVO> queryAllStatus(Long organizationId);

    Map<Long, StatusMapVO> queryAllStatusMap(Long organizationId);

    /**
     * 校验状态名字是否未被使用
     *
     * @param organizationId 组织id
     * @param name           名称
     * @return
     */
    StatusCheckVO checkName(Long organizationId, String name);

    Map<Long, StatusDTO> batchStatusGet(List<Long> ids);

    /**
     * 敏捷添加状态
     *
     * @param organizationId
     * @param statusVO
     * @return
     */
    StatusVO createStatusForAgile(Long organizationId, Long stateMachineId, StatusVO statusVO);

    /**
     * 敏捷移除状态
     *
     * @param organizationId
     * @param stateMachineId
     * @param statusId
     */
    void removeStatusForAgile(Long organizationId, Long stateMachineId, Long statusId);

    /**
     * 查询状态机下的所有状态
     *
     * @param organizationId
     * @param stateMachineIds
     * @return
     */
    List<StatusVO> queryByStateMachineIds(Long organizationId, List<Long> stateMachineIds);

}
