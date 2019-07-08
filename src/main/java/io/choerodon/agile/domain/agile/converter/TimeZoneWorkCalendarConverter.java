package io.choerodon.agile.domain.agile.converter;

import io.choerodon.agile.api.vo.TimeZoneWorkCalendarDTO;
import io.choerodon.agile.domain.agile.entity.TimeZoneWorkCalendarE;
import io.choerodon.agile.infra.dataobject.TimeZoneWorkCalendarDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/12
 */
@Component
public class TimeZoneWorkCalendarConverter implements ConvertorI<TimeZoneWorkCalendarE, TimeZoneWorkCalendarDO, TimeZoneWorkCalendarDTO> {

    @Override
    public TimeZoneWorkCalendarE dtoToEntity(TimeZoneWorkCalendarDTO timeZoneWorkCalendarDTO) {
        TimeZoneWorkCalendarE timeZoneWorkCalendarE = new TimeZoneWorkCalendarE();
        BeanUtils.copyProperties(timeZoneWorkCalendarDTO, timeZoneWorkCalendarE);
        return timeZoneWorkCalendarE;
    }

    @Override
    public TimeZoneWorkCalendarE doToEntity(TimeZoneWorkCalendarDO timeZoneWorkCalendarDO) {
        TimeZoneWorkCalendarE timeZoneWorkCalendarE = new TimeZoneWorkCalendarE();
        BeanUtils.copyProperties(timeZoneWorkCalendarDO, timeZoneWorkCalendarE);
        return timeZoneWorkCalendarE;
    }

    @Override
    public TimeZoneWorkCalendarDTO entityToDto(TimeZoneWorkCalendarE timeZoneWorkCalendarE) {
        TimeZoneWorkCalendarDTO timeZoneWorkCalendarDTO = new TimeZoneWorkCalendarDTO();
        BeanUtils.copyProperties(timeZoneWorkCalendarE, timeZoneWorkCalendarDTO);
        return timeZoneWorkCalendarDTO;
    }

    @Override
    public TimeZoneWorkCalendarDO entityToDo(TimeZoneWorkCalendarE timeZoneWorkCalendarE) {
        TimeZoneWorkCalendarDO timeZoneWorkCalendarDO = new TimeZoneWorkCalendarDO();
        BeanUtils.copyProperties(timeZoneWorkCalendarE, timeZoneWorkCalendarDO);
        return timeZoneWorkCalendarDO;
    }

    @Override
    public TimeZoneWorkCalendarDTO doToDto(TimeZoneWorkCalendarDO timeZoneWorkCalendarDO) {
        TimeZoneWorkCalendarDTO timeZoneWorkCalendarDTO = new TimeZoneWorkCalendarDTO();
        BeanUtils.copyProperties(timeZoneWorkCalendarDO, timeZoneWorkCalendarDTO);
        return timeZoneWorkCalendarDTO;
    }

    @Override
    public TimeZoneWorkCalendarDO dtoToDo(TimeZoneWorkCalendarDTO timeZoneWorkCalendarDTO) {
        TimeZoneWorkCalendarDO timeZoneWorkCalendarDO = new TimeZoneWorkCalendarDO();
        BeanUtils.copyProperties(timeZoneWorkCalendarDTO, timeZoneWorkCalendarDO);
        return timeZoneWorkCalendarDO;
    }
}
