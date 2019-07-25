package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.StateMachineNodeDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author peng.jiang, dinghuang123@gmail.com
 */
public interface StateMachineNodeMapper extends Mapper<StateMachineNodeDTO> {

    StateMachineNodeDTO getNodeDeployById(@Param("nodeId") Long nodeId);

    StateMachineNodeDTO getNodeDeployByStatusId(@Param("stateMachineId") Long stateMachineId, @Param("statusId") Long statusId);


    List<StateMachineNodeDTO> selectByStateMachineId(@Param("stateMachineId") Long stateMachineId);

    Long checkStateDelete(@Param("organizationId") Long organizationId, @Param("statusId") Long statusId);

    StateMachineNodeDTO queryById(@Param("organizationId") Long organizationId, @Param("id") Long id);

    List<StateMachineNodeDTO> queryInitByStateMachineIds(@Param("stateMachineIds") List<Long> stateMachineIds, @Param("organizationId") Long organizationId);

    /**
     * 获取最大的postionY
     *
     * @param stateMachineId
     * @return
     */
    StateMachineNodeDTO selectMaxPositionY(@Param("stateMachineId") Long stateMachineId);

    /**
     * 单独写更新，版本号不变，否则前端处理复杂
     */
    int updateAllStatusTransformId(@Param("organizationId") Long organizationId, @Param("id") Long id, @Param("allStatusTransformId") Long allStatusTransformId);

    List<StateMachineNodeDTO> queryByStateMachineIds(@Param("organizationId") Long organizationId, @Param("stateMachineIds") List<Long> stateMachineIds);

}
