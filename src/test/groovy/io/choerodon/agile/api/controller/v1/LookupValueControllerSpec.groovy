package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.LookupTypeWithValuesDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Creator: ChangpingShi0213@gmail.com
 * Date:  16:30 2018/8/21
 * Description:
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class LookupValueControllerSpec extends Specification {
    @Autowired
    TestRestTemplate restTemplate

    @Shared
    def projectId = 1

    def 'queryLookupValueByCode'() {
        when: '向查询接口发送请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/lookup_values/{typeCode}', LookupTypeWithValuesDTO,projectId, typeCode)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        LookupTypeWithValuesDTO lookupTypeWithValuesDTO = entity.body

        expect: '设置期望值'
        lookupTypeWithValuesDTO.lookupValues.size() == expectConut

        where: '给定参数'
        typeCode       | expectConut
        'column_color' | 4
        'constraint'   | 3
        'epic_color'   | 8
        'issue_type'   | 6
    }

    def 'queryConstraintLookupValue'() {
        when: '向查询列约束下的value值接口发送请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/lookup_values/constraint/list', LookupTypeWithValuesDTO,projectId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        LookupTypeWithValuesDTO lookupTypeWithValuesDTO = entity.body

        expect: '设置期望值'
        lookupTypeWithValuesDTO.lookupValues.size() == 3

    }

}
