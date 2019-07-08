package io.choerodon.agile.api.validator;

import io.choerodon.agile.infra.dataobject.TimeZoneWorkCalendarDO;
import io.choerodon.agile.infra.mapper.TimeZoneWorkCalendarMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/8.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class TimeZoneWorkCalendarValidator {

    @Autowired
    private TimeZoneWorkCalendarMapper timeZoneWorkCalendarMapper;

    public void verifyCreateTimeZoneWorkCalendarRef(Long organizationId, Long timeZoneId) {
        TimeZoneWorkCalendarDO timeZoneWorkCalendarDO = new TimeZoneWorkCalendarDO();
        timeZoneWorkCalendarDO.setOrganizationId(organizationId);
        timeZoneWorkCalendarDO.setTimeZoneId(timeZoneId);
        TimeZoneWorkCalendarDO query = timeZoneWorkCalendarMapper.selectOne(timeZoneWorkCalendarDO);
        if (query == null) {
            throw new CommonException("error.TimeZoneWorkCalendar.notFound");
        }
    }

}
