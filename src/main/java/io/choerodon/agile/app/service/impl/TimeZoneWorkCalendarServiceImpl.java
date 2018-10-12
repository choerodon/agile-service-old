package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.TimeZoneWorkCalendarCreateDTO;
import io.choerodon.agile.api.dto.TimeZoneWorkCalendarDTO;
import io.choerodon.agile.app.assembler.TimeZoneWorkCalendarAssembler;
import io.choerodon.agile.app.service.TimeZoneWorkCalendarService;
import io.choerodon.agile.domain.agile.entity.TimeZoneWorkCalendarE;
import io.choerodon.agile.domain.agile.repository.TimeZoneWorkCalendarRepository;
import io.choerodon.core.convertor.ConvertHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/12
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class TimeZoneWorkCalendarServiceImpl implements TimeZoneWorkCalendarService {

    @Autowired
    private TimeZoneWorkCalendarRepository timeZoneWorkCalendarRepository;

    @Autowired
    private TimeZoneWorkCalendarAssembler timeZoneWorkCalendarAssembler;

    @Override
    public TimeZoneWorkCalendarDTO createTimeZoneWorkCalendar(Long organizationId,
                                                              TimeZoneWorkCalendarCreateDTO timeZoneWorkCalendarCreateDTO) {
        timeZoneWorkCalendarCreateDTO.setOrganizationId(organizationId);
        return ConvertHelper.convert(timeZoneWorkCalendarRepository.create(timeZoneWorkCalendarAssembler
                .toTarget(timeZoneWorkCalendarCreateDTO, TimeZoneWorkCalendarE.class)), TimeZoneWorkCalendarDTO.class);
    }

    @Override
    public TimeZoneWorkCalendarDTO updateTimeZoneWorkCalendar(Long organizationId, TimeZoneWorkCalendarDTO timeZoneWorkCalendarDTO) {
        return ConvertHelper.convert(timeZoneWorkCalendarRepository.create(timeZoneWorkCalendarAssembler
                .toTarget(timeZoneWorkCalendarDTO, TimeZoneWorkCalendarE.class)), TimeZoneWorkCalendarDTO.class);
    }

    @Override
    public void deleteTimeZoneWorkCalendar(Long organizationId, Long timeZoneId) {
        timeZoneWorkCalendarRepository.delete(organizationId, timeZoneId);
    }
}
