package io.choerodon.agile.infra.common.utils

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.infra.dataobject.TimeZoneWorkCalendarDTO
import io.choerodon.agile.infra.dataobject.TimeZoneWorkCalendarRefDTO
import io.choerodon.agile.infra.mapper.TimeZoneWorkCalendarMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification
import spock.lang.Stepwise

import java.text.SimpleDateFormat

/**
 *
 * @author dinghuang123@gmail.com
 * @since 2018/10/17
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class DateUtilSpec extends Specification {

    @Autowired
    DateUtil dateUtil

    def setup() {
        given: '设置feign调用mockito'
        def timeZoneWorkCalendarMapper = Mock(TimeZoneWorkCalendarMapper.class)
        dateUtil.setTimeZoneWorkCalendarMapper(timeZoneWorkCalendarMapper)
        TimeZoneWorkCalendarDTO timeZoneWorkCalendarDO = new TimeZoneWorkCalendarDTO()
        timeZoneWorkCalendarDO.saturdayWork = true
        timeZoneWorkCalendarDO.sundayWork = true
        timeZoneWorkCalendarDO.useHoliday = true
        List<TimeZoneWorkCalendarRefDTO> timeZoneWorkCalendarRefDOS = new ArrayList<>()
        TimeZoneWorkCalendarRefDTO timeZoneWorkCalendarRefDO = new TimeZoneWorkCalendarRefDTO()
        timeZoneWorkCalendarRefDO.status = 0
        timeZoneWorkCalendarRefDO.workDay = "2018-10-1"
        timeZoneWorkCalendarRefDO.year = 2018
        timeZoneWorkCalendarRefDOS.add(timeZoneWorkCalendarRefDO)
        timeZoneWorkCalendarDO.timeZoneWorkCalendarRefDTOS = timeZoneWorkCalendarRefDOS
        timeZoneWorkCalendarMapper.queryTimeZoneDetailByOrganizationId(1) >> timeZoneWorkCalendarDO
    }

    def 'getDaysBetweenDifferentDate'() {
        given: 'mockFeign'
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateOne = simpleDateFormat.parse("2018-10-21")
        Date dateTwo = dateOne
        Calendar calendar = Calendar.getInstance()
        calendar.setTime(dateOne)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        Date dateThree = calendar.getTime()
        List<Date> holiday = new ArrayList<>()
        holiday.add(dateOne)
        List<Date> workDay = new ArrayList<>()
        workDay.add(dateTwo)

        and: "Mock"
        def timeZoneWorkCalendarMapper = Mock(TimeZoneWorkCalendarMapper.class)
        dateUtil.setTimeZoneWorkCalendarMapper(timeZoneWorkCalendarMapper)
        TimeZoneWorkCalendarDTO timeZoneWorkCalendarDO = new TimeZoneWorkCalendarDTO()
        timeZoneWorkCalendarDO.saturdayWork = true
        timeZoneWorkCalendarDO.sundayWork = true
        timeZoneWorkCalendarDO.useHoliday = true
        List<TimeZoneWorkCalendarRefDTO> timeZoneWorkCalendarRefDOS = new ArrayList<>()
        TimeZoneWorkCalendarRefDTO timeZoneWorkCalendarRefDO = new TimeZoneWorkCalendarRefDTO()
        timeZoneWorkCalendarRefDO.status = 0
        timeZoneWorkCalendarRefDO.workDay = "2018-10-1"
        timeZoneWorkCalendarRefDO.year = 2018
        timeZoneWorkCalendarRefDOS.add(timeZoneWorkCalendarRefDO)
        timeZoneWorkCalendarDO.timeZoneWorkCalendarRefDTOS = timeZoneWorkCalendarRefDOS
        timeZoneWorkCalendarMapper.queryTimeZoneDetailByOrganizationId(1) >> timeZoneWorkCalendarDO

        when: '获取不同时间'
        Integer dayOne = dateUtil.getDaysBetweenDifferentDate(dateOne, dateTwo, null, null, 1)
        dateUtil.getDaysBetweenDifferentDate(dateOne, dateThree, holiday, workDay, 1)

        then: '判断mock交互并且设置返回值'
        dayOne == 1


    }

    def 'getNonWorkdaysDuring'() {
        given: 'mockFeign'
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateOne = simpleDateFormat.parse("2018-10-21")
        Calendar calendar = Calendar.getInstance()
        calendar.setTime(dateOne)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        Date dateTwo = calendar.getTime()
        calendar.add(Calendar.DAY_OF_MONTH, -20)
        Date dateThree = calendar.getTime()

        and: "Mock"
        def timeZoneWorkCalendarMapper = Mock(TimeZoneWorkCalendarMapper.class)
        dateUtil.setTimeZoneWorkCalendarMapper(timeZoneWorkCalendarMapper)
        TimeZoneWorkCalendarDTO timeZoneWorkCalendarDO = new TimeZoneWorkCalendarDTO()
        timeZoneWorkCalendarDO.saturdayWork = true
        timeZoneWorkCalendarDO.sundayWork = true
        timeZoneWorkCalendarDO.useHoliday = true
        List<TimeZoneWorkCalendarRefDTO> timeZoneWorkCalendarRefDOS = new ArrayList<>()
        TimeZoneWorkCalendarRefDTO timeZoneWorkCalendarRefDO = new TimeZoneWorkCalendarRefDTO()
        timeZoneWorkCalendarRefDO.status = 0
        timeZoneWorkCalendarRefDO.workDay = "2018-10-1"
        timeZoneWorkCalendarRefDO.year = 2018
        timeZoneWorkCalendarRefDOS.add(timeZoneWorkCalendarRefDO)
        timeZoneWorkCalendarDO.timeZoneWorkCalendarRefDTOS = timeZoneWorkCalendarRefDOS

        when: '根据参数查询用户信息'
        timeZoneWorkCalendarMapper.queryTimeZoneDetailByOrganizationId(1) >> timeZoneWorkCalendarDO
        Set<Date> day = dateUtil.getNonWorkdaysDuring(dateOne, dateTwo, 1)
        timeZoneWorkCalendarDO.saturdayWork = false
        timeZoneWorkCalendarDO.sundayWork = false
        timeZoneWorkCalendarDO.useHoliday = false
        timeZoneWorkCalendarMapper.queryTimeZoneDetailByOrganizationId(1) >> timeZoneWorkCalendarDO
        dateUtil.getNonWorkdaysDuring(dateOne, dateThree, 1)

        then: '期望'
        day.size() == 0

    }
}
