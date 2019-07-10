package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.domain.agile.entity.TimeZoneWorkCalendarE;
import io.choerodon.agile.infra.dataobject.TimeZoneWorkCalendarDTO;
import io.choerodon.agile.infra.repository.TimeZoneWorkCalendarRepository;
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
        TimeZoneWorkCalendarDTO timeZoneWorkCalendarDTO = ConvertHelper.convert(timeZoneWorkCalendarE, TimeZoneWorkCalendarDTO.class);
        if (timeZoneWorkCalendarMapper.updateByPrimaryKeySelective(timeZoneWorkCalendarDTO) != 1) {
            throw new CommonException(UPDATE_ERROR);
        }
        return ConvertHelper.convert(timeZoneWorkCalendarMapper.selectByPrimaryKey(timeZoneWorkCalendarDTO.getTimeZoneId()), TimeZoneWorkCalendarE.class);
    }

    @Override
    public TimeZoneWorkCalendarE create(TimeZoneWorkCalendarE timeZoneWorkCalendarE) {
        TimeZoneWorkCalendarDTO timeZoneWorkCalendarDTO = ConvertHelper.convert(timeZoneWorkCalendarE, TimeZoneWorkCalendarDTO.class);
        if (timeZoneWorkCalendarMapper.insert(timeZoneWorkCalendarDTO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        return ConvertHelper.convert(timeZoneWorkCalendarMapper.selectByPrimaryKey(timeZoneWorkCalendarDTO.getTimeZoneId()), TimeZoneWorkCalendarE.class);
    }

}
