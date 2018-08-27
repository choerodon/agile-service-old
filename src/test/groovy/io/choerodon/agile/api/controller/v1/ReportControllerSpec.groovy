package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.IssueCreateDTO
import io.choerodon.agile.api.dto.IssueDTO
import io.choerodon.agile.api.dto.ReportIssueDTO
import io.choerodon.agile.api.dto.SprintDetailDTO
import io.choerodon.agile.api.dto.SprintUpdateDTO
import io.choerodon.agile.app.service.IssueService
import io.choerodon.agile.app.service.SprintService
import io.choerodon.agile.domain.agile.repository.UserRepository
import io.choerodon.agile.infra.common.utils.MybatisFunctionTestUtil
import io.choerodon.agile.infra.dataobject.UserDO
import io.choerodon.agile.infra.dataobject.UserMessageDO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

/**
 *
 * @author dinghuang123@gmail.com
 * @since 2018/8/27
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class ReportControllerSpec extends Specification {

    @Autowired
    @Qualifier("mockUserRepository")
    private UserRepository userRepository

    @Autowired
    private TestRestTemplate restTemplate

    @Autowired
    private SprintService sprintService

    @Autowired
    private IssueService issueService

    @Shared
    def projectId = 1

    @Shared
    def sprintId = null

    @Shared
    def issueId = null

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
    }

    def 'createSprintToStart'() {
        given: '创建一个冲刺'
        SprintDetailDTO sprintDetailDTO = sprintService.createSprint(1)
        sprintId = sprintDetailDTO.sprintId

        and: '将issue加入到冲刺中'
        IssueCreateDTO issueCreateDTO = new IssueCreateDTO()
        issueCreateDTO.projectId = projectId
        issueCreateDTO.sprintId = sprintId
        issueCreateDTO.summary = '加入冲刺issue'
        issueCreateDTO.typeCode = 'story'
        issueCreateDTO.priorityCode = 'low'
        issueCreateDTO.reporterId = 1
        IssueDTO issueDTO = issueService.createIssue(issueCreateDTO)
        issueId = issueDTO.issueId

        and: '设置冲刺开启对象'
        SprintUpdateDTO sprintUpdateDTO = new SprintUpdateDTO()
        sprintUpdateDTO.sprintId = sprintId
        sprintUpdateDTO.projectId = projectId
        sprintUpdateDTO.objectVersionNumber = sprintDetailDTO.objectVersionNumber
        sprintUpdateDTO.startDate = new Date()
        sprintUpdateDTO.endDate = MybatisFunctionTestUtil.dataSubFunction(sprintUpdateDTO.startDate, -10)

        when: '将冲刺开启'
        SprintDetailDTO startSprint = sprintService.startSprint(projectId, sprintUpdateDTO)

        then: '验证冲刺是否开启成功'
        startSprint.statusCode == 'started'
    }

    def 'queryBurnDownReport'() {
        when: '向开始查询冲刺对应的燃尽图报告信息的接口发请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/reports/{sprintId}/burn_down_report?type={type}',
                List, projectId, sprintId, type)

        then: '接口是否请求成功'
        entity.statusCode.is2xxSuccessful()

        and: '设置返回值值'
        List<ReportIssueDTO> reportIssueDTOList = entity.body

        expect: '验证期望值'
        reportIssueDTOList.size() == expectSize

        where: '设置期望值'
        type                     | expectSize
        'storyPoints'            | 1
        'remainingEstimatedTime' | 1
        'issueCount'             | 1

    }


}
