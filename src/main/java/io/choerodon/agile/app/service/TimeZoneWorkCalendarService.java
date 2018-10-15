package io.choerodon.agile.app.service;

import io.choerodon.agile.api.dto.TimeZoneWorkCalendarCreateDTO;
import io.choerodon.agile.api.dto.TimeZoneWorkCalendarDTO;
import io.choerodon.agile.api.dto.TimeZoneWorkCalendarRefDTO;
import io.choerodon.agile.api.dto.TimeZoneWorkCalendarUpdateDTO;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/12
 */
public interface TimeZoneWorkCalendarService {
    /**
     * 创建时区设置
     *
     * @param organizationId                organizationId
     * @param timeZoneWorkCalendarCreateDTO timeZoneWorkCalendarCreateDTO
     * @return TimeZoneWorkCalendarDTO
     */
    TimeZoneWorkCalendarDTO createTimeZoneWorkCalendar(Long organizationId, TimeZoneWorkCalendarCreateDTO timeZoneWorkCalendarCreateDTO);

    /**
     * 更新时区设置
     *
     * @param organizationId                organizationId
     * @param timeZoneId                    timeZoneId
     * @param timeZoneWorkCalendarUpdateDTO timeZoneWorkCalendarUpdateDTO
     * @return TimeZoneWorkCalendarDTO
     */
    TimeZoneWorkCalendarDTO updateTimeZoneWorkCalendar(Long organizationId, Long timeZoneId, TimeZoneWorkCalendarUpdateDTO timeZoneWorkCalendarUpdateDTO);

    /**
     * 删除时区设置
     *
     * @param organizationId organizationId
     * @param timeZoneId     timeZoneId
     */
    void deleteTimeZoneWorkCalendar(Long organizationId, Long timeZoneId);

    /**
     * 创建时区下的工作日历
     *
     * @param organizationId organizationId
     * @param timeZoneId     timeZoneId
     * @param date           date
     * @return TimeZoneWorkCalendarRefDTO
     */
    TimeZoneWorkCalendarRefDTO createTimeZoneWorkCalendarRef(Long organizationId, Long timeZoneId, String date);

    /**
     * 删除工作日历
     *
     * @param organizationId organizationId
     * @param calendarId     calendarId
     */
    void deleteTimeZoneWorkCalendarRef(Long organizationId, Long calendarId);

    /**
     * 查询时区信息
     *
     * @param organizationId organizationId
     * @return TimeZoneWorkCalendarDTO
     */
    TimeZoneWorkCalendarDTO queryTimeZoneWorkCalendar(Long organizationId);

    /**
     * 根据时区id获取工作日历
     *
     * @param organizationId organizationId
     * @param timeZoneId     timeZoneId
     * @return TimeZoneWorkCalendarRefDTO
     */
    List<TimeZoneWorkCalendarRefDTO> queryTimeZoneWorkCalendarRefByTimeZoneId(Long organizationId, Long timeZoneId);
}
