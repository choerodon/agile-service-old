//package io.choerodon.agile.domain.agile.converter;
//
//import io.choerodon.agile.api.vo.TimeZoneWorkCalendarVO;
//import io.choerodon.agile.domain.agile.entity.TimeZoneWorkCalendarE;
//import io.choerodon.agile.infra.dataobject.TimeZoneWorkCalendarDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * @author dinghuang123@gmail.com
// * @since 2018/10/12
// */
//@Component
//public class TimeZoneWorkCalendarConverter implements ConvertorI<TimeZoneWorkCalendarE, TimeZoneWorkCalendarDTO, TimeZoneWorkCalendarVO> {
//
//    @Override
//    public TimeZoneWorkCalendarE dtoToEntity(TimeZoneWorkCalendarVO timeZoneWorkCalendarVO) {
//        TimeZoneWorkCalendarE timeZoneWorkCalendarE = new TimeZoneWorkCalendarE();
//        BeanUtils.copyProperties(timeZoneWorkCalendarVO, timeZoneWorkCalendarE);
//        return timeZoneWorkCalendarE;
//    }
//
//    @Override
//    public TimeZoneWorkCalendarE doToEntity(TimeZoneWorkCalendarDTO timeZoneWorkCalendarDTO) {
//        TimeZoneWorkCalendarE timeZoneWorkCalendarE = new TimeZoneWorkCalendarE();
//        BeanUtils.copyProperties(timeZoneWorkCalendarDTO, timeZoneWorkCalendarE);
//        return timeZoneWorkCalendarE;
//    }
//
//    @Override
//    public TimeZoneWorkCalendarVO entityToDto(TimeZoneWorkCalendarE timeZoneWorkCalendarE) {
//        TimeZoneWorkCalendarVO timeZoneWorkCalendarVO = new TimeZoneWorkCalendarVO();
//        BeanUtils.copyProperties(timeZoneWorkCalendarE, timeZoneWorkCalendarVO);
//        return timeZoneWorkCalendarVO;
//    }
//
//    @Override
//    public TimeZoneWorkCalendarDTO entityToDo(TimeZoneWorkCalendarE timeZoneWorkCalendarE) {
//        TimeZoneWorkCalendarDTO timeZoneWorkCalendarDTO = new TimeZoneWorkCalendarDTO();
//        BeanUtils.copyProperties(timeZoneWorkCalendarE, timeZoneWorkCalendarDTO);
//        return timeZoneWorkCalendarDTO;
//    }
//
//    @Override
//    public TimeZoneWorkCalendarVO doToDto(TimeZoneWorkCalendarDTO timeZoneWorkCalendarDTO) {
//        TimeZoneWorkCalendarVO timeZoneWorkCalendarVO = new TimeZoneWorkCalendarVO();
//        BeanUtils.copyProperties(timeZoneWorkCalendarDTO, timeZoneWorkCalendarVO);
//        return timeZoneWorkCalendarVO;
//    }
//
//    @Override
//    public TimeZoneWorkCalendarDTO dtoToDo(TimeZoneWorkCalendarVO timeZoneWorkCalendarVO) {
//        TimeZoneWorkCalendarDTO timeZoneWorkCalendarDTO = new TimeZoneWorkCalendarDTO();
//        BeanUtils.copyProperties(timeZoneWorkCalendarVO, timeZoneWorkCalendarDTO);
//        return timeZoneWorkCalendarDTO;
//    }
//}
