package io.choerodon.agile.api.controller.v1

import com.alibaba.fastjson.JSONObject
import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.IssueCreateDTO
import io.choerodon.agile.api.dto.IssueDTO
import io.choerodon.agile.api.dto.ProjectDTO
import io.choerodon.agile.api.eventhandler.AgileEventHandler
import io.choerodon.agile.app.service.IssueService
import io.choerodon.agile.app.service.impl.StateMachineServiceImpl
import io.choerodon.agile.domain.agile.entity.IssueE
import io.choerodon.agile.domain.agile.entity.ProjectInfoE
import io.choerodon.agile.domain.agile.event.CreateIssuePayload
import io.choerodon.agile.domain.agile.event.ProjectConfig
import io.choerodon.agile.domain.agile.event.StateMachineSchemeDeployCheckIssue
import io.choerodon.agile.domain.agile.repository.UserRepository
import io.choerodon.agile.infra.common.utils.SiteMsgUtil
import io.choerodon.agile.infra.dataobject.UserDO
import io.choerodon.agile.infra.feign.IssueFeignClient
import io.choerodon.agile.infra.mapper.*
import io.choerodon.asgard.saga.feign.SagaClient
import org.mockito.Matchers
import org.mockito.Mockito
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author shinan.chen
 * @since 2018/12/6
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class StateMachineControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate
    @Autowired
    StateMachineServiceImpl stateMachineService
    @Autowired
    IssueMapper issueMapper
    @Autowired
    @Qualifier("userRepository")
    private UserRepository userRepository
    @Shared
    def baseUrl = '/v1/organizations/{organization_id}/state_machine'
    @Shared
    def issueIds = []

    def setup() {
        given: '设置feign调用mockito'
        ProjectDTO projectDTO = new ProjectDTO()
        projectDTO.setCode("AG")
        projectDTO.setName("AG")
        projectDTO.setId(1L)
        projectDTO.setOrganizationId(1L)
        Mockito.when(userRepository.queryProject(Matchers.anyLong())).thenReturn(projectDTO)
        UserDO userDO = new UserDO()
        userDO.setRealName("管理员")
        Mockito.when(userRepository.queryUserNameByOption(Matchers.anyLong(), Matchers.anyBoolean())).thenReturn(userDO)
    }

    /**
     * 【内部调用】校验是否可以删除状态机的节点
     * @return
     */
    def 'checkDeleteNode'() {
        given: '准备工作'
        List<ProjectConfig> projectConfigs = new ArrayList<>()
        ProjectConfig projectConfig = new ProjectConfig()
        projectConfig.setProjectId(projectId)
        projectConfig.setApplyType(applyType)
        projectConfigs.add(projectConfig)
        Long testStatusId = statusId
        Long testOrganizationId = 1L

        and: '插入符合条件的issue'
        IssueCreateDTO issueCreateDTO = new IssueCreateDTO()
        issueCreateDTO.projectId = 1L
        issueCreateDTO.sprintId = 1L
        issueCreateDTO.summary = 'issue'
        issueCreateDTO.typeCode = 'story'
        issueCreateDTO.priorityCode = 'low'
        issueCreateDTO.priorityId = 1L
        issueCreateDTO.issueTypeId = 1L
        issueCreateDTO.reporterId = 1L
        IssueDTO issueDTO = stateMachineService.createIssue(issueCreateDTO, "agile")
        IssueE issueE = new IssueE()
        BeanUtils.copyProperties(issueDTO, issueE)
        issueE.setSprintId(1L)
        ProjectInfoE projectInfoE = new ProjectInfoE()
        projectInfoE.setProjectId(1L)
        CreateIssuePayload createIssuePayload = new CreateIssuePayload(issueCreateDTO, issueE, projectInfoE)
        stateMachineService.createIssue(issueE.getIssueId(), 1, JSONObject.toJSONString(createIssuePayload))
        issueIds.add(issueDTO.issueId)

        when: '校验是否可以删除状态机的节点'
        HttpEntity<List<ProjectConfig>> httpEntity = new HttpEntity<>(projectConfigs)
        def entity = restTemplate.exchange(baseUrl + "/check_delete_node?status_id=" + testStatusId, HttpMethod.POST, httpEntity, Map, testOrganizationId)

        then: '状态码为200，调用成功'
        def actRequest = false
        def actResponse = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null) {
                    Map<String, Object> map = entity.getBody()
                    actResponse = (Boolean) map.get("canDelete")
                }
            }
        }
        actRequest == expRequest
        actResponse == expResponse

        where: '测试用例：'
        projectId | applyType | statusId || expRequest | expResponse
        1         | 'agile'   | 1        || true       | false
        1         | 'agile'   | 2        || true       | true
        1         | 'test'    | 1        || true       | true
        2         | 'agile'   | 1        || true       | true
    }

    /**
     * 【内部调用】查询状态机方案变更后对issue的影响
     * @return
     */
    def 'checkStateMachineSchemeChange'() {
        given: '准备工作'
        List<ProjectConfig> projectConfigs = new ArrayList<>()
        ProjectConfig projectConfig = new ProjectConfig()
        projectConfig.setProjectId(projectId)
        projectConfig.setApplyType(applyType)
        projectConfigs.add(projectConfig)
        List<Long> issueTypeIds = new ArrayList<>()
        issueTypeIds.add(issueTypeId)
        StateMachineSchemeDeployCheckIssue deployCheckIssue = new StateMachineSchemeDeployCheckIssue()
        deployCheckIssue.issueTypeIds = issueTypeIds
        deployCheckIssue.projectConfigs = projectConfigs
        Long testOrganizationId = 1L

        and: '插入符合条件的issue'
        IssueCreateDTO issueCreateDTO = new IssueCreateDTO()
        issueCreateDTO.projectId = 1L
        issueCreateDTO.sprintId = 1L
        issueCreateDTO.summary = 'issue'
        issueCreateDTO.typeCode = 'story'
        issueCreateDTO.priorityCode = 'low'
        issueCreateDTO.priorityId = 1L
        issueCreateDTO.issueTypeId = 1L
        issueCreateDTO.reporterId = 1L
        IssueDTO issueDTO = stateMachineService.createIssue(issueCreateDTO, "agile")
        IssueE issueE = new IssueE()
        BeanUtils.copyProperties(issueDTO, issueE)
        issueE.setSprintId(1L)
        ProjectInfoE projectInfoE = new ProjectInfoE()
        projectInfoE.setProjectId(1L)
        CreateIssuePayload createIssuePayload = new CreateIssuePayload(issueCreateDTO, issueE, projectInfoE)
        stateMachineService.createIssue(issueE.getIssueId(), 1L, JSONObject.toJSONString(createIssuePayload))
        issueIds.add(issueDTO.issueId)

        when: '查询状态机方案变更后对issue的影响'
        HttpEntity<StateMachineSchemeDeployCheckIssue> httpEntity = new HttpEntity<>(deployCheckIssue)
        ParameterizedTypeReference<Map<Long, Long>> typeRef = new ParameterizedTypeReference<Map<Long, Long>>() {
        }
        def entity = restTemplate.exchange(baseUrl + "/check_state_machine_scheme_change", HttpMethod.POST, httpEntity, typeRef, testOrganizationId)

        then: '状态码为200，调用成功'
        def actRequest = false
        def actResponse = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null) {
                    Map<Long, Long> map = entity.getBody()
                    if (map.get(1L) >= 1L) {
                        actResponse = true
                    }
                }
            }
        }
        actRequest == expRequest
        actResponse == expResponse

        where: '测试用例：'
        projectId | applyType | issueTypeId || expRequest | expResponse
        1L        | 'agile'   | 1L          || true       | true
        1L         | 'agile'   | 2L           || true       | false
        1L         | 'test'    | 1L           || true       | false
        2L         | 'agile'   | 1L           || true       | false
    }

    def "deleteIssue"() {
        when: '执行方法'
        restTemplate.delete('/v1/projects/{project_id}/issues/{issueId}', 1L, issueId)

        then: '返回值'
        def result = issueMapper.selectByPrimaryKey(issueId as Long)

        expect: '期望值'
        result == null

        where: '判断issue是否删除'
        issueId << issueIds


    }
}
