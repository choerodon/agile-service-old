package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.domain.agile.entity.TimeZoneWorkCalendarE;
import io.choerodon.agile.domain.agile.repository.TimeZoneWorkCalendarRepository;
import io.choerodon.agile.infra.dataobject.TimeZoneWorkCalendarDO;
import io.choerodon.agile.infra.mapper.TimeZoneWorkCalendarMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/12
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class TimeZoneWorkCalendarRepositoryImpl implements TimeZoneWorkCalendarRepository {

    private static final String UPDATE_ERROR = "error.TimeZoneWorkCalendar.update";
    private static final String INSERT_ERROR = "error.TimeZoneWorkCalendar.create";

    @Autowired
    private TimeZoneWorkCalendarMapper timeZoneWorkCalendarMapper;

    @Override
    public TimeZoneWorkCalendarE update(TimeZoneWorkCalendarE timeZoneWorkCalendarE) {
        TimeZoneWorkCalendarDO timeZoneWorkCalendarDO = ConvertHelper.convert(timeZoneWorkCalendarE, TimeZoneWorkCalendarDO.class);
        if (timeZoneWorkCalendarMapper.updateByPrimaryKeySelective(timeZoneWorkCalendarDO) != 1) {
            throw new CommonException(UPDATE_ERROR);
        }
        return ConvertHelper.convert(timeZoneWorkCalendarMapper.selectByPrimaryKey(timeZoneWorkCalendarDO.getTimeZoneId()), TimeZoneWorkCalendarE.class);
    }

    @Override
    public TimeZoneWorkCalendarE create(TimeZoneWorkCalendarE timeZoneWorkCalendarE) {
        TimeZoneWorkCalendarDO timeZoneWorkCalendarDO = ConvertHelper.convert(timeZoneWorkCalendarE, TimeZoneWorkCalendarDO.class);
        if (timeZoneWorkCalendarMapper.insert(timeZoneWorkCalendarDO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        return ConvertHelper.convert(timeZoneWorkCalendarMapper.selectByPrimaryKey(timeZoneWorkCalendarDO.getTimeZoneId()), TimeZoneWorkCalendarE.class);
    }

    @Override
    public int delete(Long organizationId, Long timeZoneId) {
        TimeZoneWorkCalendarDO issueLinkTypeDO = new TimeZoneWorkCalendarDO();
        issueLinkTypeDO.setTimeZoneId(timeZoneId);
        issueLinkTypeDO.setOrganizationId(organizationId);
        return timeZoneWorkCalendarMapper.delete(issueLinkTypeDO);
    }
}
