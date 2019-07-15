package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.vo.SprintWorkCalendarVO
import io.choerodon.agile.api.vo.WorkCalendarRefCreateVO
import io.choerodon.agile.api.vo.WorkCalendarRefVO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

/**
 * @author shinan.chen
 * @date 2019/4/28
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class WorkCalendarRefControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Shared
    def projectId = 1L

    def "createSprintWorkCalendarRef"() {
        given: '创建冲刺工作日历'
        WorkCalendarRefCreateVO createDTO = new WorkCalendarRefCreateVO()
        createDTO.workDay = "2018-10-10"
        createDTO.status = 0

        when: '发送请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/work_calendar_ref/sprint/{sprint_id}', createDTO, WorkCalendarRefVO, projectId, 1L)

        then: '请求结果'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        WorkCalendarRefVO result = entity.body

        expect: '期望值'
        result.workDay == "2018-10-10"
        result.status == 0
    }

    def "createProjectWorkCalendarRef"() {
        given: '创建项目工作日历'
        WorkCalendarRefCreateVO createDTO = new WorkCalendarRefCreateVO()
        createDTO.workDay = "2018-10-10"
        createDTO.status = 0

        when: '发送请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/work_calendar_ref/project', createDTO, WorkCalendarRefVO, projectId)

        then: '请求结果'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        WorkCalendarRefVO result = entity.body

        expect: '期望值'
        result.workDay == "2018-10-10"
        result.status == 0
    }

    def 'querySprintWorkCalendarRefs'() {
        when:
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/work_calendar_ref/sprint?year={year}', SprintWorkCalendarVO, projectId, 2018)

        then:
        entity.statusCode.is2xxSuccessful()
        SprintWorkCalendarVO sprintWorkCalendarDTO = entity.body

        expect: "验证"
        sprintWorkCalendarDTO != null
    }

    def 'queryProjectWorkCalendarRefs'() {
        when:
        ParameterizedTypeReference<List<WorkCalendarRefVO>> typeRef = new ParameterizedTypeReference<List<WorkCalendarRefVO>>() {
        }
        def entity = restTemplate.exchange('/v1/projects/{project_id}/work_calendar_ref/project?year={year}', HttpMethod.GET,null, typeRef, projectId, 2018)

        then:
        entity.statusCode.is2xxSuccessful()
        List<WorkCalendarRefVO> refs = entity.body

        expect: "验证"
        refs.size() != 0
    }

    def "deleteSprintWorkCalendarRef"() {
        when: '发送请求'
        def entity = restTemplate.exchange('/v1/projects/{project_id}/work_calendar_ref/{calendar_id}', HttpMethod.DELETE, new HttpEntity<Object>(), ResponseEntity.class, projectId, 1)

        then: '请求结果'
        entity.statusCode == HttpStatus.NO_CONTENT

    }
}
