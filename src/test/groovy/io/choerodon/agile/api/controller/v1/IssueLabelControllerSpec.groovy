package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.vo.IssueLabelVO
import io.choerodon.agile.infra.dataobject.LabelIssueRelDO
import io.choerodon.agile.infra.mapper.IssueMapper
import io.choerodon.agile.infra.mapper.LabelIssueRelMapper
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
    IssueMapper issueMapper

    @Autowired
    LabelIssueRelMapper labelIssueRelMapper

    @Shared
    def projectId = 1L

    @Shared
    def realCount = 0

    def 'listIssueLinkByIssueId'() {
        given: '初始化值'
        realCount = 0
        when: '发送请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/issue_labels', List, projectIds)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<IssueLabelVO> result = entity.body

        if (result.size() == 0) {
            LabelIssueRelDO labelIssueRelDO = new LabelIssueRelDO()
            labelIssueRelDO.projectId = projectIds
            if (labelIssueRelMapper.selectOne(labelIssueRelDO) != null) {
                realCount = 1
            }
        } else {
            realCount = result.size()
        }

        expect: '设置期望值'
        result.size() == realCount

        where: '给定参数'
        projectIds | expectConut
        1L         | realCount
        2L         | realCount
        20L        | realCount

    }

}
