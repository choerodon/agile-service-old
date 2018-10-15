package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.EpicDataDTO
import io.choerodon.agile.api.dto.EpicSequenceDTO
import io.choerodon.agile.api.dto.TimeZoneWorkCalendarCreateDTO
import io.choerodon.agile.api.dto.TimeZoneWorkCalendarDTO
import io.choerodon.agile.api.dto.TimeZoneWorkCalendarRefDTO
import io.choerodon.agile.api.dto.TimeZoneWorkCalendarUpdateDTO
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

    def 'createTimeZoneWorkCalendar'() {
        given: '消息对象'
        TimeZoneWorkCalendarCreateDTO timeZoneWorkCalendarCreateDTO = new TimeZoneWorkCalendarCreateDTO()
        timeZoneWorkCalendarCreateDTO.areaCode = 'Asia'
        timeZoneWorkCalendarCreateDTO.timeZoneCode = 'Shanghai'
        timeZoneWorkCalendarCreateDTO.workTypeCode = 'include'
        when: '向创建创建时区设置的接口发请求'
        def entity = restTemplate.postForEntity('/v1/organizations/{organization_id}/time_zone_work_calendars', timeZoneWorkCalendarCreateDTO, TimeZoneWorkCalendarDTO, organizationId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        TimeZoneWorkCalendarDTO timeZoneWorkCalendarDTO = entity.body

        expect: '期望值'
        timeZoneWorkCalendarDTO.workTypeCode == 'include'
        timeZoneWorkCalendarDTO.timeZoneCode == 'Shanghai'
        timeZoneWorkCalendarDTO.areaCode == 'Asia'
    }

    def 'queryTimeZoneWorkCalendar'() {
        when:
        def entity = restTemplate.getForEntity('/v1/organizations/{organization_id}/time_zone_work_calendars', TimeZoneWorkCalendarDTO, organizationId)

        then:
        entity.statusCode.is2xxSuccessful()
        TimeZoneWorkCalendarDTO timeZoneWorkCalendarDTO = entity.body

        expect: '期望值'
        timeZoneWorkCalendarDTO.workTypeCode == 'include'
        timeZoneWorkCalendarDTO.timeZoneCode == 'Shanghai'
        timeZoneWorkCalendarDTO.areaCode == 'Asia'
    }

    def 'updateTimeZoneWorkCalendar'() {
        given: '消息对象'
        TimeZoneWorkCalendarUpdateDTO timeZoneWorkCalendarUpdateDTO = new TimeZoneWorkCalendarUpdateDTO()
        timeZoneWorkCalendarUpdateDTO.areaCode = 'Europ'
        timeZoneWorkCalendarUpdateDTO.timeZoneCode = 'China'
        timeZoneWorkCalendarUpdateDTO.workTypeCode = 'add'
        timeZoneWorkCalendarUpdateDTO.objectVersionNumber = 1
        HttpEntity<EpicSequenceDTO> requestEntity = new HttpEntity<EpicSequenceDTO>(timeZoneWorkCalendarUpdateDTO, null)
        when: '向创建创建时区设置的接口发请求'
        def entity = restTemplate.exchange('/v1/organizations/{organization_id}/time_zone_work_calendars/{timeZoneId}', HttpMethod.PUT, requestEntity, TimeZoneWorkCalendarDTO, organizationId, 1)
        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        TimeZoneWorkCalendarDTO timeZoneWorkCalendarDTO = entity.body

        expect: '期望值'
        timeZoneWorkCalendarDTO.areaCode == 'Europ'
        timeZoneWorkCalendarDTO.timeZoneCode == 'China'
        timeZoneWorkCalendarDTO.workTypeCode == 'add'
    }

    def 'createTimeZoneWorkCalendarRef'() {
        when: '向创建创建时区日历的接口发请求'
        def entity = restTemplate.postForEntity('/v1/organizations/{organization_id}/time_zone_work_calendars/ref/{timeZoneId}?date={date}', null, TimeZoneWorkCalendarRefDTO, organizationId, 1, "2018-09-11")

        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        TimeZoneWorkCalendarRefDTO timeZoneWorkCalendarRefDTO = entity.body

        expect: '期望值'
        timeZoneWorkCalendarRefDTO.timeZoneId == 1
        timeZoneWorkCalendarRefDTO.workDay == '2018-09-11'
        timeZoneWorkCalendarRefDTO.year == 2018
        timeZoneWorkCalendarRefDTO.organizationId == organizationId
    }

    def 'queryTimeZoneWorkCalendarRefByTimeZoneId'() {
        when:
        def entity = restTemplate.getForEntity('/v1/organizations/{organization_id}/time_zone_work_calendars/ref/{timeZoneId}', List, organizationId, 1)

        then:
        entity.statusCode.is2xxSuccessful()
        List<TimeZoneWorkCalendarRefDTO> timeZoneWorkCalendarRefDTOList = entity.body

        expect: '期望值'
        timeZoneWorkCalendarRefDTOList.size() == 1
    }

    def 'deleteTimeZoneWorkCalendarRef'() {
        when:
        def entity = restTemplate.delete('/v1/organizations/{organization_id}/time_zone_work_calendars/ref/{calendarId}', organizationId, 1)

        then:
        entity == null

    }

    def 'deleteTimeZoneWorkCalendar'() {
        when:
        def entity = restTemplate.delete('/v1/organizations/{organization_id}/time_zone_work_calendars/{timeZoneId}', organizationId, 1)

        then:
        entity == null

    }
}
