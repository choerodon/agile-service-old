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
 * @since 2018/12/24
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class FixDataControllerTest extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Shared
    def projectId = 1L

    def 'moveStatus'() {
        when: '迁移数据，查询所有状态，执行1'
        def entity = restTemplate.getForEntity("/v1/fix_data/move_status",null)
        then:
        entity.statusCode.is2xxSuccessful()

    }

    def 'updateAllData'() {
        when: '迁移数据，查询所有状态，执行2'
        def entity = restTemplate.getForEntity("/v1/fix_data/update_all_data",null)
        then:
        entity.statusCode.is2xxSuccessful()

    }
}
