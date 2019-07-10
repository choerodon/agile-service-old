package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.vo.TimeZoneWorkCalendarRefCreateVO
import io.choerodon.agile.api.vo.TimeZoneWorkCalendarRefVO
import io.choerodon.agile.api.vo.TimeZoneWorkCalendarRefDetailVO
import io.choerodon.agile.api.vo.TimeZoneWorkCalendarUpdateVO
import io.choerodon.agile.api.vo.TimeZoneWorkCalendarVO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 *
 * @author dinghuang123@gmail.com
 * @since 2018/10/15
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class TimeZoneWorkCalendarControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Shared
    def organizationId = 1L

    def 'queryTimeZoneWorkCalendar'() {
        when:
        def entity = restTemplate.getForEntity('/v1/organizations/{organization_id}/time_zone_work_calendars', TimeZoneWorkCalendarVO, organizationId)

        then:
        entity.statusCode.is2xxSuccessful()
        TimeZoneWorkCalendarVO timeZoneWorkCalendarDTO = entity.body

        expect: '期望值'
        !timeZoneWorkCalendarDTO.saturdayWork
        !timeZoneWorkCalendarDTO.sundayWork
        timeZoneWorkCalendarDTO.useHoliday
        timeZoneWorkCalendarDTO.timeZoneCode == 'Asia/Shanghai'
        timeZoneWorkCalendarDTO.areaCode == 'Asia'
    }

    def 'updateTimeZoneWorkCalendar'() {
        given: '消息对象'
        TimeZoneWorkCalendarUpdateVO timeZoneWorkCalendarUpdateDTO = new TimeZoneWorkCalendarUpdateVO()
        timeZoneWorkCalendarUpdateDTO.areaCode = 'Europ'
        timeZoneWorkCalendarUpdateDTO.timeZoneCode = 'China'
        timeZoneWorkCalendarUpdateDTO.saturdayWork = true
        timeZoneWorkCalendarUpdateDTO.sundayWork = true
        timeZoneWorkCalendarUpdateDTO.useHoliday = true
        timeZoneWorkCalendarUpdateDTO.objectVersionNumber = 1
        HttpEntity<TimeZoneWorkCalendarUpdateVO> requestEntity = new HttpEntity<TimeZoneWorkCalendarUpdateVO>(timeZoneWorkCalendarUpdateDTO, null)
        when: '向创建创建时区设置的接口发请求'
        def entity = restTemplate.exchange('/v1/organizations/{organization_id}/time_zone_work_calendars/{timeZoneId}', HttpMethod.PUT, requestEntity, TimeZoneWorkCalendarVO, organizationId, 1)
        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        TimeZoneWorkCalendarVO timeZoneWorkCalendarDTO = entity.body

        expect: '期望值'
        timeZoneWorkCalendarDTO.areaCode == 'Europ'
        timeZoneWorkCalendarDTO.timeZoneCode == 'China'
        timeZoneWorkCalendarUpdateDTO.saturdayWork
        timeZoneWorkCalendarUpdateDTO.sundayWork
        timeZoneWorkCalendarUpdateDTO.useHoliday
    }

    def 'createTimeZoneWorkCalendarRef'() {
        given: '消息体'
        TimeZoneWorkCalendarRefCreateVO timeZoneWorkCalendarRefCreateDTO = new TimeZoneWorkCalendarRefCreateVO()
        timeZoneWorkCalendarRefCreateDTO.status = 0
        timeZoneWorkCalendarRefCreateDTO.workDay = "2018-10-1"
        when: '向创建创建时区日历的接口发请求'
        def entity = restTemplate.postForEntity('/v1/organizations/{organization_id}/time_zone_work_calendars/ref/{timeZoneId}', timeZoneWorkCalendarRefCreateDTO, TimeZoneWorkCalendarRefVO, organizationId, 1)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        TimeZoneWorkCalendarRefVO timeZoneWorkCalendarRefDTO = entity.body

        expect: '期望值'
        timeZoneWorkCalendarRefDTO.timeZoneId == 1
        timeZoneWorkCalendarRefDTO.workDay == '2018-10-1'
        timeZoneWorkCalendarRefDTO.year == 2018
        timeZoneWorkCalendarRefDTO.status == 0
        timeZoneWorkCalendarRefDTO.organizationId == organizationId
    }

    def 'queryTimeZoneWorkCalendarRefByTimeZoneId'() {
        when:
        def entity = restTemplate.getForEntity('/v1/organizations/{organization_id}/time_zone_work_calendars/ref/{timeZoneId}?year={year}', List, organizationId, 1, 2018)

        then:
        entity.statusCode.is2xxSuccessful()
        List<TimeZoneWorkCalendarRefVO> timeZoneWorkCalendarRefDTOList = entity.body

        expect: '期望值'
        timeZoneWorkCalendarRefDTOList.size() == 1
    }

    def 'queryTimeZoneWorkCalendarDetail'() {
        when:
        def entity = restTemplate.getForEntity('/v1/organizations/{organization_id}/time_zone_work_calendars/detail?year={year}', TimeZoneWorkCalendarRefDetailVO, organizationId, 2018)

        then:
        entity.statusCode.is2xxSuccessful()
        TimeZoneWorkCalendarRefDetailVO timeZoneWorkCalendarRefDetailDTO = entity.body

        expect: '期望值'
        timeZoneWorkCalendarRefDetailDTO.useHoliday
        timeZoneWorkCalendarRefDetailDTO.sundayWork
        timeZoneWorkCalendarRefDetailDTO.saturdayWork
    }

    def 'deleteTimeZoneWorkCalendarRef'() {
        when:
        def entity = restTemplate.delete('/v1/organizations/{organization_id}/time_zone_work_calendars/ref/{calendarId}', organizationId, 1)

        then:
        entity == null

    }

}
