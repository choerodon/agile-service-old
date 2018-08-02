package io.choerodon.agile.api.controller.v1

import com.alibaba.fastjson.JSON
import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.IssueCreateDTO
import io.choerodon.agile.api.dto.IssueDTO
import io.choerodon.agile.api.eventhandler.AgileEventHandler
import io.choerodon.agile.app.service.IssueService
import io.choerodon.agile.domain.agile.event.ProjectEvent
import io.choerodon.agile.domain.agile.repository.UserRepository
import io.choerodon.agile.infra.dataobject.IssueDO
import io.choerodon.agile.infra.dataobject.ProjectInfoDO
import io.choerodon.agile.infra.mapper.IssueMapper
import io.choerodon.agile.infra.mapper.ProjectInfoMapper
import io.choerodon.event.producer.execute.EventProducerTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
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
@Stepwise
class IssueControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    AgileEventHandler agileEventHandler

    @Autowired
    IssueService issueService

    @Autowired
    IssueMapper issueMapper

    @Autowired
    ProjectInfoMapper projectInfoMapper

    @Autowired
    @Qualifier("mockUserRepository")
    private UserRepository userRepository

    @Autowired
    @Qualifier("mockEventProducerTemplate")
    private EventProducerTemplate eventProducerTemplate

    static final projectId = 1

    /**
     *  setupSpec：不能访问Spring上下文，每个测试类启动前执行一次
     *  setup：可以访问Spring上下文，每个方法前执行一次
     * @return
     */
    def setup() {
        given: '给一个项目的DTO用于创建项目'
        ProjectEvent projectEvent = new ProjectEvent()
        projectEvent.projectId = projectId
        projectEvent.projectCode = "AG"

        and: '把项目数据转换成json字符串'
        String data = JSON.toJSONString(projectEvent)

        when: '执行方法'
        agileEventHandler.handleProjectInitByConsumeSagaTask(data)

        then: '验证项目执行结果'
        ProjectInfoDO projectInfoDO = new ProjectInfoDO()
        projectInfoDO.projectId = projectId
        println "项目执行结果：${projectInfoMapper.selectOne(projectInfoDO)?.toString()}"

    }

    def cleanup() {
        given: '给一个删除项目的DO用于删除项目，给一个删除issue的DO用于删除issue'
        ProjectInfoDO projectInfoDO = new ProjectInfoDO()
        IssueDO issueDO = new IssueDO()

        and: '设置项目id'
        projectInfoDO.projectId = projectId
        issueDO.projectId = projectId

        when: '执行方法'
        projectInfoMapper.delete(projectInfoDO)
        issueMapper.delete(issueDO)

        then: '验证删除'

        if (!projectInfoMapper.selectOne(projectInfoDO)) {
            println "项目删除成功"
        }

        if (!issueMapper.select(issueDO)) {
            println "issue删除成功"
        }

    }

    def '创建issueAPI测试'() {
        given: '给一个创建issue的DTO'
        IssueCreateDTO issueCreateDTO = new IssueCreateDTO()

        and: '设置issue属性'
        issueCreateDTO.typeCode = typeCode
        issueCreateDTO.projectId = projectId
        issueCreateDTO.description = "测试issue描述"
        issueCreateDTO.summary = "测试issue概要"
        issueCreateDTO.priorityCode = "hight"

        and: '设置feign调用mockito'
        // *_表示任何长度的参数（这里表示只要执行了queryUsersMap这个方法，就让它返回一个空的Map）
        userRepository.queryUsersMap(*_) >> new HashMap<>()

        when: '向开始创建issue的接口发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issues', issueCreateDTO, IssueDTO, projectId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        // 执行成功一次这个方法并且返回了期望的值（底下设置了不同期望，会执行3次这个，就不能用来判断了）
//        1 * userRepository.queryUsersMap(*_) >> new HashMap<>()
        println "创建issue执行结果：${entity.body?.toString()}"

        expect: '设置期望值'
        entity.body.typeCode == expectedTypeCode

        where: '不同issue类型返回值与期望值对比'
        typeCode     | expectedTypeCode
        "story"      | "story"
        "task"       | "task"
        "issue_epic" | "issue_epic"
    }

}
