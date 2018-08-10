package io.choerodon.agile.api.controller.v1

import com.alibaba.fastjson.JSON
import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.ComponentIssueRelDTO
import io.choerodon.agile.api.dto.IssueComponentDTO
import io.choerodon.agile.api.dto.IssueCreateDTO
import io.choerodon.agile.api.dto.IssueDTO
import io.choerodon.agile.api.dto.UserMapIssueDTO
import io.choerodon.agile.api.eventhandler.AgileEventHandler
import io.choerodon.agile.app.service.IssueService
import io.choerodon.agile.domain.agile.event.ProjectEvent
import io.choerodon.agile.domain.agile.repository.UserRepository
import io.choerodon.agile.infra.dataobject.IssueDO
import io.choerodon.agile.infra.dataobject.IssueSprintRelDO
import io.choerodon.agile.infra.dataobject.ProjectInfoDO
import io.choerodon.agile.infra.dataobject.SprintDO
import io.choerodon.agile.infra.dataobject.UserMessageDO
import io.choerodon.agile.infra.mapper.IssueMapper
import io.choerodon.agile.infra.mapper.IssueSprintRelMapper
import io.choerodon.agile.infra.mapper.ProjectInfoMapper
import io.choerodon.agile.infra.mapper.SprintMapper
import io.choerodon.asgard.saga.feign.SagaClient
import io.choerodon.event.producer.execute.EventProducerTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import org.springframework.beans.factory.annotation.Qualifier

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 *
 * @author dinghuang123@gmail.com
 * @since 2018/8/1
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class IssueControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    AgileEventHandler agileEventHandler

    @Autowired
    @Qualifier("issueService")
    IssueService issueService

    @Autowired
    IssueMapper issueMapper

    @Autowired
    SagaClient sagaClient

    @Autowired
    private SprintMapper sprintMapper

    @Autowired
    private IssueSprintRelMapper issueSprintRelMapper

    @Autowired
    ProjectInfoMapper projectInfoMapper

    @Autowired
    @Qualifier("mockUserRepository")
    private UserRepository userRepository

    @Autowired
    @Qualifier("mockEventProducerTemplate")
    private EventProducerTemplate eventProducerTemplate

    @Shared
    def projectId = 1
    @Shared
    def componentId = null
    @Shared
    private List<Long> issueIdList = new ArrayList<>()

    def 'listIssuesByProjectId'() {
        given:
        def type = 'sprint'
        def pageType = 'usermap'
        Map<Long, UserMessageDO> userMessageDOMap = new HashMap<>()
        UserMessageDO userMessageDO = new UserMessageDO("admin", "admin.png", "admin@gmail.com")
        userMessageDOMap.put(1, userMessageDO)
        userRepository.queryUsersMap(*_) >> userMessageDOMap
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/issues/user_map/issues?type={type}&pageType={pageType}",
                HttpMethod.GET,
                new HttpEntity<>(),
                List.class,
                projectId,
                type,
                pageType)

        then:
        entity.statusCode.is2xxSuccessful()
        List<UserMapIssueDTO> userMapIssueDTOList = entity.body
        def count = 0
        for (UserMapIssueDTO userMapIssueDTO : userMapIssueDTOList) {
            if (userMapIssueDTO.sprintId != null) {
                count += 1
                userMapIssueDTO.issueId == 1L
            }
        }
        count == 1
    }

