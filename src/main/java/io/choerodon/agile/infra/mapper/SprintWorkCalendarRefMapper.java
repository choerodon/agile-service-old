package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.SprintWorkCalendarRefDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/10
 */
public interface SprintWorkCalendarRefMapper extends BaseMapper<SprintWorkCalendarRefDO> {

    /**
     * 根据冲刺id查询冲刺加班日期
     *
     * @param sprintId  sprintId
     * @param projectId projectId
     * @return Date
     */
    List<Date> queryWorkBySprintIdAndProjectId(@Param("sprintId") Long sprintId, @Param("projectId") Long projectId);

    /**
     * 根据冲刺id查询冲刺节假日期
     *
     * @param sprintId  sprintId
     * @param projectId projectId
     * @return Date
     */
    List<Date> queryHolidayBySprintIdAndProjectId(@Param("sprintId") Long sprintId, @Param("projectId") Long projectId);
}
