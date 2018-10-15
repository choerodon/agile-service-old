package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.domain.agile.entity.TimeZoneWorkCalendarRefE;
import io.choerodon.agile.domain.agile.repository.TimeZoneWorkCalendarRefRepository;
import io.choerodon.agile.infra.dataobject.TimeZoneWorkCalendarRefDO;
import io.choerodon.agile.infra.mapper.TimeZoneWorkCalendarRefMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/15
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class TimeZoneWorkCalendarRefRepositoryImpl implements TimeZoneWorkCalendarRefRepository {

    private static final String CREATE_ERROR = "error.TimeZoneWorkCalendarRef.create";
    private static final String DELETE_ERROR = "error.TimeZoneWorkCalendarRef.delete";

    @Autowired
    private TimeZoneWorkCalendarRefMapper timeZoneWorkCalendarRefMapper;

    @Override
    public TimeZoneWorkCalendarRefE create(TimeZoneWorkCalendarRefE timeZoneWorkCalendarRefRefE) {
        TimeZoneWorkCalendarRefDO timeZoneWorkCalendarRefDO = ConvertHelper.convert(timeZoneWorkCalendarRefRefE, TimeZoneWorkCalendarRefDO.class);
        if (timeZoneWorkCalendarRefMapper.insert(timeZoneWorkCalendarRefDO) != 1) {
            throw new CommonException(CREATE_ERROR);
        }
        return ConvertHelper.convert(timeZoneWorkCalendarRefMapper.selectByPrimaryKey(timeZoneWorkCalendarRefDO), TimeZoneWorkCalendarRefE.class);
    }

    @Override
    public int delete(Long organizationId, Long calendarId) {
        TimeZoneWorkCalendarRefDO timeZoneWorkCalendarRefDO = new TimeZoneWorkCalendarRefDO();
        timeZoneWorkCalendarRefDO.setOrganizationId(organizationId);
        timeZoneWorkCalendarRefDO.setCalendarId(calendarId);
        int isDelete = timeZoneWorkCalendarRefMapper.delete(timeZoneWorkCalendarRefDO);
        if (isDelete != 1) {
            throw new CommonException(DELETE_ERROR);
        }
        return isDelete;
    }

    @Override
    public void batchDeleteByTimeZoneId(Long organizationId, Long timeZoneId) {
        TimeZoneWorkCalendarRefDO timeZoneWorkCalendarRefDO = new TimeZoneWorkCalendarRefDO();
        timeZoneWorkCalendarRefDO.setOrganizationId(organizationId);
        timeZoneWorkCalendarRefDO.setTimeZoneId(timeZoneId);
        timeZoneWorkCalendarRefMapper.delete(timeZoneWorkCalendarRefDO);
    }
}
