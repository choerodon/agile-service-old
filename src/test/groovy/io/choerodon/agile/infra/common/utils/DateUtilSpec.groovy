package io.choerodon.agile.infra.common.utils

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.infra.dataobject.TimeZoneWorkCalendarDO
import io.choerodon.agile.infra.dataobject.TimeZoneWorkCalendarRefDO
import io.choerodon.agile.infra.mapper.TimeZoneWorkCalendarMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification
import spock.lang.Stepwise

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
        TimeZoneWorkCalendarDO timeZoneWorkCalendarDO = new TimeZoneWorkCalendarDO()
        timeZoneWorkCalendarDO.saturdayWork = true
        timeZoneWorkCalendarDO.sundayWork = true
        timeZoneWorkCalendarDO.useHoliday = true
        List<TimeZoneWorkCalendarRefDO> timeZoneWorkCalendarRefDOS = new ArrayList<>()
        TimeZoneWorkCalendarRefDO timeZoneWorkCalendarRefDO = new TimeZoneWorkCalendarRefDO()
        timeZoneWorkCalendarRefDO.status = 0
        timeZoneWorkCalendarRefDO.workDay = "2018-10-1"
        timeZoneWorkCalendarRefDO.year = 2018
        timeZoneWorkCalendarRefDOS.add(timeZoneWorkCalendarRefDO)
        timeZoneWorkCalendarDO.timeZoneWorkCalendarRefDOS = timeZoneWorkCalendarRefDOS
        timeZoneWorkCalendarMapper.queryTimeZoneDetailByOrganizationId(1) >> timeZoneWorkCalendarDO
    }

    def 'getDaysBetweenDifferentDate'() {
        given: 'mockFeign'
        Date dateOne = new Date()
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
        TimeZoneWorkCalendarDO timeZoneWorkCalendarDO = new TimeZoneWorkCalendarDO()
        timeZoneWorkCalendarDO.saturdayWork = true
        timeZoneWorkCalendarDO.sundayWork = true
        timeZoneWorkCalendarDO.useHoliday = true
        List<TimeZoneWorkCalendarRefDO> timeZoneWorkCalendarRefDOS = new ArrayList<>()
        TimeZoneWorkCalendarRefDO timeZoneWorkCalendarRefDO = new TimeZoneWorkCalendarRefDO()
        timeZoneWorkCalendarRefDO.status = 0
        timeZoneWorkCalendarRefDO.workDay = "2018-10-1"
        timeZoneWorkCalendarRefDO.year = 2018
        timeZoneWorkCalendarRefDOS.add(timeZoneWorkCalendarRefDO)
        timeZoneWorkCalendarDO.timeZoneWorkCalendarRefDOS = timeZoneWorkCalendarRefDOS
        timeZoneWorkCalendarMapper.queryTimeZoneDetailByOrganizationId(1) >> timeZoneWorkCalendarDO

        when: '根据参数查询用户信息'
        Integer dayOne = dateUtil.getDaysBetweenDifferentDate(dateOne, dateTwo, null, null, 1)
        dateUtil.getDaysBetweenDifferentDate(dateOne, dateThree, holiday, workDay, 1)

        then: '判断mock交互并且设置返回值'
        dayOne == 1


    }

    def 'getNonWorkdaysDuring'() {
        given: 'mockFeign'
        Date dateOne = new Date()
        Calendar calendar = Calendar.getInstance()
        calendar.setTime(dateOne)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        Date dateTwo = calendar.getTime()
        calendar.add(Calendar.DAY_OF_MONTH, -20)
        Date dateThree = calendar.getTime()

        and: "Mock"
        def timeZoneWorkCalendarMapper = Mock(TimeZoneWorkCalendarMapper.class)
        dateUtil.setTimeZoneWorkCalendarMapper(timeZoneWorkCalendarMapper)
        TimeZoneWorkCalendarDO timeZoneWorkCalendarDO = new TimeZoneWorkCalendarDO()
        timeZoneWorkCalendarDO.saturdayWork = true
        timeZoneWorkCalendarDO.sundayWork = true
        timeZoneWorkCalendarDO.useHoliday = true
        List<TimeZoneWorkCalendarRefDO> timeZoneWorkCalendarRefDOS = new ArrayList<>()
        TimeZoneWorkCalendarRefDO timeZoneWorkCalendarRefDO = new TimeZoneWorkCalendarRefDO()
        timeZoneWorkCalendarRefDO.status = 0
        timeZoneWorkCalendarRefDO.workDay = "2018-10-1"
        timeZoneWorkCalendarRefDO.year = 2018
        timeZoneWorkCalendarRefDOS.add(timeZoneWorkCalendarRefDO)
        timeZoneWorkCalendarDO.timeZoneWorkCalendarRefDOS = timeZoneWorkCalendarRefDOS

        when: '根据参数查询用户信息'
        timeZoneWorkCalendarMapper.queryTimeZoneDetailByOrganizationId(1) >> timeZoneWorkCalendarDO
        Set<Date> dayOne = dateUtil.getNonWorkdaysDuring(dateOne, dateTwo, 1)
        timeZoneWorkCalendarDO.saturdayWork = false
        timeZoneWorkCalendarDO.sundayWork = false
        timeZoneWorkCalendarDO.useHoliday = false
        timeZoneWorkCalendarMapper.queryTimeZoneDetailByOrganizationId(1) >> timeZoneWorkCalendarDO
        dateUtil.getNonWorkdaysDuring(dateOne, dateThree, 1)

        then: '判断mock交互并且设置返回值'
        dayOne.size() == 1

    }
}
