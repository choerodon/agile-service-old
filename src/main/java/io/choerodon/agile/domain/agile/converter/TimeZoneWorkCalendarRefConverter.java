//package io.choerodon.agile.domain.agile.converter;
//
//import io.choerodon.agile.api.vo.TimeZoneWorkCalendarRefVO;
//import io.choerodon.agile.domain.agile.entity.TimeZoneWorkCalendarRefE;
//import io.choerodon.agile.infra.dataobject.TimeZoneWorkCalendarRefDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * @author dinghuang123@gmail.com
// * @since 2018/10/12
// */
//@Component
//public class TimeZoneWorkCalendarRefConverter implements ConvertorI<TimeZoneWorkCalendarRefE, TimeZoneWorkCalendarRefDTO, TimeZoneWorkCalendarRefVO> {
//
//    @Override
//    public TimeZoneWorkCalendarRefE dtoToEntity(TimeZoneWorkCalendarRefVO timeZoneWorkCalendarRefVO) {
//        TimeZoneWorkCalendarRefE timeZoneWorkCalendarRefE = new TimeZoneWorkCalendarRefE();
//        BeanUtils.copyProperties(timeZoneWorkCalendarRefVO, timeZoneWorkCalendarRefE);
//        return timeZoneWorkCalendarRefE;
//    }
//
//    @Override
//    public TimeZoneWorkCalendarRefE doToEntity(TimeZoneWorkCalendarRefDTO timeZoneWorkCalendarRefDTO) {
//        TimeZoneWorkCalendarRefE timeZoneWorkCalendarRefE = new TimeZoneWorkCalendarRefE();
//        BeanUtils.copyProperties(timeZoneWorkCalendarRefDTO, timeZoneWorkCalendarRefE);
//        return timeZoneWorkCalendarRefE;
//    }
//
//    @Override
//    public TimeZoneWorkCalendarRefVO entityToDto(TimeZoneWorkCalendarRefE timeZoneWorkCalendarRefE) {
//        TimeZoneWorkCalendarRefVO timeZoneWorkCalendarRefVO = new TimeZoneWorkCalendarRefVO();
//        BeanUtils.copyProperties(timeZoneWorkCalendarRefE, timeZoneWorkCalendarRefVO);
//        return timeZoneWorkCalendarRefVO;
//    }
//
//    @Override
//    public TimeZoneWorkCalendarRefDTO entityToDo(TimeZoneWorkCalendarRefE timeZoneWorkCalendarRefE) {
//        TimeZoneWorkCalendarRefDTO timeZoneWorkCalendarRefDTO = new TimeZoneWorkCalendarRefDTO();
//        BeanUtils.copyProperties(timeZoneWorkCalendarRefE, timeZoneWorkCalendarRefDTO);
//        return timeZoneWorkCalendarRefDTO;
//    }
//
//    @Override
//    public TimeZoneWorkCalendarRefVO doToDto(TimeZoneWorkCalendarRefDTO timeZoneWorkCalendarRefDTO) {
//        TimeZoneWorkCalendarRefVO timeZoneWorkCalendarRefVO = new TimeZoneWorkCalendarRefVO();
//        BeanUtils.copyProperties(timeZoneWorkCalendarRefDTO, timeZoneWorkCalendarRefVO);
//        return timeZoneWorkCalendarRefVO;
//    }
//
//    @Override
//    public TimeZoneWorkCalendarRefDTO dtoToDo(TimeZoneWorkCalendarRefVO timeZoneWorkCalendarRefVO) {
//        TimeZoneWorkCalendarRefDTO timeZoneWorkCalendarRefDTO = new TimeZoneWorkCalendarRefDTO();
//        BeanUtils.copyProperties(timeZoneWorkCalendarRefVO, timeZoneWorkCalendarRefDTO);
//        return timeZoneWorkCalendarRefDTO;
//    }
//}
