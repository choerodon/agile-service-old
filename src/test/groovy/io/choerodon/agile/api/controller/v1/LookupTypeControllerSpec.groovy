package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.vo.LookupTypeVO
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
 * Date:  14:35 2018/8/22
 * Description:
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class LookupTypeControllerSpec extends Specification {
    @Autowired
    TestRestTemplate restTemplate

    @Shared
    def projectId = 1

    def 'listLookupType'() {
        when: '向查询接口发送请求'
        def entity = restTemplate.getForEntity("/v1/projects/{project_id}/lookup_types", List, projectId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<LookupTypeVO> list = entity.getBody()

        expect: '设置期望值'
        list.size() == 11
        list.get(0).typeCode == 'status_category'
    }
}