//    def "创建项目"() {
//        given: '给一个项目的DTO用于创建项目'
//        ProjectEvent projectEvent = new ProjectEvent()
//        projectEvent.projectId = projectId
//        projectEvent.projectCode = "AG"
//
//        and: '把项目数据转换成json字符串'
//        String data = JSON.toJSONString(projectEvent)
//
//        when: '执行方法'
//        agileEventHandler.handleProjectInitByConsumeSagaTask(data)
//
//        and: '给定条件'
//        ProjectInfoDO projectInfoDO = new ProjectInfoDO()
//        projectInfoDO.setProjectId(projectId)
//        projectInfoDO = projectInfoMapper.selectOne(projectInfoDO)
//
//        then: '验证项目执行结果'
//        println "项目执行结果：${projectInfoDO.toString()}"
//
//        expect: '验证结果'
//        projectInfoDO.projectId == 1
//        projectInfoDO.projectCode == "AG"
//
//    }
//
//    def '创建模块api测试'() {
//        given: '给一个创建模块的DTO'
//        IssueComponentDTO issueComponentDTO = new IssueComponentDTO()
//
//        and: '设置模块属性'
//        issueComponentDTO.projectId = projectId
//        issueComponentDTO.name = "测试模块"
//        issueComponentDTO.description = "测试模块描述"
//        issueComponentDTO.managerId = 1
//        issueComponentDTO.defaultAssigneeRole = "模块负责人"
//
//        when: '向开始创建issue的接口发请求'
//        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/component', issueComponentDTO, IssueComponentDTO, projectId)
//
//        then: '返回值'
//        entity.statusCode.is2xxSuccessful()
//        println "创建模块的执行结果：${entity.body?.toString()}"
//
//        and: '设置模块值'
//        if (entity.body.getComponentId()) {
//            componentId = entity.body.getComponentId()
//        }
//
//        expect: '验证结果'
//        entity.body.description == "测试模块描述"
//        entity.body.name == "测试模块"
//        entity.body.managerId == 1
//        entity.body.defaultAssigneeRole == "模块负责人"
//
//    }
//
//    def '创建issueAPI测试'() {
//        given: '给一个创建issue的DTO'
//        IssueCreateDTO issueCreateDTO = new IssueCreateDTO()
//
//        and: '设置issue属性'
//        issueCreateDTO.typeCode = typeCode
//        issueCreateDTO.projectId = projectId
//        issueCreateDTO.description = "测试issue描述"
//        issueCreateDTO.summary = "测试issue概要"
//        issueCreateDTO.priorityCode = "hight"
//        issueCreateDTO.assigneeId = 1
//
//        and: '设置模块'
//        List<ComponentIssueRelDTO> componentIssueRelDTOList = new ArrayList<>()
//        ComponentIssueRelDTO componentIssueRelDTO = new ComponentIssueRelDTO()
//        componentIssueRelDTO.projectId = projectId
//        componentIssueRelDTO.name = "测试模块"
//        componentIssueRelDTOList.add(componentIssueRelDTO)
//        ComponentIssueRelDTO componentExit = new ComponentIssueRelDTO()
//        componentExit.projectId = projectId
//        componentExit.componentId = componentId
//        componentIssueRelDTOList.add(componentExit)
//        ComponentIssueRelDTO componentCreate = new ComponentIssueRelDTO()
//        componentCreate.projectId = projectId
//        componentCreate.name = "测试模块2"
//        componentIssueRelDTOList.add(componentCreate)
//        issueCreateDTO.componentIssueRelDTOList = componentIssueRelDTOList
//
//        and: '设置feign调用mockito'
//        // *_表示任何长度的参数（这里表示只要执行了queryUsersMap这个方法，就让它返回一个空的Map
//        Map<Long, UserMessageDO> userMessageDOMap = new HashMap<>()
//        UserMessageDO userMessageDO = new UserMessageDO("管理员", "http://XXX.png", "dinghuang123@gmail.com")
//        userMessageDOMap.put(1, userMessageDO)
//        userRepository.queryUsersMap(*_) >> userMessageDOMap
//
//        when: '向开始创建issue的接口发请求'
//        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issues', issueCreateDTO, IssueDTO, projectId)
//
//        then: '返回值'
//        entity.statusCode.is2xxSuccessful()
//        println "创建issue执行结果：${entity.body?.toString()}"
//        issueIdList.add(entity.body.issueId)
//
//        expect: '设置期望值'
//        entity.body.typeCode == expectedTypeCode
//
//        where: '不同issue类型返回值与期望值对比'
//        typeCode     | expectedTypeCode
//        "story"      | "story"
//        "task"       | "task"
//        "issue_epic" | "issue_epic"
//    }

//    def "删除issue"() {
//        given: 'mockSagaClient'
//        sagaClient.startSaga(_,_) >> null
//
//        when: '执行方法'
//        restTemplate.delete('/v1/projects/{project_id}/issues/{issueId}', projectId, issueId)
//
//        then: '返回值'
//        issueMapper.selectByPrimaryKey(issueId) == result
//
//        where: '不同issue类型返回值与期望值对比'
//        issueId            | result
//        issueIdList.get(0) | null
//        issueIdList.get(1) | null
//        issueIdList.get(2) | null
//
//    }
//
//    def "删除项目"() {
//        given: '给一个删除项目的DO用于删除项目'
//        ProjectInfoDO projectInfoDO = new ProjectInfoDO()
//
//        and: '设置项目id'
//        projectInfoDO.projectId = projectId
//
//        when: '执行方法'
//        projectInfoMapper.delete(projectInfoDO)
//
//        then: '验证删除'
//        if (!projectInfoMapper.selectOne(projectInfoDO)) {
//            println "项目删除成功"
//        }
//
//    }

}
