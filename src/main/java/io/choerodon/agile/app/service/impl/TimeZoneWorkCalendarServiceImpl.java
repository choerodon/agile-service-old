package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.api.validator.WorkCalendarValidator;
import io.choerodon.agile.app.assembler.TimeZoneWorkCalendarAssembler;
import io.choerodon.agile.app.service.TimeZoneWorkCalendarService;
import io.choerodon.agile.infra.dataobject.TimeZoneWorkCalendarDTO;
import io.choerodon.agile.infra.dataobject.TimeZoneWorkCalendarRefDTO;
import io.choerodon.agile.infra.common.utils.DateUtil;
import io.choerodon.agile.infra.dataobject.WorkCalendarHolidayRefDTO;
import io.choerodon.agile.infra.mapper.TimeZoneWorkCalendarMapper;
import io.choerodon.agile.infra.mapper.TimeZoneWorkCalendarRefMapper;
import io.choerodon.agile.infra.mapper.WorkCalendarHolidayRefMapper;
import io.choerodon.core.exception.CommonException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/12
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class TimeZoneWorkCalendarServiceImpl implements TimeZoneWorkCalendarService {

//    @Autowired
//    private TimeZoneWorkCalendarRepository timeZoneWorkCalendarRepository;

    @Autowired
    private TimeZoneWorkCalendarMapper timeZoneWorkCalendarMapper;

    @Autowired
    private TimeZoneWorkCalendarRefMapper timeZoneWorkCalendarRefMapper;

    @Autowired
    private WorkCalendarHolidayRefMapper workCalendarHolidayRefMapper;

    @Autowired
    private TimeZoneWorkCalendarAssembler timeZoneWorkCalendarAssembler;

//    @Autowired
//    private TimeZoneWorkCalendarRefRepository timeZoneWorkCalendarRefRepository;

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeZoneWorkCalendarServiceImpl.class);
    private static final String PARSE_EXCEPTION = "ParseException{}";
    private static final String UPDATE_ERROR = "error.TimeZoneWorkCalendar.update";
    private static final String INSERT_ERROR = "error.TimeZoneWorkCalendar.create";
    private static final String CREATE_ERROR = "error.TimeZoneWorkCalendarRef.create";
    private static final String DELETE_ERROR = "error.TimeZoneWorkCalendarRef.delete";

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public TimeZoneWorkCalendarVO updateTimeZoneWorkCalendar(Long organizationId, Long timeZoneId, TimeZoneWorkCalendarUpdateVO timeZoneWorkCalendarUpdateVO) {
        TimeZoneWorkCalendarDTO timeZoneWorkCalendarDTO = timeZoneWorkCalendarAssembler.toTarget(timeZoneWorkCalendarUpdateVO, TimeZoneWorkCalendarDTO.class);
        timeZoneWorkCalendarDTO.setOrganizationId(organizationId);
        timeZoneWorkCalendarDTO.setTimeZoneId(timeZoneId);
        return modelMapper.map(update(timeZoneWorkCalendarDTO), TimeZoneWorkCalendarVO.class);
    }

    @Override
    public TimeZoneWorkCalendarRefVO createTimeZoneWorkCalendarRef(Long organizationId, Long timeZoneId, TimeZoneWorkCalendarRefCreateVO timeZoneWorkCalendarRefCreateVO) {
        TimeZoneWorkCalendarRefDTO timeZoneWorkCalendarRefDTO;
        timeZoneWorkCalendarRefDTO = new TimeZoneWorkCalendarRefDTO();
        timeZoneWorkCalendarRefDTO.setTimeZoneId(timeZoneId);
        timeZoneWorkCalendarRefDTO.setWorkDay(timeZoneWorkCalendarRefCreateVO.getWorkDay());
        timeZoneWorkCalendarRefDTO.setStatus(timeZoneWorkCalendarRefCreateVO.getStatus());
        timeZoneWorkCalendarRefDTO.setYear(WorkCalendarValidator.checkWorkDayAndStatus(timeZoneWorkCalendarRefCreateVO.getWorkDay(), timeZoneWorkCalendarRefCreateVO.getStatus()));
        timeZoneWorkCalendarRefDTO.setOrganizationId(organizationId);
        if (timeZoneWorkCalendarRefMapper.insert(timeZoneWorkCalendarRefDTO) != 1) {
            throw new CommonException(CREATE_ERROR);
        }
        return modelMapper.map(timeZoneWorkCalendarRefMapper.selectByPrimaryKey(timeZoneWorkCalendarRefDTO), TimeZoneWorkCalendarRefVO.class);
    }

    @Override
    public void deleteTimeZoneWorkCalendarRef(Long organizationId, Long calendarId) {
//        timeZoneWorkCalendarRefRepository.delete(organizationId, calendarId);
        TimeZoneWorkCalendarRefDTO timeZoneWorkCalendarRefDTO = new TimeZoneWorkCalendarRefDTO();
        timeZoneWorkCalendarRefDTO.setOrganizationId(organizationId);
        timeZoneWorkCalendarRefDTO.setCalendarId(calendarId);
        int isDelete = timeZoneWorkCalendarRefMapper.delete(timeZoneWorkCalendarRefDTO);
        if (isDelete != 1) {
            throw new CommonException(DELETE_ERROR);
        }
    }

    @Override
    public TimeZoneWorkCalendarVO queryTimeZoneWorkCalendar(Long organizationId) {
        TimeZoneWorkCalendarDTO timeZoneWorkCalendarDTO = new TimeZoneWorkCalendarDTO();
        timeZoneWorkCalendarDTO.setOrganizationId(organizationId);
        TimeZoneWorkCalendarDTO query = timeZoneWorkCalendarMapper.selectOne(timeZoneWorkCalendarDTO);
        if (query == null) {
            timeZoneWorkCalendarDTO.setAreaCode("Asia");
            timeZoneWorkCalendarDTO.setTimeZoneCode("Asia/Shanghai");
            timeZoneWorkCalendarDTO.setSaturdayWork(false);
            timeZoneWorkCalendarDTO.setSundayWork(false);
            timeZoneWorkCalendarDTO.setUseHoliday(true);
//            TimeZoneWorkCalendarE timeZoneWorkCalendarE = timeZoneWorkCalendarAssembler
//                    .toTarget(timeZoneWorkCalendarDTO, TimeZoneWorkCalendarE.class);
//            timeZoneWorkCalendarE.setOrganizationId(organizationId);
            return modelMapper.map(create(timeZoneWorkCalendarDTO), TimeZoneWorkCalendarVO.class);
        } else {
            return modelMapper.map(query, TimeZoneWorkCalendarVO.class);
        }
    }

    @Override
    public List<TimeZoneWorkCalendarRefVO> queryTimeZoneWorkCalendarRefByTimeZoneId(Long organizationId, Long timeZoneId, Integer year) {
        return modelMapper.map(timeZoneWorkCalendarRefMapper.queryWithNextYearByYear(organizationId, timeZoneId, year), new TypeToken<List<TimeZoneWorkCalendarRefVO>>(){}.getType());
    }

    @Override
    public TimeZoneWorkCalendarRefDetailVO queryTimeZoneWorkCalendarDetail(Long organizationId, Integer year) {
        TimeZoneWorkCalendarDTO query = new TimeZoneWorkCalendarDTO();
        query.setOrganizationId(organizationId);
        TimeZoneWorkCalendarDTO timeZoneWorkCalendarDTO = timeZoneWorkCalendarMapper.selectOne(query);
        if (timeZoneWorkCalendarDTO != null) {
            return initTimeZoneWorkCalendarRefDetailDTO(timeZoneWorkCalendarDTO, organizationId, year);
        } else {
            timeZoneWorkCalendarDTO = new TimeZoneWorkCalendarDTO();
            timeZoneWorkCalendarDTO.setAreaCode("Asia");
            timeZoneWorkCalendarDTO.setTimeZoneCode("Asia/Shanghai");
            timeZoneWorkCalendarDTO.setSaturdayWork(false);
            timeZoneWorkCalendarDTO.setSundayWork(false);
            timeZoneWorkCalendarDTO.setUseHoliday(true);
//            TimeZoneWorkCalendarE timeZoneWorkCalendarE = timeZoneWorkCalendarAssembler
//                    .toTarget(timeZoneWorkCalendarDTO, TimeZoneWorkCalendarE.class);
            timeZoneWorkCalendarDTO.setOrganizationId(organizationId);
            timeZoneWorkCalendarDTO = modelMapper.map(create(timeZoneWorkCalendarDTO), TimeZoneWorkCalendarDTO.class);
            return initTimeZoneWorkCalendarRefDetailDTO(timeZoneWorkCalendarDTO, organizationId, year);
        }
    }

    private TimeZoneWorkCalendarRefDetailVO initTimeZoneWorkCalendarRefDetailDTO(TimeZoneWorkCalendarDTO timeZoneWorkCalendarDTO, Long organizationId, Integer year) {
        TimeZoneWorkCalendarRefDetailVO timeZoneWorkCalendarRefDetailVO = timeZoneWorkCalendarAssembler.toTarget(timeZoneWorkCalendarDTO, TimeZoneWorkCalendarRefDetailVO.class);
        timeZoneWorkCalendarRefDetailVO.setTimeZoneWorkCalendarDTOS(timeZoneWorkCalendarRefMapper.queryWithNextYearByYear(organizationId, timeZoneWorkCalendarDTO.getTimeZoneId(), year)
                .stream().map(d -> {
                    TimeZoneWorkCalendarRefCreateVO timeZoneWorkCalendarRefCreateVO = new TimeZoneWorkCalendarRefCreateVO();
                    timeZoneWorkCalendarRefCreateVO.setWorkDay(d.getWorkDay());
                    timeZoneWorkCalendarRefCreateVO.setStatus(d.getStatus());
                    return timeZoneWorkCalendarRefCreateVO;
                }).collect(Collectors.toSet()));
        if (timeZoneWorkCalendarDTO.getUseHoliday()) {
            List<WorkCalendarHolidayRefDTO> workCalendarHolidayRefDTOS = workCalendarHolidayRefMapper.queryWorkCalendarHolidayRelWithNextYearByYear(year);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
            workCalendarHolidayRefDTOS.forEach(workCalendarHolidayRefDO -> {
                try {
                    workCalendarHolidayRefDO.setHoliday(simpleDateFormat.format(simpleDateFormat.parse(workCalendarHolidayRefDO.getHoliday())));
                } catch (ParseException e) {
                    LOGGER.warn(PARSE_EXCEPTION, e);
                }
            });
            timeZoneWorkCalendarRefDetailVO.setWorkHolidayCalendarDTOS(DateUtil.stringDateCompare().
                    sortedCopy(workCalendarHolidayRefDTOS).stream().map(d -> {
                TimeZoneWorkCalendarHolidayRefVO timeZoneWorkCalendarHolidayRefVO = new TimeZoneWorkCalendarHolidayRefVO();
                timeZoneWorkCalendarHolidayRefVO.setStatus(d.getStatus());
                timeZoneWorkCalendarHolidayRefVO.setHoliday(d.getHoliday());
                timeZoneWorkCalendarHolidayRefVO.setName(d.getName());
                return timeZoneWorkCalendarHolidayRefVO;
            }).collect(Collectors.toSet()));
        }
        return timeZoneWorkCalendarRefDetailVO;
    }

    @Override
    public TimeZoneWorkCalendarDTO create(TimeZoneWorkCalendarDTO timeZoneWorkCalendarDTO) {
        if (timeZoneWorkCalendarMapper.insert(timeZoneWorkCalendarDTO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        return timeZoneWorkCalendarMapper.selectByPrimaryKey(timeZoneWorkCalendarDTO.getTimeZoneId());
    }

    @Override
    public TimeZoneWorkCalendarDTO update(TimeZoneWorkCalendarDTO timeZoneWorkCalendarDTO) {
        if (timeZoneWorkCalendarMapper.updateByPrimaryKeySelective(timeZoneWorkCalendarDTO) != 1) {
            throw new CommonException(UPDATE_ERROR);
        }
        return timeZoneWorkCalendarMapper.selectByPrimaryKey(timeZoneWorkCalendarDTO.getTimeZoneId());
    }

}
