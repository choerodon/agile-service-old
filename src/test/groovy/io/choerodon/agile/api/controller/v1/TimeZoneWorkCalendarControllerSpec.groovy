package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.vo.TimeZoneWorkCalendarDTO
import io.choerodon.agile.api.vo.TimeZoneWorkCalendarRefCreateDTO
import io.choerodon.agile.api.vo.TimeZoneWorkCalendarRefDTO
import io.choerodon.agile.api.vo.TimeZoneWorkCalendarRefDetailDTO
import io.choerodon.agile.api.vo.TimeZoneWorkCalendarUpdateDTO
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
        def entity = restTemplate.getForEntity('/v1/organizations/{organization_id}/time_zone_work_calendars', TimeZoneWorkCalendarDTO, organizationId)

        then:
        entity.statusCode.is2xxSuccessful()
        TimeZoneWorkCalendarDTO timeZoneWorkCalendarDTO = entity.body

        expect: '期望值'
        !timeZoneWorkCalendarDTO.saturdayWork
        !timeZoneWorkCalendarDTO.sundayWork
        timeZoneWorkCalendarDTO.useHoliday
        timeZoneWorkCalendarDTO.timeZoneCode == 'Asia/Shanghai'
        timeZoneWorkCalendarDTO.areaCode == 'Asia'
    }

    def 'updateTimeZoneWorkCalendar'() {
        given: '消息对象'
        TimeZoneWorkCalendarUpdateDTO timeZoneWorkCalendarUpdateDTO = new TimeZoneWorkCalendarUpdateDTO()
        timeZoneWorkCalendarUpdateDTO.areaCode = 'Europ'
        timeZoneWorkCalendarUpdateDTO.timeZoneCode = 'China'
        timeZoneWorkCalendarUpdateDTO.saturdayWork = true
        timeZoneWorkCalendarUpdateDTO.sundayWork = true
        timeZoneWorkCalendarUpdateDTO.useHoliday = true
        timeZoneWorkCalendarUpdateDTO.objectVersionNumber = 1
        HttpEntity<TimeZoneWorkCalendarUpdateDTO> requestEntity = new HttpEntity<TimeZoneWorkCalendarUpdateDTO>(timeZoneWorkCalendarUpdateDTO, null)
        when: '向创建创建时区设置的接口发请求'
        def entity = restTemplate.exchange('/v1/organizations/{organization_id}/time_zone_work_calendars/{timeZoneId}', HttpMethod.PUT, requestEntity, TimeZoneWorkCalendarDTO, organizationId, 1)
        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        TimeZoneWorkCalendarDTO timeZoneWorkCalendarDTO = entity.body

        expect: '期望值'
        timeZoneWorkCalendarDTO.areaCode == 'Europ'
        timeZoneWorkCalendarDTO.timeZoneCode == 'China'
        timeZoneWorkCalendarUpdateDTO.saturdayWork
        timeZoneWorkCalendarUpdateDTO.sundayWork
        timeZoneWorkCalendarUpdateDTO.useHoliday
    }

    def 'createTimeZoneWorkCalendarRef'() {
        given: '消息体'
        TimeZoneWorkCalendarRefCreateDTO timeZoneWorkCalendarRefCreateDTO = new TimeZoneWorkCalendarRefCreateDTO()
        timeZoneWorkCalendarRefCreateDTO.status = 0
        timeZoneWorkCalendarRefCreateDTO.workDay = "2018-10-1"
        when: '向创建创建时区日历的接口发请求'
        def entity = restTemplate.postForEntity('/v1/organizations/{organization_id}/time_zone_work_calendars/ref/{timeZoneId}', timeZoneWorkCalendarRefCreateDTO, TimeZoneWorkCalendarRefDTO, organizationId, 1)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        TimeZoneWorkCalendarRefDTO timeZoneWorkCalendarRefDTO = entity.body

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
        List<TimeZoneWorkCalendarRefDTO> timeZoneWorkCalendarRefDTOList = entity.body

        expect: '期望值'
        timeZoneWorkCalendarRefDTOList.size() == 1
    }

    def 'queryTimeZoneWorkCalendarDetail'() {
        when:
        def entity = restTemplate.getForEntity('/v1/organizations/{organization_id}/time_zone_work_calendars/detail?year={year}', TimeZoneWorkCalendarRefDetailDTO, organizationId, 2018)

        then:
        entity.statusCode.is2xxSuccessful()
        TimeZoneWorkCalendarRefDetailDTO timeZoneWorkCalendarRefDetailDTO = entity.body

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
