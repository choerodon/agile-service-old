package io.choerodon.agile.api.controller.v1


import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.ActiveSprintDTO
import io.choerodon.agile.api.dto.BackLogIssueDTO
import io.choerodon.agile.api.dto.IssueCreateDTO
import io.choerodon.agile.api.dto.IssueDTO
import io.choerodon.agile.api.dto.IssueListDTO
import io.choerodon.agile.api.dto.ProjectDTO
import io.choerodon.agile.api.dto.SprintCompleteDTO
import io.choerodon.agile.api.dto.SprintCompleteMessageDTO
import io.choerodon.agile.api.dto.SprintDetailDTO
import io.choerodon.agile.api.dto.SprintNameDTO
import io.choerodon.agile.api.dto.SprintSearchDTO
import io.choerodon.agile.api.dto.SprintUpdateDTO
import io.choerodon.agile.api.dto.SprintWorkCalendarDTO
import io.choerodon.agile.api.dto.WorkCalendarRefCreateDTO
import io.choerodon.agile.api.dto.WorkCalendarRefDTO
import io.choerodon.agile.api.dto.TimeZoneWorkCalendarRefDetailDTO
import io.choerodon.agile.api.eventhandler.AgileEventHandler
import io.choerodon.agile.app.service.impl.StateMachineServiceImpl
import io.choerodon.agile.domain.agile.repository.UserRepository
import io.choerodon.agile.infra.common.utils.MybatisFunctionTestUtil
import io.choerodon.agile.infra.dataobject.SprintDO
import io.choerodon.agile.infra.mapper.DataLogMapper
import io.choerodon.agile.infra.mapper.IssueMapper
import io.choerodon.agile.infra.mapper.IssueSprintRelMapper
import io.choerodon.agile.infra.mapper.ProjectInfoMapper
import io.choerodon.agile.infra.mapper.SprintMapper
import io.choerodon.asgard.saga.feign.SagaClient
import io.choerodon.core.domain.Page
import org.mockito.Matchers
import org.mockito.Mockito

//import io.choerodon.event.producer.execute.EventProducerTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
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

