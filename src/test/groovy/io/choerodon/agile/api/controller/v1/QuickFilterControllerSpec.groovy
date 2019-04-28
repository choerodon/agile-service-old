package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.QuickFilterDTO
import io.choerodon.agile.api.dto.QuickFilterFieldDTO
import io.choerodon.agile.api.dto.QuickFilterSearchDTO
import io.choerodon.agile.api.dto.QuickFilterSequenceDTO
import io.choerodon.agile.api.dto.QuickFilterValueDTO
import io.choerodon.agile.infra.mapper.QuickFilterFieldMapper
import io.choerodon.agile.infra.mapper.QuickFilterMapper
import io.choerodon.core.convertor.ConvertHelper
import io.choerodon.core.domain.Page
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
        quickFilterFieldMapper.selectAll()
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
        QuickFilterValueDTO quickFilterValueDTO2 = new QuickFilterValueDTO()
        quickFilterValueDTO2.fieldCode = 'component'
        quickFilterValueDTO2.operation = 'component'
        quickFilterValueDTO2.value = 'component'
        QuickFilterValueDTO quickFilterValueDTO3 = new QuickFilterValueDTO()
        quickFilterValueDTO3.fieldCode = 'fix_version'
        quickFilterValueDTO3.operation = 'fix_version'
        quickFilterValueDTO3.value = 'fix_version'
        QuickFilterValueDTO quickFilterValueDTO4 = new QuickFilterValueDTO()
        quickFilterValueDTO4.fieldCode = 'label'
        quickFilterValueDTO4.operation = 'label'
        quickFilterValueDTO4.value = 'label'
        QuickFilterValueDTO quickFilterValueDTO5 = new QuickFilterValueDTO()
        quickFilterValueDTO5.fieldCode = 'sprint'
        quickFilterValueDTO5.operation = 'sprint'
        quickFilterValueDTO5.value = 'sprint'
        QuickFilterValueDTO quickFilterValueDTO6 = new QuickFilterValueDTO()
        quickFilterValueDTO6.fieldCode = 'creation_date'
        quickFilterValueDTO6.operation = 'creation_date'
        quickFilterValueDTO6.value = System.currentTimeMillis()
        QuickFilterValueDTO quickFilterValueDTO7 = new QuickFilterValueDTO()
        quickFilterValueDTO7.fieldCode = 'last_update_date'
        quickFilterValueDTO7.operation = 'last_update_date'
        quickFilterValueDTO7.value = System.currentTimeMillis()
        QuickFilterValueDTO quickFilterValueDTO8 = new QuickFilterValueDTO()
        quickFilterValueDTO8.fieldCode = 'influence_version'
        quickFilterValueDTO8.operation = 'influence_version'
        quickFilterValueDTO8.value = 'influence_version'
        list.add(quickFilterValueDTO)
        list.add(quickFilterValueDTO1)
        list.add(quickFilterValueDTO2)
        list.add(quickFilterValueDTO3)
        list.add(quickFilterValueDTO4)
        list.add(quickFilterValueDTO5)
        list.add(quickFilterValueDTO6)
        list.add(quickFilterValueDTO7)
        list.add(quickFilterValueDTO8)
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

    def 'checkName'() {
        given:
        String quickFilterNameTrue = '这是一个name'
        String quickFilterNameFalse = '这是一个name1'

        when:
        def entityTrue = restTemplate.exchange("/v1/projects/{project_id}/quick_filter/check_name?quickFilterName={quickFilterName}",
                HttpMethod.GET,
                new HttpEntity<>(),
                Boolean.class,
                projectId,
                quickFilterNameTrue)

        def entityFalse = restTemplate.exchange("/v1/projects/{project_id}/quick_filter/check_name?quickFilterName={quickFilterName}",
                HttpMethod.GET,
                new HttpEntity<>(),
                Boolean.class,
                projectId,
                quickFilterNameFalse)

        then:
        entityTrue.statusCode.is2xxSuccessful()
        entityTrue.body == true
        entityFalse.statusCode.is2xxSuccessful()
        entityFalse.body == false
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

        given: '准备数据'
        QuickFilterSearchDTO quickFilterSearchDTO = new QuickFilterSearchDTO()

        when: '发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/quick_filter/query_all', quickFilterSearchDTO, List, projectId)


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

        QuickFilterDTO quickFilterDTO1 = new QuickFilterDTO()
        quickFilterDTO1.projectId = projectId
        quickFilterDTO1.description = '这是一个新增描述1111'
        quickFilterDTO1.name = '这是一个新增name1111'
        quickFilterDTO1.childIncluded = true
        quickFilterDTO1.expressQuery = '类型 = 故事 AND 模块 in [模块1]新增'
        List<QuickFilterValueDTO> list1 = new ArrayList<>()
        QuickFilterValueDTO quickFilterValueDTO2 = new QuickFilterValueDTO()
        quickFilterValueDTO2.fieldCode = 'priority'
        quickFilterValueDTO2.operation = 'priority'
        quickFilterValueDTO2.value = 'priority'
        list1.add(quickFilterValueDTO2)
        quickFilterDTO1.quickFilterValueDTOList = list1
        List<String> stringList1 = new ArrayList<>()
        stringList.add("test2")
        stringList.add("test3")
        quickFilterDTO1.relationOperations = stringList1
        restTemplate.postForEntity('/v1/projects/{project_id}/quick_filter', quickFilterDTO1, QuickFilterDTO, projectId)

        QuickFilterSequenceDTO quickFilterSequenceDTO = new QuickFilterSequenceDTO()
        quickFilterSequenceDTO.filterId = 1L
        quickFilterSequenceDTO.afterSequence = quickFilterMapper.selectAll().get(2).sequence
        HttpEntity<QuickFilterSequenceDTO> requestEntity1 = new HttpEntity<QuickFilterSequenceDTO>(quickFilterSequenceDTO, null)

        QuickFilterSequenceDTO quickFilterSequenceDTO1 = new QuickFilterSequenceDTO()
        quickFilterSequenceDTO1.filterId = 2L
        quickFilterSequenceDTO1.beforeSequence = quickFilterMapper.selectAll().get(0).sequence
        HttpEntity<QuickFilterSequenceDTO> requestEntity2 = new HttpEntity<QuickFilterSequenceDTO>(quickFilterSequenceDTO1, null)

        QuickFilterSequenceDTO quickFilterSequenceDTO2 = new QuickFilterSequenceDTO()
        quickFilterSequenceDTO2.filterId = 3L
        quickFilterSequenceDTO2.afterSequence = quickFilterMapper.selectAll().get(1).sequence
        HttpEntity<QuickFilterSequenceDTO> requestEntity3 = new HttpEntity<QuickFilterSequenceDTO>(quickFilterSequenceDTO2, null)

        when: '发请求'
        def entity1 = restTemplate.exchange('/v1/projects/{project_id}/quick_filter/drag',
                HttpMethod.PUT,
                requestEntity1,
                QuickFilterDTO.class,
                projectId)
        def entity2 = restTemplate.exchange('/v1/projects/{project_id}/quick_filter/drag',
                HttpMethod.PUT,
                requestEntity2,
                QuickFilterDTO.class,
                projectId)
        def entity3 = restTemplate.exchange('/v1/projects/{project_id}/quick_filter/drag',
                HttpMethod.PUT,
                requestEntity3,
                QuickFilterDTO.class,
                projectId)
        then: '返回值'
        entity1.statusCode.is2xxSuccessful()
        entity2.statusCode.is2xxSuccessful()
        entity3.statusCode.is2xxSuccessful()

        and: '设置值'
        QuickFilterDTO result1 = entity1.body

        expect: '设置期望值'
        result1.description == "这是一个更新描述"
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
        3L       | null
        4L       | Exception
    }

}
