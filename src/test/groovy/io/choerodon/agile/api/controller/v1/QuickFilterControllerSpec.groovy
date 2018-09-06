package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.QuickFilterDTO
import io.choerodon.agile.api.dto.QuickFilterFieldDTO
import io.choerodon.agile.api.dto.QuickFilterSequenceDTO
import io.choerodon.agile.api.dto.QuickFilterValueDTO
import io.choerodon.agile.infra.mapper.QuickFilterFieldMapper
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

    @Autowired
    QuickFilterFieldMapper quickFilterFieldMapper
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
        List<QuickFilterValueDTO> list = new ArrayList<>()
        QuickFilterValueDTO quickFilterValueDTO = new QuickFilterValueDTO()
        quickFilterValueDTO.fieldCode = 'priority'
        quickFilterValueDTO.operation = 'priority'
        quickFilterValueDTO.value = 'priority'
        QuickFilterValueDTO quickFilterValueDTO1 = new QuickFilterValueDTO()
        quickFilterValueDTO1.fieldCode = 'assignee'
        quickFilterValueDTO1.operation = 'assignee'
        quickFilterValueDTO1.value = 'assignee'
        list.add(quickFilterValueDTO)
        list.add(quickFilterValueDTO1)
        quickFilterDTO.quickFilterValueDTOList = list
        List<String> stringList = new ArrayList<>()
        stringList.add("test")
        stringList.add("test1")
        quickFilterDTO.relationOperations = stringList

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
        List<QuickFilterValueDTO> list = new ArrayList<>()
        QuickFilterValueDTO quickFilterValueDTO = new QuickFilterValueDTO()
        quickFilterValueDTO.fieldCode = 'priority'
        quickFilterValueDTO.operation = 'priority'
        quickFilterValueDTO.value = 'priority'
        QuickFilterValueDTO quickFilterValueDTO1 = new QuickFilterValueDTO()
        quickFilterValueDTO1.fieldCode = 'assignee'
        quickFilterValueDTO1.operation = 'assignee'
        quickFilterValueDTO1.value = 'assignee'
        list.add(quickFilterValueDTO)
        list.add(quickFilterValueDTO1)
        quickFilterDTO.quickFilterValueDTOList = list
        List<String> stringList = new ArrayList<>()
        stringList.add("test")
        stringList.add("test1")
        quickFilterDTO.relationOperations = stringList
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
        result.size() == 1
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
        List<QuickFilterFieldDTO> result = entity.body

        expect: '设置期望值'
        result.size() == 17
        result.get(0).fieldCode == "assignee"
        result.get(0).type == "long"
        result.get(0).name == "经办人"
    }

    def 'dragFilter'() {
        given: '准备数据'
        QuickFilterDTO quickFilterDTO = new QuickFilterDTO()
        quickFilterDTO.projectId = projectId
        quickFilterDTO.description = '这是一个新增描述'
        quickFilterDTO.name = '这是一个新增name'
        quickFilterDTO.childIncluded = true
        quickFilterDTO.expressQuery = '类型 = 故事 AND 模块 in [模块1]新增'
        List<QuickFilterValueDTO> list = new ArrayList<>()
        QuickFilterValueDTO quickFilterValueDTO = new QuickFilterValueDTO()
        quickFilterValueDTO.fieldCode = 'priority'
        quickFilterValueDTO.operation = 'priority'
        quickFilterValueDTO.value = 'priority'
        QuickFilterValueDTO quickFilterValueDTO1 = new QuickFilterValueDTO()
        quickFilterValueDTO1.fieldCode = 'assignee'
        quickFilterValueDTO1.operation = 'assignee'
        quickFilterValueDTO1.value = 'assignee'
        list.add(quickFilterValueDTO)
        list.add(quickFilterValueDTO1)
        quickFilterDTO.quickFilterValueDTOList = list
        List<String> stringList = new ArrayList<>()
        stringList.add("test")
        stringList.add("test1")
        quickFilterDTO.relationOperations = stringList
        restTemplate.postForEntity('/v1/projects/{project_id}/quick_filter', quickFilterDTO, QuickFilterDTO, projectId)
        quickFilterMapper.selectAll()

        QuickFilterSequenceDTO quickFilterSequenceDTO = new QuickFilterSequenceDTO()
        quickFilterSequenceDTO.filterId = 1L
        quickFilterSequenceDTO.beforeSequence = quickFilterMapper.selectAll().get(0).sequence
        HttpEntity<QuickFilterSequenceDTO> requestEntity = new HttpEntity<QuickFilterSequenceDTO>(quickFilterSequenceDTO, null)


        when: '发请求'
        def entity = restTemplate.exchange('/v1/projects/{project_id}/quick_filter/drag',
                HttpMethod.PUT,
                requestEntity,
                QuickFilterDTO.class,
                projectId)

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
        filterId | expectObject
        1L       | null
        2L       | null
        3L       | Exception
    }

}
