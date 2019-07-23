package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.api.vo.event.StatusPayload;
import io.choerodon.agile.infra.dataobject.StateMachineDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author peng.jiang, dinghuang123@gmail.com
 */
public interface StateMachineMapper extends Mapper<StateMachineDTO> {

    /**
     * 分页查询状态机
     *
     * @param stateMachine 状态机
     * @param param        模糊查询参数
     * @return 状态机列表
     */
    List<StateMachineDTO> fulltextSearch(@Param("stateMachine") StateMachineDTO stateMachine, @Param("param") String param);

    List<StateMachineDTO> queryByIds(@Param("organizationId") Long organizationId, @Param("stateMachineIds") List<Long> stateMachineIds);

    StateMachineDTO queryById(@Param("organizationId") Long organizationId, @Param("id") Long id);

    List<StatusPayload> getStatusBySmId(@Param("projectId") Long projectId, @Param("stateMachineId") Long stateMachineId);
}
