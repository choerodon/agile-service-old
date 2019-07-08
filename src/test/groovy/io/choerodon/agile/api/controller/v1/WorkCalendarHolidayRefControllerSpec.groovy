package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.AgileTestConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

/**
 *
 * @author dinghuang123@gmail.com
 * @since 2018/10/10
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class WorkCalendarHolidayRefControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Shared
    def organizationId = 1L

    def 'updateWorkCalendarHolidayRefByYear'() {
        when: '向创建冲刺的接口发请求'
        def entity = restTemplate.postForEntity('/v1/organizations/{organization_id}/work_calendar_holiday_refs?year={year}', null, null, organizationId, 2018)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()
    }

    def 'queryWorkCalendarHolidayRelByYear'() {
        when:
        def entity = restTemplate.getForEntity('/v1/organizations/{organization_id}/work_calendar_holiday_refs?year={year}', List.class, organizationId, 2018)

        then:
        entity.statusCode.is2xxSuccessful()
    }

}
