package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.WorkCalendarHolidayRefDO;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/9
 */
public interface WorkCalendarHolidayRefMapper extends BaseMapper<WorkCalendarHolidayRefDO> {

    /**
     * 查询最新的年份
     *
     * @return String
     */
    Integer queryLastYear();

    /**
     * 根据年份查询节假日
     *
     * @param year year
     * @return WorkCalendarHolidayRefDO
     */
    List<WorkCalendarHolidayRefDO> queryWorkCalendarHolidayRelByYear(Integer year);
}
