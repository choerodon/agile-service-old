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
    private IssueComponentMapper issueComponentMapper

    @Autowired
    private ProductVersionMapper productVersionMapper

    @Autowired
    private VersionIssueRelMapper versionIssueRelMapper

    @Autowired
    AgileEventHandler agileEventHandler

    @Autowired
    IssueService issueService

    @Autowired
    StateMachineServiceImpl stateMachineService

    @Autowired
    IssueController issueController

    @Autowired
    IssueMapper issueMapper

    @Autowired
    SagaClient sagaClient

    @Autowired
    private SprintMapper sprintMapper

    @Autowired
    private IssueFeignClient issueFeignClient

    @Autowired
    private IssueSprintRelMapper issueSprintRelMapper

    @Autowired
    private ProjectInfoMapper projectInfoMapper

    @Autowired
    private DataLogMapper dataLogMapper

    @Autowired
    @Qualifier("userRepository")
    private UserRepository userRepository

    @Autowired
    private SiteMsgUtil siteMsgUtil

    @Shared
    def projectId = 1
    @Shared
    def organizationId = 1
    @Shared
    def componentId = 1
    @Shared
    List issueIdList = new ArrayList()
    @Shared
    def issueTestId = null
    @Shared
    def sprintId = 1
    @Shared
    def versionId = 1
    @Shared
    def issueObjectVersionNumberList = []

    @Shared
    def issues = []
    @Shared
    def issuesSize = 0
    @Shared
    def resultId = 0
    @Shared
    def productVersionId = 0
    @Shared
    def testSprintId = 0
    @Shared
    def baseUrl = '/v1/organizations/{organization_id}/state_machine'
    @Shared
    def issueIds = []

    def setup() {
        given: '设置feign调用mockito'
//        // *_表示任何长度的参数（这里表示只要执行了queryUsersMap这个方法，就让它返回一个空的Map
//        Map<Long, UserMessageDO> userMessageDOMap = new HashMap<>()
//        UserMessageDO userMessageDO = new UserMessageDO("管理员", "http://XXX.png", "dinghuang123@gmail.com")
//        userMessageDOMap.put(1, userMessageDO)
//        userRepository.queryUsersMap(*_) >> userMessageDOMap
//        issues = issueMapper.selectAll()
//        issuesSize = issues.size()
//
//        and: 'mockSagaClient'
//        sagaClient.startSaga(_, _) >> null
//
//        and:
//        siteMsgUtil.issueCreate(*_) >> null
//        siteMsgUtil.issueAssignee(*_) >> null
//        siteMsgUtil.issueSolve(*_) >> null
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
        issueE.setSprintId(sprintId)
        ProjectInfoE projectInfoE = new ProjectInfoE()
        projectInfoE.setProjectId(1L)
        CreateIssuePayload createIssuePayload = new CreateIssuePayload(issueCreateDTO, issueE, projectInfoE)
        stateMachineService.createIssue(issueE.getIssueId(), 1, JSONObject.toJSONString(createIssuePayload))
        issueIds.add(issueDTO.issueId)

        when: '创建问题类型'
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
//                    Object count = map.get("count")
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
}
