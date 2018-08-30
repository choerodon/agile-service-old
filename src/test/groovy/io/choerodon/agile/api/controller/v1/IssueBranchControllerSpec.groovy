package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.IssueBranchDTO
import io.choerodon.agile.infra.dataobject.IssueBranchDO
import io.choerodon.agile.infra.mapper.IssueBranchMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Creator: scp
 * Date:  21:02 2018/8/27
 * Description:
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class IssueBranchControllerSpec extends Specification {
    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    private IssueBranchMapper issueBranchMapper

    @Shared
    def projectId = 1

    @Shared
    def issueId = 1

    @Shared
    def branchId = 1L

    def 'createIssueBranch'() {
        given: '创建IssueBranchDTO对象'
        IssueBranchDTO issueBranchDTO = new IssueBranchDTO()
        issueBranchDTO.issueId = issueId
        issueBranchDTO.branchName = 'feature-test-01'
        issueBranchDTO.projectId = projectId
        issueBranchDTO.applicationId = 1L
        issueBranchDTO.branchType = 'feature'

        when: '发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issue_branch', issueBranchDTO, IssueBranchDTO, projectId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        IssueBranchDTO result = entity.body

        expect: '设置期望值'
        result.branchType == issueBranchDTO.branchType
        result.branchName == issueBranchDTO.branchName
        result.projectId == issueBranchDTO.projectId
        result.issueId == issueBranchDTO.issueId
    }

    def 'updateIssueBranch'() {
        given: '更新IssueBranchDTO对象'
        IssueBranchDO issueBranchDO = issueBranchMapper.selectByPrimaryKey(branchId)
        IssueBranchDTO issueBranchDTO = new IssueBranchDTO()
        issueBranchDTO.branchId = issueBranchDO.branchId
        issueBranchDTO.projectId = issueBranchDO.projectId
        issueBranchDTO.issueId = issueBranchDO.issueId
        issueBranchDTO.branchName = 'feature-test-02'
        issueBranchDTO.branchType = issueBranchDO.branchType
        issueBranchDTO.applicationId = issueBranchDO.applicationId
        issueBranchDTO.objectVersionNumber = issueBranchDO.objectVersionNumber

        when: '发请求'
        def entity = restTemplate.patchForObject('/v1/projects/{project_id}/issue_branch/{branchId}', issueBranchDTO, IssueBranchDTO.class, projectId, branchId)

        then: '设置值'
        entity.branchId != null

        expect: '设置期望值'
        entity.branchType == issueBranchDTO.branchType
        entity.branchName == 'feature-test-02'
        entity.projectId == issueBranchDTO.projectId
        entity.issueId == issueBranchDTO.issueId
    }

    def 'queryIssueBranchById'() {
        when: '发请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/issue_branch/{branchId}', IssueBranchDTO, projectId, branchIds)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        IssueBranchDTO result = entity.body

        expect: '设置值'
        result.projectId == expectObject

        where: '设置期望值'
        branchIds | expectObject
        1L        | 1L
        2L        | null
    }

    def 'deleteIssueBranch'() {
        when: '发送请求'
        def entity = restTemplate.exchange("/v1/projects/{project_id}/issue_branch/{branchId}",
                HttpMethod.DELETE,
                null,
                ResponseEntity.class,
                projectId,
                branchIds
        )

        then: '返回值'
        entity.statusCode == expectCode

        where: '设置期望值'
        branchIds | expectCode
        1L        | HttpStatus.NO_CONTENT
    }

}
