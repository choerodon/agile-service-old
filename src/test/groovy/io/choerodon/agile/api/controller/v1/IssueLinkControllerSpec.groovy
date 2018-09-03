package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.IssueLinkCreateDTO
import io.choerodon.agile.api.dto.IssueLinkDTO
import io.choerodon.agile.infra.mapper.IssueLinkMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Creator: ChangpingShi0213@gmail.com
 * Date:  14:59 2018/8/22
 * Description:
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class IssueLinkControllerSpec extends Specification {
    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    private IssueLinkMapper issueLinkMapper

    @Shared
    def projectId = 1


    def 'createIssueLinkList'() {
        given: '链接issueDTO对象'
        List<IssueLinkCreateDTO> issueLinkCreateDTOList = new ArrayList<IssueLinkCreateDTO>()
        IssueLinkCreateDTO issueLinkCreateDTO = new IssueLinkCreateDTO()
        issueLinkCreateDTO.linkTypeId = 1L
        issueLinkCreateDTO.issueId = 1L
        issueLinkCreateDTO.linkedIssueId = 2L
        issueLinkCreateDTOList.add(issueLinkCreateDTO)

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

    def 'listIssueLinkByIssueId'() {
        when: '发送请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/issue_links/{issueId}', List, projectId, issueId, noIssueTest)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<IssueLinkDTO> result = entity.body

        expect: '设置期望值'
        result.size() == expectConut

        where: '给定参数'
        issueId | noIssueTest | expectConut
        1L      | true        | 1
        2L      | true        | 1
        10L     | true        | 0
        1L      | false       | 1
    }

    def 'listIssueLinkByBatch'() {
        given: '设置issueIds'
        List<Long> issueIds = new ArrayList<>()
        issueIds.add(1L)
        issueIds.add(2L)
        issueIds.add(19L)

        when: '发送请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issue_links/issues', issueIds, List, projectId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<IssueLinkDTO> result = entity.body

        expect: '设置期望值'
        result.size() == 2
    }

    def 'deleteIssueLink'() {
        when: '发送请求'
        def entity = restTemplate.exchange("/v1/projects/{project_id}/issue_links/{issueLinkId}",
                HttpMethod.DELETE,
                null,
                ResponseEntity.class,
                projectId,
                linkId
        )

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        issueLinkMapper.selectByPrimaryKey(linkId) == expectObject

        where: '期望值'
        linkId | expectObject
        1L     | null
        10L    | null

    }

}
