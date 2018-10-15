package io.choerodon.agile.domain.agile.repository;

import io.choerodon.agile.domain.agile.entity.TimeZoneWorkCalendarRefE;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/15
 */
public interface TimeZoneWorkCalendarRefRepository {

    TimeZoneWorkCalendarRefE create(TimeZoneWorkCalendarRefE timeZoneWorkCalendarRefRefE);

    int delete(Long organizationId, Long calendarId);

    void batchDeleteByTimeZoneId(Long organizationId, Long timeZoneId);
}
