package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.QuickFilterDTO
import io.choerodon.agile.infra.mapper.QuickFilterMapper
import io.choerodon.core.convertor.ConvertHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Creator: changpingshi0213@gmail.com
 * Date:  16:19 2018/8/31
 * Description:
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class QuickFilterControllerSpec extends Specification {
    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    QuickFilterMapper quickFilterMapper

    @Shared
    def projectId = 1L

    @Shared
    def filterId = 1L

    def 'create'() {
        given: '准备QuickFilterDTO'
        QuickFilterDTO quickFilterDTO = new QuickFilterDTO()
        quickFilterDTO.projectId = projectId
        quickFilterDTO.description = '这是一个描述'
        quickFilterDTO.name = '这是一个name'
        quickFilterDTO.childIncluded = true
        quickFilterDTO.expressQuery = '类型 = 故事 AND 模块 in [模块1]'

        when: '发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/quick_filter', quickFilterDTO, QuickFilterDTO, projectId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        QuickFilterDTO result = entity.body

        expect: '设置期望值'
        result.projectId == quickFilterDTO.projectId
        result.description == quickFilterDTO.description
        result.name == quickFilterDTO.name
        result.childIncluded == quickFilterDTO.childIncluded
        result.expressQuery == quickFilterDTO.expressQuery
    }

    def 'update'() {
        given: '准备QuickFilterDTO'
        QuickFilterDTO quickFilterDTO = ConvertHelper.convert(quickFilterMapper.selectByPrimaryKey(filterId), QuickFilterDTO.class)
        quickFilterDTO.description = "这是一个更新描述"
        HttpEntity<QuickFilterDTO> requestEntity = new HttpEntity<QuickFilterDTO>(quickFilterDTO, null)

        when: '发请求'
        def entity = restTemplate.exchange('/v1/projects/{project_id}/quick_filter/{filterId}',
                HttpMethod.PUT,
                requestEntity,
                QuickFilterDTO,
                projectId,
                filterId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        QuickFilterDTO result = entity.body

        expect: '设置期望值'
        result.projectId == quickFilterDTO.projectId
        result.description == "这是一个更新描述"
        result.name == quickFilterDTO.name
        result.childIncluded == quickFilterDTO.childIncluded
        result.expressQuery == quickFilterDTO.expressQuery
    }

    def 'queryById'() {
        when: '发请求'
        def entity = restTemplate.exchange('/v1/projects/{project_id}/quick_filter/{filterId}',
                HttpMethod.GET,
                null,
                QuickFilterDTO,
                projectId,
                filterId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        QuickFilterDTO result = entity.body

        expect: '设置期望值'
        result.projectId == projectId
        result.description == "这是一个更新描述"
        result.name == "这是一个name"
        result.expressQuery == "类型 = 故事 AND 模块 in [模块1]"
    }

    def 'listByProjectId'() {
        when: '发请求'
        def entity = restTemplate.exchange('/v1/projects/{project_id}/quick_filter',
                HttpMethod.GET,
                null,
                List,
                projectId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<QuickFilterDTO> result = entity.body

        expect: '设置期望值'
        result.size()==1
        result.get(0).projectId == projectId
        result.get(0).description == "这是一个更新描述"
        result.get(0).name == "这是一个name"
        result.get(0).expressQuery == "类型 = 故事 AND 模块 in [模块1]"
    }

    def 'list'() {
        when: '发请求'
        def entity = restTemplate.exchange('/v1/projects/{project_id}/quick_filter/fields',
                HttpMethod.GET,
                null,
                List,
                projectId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<QuickFilterDTO> result = entity.body

        expect: '设置期望值'
        result.size()==1
        result.get(0).projectId == projectId
        result.get(0).description == "这是一个更新描述"
        result.get(0).name == "这是一个name"
        result.get(0).expressQuery == "类型 = 故事 AND 模块 in [模块1]"
    }

    def 'deleteById'() {
        when: '发送请求'
        try {
            restTemplate.exchange("/v1/projects/{project_id}/quick_filter/{filterId}",
                    HttpMethod.DELETE,
                    null,
                    QuickFilterDTO.class,
                    projectId,
                    filterId)
        } catch (Exception e) {
            expectObject = e
        }

        then: '返回值'
        if (expectObject != null) {
            quickFilterMapper.selectByPrimaryKey(filterId) == expectObject
        }

        where: '期望值'
        issueAttachmentId | expectObject
        1L                | null
        2L                | Exception
    }

}