import java.text.SimpleDateFormat

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
    StateMachineServiceImpl stateMachineService

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
    @Qualifier("userRepository")
    private UserRepository userRepository

    @Shared
    def projectId = 1

    @Shared
    def organizationId = 1

    @Shared
    def sprintIds = []

    @Shared
    def issueIds = []

    def setup() {
        given: '设置feign调用mockito'
        ProjectDTO projectDTO = new ProjectDTO()
        projectDTO.setCode("AG")
        projectDTO.setName("AG")
        Mockito.when(userRepository.queryProject(Matchers.anyLong())).thenReturn(projectDTO)

    }

    def 'createSprint'() {
        when: '向创建冲刺的接口发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/sprint', null, SprintDetailDTO, projectId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        SprintDetailDTO sprintDetailDTO = entity.body
        sprintIds << sprintDetailDTO.sprintId

        expect: '设置期望值'
        sprintDetailDTO.sprintId != null
        sprintDetailDTO.projectId == projectId
        sprintDetailDTO.statusCode == 'sprint_planning'
    }

    def 'initIssueToSprint'() {
        given: 'Issue加入到冲刺中'
        IssueCreateDTO issueCreateDTO = new IssueCreateDTO()
        issueCreateDTO.projectId = projectId
        issueCreateDTO.sprintId = sprintIds[0]
        issueCreateDTO.summary = '加入冲刺issue'
        issueCreateDTO.typeCode = 'story'
        issueCreateDTO.priorityCode = 'low'
        issueCreateDTO.priorityId = 1
        issueCreateDTO.issueTypeId = 1
        issueCreateDTO.reporterId = 1

        when: '更新issue'
        IssueDTO issueDTO = stateMachineService.createIssue(issueCreateDTO, "agile")
        issueIds.add(issueDTO.getIssueId())

        then: '判断issue是否成功生成'
        issueDTO.objectVersionNumber == 1
        issueDTO.projectId == 1
        issueDTO.summary == '加入冲刺issue'
        issueDTO.typeCode == 'story'
        issueDTO.reporterId == 1

    }

    def "updateSprint"() {
        given: '冲刺DTO对象'
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd")
        SprintUpdateDTO sprintUpdateDTO = new SprintUpdateDTO()
        sprintUpdateDTO.projectId = projectId
        sprintUpdateDTO.sprintId = sprintIds[0]
        sprintUpdateDTO.objectVersionNumber = 1
        sprintUpdateDTO.sprintName = '测试冲刺1'
        sprintUpdateDTO.startDate = new Date()
        sprintUpdateDTO.endDate = MybatisFunctionTestUtil.dataSubFunction(sprintUpdateDTO.startDate, -10)
        String startDate = dateFormat.format(sprintUpdateDTO.startDate)
        String endDate = dateFormat.format(sprintUpdateDTO.endDate)
        HttpEntity<SprintUpdateDTO> requestEntity = new HttpEntity<SprintUpdateDTO>(sprintUpdateDTO, null)

        when: '分页过滤查询issue列表提供给测试模块用'
        def entity = restTemplate.exchange('/v1/projects/{project_id}/sprint', HttpMethod.PUT, requestEntity, SprintDetailDTO, projectId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        SprintDetailDTO sprintDetailDTO = entity.getBody()

        expect: '设置期望值'
        sprintDetailDTO.sprintId == sprintIds[0]
        sprintDetailDTO.sprintName == '测试冲刺1'
        sprintDetailDTO.projectId == projectId
        dateFormat.format(sprintDetailDTO.startDate) == startDate
        dateFormat.format(sprintDetailDTO.endDate) == endDate
    }

    def "queryByProjectId"() {
        given: '给定查询参数'
        Map<String, Object> searchParamMap = new HashMap<>()
        searchParamMap.put("advancedSearchArgs", null)

        when: '发送请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/sprint/issues?organizationId={organizationId}', searchParamMap, Map, projectId, 1L)

        then: '请求结果'
        entity.statusCode.is2xxSuccessful()
        Map<String, Object> result = entity.body

        and: '返回值'
        BackLogIssueDTO backLogIssueDTO = result.get("backlogData") as BackLogIssueDTO
        List<SprintSearchDTO> searchDTOList = result.get("sprintData") as List<SprintSearchDTO>

        expect: '期望值'
        backLogIssueDTO.backlogIssueCount == 0
        backLogIssueDTO.backLogIssue == []
        searchDTOList.size() == 2
        searchDTOList.get(0).sprintId == sprintIds[0]
        searchDTOList.get(0).sprintName == '测试冲刺1'
        searchDTOList.get(0).statusCode == 'sprint_planning'
        searchDTOList.get(0).issueCount == 1
        searchDTOList.get(0).issueSearchDTOList.size() == 1
        searchDTOList.get(0).issueSearchDTOList.get(0).issueTypeDTO.typeCode == 'story'
        searchDTOList.get(0).issueSearchDTOList.get(0).summary == '加入冲刺issue'
        searchDTOList.get(0).assigneeIssues.size() == 1
        searchDTOList.get(0).assigneeIssues.get(0).sprintId == sprintIds[0]
        searchDTOList.get(0).assigneeIssues.get(0).assigneeId == 0
        searchDTOList.get(0).assigneeIssues.get(0).totalStoryPoints == null
        searchDTOList.get(0).assigneeIssues.get(0).issueCount == 1
    }

    def "queryNameByOptions"() {
        given: '给定查询参数'
        def sprintStatusCodes = ["sprint_planning", "started", "closed"]

        when: '发送请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/sprint/names', sprintStatusCodes, List, projectId)

        then: '请求结果'
        entity.statusCode.is2xxSuccessful()

        and: '返回值'
        List<SprintNameDTO> result = entity.body

        expect: '期望值'
        result.size() == 2
        result.get(0).sprintId == sprintIds[0]
        result.get(0).sprintName == '测试冲刺1'
    }

    def "queryCompleteMessageBySprintId"() {
        given: '给定查询参数'
        def sprintId = sprintIds[0]

        when: '发送请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/sprint/{sprintId}/names', SprintCompleteMessageDTO.class, projectId, sprintId)

        then: '请求结果'
        entity.statusCode.is2xxSuccessful()

        and: '返回值'
        SprintCompleteMessageDTO result = entity.body

        expect: '期望值'
        result.incompleteIssues == 1
        result.parentsDoneUnfinishedSubtasks.size() == 0
        result.sprintNames.size() == 2
        result.sprintNames.get(1).sprintName == '测试冲刺1'
        result.sprintNames.get(1).sprintId == sprintId
        result.partiallyCompleteIssues == 0

    }

    def "querySprintById"() {
        given: '给定查询参数'
        def sprintId = sprintIds[0]

        when: '发送请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/sprint/{sprintId}', SprintDetailDTO.class, projectId, sprintId)

        then: '请求结果'
        entity.statusCode.is2xxSuccessful()

        and: '返回值'
        SprintDetailDTO result = entity.body

        expect: '期望值'
        result.sprintName == '测试冲刺1'
        result.sprintId == sprintId
        result.projectId == projectId
        result.objectVersionNumber == 2
        result.statusCode == 'sprint_planning'
        result.issueCount == 1

    }

    def "queryIssueByOptions"() {
        given: '给定查询参数'
        def sprintId = sprintIds[0]

        when: '发送请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/sprint/{sprintId}/issues?status={status}&&organizationId={organizationId}',
                Page, projectId, sprintId, "sprint_planning", organizationId)

        then: '请求结果'
        entity.statusCode.is2xxSuccessful()

        and: '返回值'
        List<IssueListDTO> result = entity.body.content

        expect: '期望值'
        result.size() == 0

    }

    def "queryCurrentSprintCreateName"() {
        when: '发送请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/sprint/current_create_name', String.class, projectId)

        then: '请求结果'
        entity.statusCode.is2xxSuccessful()

        and: '返回值'
        String result = entity.body

        expect: '期望值'
        result == '测试冲刺2'
    }

    def "createBySprintName"() {
        when: '发送请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/sprint/create?sprintName={sprintName}', null, SprintDetailDTO.class, projectId, '测试冲刺2')

        then: '请求结果'
        entity.statusCode.is2xxSuccessful()

        and: '返回值'
        SprintDetailDTO result = entity.body
        sprintIds << result.sprintId

        expect: '期望值'
        result.sprintName == '测试冲刺2'
        result.projectId == projectId
        result.issueCount == null
        result.objectVersionNumber == 1
        result.statusCode == 'sprint_planning'
    }

    def "startSprint"() {
        given: '冲刺更新对象'
        SprintDO sprintDO = sprintMapper.queryByProjectIdAndSprintId(projectId, sprintIds[0])
        SprintUpdateDTO sprintUpdateDTO = new SprintUpdateDTO()
        sprintUpdateDTO.sprintId = sprintIds[0]
        sprintUpdateDTO.projectId = projectId
        sprintUpdateDTO.objectVersionNumber = sprintDO.objectVersionNumber
        sprintUpdateDTO.startDate = sprintDO.startDate
        sprintUpdateDTO.endDate = sprintDO.endDate
        List<WorkCalendarRefDTO> dateList = new ArrayList<>()
        WorkCalendarRefDTO sprintWorkCalendarRefDTO = new WorkCalendarRefDTO()
        sprintWorkCalendarRefDTO.status = 1
        sprintWorkCalendarRefDTO.workDay = "2018-10-1"
        WorkCalendarRefDTO sprintWorkCalendarRefDTOTwo = new WorkCalendarRefDTO()
        sprintWorkCalendarRefDTOTwo.status = 0
        sprintWorkCalendarRefDTOTwo.workDay = "2018-10-2"
        dateList.add(sprintWorkCalendarRefDTO)
        sprintUpdateDTO.workDates = dateList

        when: '发送请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/sprint/start', sprintUpdateDTO, SprintDetailDTO.class, projectId)

        then: '请求结果'
        entity.statusCode.is2xxSuccessful()

        and: '返回值'
        SprintDetailDTO result = entity.body

        expect: '期望值'
        result.sprintName == '测试冲刺1'
        result.projectId == projectId
        result.issueCount == null
        result.objectVersionNumber == 3
        result.statusCode == 'started'
    }

    def 'queryUnClosedSprint'() {
        when:
        def entity = restTemplate.exchange('/v1/projects/{project_id}/sprint/unclosed',
                HttpMethod.GET,
                new HttpEntity<>(),
                List.class,
                projectId)

        then:
        entity.statusCode.is2xxSuccessful()
        entity.body.size() != 0
    }

    def 'queryActiveSprint'() {
        when:
        def entity = restTemplate.exchange('/v1/projects/{project_id}/sprint/active/{organizationId}',
                HttpMethod.GET,
                new HttpEntity<>(),
                ActiveSprintDTO.class,
                projectId, 1)

        then:
        entity.statusCode.is2xxSuccessful()
        entity.body != null
        entity.body.sprintId != null
    }

    def "completeSprint"() {
        given: '冲刺更新对象'
        SprintCompleteDTO sprintCompleteDTO = new SprintCompleteDTO()
        sprintCompleteDTO.projectId = projectId
        sprintCompleteDTO.sprintId = sprintIds[0]
        sprintCompleteDTO.incompleteIssuesDestination = 0

        when: '发送请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/sprint/complete', sprintCompleteDTO, Boolean.class, projectId)

        then: '请求结果'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        Boolean result = entity

        expect: '期望值'
        result
    }

    def "queryNonWorkdays"() {
        when: '发送请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/sprint/query_non_workdays/{sprint_id}/{organizationId}', List.class, projectId, sprintIds[0], 1)

        then: '请求结果'
        entity.statusCode.is2xxSuccessful()

    }

    def 'queryTimeZoneWorkCalendarDetail'() {
        when:
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/sprint/time_zone_detail/{organization_id}?year={year}', TimeZoneWorkCalendarRefDetailDTO, projectId, 1, 2018)

        then:
        entity.statusCode.is2xxSuccessful()
    }

    def "deleteSprint"() {
        when: '发送请求'
        def entity = restTemplate.exchange('/v1/projects/{project_id}/sprint/{sprintId}', HttpMethod.DELETE, new HttpEntity<Object>(), ResponseEntity.class, projectId, sprintId)

        then: '请求结果'
        entity.statusCode == statusCode

        where: '期望值'
        sprintId     | statusCode
        sprintIds[1] | HttpStatus.NO_CONTENT
        sprintIds[0] | HttpStatus.NO_CONTENT
    }

    def "deleteIssue"() {
        when: '执行方法'
        restTemplate.delete('/v1/projects/{project_id}/issues/{issueId}', projectId, issueId)

        then: '返回值'
        def result = issueMapper.selectByPrimaryKey(issueId as Long)

        expect: '期望值'
        result == null

        where: '判断issue是否删除'
        issueId << issueIds


    }

}
