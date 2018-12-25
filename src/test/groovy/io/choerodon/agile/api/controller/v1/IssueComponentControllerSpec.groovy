package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.ComponentForListDTO
import io.choerodon.agile.api.dto.IssueComponentDTO
import io.choerodon.agile.api.dto.IssueDTO
import io.choerodon.agile.domain.agile.repository.UserRepository
import io.choerodon.agile.infra.dataobject.IssueComponentDO
import io.choerodon.agile.infra.dataobject.UserDO
import io.choerodon.agile.infra.dataobject.UserMessageDO
import io.choerodon.agile.infra.mapper.IssueComponentMapper
import io.choerodon.core.domain.Page
import org.mockito.Matchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Creator: ChangpingShi0213@gmail.com
 * Date:  13:50 2018/8/24
 * Description:
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class IssueComponentControllerSpec extends Specification {
    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    IssueComponentMapper issueComponentMapper

    @Autowired
    @Qualifier("userRepository")
    private UserRepository userRepository

    @Shared
    def projectId = 1L

    @Shared
    def componentId = 1L

    def setup() {
        given: '设置feign调用mockito'
        // *_表示任何长度的参数（这里表示只要执行了queryUsersMap这个方法，就让它返回一个空的Map
        Map<Long, UserMessageDO> userMessageDOMap = new HashMap<>()
        UserMessageDO userMessageDO = new UserMessageDO("管理员", "http://XXX.png", "dinghuang123@gmail.com")
        userMessageDOMap.put(1, userMessageDO)
        Mockito.when(userRepository.queryUsersMap(Matchers.any(List.class), Matchers.anyBoolean())).thenReturn(userMessageDOMap)
        UserDO userDO = new UserDO()
        userDO.setRealName("管理员")
        Mockito.when(userRepository.queryUserNameByOption(Matchers.anyLong(), Matchers.anyBoolean())).thenReturn(userDO)

    }

    def 'createComponent'() {
        given: '准备IssueCommentCreateDTO'
        IssueComponentDTO issueComponentDTO = new IssueComponentDTO()
        issueComponentDTO.projectId = 1L
        issueComponentDTO.name = 'test_component'
        issueComponentDTO.description = 'this is a description'
        issueComponentDTO.managerId = 1L
        issueComponentDTO.defaultAssigneeRole = 'this is a defaultAssigneeRole'

        when: '发送创建component请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/component', issueComponentDTO, IssueComponentDTO, projectId)

        then: '请求结果'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        IssueComponentDTO resultIssueComponentDTO = entity.body

        expect: '设置期望值'
        resultIssueComponentDTO.projectId == issueComponentDTO.projectId
        resultIssueComponentDTO.name == issueComponentDTO.name
        resultIssueComponentDTO.description == issueComponentDTO.description
        resultIssueComponentDTO.managerId == issueComponentDTO.managerId
        resultIssueComponentDTO.defaultAssigneeRole == issueComponentDTO.defaultAssigneeRole
    }

    def 'checkComponentName'() {
        given:
        String componentNameTrue = 'test_component'
        String componentNameFalse = 'test_component1'

        when:
        def entityTrue = restTemplate.exchange("/v1/projects/{project_id}/component/check_name?componentName={componentName}",
                HttpMethod.GET,
                new HttpEntity<>(),
                Boolean.class,
                projectId,
                componentNameTrue)

        def entityFalse = restTemplate.exchange("/v1/projects/{project_id}/component/check_name?componentName={componentName}",
                HttpMethod.GET,
                new HttpEntity<>(),
                Boolean.class,
                projectId,
                componentNameFalse)

        then:
        entityTrue.statusCode.is2xxSuccessful()
        entityTrue.body == true
        entityFalse.statusCode.is2xxSuccessful()
        entityFalse.body == false
    }

    def 'updateComponent'() {
        given: '修改component'
        List<IssueComponentDO> list = issueComponentMapper.selectAll()
        IssueComponentDO issueComponentDO = list.get(1)
        componentId = issueComponentDO.componentId
        IssueComponentDTO issueComponentDTO = new IssueComponentDTO()
        issueComponentDTO.projectId = issueComponentDO.projectId
        issueComponentDTO.name = issueComponentDO.name
        issueComponentDTO.managerId = issueComponentDO.managerId
        issueComponentDTO.objectVersionNumber = issueComponentDO.objectVersionNumber
        issueComponentDTO.description = "this is a description for update"
        issueComponentDTO.defaultAssigneeRole = "this is a defaultAssigneeRole for update"

        when: '发送更新component请求'
        HttpEntity<IssueComponentDTO> requestEntity = new HttpEntity<IssueComponentDTO>(issueComponentDTO, null)
        def entity = restTemplate.exchange("/v1/projects/{project_id}/component/{id}",
                HttpMethod.PUT,
                requestEntity,
                IssueComponentDTO,
                projectId,
                componentId
        )

        then: '请求结果'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        IssueComponentDTO resultIssueComponentDTO = entity.body

        expect: '设置期望值'
        resultIssueComponentDTO.projectId == issueComponentDO.projectId
        resultIssueComponentDTO.name == issueComponentDO.name
        resultIssueComponentDTO.description == "this is a description for update"
        resultIssueComponentDTO.managerId == issueComponentDO.managerId
        resultIssueComponentDTO.defaultAssigneeRole == "this is a defaultAssigneeRole for update"
    }


    def 'queryComponentById'() {
        when: '发送查询component请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/component/{id}', IssueComponentDTO, projectId, componentId)

        then: '请求结果'
        entity.statusCode.is2xxSuccessful()


        and: '设置值'
        IssueComponentDTO result = entity.body

        expect: "设置期望值"
        result.componentId == componentId
        result.projectId == projectId
    }


    def 'listByProjectId'() {
        given:
        Map<String, Object> searchParamMap = new HashMap<>()
        searchParamMap.put("searchArgs", new HashMap<>())
        searchParamMap.put("advancedSearchArgs", new HashMap<>())
        searchParamMap.put("content", "")

        when: '根据project id查询component'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/component/query_all', searchParamMap, Page, projectIds)

        then: '请求结果'
        entity.statusCode.is2xxSuccessful()
        List<ComponentForListDTO> result = entity.body

        and: '设置值'
        result.size() == expectSize

        where: '期望值'
        projectIds | noIssueTest | expectSize
        1L         | true        | 2
        2L         | true        | 0
        10L        | true        | 0
    }

    def 'listByProjectIdForTest'() {
        when: '根据project id查询component'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/component', List, projectIds, componentId, noIssueTest)

        then: '请求结果'
        entity.statusCode.is2xxSuccessful()
        List<ComponentForListDTO> result = entity.body

        and: '设置值'
        result.size() == expectSize

        where: '期望值'
        projectIds | noIssueTest | expectSize
        1L         | true        | 2
        2L         | true        | 0
        10L        | true        | 0
    }

    def 'listByOptions'() {
        when: '根据id查询component下的issues'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/component/{id}/issues', List, projectId, componentIds)

        then: '请求结果'
        entity.statusCode.is2xxSuccessful()
        List<IssueDTO> result = entity.body

        and: '设置值'
        result.size() == expectSize

        where: '期望值'
        componentIds | expectSize
        1L           | 0
        2L           | 0
        10L          | 0
    }

    def 'deleteComponent'() {
        given:
        def expectObject = null
        when: '删除component'
        try {
            restTemplate.exchange("/v1/projects/{project_id}/component/{id}?relateComponentId={relateComponentId}",
                    HttpMethod.DELETE,
                    null,
                    ResponseEntity.class,
                    projectId,
                    componentId,
                    relateComponentId
            )
        }
        catch (Exception e) {
            expectObject = Exception
        }

        then: '返回值'
        expectObject == relExpectObject

        where: '期望值'
        relateComponentId || relExpectObject
        1L                || null
        null              || Exception
    }

}
