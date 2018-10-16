package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.dto.*;
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
import io.choerodon.agile.infra.mapper.WorkCalendarHolidayRefMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

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
    private WorkCalendarHolidayRefMapper workCalendarHolidayRefMapper;

    @Autowired
    private TimeZoneWorkCalendarAssembler timeZoneWorkCalendarAssembler;

    @Autowired
    private TimeZoneWorkCalendarRefRepository timeZoneWorkCalendarRefRepository;

    @Override
    public TimeZoneWorkCalendarDTO updateTimeZoneWorkCalendar(Long organizationId, Long timeZoneId, TimeZoneWorkCalendarUpdateDTO timeZoneWorkCalendarUpdateDTO) {
        TimeZoneWorkCalendarE timeZoneWorkCalendarE = timeZoneWorkCalendarAssembler.toTarget(timeZoneWorkCalendarUpdateDTO, TimeZoneWorkCalendarE.class);
        timeZoneWorkCalendarE.setOrganizationId(organizationId);
        timeZoneWorkCalendarE.setTimeZoneId(timeZoneId);
        return ConvertHelper.convert(timeZoneWorkCalendarRepository.update(timeZoneWorkCalendarAssembler
                .toTarget(timeZoneWorkCalendarE, TimeZoneWorkCalendarE.class)), TimeZoneWorkCalendarDTO.class);
    }

    @Override
    public TimeZoneWorkCalendarRefDTO createTimeZoneWorkCalendarRef(Long organizationId, Long timeZoneId, TimeZoneWorkCalendarRefCreateDTO timeZoneWorkCalendarRefCreateDTO) {
        TimeZoneWorkCalendarRefE timeZoneWorkCalendarRefE;
        try {
            timeZoneWorkCalendarRefE = new TimeZoneWorkCalendarRefE(timeZoneId, timeZoneWorkCalendarRefCreateDTO.getWorkDay(), timeZoneWorkCalendarRefCreateDTO.getStatus(), organizationId);
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
        TimeZoneWorkCalendarDO query = timeZoneWorkCalendarMapper.selectOne(timeZoneWorkCalendarDO);
        if (query == null) {
            timeZoneWorkCalendarDO.setAreaCode("Asia");
            timeZoneWorkCalendarDO.setTimeZoneCode("Asia/Shanghai");
            timeZoneWorkCalendarDO.setSaturdayWork(false);
            timeZoneWorkCalendarDO.setSundayWork(false);
            timeZoneWorkCalendarDO.setUseHoliday(true);
            TimeZoneWorkCalendarE timeZoneWorkCalendarE = timeZoneWorkCalendarAssembler
                    .toTarget(timeZoneWorkCalendarDO, TimeZoneWorkCalendarE.class);
            timeZoneWorkCalendarE.setOrganizationId(organizationId);
            return ConvertHelper.convert(timeZoneWorkCalendarRepository.create(timeZoneWorkCalendarE), TimeZoneWorkCalendarDTO.class);
        } else {
            return ConvertHelper.convert(query, TimeZoneWorkCalendarDTO.class);
        }
    }

    @Override
    public List<TimeZoneWorkCalendarRefDTO> queryTimeZoneWorkCalendarRefByTimeZoneId(Long organizationId, Long timeZoneId) {
        TimeZoneWorkCalendarRefDO timeZoneWorkCalendarRefDO = new TimeZoneWorkCalendarRefDO();
        timeZoneWorkCalendarRefDO.setOrganizationId(organizationId);
        timeZoneWorkCalendarRefDO.setTimeZoneId(timeZoneId);
        return ConvertHelper.convertList(timeZoneWorkCalendarRefMapper.select(timeZoneWorkCalendarRefDO), TimeZoneWorkCalendarRefDTO.class);
    }

    @Override
    public TimeZoneWorkCalendarRefDetailDTO queryTimeZoneWorkCalendarDetail(Long organizationId) {
        TimeZoneWorkCalendarRefDetailDTO timeZoneWorkCalendarRefDetailDTO = new TimeZoneWorkCalendarRefDetailDTO();
        TimeZoneWorkCalendarDO query = new TimeZoneWorkCalendarDO();
        query.setOrganizationId(organizationId);
        TimeZoneWorkCalendarDO timeZoneWorkCalendarDO = timeZoneWorkCalendarMapper.selectOne(query);
        if (timeZoneWorkCalendarDO != null) {
            TimeZoneWorkCalendarRefDO timeZoneWorkCalendarRefDO = new TimeZoneWorkCalendarRefDO();
            timeZoneWorkCalendarRefDO.setOrganizationId(organizationId);
            timeZoneWorkCalendarRefDO.setTimeZoneId(timeZoneWorkCalendarDO.getTimeZoneId());
            timeZoneWorkCalendarRefDetailDTO = timeZoneWorkCalendarAssembler.toTarget(timeZoneWorkCalendarDO, TimeZoneWorkCalendarRefDetailDTO.class);
            timeZoneWorkCalendarRefDetailDTO.setTimeZoneWorkCalendarDTOS(timeZoneWorkCalendarRefMapper.select(timeZoneWorkCalendarRefDO)
                    .stream().map(d -> {
                        TimeZoneWorkCalendarRefCreateDTO timeZoneWorkCalendarRefCreateDTO = new TimeZoneWorkCalendarRefCreateDTO();
                        timeZoneWorkCalendarRefCreateDTO.setWorkDay(d.getWorkDay());
                        timeZoneWorkCalendarRefCreateDTO.setStatus(d.getStatus());
                        return timeZoneWorkCalendarRefCreateDTO;
                    }).collect(Collectors.toSet()));
            if (timeZoneWorkCalendarDO.getUseHoliday()) {
                timeZoneWorkCalendarRefDetailDTO.setWorkHolidayCalendarDTOS(workCalendarHolidayRefMapper.selectAll().stream().map(d -> {
                    TimeZoneWorkCalendarRefCreateDTO timeZoneWorkCalendarRefCreateDTO = new TimeZoneWorkCalendarRefCreateDTO();
                    timeZoneWorkCalendarRefCreateDTO.setStatus(d.getStatus());
                    timeZoneWorkCalendarRefCreateDTO.setWorkDay(d.getHoliday());
                    return timeZoneWorkCalendarRefCreateDTO;
                }).collect(Collectors.toSet()));
            }
        }
        return timeZoneWorkCalendarRefDetailDTO;
    }
}
