package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.SprintDetailDTO
import io.choerodon.agile.api.dto.SprintUpdateDTO
import io.choerodon.agile.api.eventhandler.AgileEventHandler
import io.choerodon.agile.app.service.IssueService
import io.choerodon.agile.domain.agile.repository.UserRepository
import io.choerodon.agile.infra.common.utils.MybatisFunctionTestUtil
import io.choerodon.agile.infra.dataobject.UserDO
import io.choerodon.agile.infra.dataobject.UserMessageDO
import io.choerodon.agile.infra.mapper.DataLogMapper
import io.choerodon.agile.infra.mapper.IssueMapper
import io.choerodon.agile.infra.mapper.IssueSprintRelMapper
import io.choerodon.agile.infra.mapper.ProjectInfoMapper
import io.choerodon.agile.infra.mapper.SprintMapper
import io.choerodon.asgard.saga.feign.SagaClient
import io.choerodon.event.producer.execute.EventProducerTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
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
 *
 * @author dinghuang123@gmail.com
 * @since 2018/8/14
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class SprintControllerSpec extends Specification {
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
    private ProjectInfoMapper projectInfoMapper

    @Autowired
    private DataLogMapper dataLogMapper

    @Autowired
    @Qualifier("mockUserRepository")
    private UserRepository userRepository

    @Autowired
    @Qualifier("mockEventProducerTemplate")
    private EventProducerTemplate eventProducerTemplate

    @Shared
    def projectId = 1

    def setup() {
        given: '设置feign调用mockito'
        // *_表示任何长度的参数（这里表示只要执行了queryUsersMap这个方法，就让它返回一个空的Map
        Map<Long, UserMessageDO> userMessageDOMap = new HashMap<>()
        UserMessageDO userMessageDO = new UserMessageDO("管理员", "http://XXX.png", "dinghuang123@gmail.com")
        userMessageDOMap.put(1, userMessageDO)
        userRepository.queryUsersMap(*_) >> userMessageDOMap
        UserDO userDO = new UserDO()
        userDO.setRealName("管理员")
        userRepository.queryUserNameByOption(*_) >> userDO

        and: 'mockSagaClient'
        sagaClient.startSaga(_, _) >> null

    }

    def '创建冲刺'() {
        when: '向创建冲刺的接口发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/sprint', null, SprintDetailDTO, projectId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        SprintDetailDTO sprintDetailDTO = entity.body
        expect: '设置期望值'
        sprintDetailDTO.sprintId == 2
        sprintDetailDTO.projectId == projectId
        sprintDetailDTO.statusCode == 'sprint_planning'
    }

    def "更新冲刺部分字段"() {
        given: '冲刺DTO对象'
        def startDate = new Date()
        def endDate = MybatisFunctionTestUtil.dataSubFunction(startDate, 10)

        SprintUpdateDTO sprintUpdateDTO = new SprintUpdateDTO()
        sprintUpdateDTO.projectId = projectId
        sprintUpdateDTO.sprintId = 2
        sprintUpdateDTO.objectVersionNumber = 1
        sprintUpdateDTO.endDate = endDate
        sprintUpdateDTO.sprintName = '测试冲刺1'
        sprintUpdateDTO.startDate = startDate

        when: '分页过滤查询issue列表提供给测试模块用'
        HttpEntity<SprintUpdateDTO> requestEntity = new HttpEntity<SprintUpdateDTO>(sprintUpdateDTO, null)
        def entity = restTemplate.exchange('/v1/projects/{project_id}/sprint', HttpMethod.PUT, requestEntity, SprintDetailDTO, projectId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        SprintDetailDTO sprintDetailDTO = entity.getBody()

        expect: '设置期望值'
        sprintDetailDTO.sprintId == 2
        sprintDetailDTO.sprintName == '测试冲刺1'
        sprintDetailDTO.projectId == projectId
//        sprintDetailDTO.startDate == startDate
//        sprintDetailDTO.endDate == endDate
    }

//    def "联合查询sprint及其issue"() {
//        given: '冲刺DTO对象'
//        def startDate = new Date()
//        def endDate = MybatisFunctionTestUtil.dataSubFunction(startDate, 10)
//
//        SprintUpdateDTO sprintUpdateDTO = new SprintUpdateDTO()
//        sprintUpdateDTO.projectId = projectId
//        sprintUpdateDTO.sprintId = 2
//        sprintUpdateDTO.objectVersionNumber = 1
//        sprintUpdateDTO.endDate = endDate
//        sprintUpdateDTO.sprintName = '测试冲刺1'
//        sprintUpdateDTO.startDate = startDate
//
//        when: '分页过滤查询issue列表提供给测试模块用'
//        HttpEntity<SprintUpdateDTO> requestEntity = new HttpEntity<SprintUpdateDTO>(sprintUpdateDTO, null)
//        def entity = restTemplate.exchange('/v1/projects/{project_id}/sprint/issues', HttpMethod.PUT, requestEntity, SprintDetailDTO, projectId)
//
//        then: '返回值'
//        entity.statusCode.is2xxSuccessful()
//
//        and: '设置值'
//        SprintDetailDTO sprintDetailDTO = entity.getBody()
//
//        expect: '设置期望值'
//        sprintDetailDTO.sprintId == 2
//        sprintDetailDTO.sprintName == '测试冲刺1'
//        sprintDetailDTO.projectId == projectId
//        sprintDetailDTO.startDate == startDate
//        sprintDetailDTO.endDate == endDate
//    }
//
//    def "根据id删除冲刺"() {
//        given: '冲刺DTO对象'
//        def startDate = new Date()
//        def endDate = MybatisFunctionTestUtil.dataSubFunction(startDate, 10)
//
//        SprintUpdateDTO sprintUpdateDTO = new SprintUpdateDTO()
//        sprintUpdateDTO.projectId = projectId
//        sprintUpdateDTO.sprintId = 2
//        sprintUpdateDTO.objectVersionNumber = 1
//        sprintUpdateDTO.endDate = endDate
//        sprintUpdateDTO.sprintName = '测试冲刺1'
//        sprintUpdateDTO.startDate = startDate
//
//        when: '分页过滤查询issue列表提供给测试模块用'
//        HttpEntity<SprintUpdateDTO> requestEntity = new HttpEntity<SprintUpdateDTO>(sprintUpdateDTO, null)
//        def entity = restTemplate.exchange('/v1/projects/{project_id}/sprint', HttpMethod.PUT, requestEntity, SprintDetailDTO, projectId)
//
//        then: '返回值'
//        entity.statusCode.is2xxSuccessful()
//
//        and: '设置值'
//        SprintDetailDTO sprintDetailDTO = entity.getBody()
//
//        expect: '设置期望值'
//        sprintDetailDTO.sprintId == 2
//        sprintDetailDTO.sprintName == '测试冲刺1'
//        sprintDetailDTO.projectId == projectId
//        sprintDetailDTO.startDate == startDate
//        sprintDetailDTO.endDate == endDate
//    }

}
