package io.choerodon.agile.app.service;

import io.choerodon.agile.api.dto.TimeZoneWorkCalendarCreateDTO;
import io.choerodon.agile.api.dto.TimeZoneWorkCalendarDTO;

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
     * @param organizationId          organizationId
     * @param timeZoneWorkCalendarDTO timeZoneWorkCalendarDTO
     * @return TimeZoneWorkCalendarDTO
     */
    TimeZoneWorkCalendarDTO updateTimeZoneWorkCalendar(Long organizationId, TimeZoneWorkCalendarDTO timeZoneWorkCalendarDTO);

    /**
     * 删除时区设置
     *
     * @param organizationId organizationId
     * @param timeZoneId     timeZoneId
     */
    void deleteTimeZoneWorkCalendar(Long organizationId, Long timeZoneId);
}
