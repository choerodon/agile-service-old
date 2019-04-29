package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.WorkCalendarRefDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/10
 */
public interface WorkCalendarRefMapper extends BaseMapper<WorkCalendarRefDO> {

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

    /**
     * 查询今年包括下一年的数据
     *
     * @param projectId projectId
     * @param sprintId  sprintId
     * @param year      year
     * @return SprintWorkCalendarRefDO
     */
    List<WorkCalendarRefDO> queryWithNextYearByYear(@Param("projectId")Long projectId, @Param("sprintId")Long sprintId, @Param("year")Integer year);
}
