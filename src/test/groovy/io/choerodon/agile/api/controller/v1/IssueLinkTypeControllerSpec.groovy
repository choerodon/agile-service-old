package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.IssueLinkTypeCreateDTO
import io.choerodon.agile.api.dto.IssueLinkTypeDTO
import io.choerodon.agile.infra.dataobject.IssueLinkTypeDO
import io.choerodon.agile.infra.mapper.IssueLinkTypeMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Creator: changpingshi0213@gmail.com
 * Date:  15:00 2018/8/31
 * Description:
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class IssueLinkTypeControllerSpec extends Specification {
    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    IssueLinkTypeMapper issueLinkTypeMapper

    @Shared
    def projectId = 1

    def 'createIssueLinkType'() {
        given: '准备IssueLinkTypeCreateDTO'
        IssueLinkTypeCreateDTO issueLinkTypeCreateDTO = new IssueLinkTypeCreateDTO()
        issueLinkTypeCreateDTO.projectId = 1L
        issueLinkTypeCreateDTO.outWard = 'outWard1'
        issueLinkTypeCreateDTO.linkName = 'linkName1'
        issueLinkTypeCreateDTO.inWard = 'inWard1'

        when: '发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issue_link_types', issueLinkTypeCreateDTO, IssueLinkTypeDTO, projectId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        IssueLinkTypeDTO result = entity.body

        expect: '设置期望值'
        result.inWard == issueLinkTypeCreateDTO.inWard
        result.outWard == issueLinkTypeCreateDTO.outWard
        result.projectId == issueLinkTypeCreateDTO.projectId
        result.linkName == issueLinkTypeCreateDTO.linkName
    }

    def 'updateIssueLinkType'() {
        given: '准备IssueLinkTypeDTO'

        List<IssueLinkTypeDO> list = issueLinkTypeMapper.selectAll()
        IssueLinkTypeDTO issueLinkTypeDTO = new IssueLinkTypeDTO()
        issueLinkTypeDTO.projectId = list.get(3).projectId
        issueLinkTypeDTO.outWard = list.get(3).outWard
        issueLinkTypeDTO.linkName = 'linkName22222'
        issueLinkTypeDTO.inWard = 'inWard222222222'
        issueLinkTypeDTO.objectVersionNumber = list.get(3).objectVersionNumber
        issueLinkTypeDTO.linkTypeId = list.get(3).linkTypeId
        HttpEntity<IssueLinkTypeDTO> requestEntity = new HttpEntity<IssueLinkTypeDTO>(issueLinkTypeDTO, null)

        when: '发请求'
        def entity = restTemplate.exchange("/v1/projects/{project_id}/issue_link_types",
                HttpMethod.PUT,
                requestEntity,
                IssueLinkTypeDTO.class,
                projectId
        )

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        IssueLinkTypeDTO result = entity.body

        expect: '设置期望值'
        result.inWard == issueLinkTypeDTO.inWard
        result.outWard == issueLinkTypeDTO.outWard
        result.projectId == issueLinkTypeDTO.projectId
        result.linkName == issueLinkTypeDTO.linkName
    }

    def 'listIssueLinkType'() {
        when: '发请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/issue_link_types?issueLinkTypeId={issueLinkTypeId}', List, projectId, issueLinkTypeId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<IssueLinkTypeDTO> result = entity.body

        expect: '设置期望值'
        result.size() == exceptCount

        where: '给定参数'
        issueLinkTypeId | exceptCount
        null            | 4
        1L              | 3
        2L              | 3
    }

    def 'queryIssueLinkType'() {

        when: '发请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/issue_link_types/{linkTypeId}', IssueLinkTypeDTO, projectId, issueLinkTypeId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        IssueLinkTypeDTO result = entity.body

        expect: '设置期望值'
        result.outWard == outWard

        where: '给定参数'
        issueLinkTypeId | outWard
        1L              | '复制'
        2L              | '阻塞'
    }

    def 'deleteIssueLinkType'() {

        when: '发请求'
        def entity = restTemplate.exchange("/v1/projects/{project_id}/issue_link_types/{issueLinkTypeId}?toIssueLinkTypeId={toIssueLinkTypeId}",
                HttpMethod.DELETE,
                null,
                ResponseEntity.class,
                projectId,
                issueLinkTypeId,
                toIssueLinkTypeId

        )

        then: '返回值'
        entity.statusCode == excepCode

        where: '给定参数'
        issueLinkTypeId | toIssueLinkTypeId | excepCode
        4L              | null              | HttpStatus.OK
    }

    def 'checkLinkName'() {
        when: '发请求'
        def entity = restTemplate.exchange("/v1/projects/{project_id}/issue_link_types/check_name?issueLinkTypeName={issueLinkTypeName}&&issueLinkTypeId={issueLinkTypeId}",
                HttpMethod.GET,
                null,
                ResponseEntity.class,
                projectId,
                issueLinkTypeName,
                issueLinkTypeId
        )

        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        and:
        entity.body==expectObject

        where: '给定参数'
        issueLinkTypeId | toIssueLinkTypeId | excepCode
        4L              | null              | HttpStatus.OK
    }

}
