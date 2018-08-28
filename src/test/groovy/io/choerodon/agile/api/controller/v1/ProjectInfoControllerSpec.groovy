package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.IssueLinkCreateDTO
import io.choerodon.agile.api.dto.IssueLinkDTO
import io.choerodon.agile.api.dto.ProjectInfoDTO
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
 * Date:  9:16 2018/8/28
 * Description:
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class ProjectInfoControllerSpec extends Specification {
    @Autowired
    TestRestTemplate restTemplate

    @Shared
    def projectId = 1

    def 'updateProjectInfo'() {
        given: '链接issueDTO对象'
        ProjectInfoDTO projectInfoDTO=new ProjectInfoDTO()
        projectInfoDTO.projectId=2
        projectInfoDTO.projectCode='AGTest'

        when: '发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issue_links/{issueId}', issueLinkCreateDTOList, List, projectId, issueLinkCreateDTO.issueId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<IssueLinkDTO> result = entity.body

        expect: '设置期望值'
        result.size() == 1
        result.get(0).linkedIssueId == 2
    }

}
