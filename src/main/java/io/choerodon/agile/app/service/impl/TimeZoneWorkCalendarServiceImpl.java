package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.TimeZoneWorkCalendarCreateDTO;
import io.choerodon.agile.api.dto.TimeZoneWorkCalendarDTO;
import io.choerodon.agile.api.dto.TimeZoneWorkCalendarRefDTO;
import io.choerodon.agile.api.dto.TimeZoneWorkCalendarUpdateDTO;
import io.choerodon.agile.app.assembler.TimeZoneWorkCalendarAssembler;
import io.choerodon.agile.app.service.TimeZoneWorkCalendarService;
import io.choerodon.agile.domain.agile.entity.TimeZoneWorkCalendarE;
import io.choerodon.agile.domain.agile.entity.TimeZoneWorkCalendarRefE;
import io.choerodon.agile.domain.agile.repository.TimeZoneWorkCalendarRefRepository;
import io.choerodon.agile.domain.agile.repository.TimeZoneWorkCalendarRepository;
import io.choerodon.agile.infra.dataobject.TimeZoneWorkCalendarDO;
import io.choerodon.agile.infra.dataobject.TimeZoneWorkCalendarRefDO;
import io.choerodon.agile.infra.mapper.TimeZoneWorkCalendarMapper;
import io.choerodon.agile.infra.mapper.TimeZoneWorkCalendarRefMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.List;

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
    private TimeZoneWorkCalendarMapper timeZoneWorkCalendarMapper;

    @Autowired
    private TimeZoneWorkCalendarRefMapper timeZoneWorkCalendarRefMapper;

    @Autowired
    private TimeZoneWorkCalendarAssembler timeZoneWorkCalendarAssembler;

    @Autowired
    private TimeZoneWorkCalendarRefRepository timeZoneWorkCalendarRefRepository;

    @Override
    public TimeZoneWorkCalendarDTO createTimeZoneWorkCalendar(Long organizationId, TimeZoneWorkCalendarCreateDTO timeZoneWorkCalendarCreateDTO) {
        TimeZoneWorkCalendarDO timeZoneWorkCalendarDO = new TimeZoneWorkCalendarDO();
        timeZoneWorkCalendarDO.setOrganizationId(organizationId);
        TimeZoneWorkCalendarDO query = timeZoneWorkCalendarMapper.selectOne(timeZoneWorkCalendarDO);
        if (query == null) {
            TimeZoneWorkCalendarE timeZoneWorkCalendarE = timeZoneWorkCalendarAssembler
                    .toTarget(timeZoneWorkCalendarCreateDTO, TimeZoneWorkCalendarE.class);
            timeZoneWorkCalendarE.setOrganizationId(organizationId);
            return ConvertHelper.convert(timeZoneWorkCalendarRepository.create(timeZoneWorkCalendarE), TimeZoneWorkCalendarDTO.class);
        } else {
            return ConvertHelper.convert(query, TimeZoneWorkCalendarDTO.class);
        }

    }

    @Override
    public TimeZoneWorkCalendarDTO updateTimeZoneWorkCalendar(Long organizationId, Long timeZoneId, TimeZoneWorkCalendarUpdateDTO timeZoneWorkCalendarUpdateDTO) {
        TimeZoneWorkCalendarE timeZoneWorkCalendarE = timeZoneWorkCalendarAssembler.toTarget(timeZoneWorkCalendarUpdateDTO, TimeZoneWorkCalendarE.class);
        timeZoneWorkCalendarE.setOrganizationId(organizationId);
        timeZoneWorkCalendarE.setTimeZoneId(timeZoneId);
        return ConvertHelper.convert(timeZoneWorkCalendarRepository.update(timeZoneWorkCalendarAssembler
                .toTarget(timeZoneWorkCalendarE, TimeZoneWorkCalendarE.class)), TimeZoneWorkCalendarDTO.class);
    }

    @Override
    public void deleteTimeZoneWorkCalendar(Long organizationId, Long timeZoneId) {
        timeZoneWorkCalendarRepository.delete(organizationId, timeZoneId);
        timeZoneWorkCalendarRefRepository.batchDeleteByTimeZoneId(organizationId, timeZoneId);
    }

    @Override
    public TimeZoneWorkCalendarRefDTO createTimeZoneWorkCalendarRef(Long organizationId, Long timeZoneId, String date) {
        TimeZoneWorkCalendarRefE timeZoneWorkCalendarRefE;
        try {
            timeZoneWorkCalendarRefE = new TimeZoneWorkCalendarRefE(timeZoneId, date, organizationId);
        } catch (ParseException e) {
            throw new CommonException("ParseException{}", e);
        }
        return ConvertHelper.convert(timeZoneWorkCalendarRefRepository.create(timeZoneWorkCalendarRefE), TimeZoneWorkCalendarRefDTO.class);
    }

    @Override
    public void deleteTimeZoneWorkCalendarRef(Long organizationId, Long calendarId) {
        timeZoneWorkCalendarRefRepository.delete(organizationId, calendarId);
    }

    @Override
    public TimeZoneWorkCalendarDTO queryTimeZoneWorkCalendar(Long organizationId) {
        TimeZoneWorkCalendarDO timeZoneWorkCalendarDO = new TimeZoneWorkCalendarDO();
        timeZoneWorkCalendarDO.setOrganizationId(organizationId);
        return ConvertHelper.convert(timeZoneWorkCalendarMapper.selectOne(timeZoneWorkCalendarDO), TimeZoneWorkCalendarDTO.class);
    }

    @Override
    public List<TimeZoneWorkCalendarRefDTO> queryTimeZoneWorkCalendarRefByTimeZoneId(Long organizationId, Long timeZoneId) {
        TimeZoneWorkCalendarRefDO timeZoneWorkCalendarRefDO = new TimeZoneWorkCalendarRefDO();
        timeZoneWorkCalendarRefDO.setOrganizationId(organizationId);
        timeZoneWorkCalendarRefDO.setTimeZoneId(timeZoneId);
        return ConvertHelper.convertList(timeZoneWorkCalendarRefMapper.select(timeZoneWorkCalendarRefDO), TimeZoneWorkCalendarRefDTO.class);
    }
}
