package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.IssueLabelDTO
import io.choerodon.agile.infra.dataobject.IssueSprintRelDO
import io.choerodon.agile.infra.mapper.IssueSprintRelMapper
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
 * Creator: scp
 * Date:  9:06 2018/8/28
 * Description:
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class IssueLabelControllerSpec extends Specification {
    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    IssueSprintRelMapper issueSprintRelMapper

    @Shared
    def realConut = 0

    def 'listIssueLinkByIssueId'() {
        when: '发送请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/issue_labels', List, projectIds)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<IssueLabelDTO> result = entity.body
        if (result.size() == 0) {
            IssueSprintRelDO issueSprintRelDO = new IssueSprintRelDO()
            issueSprintRelDO.projectId = projectIds
            if (issueSprintRelMapper.selectOne(issueSprintRelDO) != null) {
                realConut = 1
            } else {
                realConut = 0
            }
        }

        and: '设置期望值'
        realConut == expectConut

        where: '给定参数'
        projectIds | expectConut
        1L         | 1
        2L         | 0

    }
}
