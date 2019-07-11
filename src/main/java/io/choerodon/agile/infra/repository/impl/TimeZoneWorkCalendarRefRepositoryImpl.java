//package io.choerodon.agile.infra.repository.impl;
//
//import io.choerodon.agile.domain.agile.entity.TimeZoneWorkCalendarRefE;
//import io.choerodon.agile.infra.dataobject.TimeZoneWorkCalendarRefDTO;
//import io.choerodon.agile.infra.repository.TimeZoneWorkCalendarRefRepository;
//import io.choerodon.agile.infra.mapper.TimeZoneWorkCalendarRefMapper;
//import io.choerodon.core.convertor.ConvertHelper;
//import io.choerodon.core.exception.CommonException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
///**
// * @author dinghuang123@gmail.com
// * @since 2018/10/15
// */
//@Component
//@Transactional(rollbackFor = Exception.class)
//public class TimeZoneWorkCalendarRefRepositoryImpl implements TimeZoneWorkCalendarRefRepository {
//
//    private static final String CREATE_ERROR = "error.TimeZoneWorkCalendarRef.create";
//    private static final String DELETE_ERROR = "error.TimeZoneWorkCalendarRef.delete";
//
//    @Autowired
//    private TimeZoneWorkCalendarRefMapper timeZoneWorkCalendarRefMapper;
//
//    @Override
//    public TimeZoneWorkCalendarRefE create(TimeZoneWorkCalendarRefE timeZoneWorkCalendarRefRefE) {
//        TimeZoneWorkCalendarRefDTO timeZoneWorkCalendarRefDTO = ConvertHelper.convert(timeZoneWorkCalendarRefRefE, TimeZoneWorkCalendarRefDTO.class);
//        if (timeZoneWorkCalendarRefMapper.insert(timeZoneWorkCalendarRefDTO) != 1) {
//            throw new CommonException(CREATE_ERROR);
//        }
//        return ConvertHelper.convert(timeZoneWorkCalendarRefMapper.selectByPrimaryKey(timeZoneWorkCalendarRefDTO), TimeZoneWorkCalendarRefE.class);
//    }
//
//    @Override
//    public int delete(Long organizationId, Long calendarId) {
//        TimeZoneWorkCalendarRefDTO timeZoneWorkCalendarRefDTO = new TimeZoneWorkCalendarRefDTO();
//        timeZoneWorkCalendarRefDTO.setOrganizationId(organizationId);
//        timeZoneWorkCalendarRefDTO.setCalendarId(calendarId);
//        int isDelete = timeZoneWorkCalendarRefMapper.delete(timeZoneWorkCalendarRefDTO);
//        if (isDelete != 1) {
//            throw new CommonException(DELETE_ERROR);
//        }
//        return isDelete;
//    }
//
//    @Override
//    public void batchDeleteByTimeZoneId(Long organizationId, Long timeZoneId) {
//        TimeZoneWorkCalendarRefDTO timeZoneWorkCalendarRefDTO = new TimeZoneWorkCalendarRefDTO();
//        timeZoneWorkCalendarRefDTO.setOrganizationId(organizationId);
//        timeZoneWorkCalendarRefDTO.setTimeZoneId(timeZoneId);
//        timeZoneWorkCalendarRefMapper.delete(timeZoneWorkCalendarRefDTO);
//    }
//}
