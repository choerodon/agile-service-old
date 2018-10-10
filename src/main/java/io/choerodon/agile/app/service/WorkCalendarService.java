package io.choerodon.agile.app.service;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/10
 */
public interface WorkCalendarService {

    /**
     * 按年份更新日历
     *
     * @param year year
     */
    void updateWorkCalendarHolidayRefByYear(String year);

    /**
     * 自动更新日历
     */
    void updateWorkCalendarHolidayRef();
}
