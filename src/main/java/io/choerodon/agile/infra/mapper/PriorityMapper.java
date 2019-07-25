package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.PriorityDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author cong.cheng
 * @Date 2018/8/21
 */
@Component
public interface PriorityMapper extends Mapper<PriorityDTO> {
    /**
     * 查询优先级表
     *
     * @param priority 精确查询的字段包装
     * @param param    模糊查询参数
     * @return 优先级列表
     */
    List<PriorityDTO> fulltextSearch(@Param("priority") PriorityDTO priority, @Param("param") String param);

    /**
     * 得到下一个顺序号
     *
     * @param organizationId 组织id
     * @return 顺序号
     */
    BigDecimal getNextSequence(@Param("organizationId") Long organizationId);

    /**
     * 根据id更新优先级的顺序
     *
     * @param priority 优先级对象
     * @return 更新是否成功
     */
    int updateSequenceById(@Param("priority") PriorityDTO priority);

    /**
     * 取消默认优先级
     *
     * @param organizationId 组织id
     */
    void cancelDefaultPriority(@Param("organizationId") Long organizationId);

    /**
     * 更新最小的id为默认优先级
     *
     * @param organizationId
     */
    void updateMinSeqAsDefault(@Param("organizationId") Long organizationId);
}
