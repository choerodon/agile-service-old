package io.choerodon.agile.domain.agile.repository;

import io.choerodon.agile.domain.agile.entity.TimeZoneWorkCalendarE;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/12
 */
public interface TimeZoneWorkCalendarRepository {

    TimeZoneWorkCalendarE create(TimeZoneWorkCalendarE timeZoneWorkCalendarE);

    TimeZoneWorkCalendarE update(TimeZoneWorkCalendarE timeZoneWorkCalendarE);

}
