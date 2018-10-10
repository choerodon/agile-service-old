package io.choerodon.agile.app.service;


import io.choerodon.agile.api.dto.WorkCalendarHolidayRefDTO;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/9
 */
public interface WorkCalendarHolidayRefService {

    /**
     * 按年份更新日历
     *
     * @param year year
     */
    void updateWorkCalendarHolidayRefByYear(String year);

    /**
     * 根据年份查询工作日历
     *
     * @param year year
     * @return WorkCalendarHolidayRefDTO
     */
    List<WorkCalendarHolidayRefDTO> queryWorkCalendarHolidayRelByYear(String year);
}
