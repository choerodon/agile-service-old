package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.dto.TimeZoneWorkCalendarRefDTO;
import io.choerodon.agile.domain.agile.entity.TimeZoneWorkCalendarRefE;
import io.choerodon.agile.infra.dataobject.TimeZoneWorkCalendarRefDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/12
 */
@Component
public class TimeZoneWorkCalendarRefConverter implements ConvertorI<TimeZoneWorkCalendarRefE, TimeZoneWorkCalendarRefDO, TimeZoneWorkCalendarRefDTO> {

    @Override
    public TimeZoneWorkCalendarRefE dtoToEntity(TimeZoneWorkCalendarRefDTO timeZoneWorkCalendarRefDTO) {
        TimeZoneWorkCalendarRefE timeZoneWorkCalendarRefE = new TimeZoneWorkCalendarRefE();
        BeanUtils.copyProperties(timeZoneWorkCalendarRefDTO, timeZoneWorkCalendarRefE);
        return timeZoneWorkCalendarRefE;
    }

    @Override
    public TimeZoneWorkCalendarRefE doToEntity(TimeZoneWorkCalendarRefDO timeZoneWorkCalendarRefDO) {
        TimeZoneWorkCalendarRefE timeZoneWorkCalendarRefE = new TimeZoneWorkCalendarRefE();
        BeanUtils.copyProperties(timeZoneWorkCalendarRefDO, timeZoneWorkCalendarRefE);
        return timeZoneWorkCalendarRefE;
    }

    @Override
    public TimeZoneWorkCalendarRefDTO entityToDto(TimeZoneWorkCalendarRefE timeZoneWorkCalendarRefE) {
        TimeZoneWorkCalendarRefDTO timeZoneWorkCalendarRefDTO = new TimeZoneWorkCalendarRefDTO();
        BeanUtils.copyProperties(timeZoneWorkCalendarRefE, timeZoneWorkCalendarRefDTO);
        return timeZoneWorkCalendarRefDTO;
    }

    @Override
    public TimeZoneWorkCalendarRefDO entityToDo(TimeZoneWorkCalendarRefE timeZoneWorkCalendarRefE) {
        TimeZoneWorkCalendarRefDO timeZoneWorkCalendarRefDO = new TimeZoneWorkCalendarRefDO();
        BeanUtils.copyProperties(timeZoneWorkCalendarRefE, timeZoneWorkCalendarRefDO);
        return timeZoneWorkCalendarRefDO;
    }

    @Override
    public TimeZoneWorkCalendarRefDTO doToDto(TimeZoneWorkCalendarRefDO timeZoneWorkCalendarRefDO) {
        TimeZoneWorkCalendarRefDTO timeZoneWorkCalendarRefDTO = new TimeZoneWorkCalendarRefDTO();
        BeanUtils.copyProperties(timeZoneWorkCalendarRefDO, timeZoneWorkCalendarRefDTO);
        return timeZoneWorkCalendarRefDTO;
    }

    @Override
    public TimeZoneWorkCalendarRefDO dtoToDo(TimeZoneWorkCalendarRefDTO timeZoneWorkCalendarRefDTO) {
        TimeZoneWorkCalendarRefDO timeZoneWorkCalendarRefDO = new TimeZoneWorkCalendarRefDO();
        BeanUtils.copyProperties(timeZoneWorkCalendarRefDTO, timeZoneWorkCalendarRefDO);
        return timeZoneWorkCalendarRefDO;
    }
}
