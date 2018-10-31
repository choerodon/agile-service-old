package io.choerodon.agile.app.service;

import io.choerodon.agile.api.dto.*;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/12
 */
public interface TimeZoneWorkCalendarService {

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
     * 创建时区下的工作日历
     *
     * @param organizationId                   organizationId
     * @param timeZoneId                       timeZoneId
     * @param timeZoneWorkCalendarRefCreateDTO timeZoneWorkCalendarRefCreateDTO
     * @return TimeZoneWorkCalendarRefDTO
     */
    TimeZoneWorkCalendarRefDTO createTimeZoneWorkCalendarRef(Long organizationId, Long timeZoneId, TimeZoneWorkCalendarRefCreateDTO timeZoneWorkCalendarRefCreateDTO);

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
     * @param year           year
     * @return TimeZoneWorkCalendarRefDTO
     */
    List<TimeZoneWorkCalendarRefDTO> queryTimeZoneWorkCalendarRefByTimeZoneId(Long organizationId, Long timeZoneId, Integer year);

    /**
     * ¬
     * 获取时区下的工作日历详情
     *
     * @param organizationId organizationId
     * @param year           year
     * @return TimeZoneWorkCalendarRefCreateDTO
     */
    TimeZoneWorkCalendarRefDetailDTO queryTimeZoneWorkCalendarDetail(Long organizationId, Integer year);
}
