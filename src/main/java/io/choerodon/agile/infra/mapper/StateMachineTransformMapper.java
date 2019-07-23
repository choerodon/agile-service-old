package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.StateMachineTransformDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author peng.jiang, dinghuang123@gmail.com
 */
public interface StateMachineTransformMapper extends Mapper<StateMachineTransformDTO> {
    /**
     * 删除节点时，删除关联的转换
     *
     * @param nodeId 节点id
     * @return
     */
    int deleteByNodeId(Long nodeId);

    StateMachineTransformDTO queryById(@Param("organizationId") Long organizationId, @Param("id") Long id);

    /**
     * 获取某个节点拥有的转换（包括全部转换）
     *
     * @param organizationId
     * @param stateMachineId
     * @param startNodeId
     * @param transformType
     * @return
     */
    List<StateMachineTransformDTO> queryByStartNodeIdOrType(@Param("organizationId") Long organizationId, @Param("stateMachineId") Long stateMachineId, @Param("startNodeId") Long startNodeId, @Param("transformType") String transformType);

    List<StateMachineTransformDTO> queryByStateMachineIds(@Param("organizationId") Long organizationId, @Param("stateMachineIds") List<Long> stateMachineIds);
}
