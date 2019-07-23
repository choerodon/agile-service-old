package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.StateMachineConfigVO;

import java.util.List;

/**
 * @author peng.jiang, dinghuang123@gmail.com
 */
public interface StateMachineConfigService {

    /**
     * 创建配置（草稿）
     *
     * @param stateMachineId 状态机Id
     * @param configDTO      配置对象
     * @return
     */
    StateMachineConfigVO create(Long organizationId, Long stateMachineId, Long transformId, StateMachineConfigVO configDTO);

    /**
     * 删除配置
     *
     * @param configId
     * @return
     */
    Boolean delete(Long organizationId, Long configId);

    /**
     * 查询配置列表（草稿、活跃）
     *
     * @param transformId 转换id
     * @param type        配置类型
     * @param isDraft     是否草稿
     * @return
     */
    List<StateMachineConfigVO> queryByTransformId(Long organizationId, Long transformId, String type, Boolean isDraft);

    /**
     * 批量获取转换的配置列表
     *
     * @param organizationId
     * @param transformIds
     * @param type
     * @return
     */
    List<StateMachineConfigVO> queryDeployByTransformIds(Long organizationId, String type, List<Long> transformIds);
}
