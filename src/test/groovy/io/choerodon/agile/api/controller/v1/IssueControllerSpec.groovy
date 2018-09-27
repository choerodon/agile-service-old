package io.choerodon.agile.api.controller.v1

import com.alibaba.fastjson.JSONObject
import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.*
import io.choerodon.agile.api.eventhandler.AgileEventHandler
import io.choerodon.agile.app.service.IssueService
import io.choerodon.agile.domain.agile.repository.UserRepository
import io.choerodon.agile.infra.dataobject.*
import io.choerodon.agile.infra.mapper.*
import io.choerodon.asgard.saga.feign.SagaClient
import io.choerodon.core.domain.Page
import io.choerodon.event.producer.execute.EventProducerTemplate
import io.choerodon.mybatis.pagehelper.domain.PageRequest
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

import java.util.stream.Collectors

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
    private IssueComponentMapper issueComponentMapper

    @Autowired
    private ProductVersionMapper productVersionMapper

    @Autowired
    private VersionIssueRelMapper versionIssueRelMapper

    @Autowired
    AgileEventHandler agileEventHandler

    @Autowired
    @Qualifier("issueService")
    IssueService issueService

    @Autowired
    IssueController issueController

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

        issues = issueMapper.selectAll()
        issuesSize = issues.size()

        and: 'mockSagaClient'
        sagaClient.startSaga(_, _) >> null

    }

    def 'createIssue'() {
        given: '给一个创建issue的DTO'
        IssueCreateDTO issueCreateDTO = new IssueCreateDTO()

        and: '设置issue属性'
        issueCreateDTO.typeCode = typeCode
        issueCreateDTO.projectId = projectId
        issueCreateDTO.description = "测试issue描述"
        issueCreateDTO.summary = "测试issue概要"
        issueCreateDTO.priorityCode = "hight"
        issueCreateDTO.assigneeId = 1
        issueCreateDTO.sprintId = sprintId

        and: '设置模块'
        List<ComponentIssueRelDTO> componentIssueRelDTOList = new ArrayList<>()
        ComponentIssueRelDTO componentIssueRelDTO = new ComponentIssueRelDTO()
        componentIssueRelDTO.projectId = projectId
        componentIssueRelDTO.name = "测试模块"
        componentIssueRelDTOList.add(componentIssueRelDTO)
        ComponentIssueRelDTO componentExit = new ComponentIssueRelDTO()
        componentExit.projectId = projectId
        componentExit.componentId = componentId
        componentIssueRelDTOList.add(componentExit)
        ComponentIssueRelDTO componentCreate = new ComponentIssueRelDTO()
        componentCreate.projectId = projectId
        componentCreate.name = "测试模块2"
        componentIssueRelDTOList.add(componentCreate)
        issueCreateDTO.componentIssueRelDTOList = componentIssueRelDTOList

        and: '设置标签'
        List<LabelIssueRelDTO> labelIssueRelDTOList = new ArrayList<>()
        LabelIssueRelDTO labelIssueRelDTO = new LabelIssueRelDTO()
        labelIssueRelDTO.projectId = projectId
        labelIssueRelDTO.labelName = "测试标签"
        LabelIssueRelDTO labelIssueRelDTO1 = new LabelIssueRelDTO()
        labelIssueRelDTO1.projectId = projectId
        labelIssueRelDTO1.labelId = 1L
        labelIssueRelDTOList.add(labelIssueRelDTO)
        labelIssueRelDTOList.add(labelIssueRelDTO1)
        issueCreateDTO.labelIssueRelDTOList = labelIssueRelDTOList

        and: '设置版本'
        List<VersionIssueRelDTO> versionIssueRelDTOList = new ArrayList<>()
        VersionIssueRelDTO versionIssueRelDTO = new VersionIssueRelDTO()
        versionIssueRelDTO.projectId = projectId
        versionIssueRelDTO.name = "测试版本"
        versionIssueRelDTO.relationType = "fix"
        VersionIssueRelDTO versionIssueRelDTO1 = new VersionIssueRelDTO()
        versionIssueRelDTO1.projectId = projectId
        versionIssueRelDTO1.versionId = versionId
        versionIssueRelDTO1.relationType = "fix"
        versionIssueRelDTOList.add(versionIssueRelDTO)
        versionIssueRelDTOList.add(versionIssueRelDTO1)
        issueCreateDTO.versionIssueRelDTOList = versionIssueRelDTOList

        when: '向开始创建issue的接口发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issues', issueCreateDTO, IssueDTO, projectId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        print(entity.body ? entity.body.toString() : null)

        and: '设置值'
        issueIdList.add(entity.body.issueId)
        issueObjectVersionNumberList << entity.body.objectVersionNumber

        expect: '设置期望值'
        entity.body.typeCode == expectedTypeCode

        where: '不同issue类型返回值与期望值对比'
        typeCode     | expectedTypeCode
        "story"      | "story"
        "task"       | "task"
        "issue_epic" | "issue_epic"
    }

    def 'createSubIssue'() {
        given: '给一个创建issue的DTO'
        IssueSubCreateDTO issueSubCreateDTO = new IssueSubCreateDTO()

        and: '设置issue属性'
        issueSubCreateDTO.projectId = projectId
        issueSubCreateDTO.description = "测试issue描述"
        issueSubCreateDTO.summary = "测试issue概要"
        issueSubCreateDTO.priorityCode = "hight"
        issueSubCreateDTO.assigneeId = 1
        issueSubCreateDTO.parentIssueId = issueIdList[0]

        and: '设置模块'
        List<ComponentIssueRelDTO> componentIssueRelDTOList = new ArrayList<>()
        ComponentIssueRelDTO componentIssueRelDTO = new ComponentIssueRelDTO()
        componentIssueRelDTO.projectId = projectId
        componentIssueRelDTO.name = "测试模块"
        componentIssueRelDTOList.add(componentIssueRelDTO)
        ComponentIssueRelDTO componentExit = new ComponentIssueRelDTO()
        componentExit.projectId = projectId
        componentExit.componentId = componentId
        componentIssueRelDTOList.add(componentExit)
        ComponentIssueRelDTO componentCreate = new ComponentIssueRelDTO()
        componentCreate.projectId = projectId
        componentCreate.name = "测试模块2"
        componentIssueRelDTOList.add(componentCreate)
        issueSubCreateDTO.componentIssueRelDTOList = componentIssueRelDTOList

        and: '设置标签'
        List<LabelIssueRelDTO> labelIssueRelDTOList = new ArrayList<>()
        LabelIssueRelDTO labelIssueRelDTO = new LabelIssueRelDTO()
        labelIssueRelDTO.projectId = projectId
        labelIssueRelDTO.labelName = "测试标签"
        LabelIssueRelDTO labelIssueRelDTO1 = new LabelIssueRelDTO()
        labelIssueRelDTO1.projectId = projectId
        labelIssueRelDTO1.labelName = "测试标签1"
        labelIssueRelDTOList.add(labelIssueRelDTO)
        labelIssueRelDTOList.add(labelIssueRelDTO1)
        issueSubCreateDTO.labelIssueRelDTOList = labelIssueRelDTOList

        and: '设置版本'
        List<VersionIssueRelDTO> versionIssueRelDTOList = new ArrayList<>()
        VersionIssueRelDTO versionIssueRelDTO = new VersionIssueRelDTO()
        versionIssueRelDTO.projectId = projectId
        versionIssueRelDTO.name = "测试版本"
        versionIssueRelDTO.relationType = "fix"
        VersionIssueRelDTO versionIssueRelDTO1 = new VersionIssueRelDTO()
        versionIssueRelDTO1.projectId = projectId
        versionIssueRelDTO1.name = "测试版本2"
        versionIssueRelDTO1.relationType = "fix"
        versionIssueRelDTOList.add(versionIssueRelDTO)
        versionIssueRelDTOList.add(versionIssueRelDTO1)
        issueSubCreateDTO.versionIssueRelDTOList = versionIssueRelDTOList

        when: '向开始创建issue的接口发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issues/sub_issue', issueSubCreateDTO, IssueSubDTO, projectId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        issueIdList.add(entity.body.issueId)
        issueObjectVersionNumberList << entity.body.objectVersionNumber
        expect: '设置期望值'
        entity.body.typeCode == "sub_task"
        entity.body.parentIssueId == issueIdList[0]
        entity.body.description == "测试issue描述"
        entity.body.summary == "测试issue概要"
        entity.body.priorityCode == "hight"

    }

    def 'updateIssue'() {
        given: '更新issue的DTO'
        JSONObject issueUpdate = new JSONObject()
        and: '设置更新issue的信息'
        issueUpdate.put("objectVersionNumber", issueObjectVersionNumberList[0])
        issueUpdate.put("issueId", issueIdList[0])
        issueUpdate.put("summary", "修改概要")
        issueUpdate.put("priorityCode", "low")
        issueUpdate.put("description", "修改描述")
        issueUpdate.put("assigneeId", 2)
        issueUpdate.put("epicId", issueIdList[2])
        issueUpdate.put("storyPoints", 10)
        issueUpdate.put("sprintId", null)
        issueUpdate.put("estimateTime", 20)
        issueUpdate.put("versionIssueRelDTOList", [])
        issueUpdate.put("labelIssueRelDTOList", [])
        issueUpdate.put("componentIssueRelDTOList", [])
        List<IssueLinkDTO> issueLinkDTOList = new ArrayList<>()
        IssueLinkDTO issueLinkDTO = new IssueLinkDTO()
        issueLinkDTO.issueId = issueIdList[0]
        issueLinkDTO.linkId = issueIdList[1]
        issueLinkDTO.linkTypeId = 1
        issueLinkDTOList.add(issueLinkDTO)
        issueUpdate.put("issueLinkDTOList", issueLinkDTOList)
        List<LabelIssueRelDTO> labelIssueRelDTOArrayList = new ArrayList<>()
        LabelIssueRelDTO labelIssueRelDTO = new LabelIssueRelDTO()
        labelIssueRelDTO.issueId = issueIdList[0]
        labelIssueRelDTO.labelId = 1
        labelIssueRelDTO.projectId = projectId
        labelIssueRelDTOArrayList.add(labelIssueRelDTO)
        issueUpdate.put("labelIssueRelDTOList", labelIssueRelDTOArrayList)

        when: '向开始创建issue的接口发请求'
        restTemplate.put('/v1/projects/{project_id}/issues', issueUpdate, projectId)

        then: '返回值'
        IssueDO issueDO = issueMapper.selectByPrimaryKey(issueIdList[0])

        and: '设置值'
        issueObjectVersionNumberList[0] << issueDO.objectVersionNumber

        expect: '验证更新是否成功'
        issueDO.description == "修改描述"
        issueDO.summary == "修改概要"
        issueDO.storyPoints == 10
        issueDO.estimateTime == 20
        issueDO.assigneeId == 2
        issueDO.epicId == issueIdList[2]
        issueDO.priorityCode == "low"
        issueDO.rank != ""
    }

    def 'queryIssue'() {
        when: '向开始查询单个issue的接口发请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/issues/{issueId}', IssueDTO, projectId, issueId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        expect: '设置期望值'
        entity.body.typeCode == expectedTypeCode

        where: '不同issue类型返回值与期望值对比'
        issueId        | expectedTypeCode
        issueIdList[0] | "story"
        issueIdList[1] | "task"
        issueIdList[2] | "issue_epic"
        issueIdList[3] | "sub_task"
    }

    def 'queryIssueSub'() {
        when: '向开始查询单个issue子任务的接口发请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/issues/sub_issue/{issueId}', IssueSubDTO, projectId, issueIdList[3])

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        expect: '设置期望值'
        entity.body.typeCode == "sub_task"

    }

    def 'listIssueWithoutSub'() {
        given: '查询参数'
        SearchDTO searchDTO = new SearchDTO()
        when: '向开始查询分页过滤查询issue列表的接口发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issues/no_sub', searchDTO, Page, projectId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<IssueListDTO> issueListDTOList = entity.body.content
        expect: '设置期望值'
        issueListDTOList.size() > 0

    }

    def 'queryIssueByOption'() {
        when: '向分页搜索查询issue列表的接口发请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/issues/summary?onlyActiveSprint={onlyActiveSprint}&self={self}' +
                '&issueId={issueId}&content={content}&page={page}&size={size}',
                Page, projectId, false, false, issueIdList[0], "测试", 0, 10)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<IssueNumDTO> issueNumDTOList = entity.body.content
        expect: '设置期望值'
        issueNumDTOList.size() > 0
    }

    def 'queryIssueByOptionForAgile'() {
        when: '向分页搜索查询issue列表的接口发请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/issues/agile/summary?self={self}' +
                '&issueId={issueId}&content={content}&page={page}&size={size}',
                Page, projectId, false, issueIdList[0], "测试", 0, 10)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<IssueNumDTO> issueNumDTOList = entity.body.content
        expect: '设置期望值'
        issueNumDTOList.size() > 0
    }

    def 'listEpic'() {
        when: '向开始查询epic的接口发请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/issues/epics',
                List, projectId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<EpicDataDTO> epicDataDTOList = entity.body
        expect: '设置期望值'
        epicDataDTOList.size() > 0
    }

    def 'batchIssueToVersion'() {
        when: '向issue批量加入版本接口发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issues/to_version/{versionId}',
                issueIdList, List, projectId, id)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<IssueSearchDTO> issueSearchDTOList = entity.body

        expect: '设置期望值'
        issueSearchDTOList.size() == expectSize

        where: '设置期望值'
        id | expectSize
        0  | 4
        1  | 4
    }

    def 'batchIssueToEpic'() {
        given: '给定一个issue数组列表'
        List<Long> issueIds = issueIdList.subList(0, 2)
        when: '向issue批量加入epic接口发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issues/to_epic/{epicId}',
                issueIds, List, projectId, issueIdList[2])

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<IssueSearchDTO> issueSearchDTOList = entity.body
        expect: '设置期望值'
        issueSearchDTOList.size() > 0
    }

    def "batchIssueToSprint"() {
        given: '移动issue的对象'
        MoveIssueDTO moveIssueDTO = new MoveIssueDTO()
        moveIssueDTO.before = true
        moveIssueDTO.issueIds = issueIds
        moveIssueDTO.rankIndex = true
        moveIssueDTO.before = true
        moveIssueDTO.outsetIssueId = 0
        when: '向issue批量加入冲刺接口发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issues/to_sprint/{sprintId}',
                moveIssueDTO, List, projectId, sprintId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<IssueSearchDTO> issueSearchDTOList = entity.body

        expect: '设置期望值'
        issueSearchDTOList.size() == expectSize

        where: '对比设置与期望'
        rankIndex | before | outsetIssueId  | issueIds                                    || expectSize
        false     | false  | issueIdList[1] | Arrays.asList(issueIdList[1]) as List<Long> || 1
        true      | true   | 0              | Arrays.asList(issueIdList[1]) as List<Long> || 1

    }

    def "listEpicSelectData"() {
        when: '向开始查询当前项目下的epic，提供给列表下拉接口发请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/issues/epics/select_data',
                List, projectId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<IssueEpicDTO> issueEpicDTOList = entity.body
        expect: '设置期望值'
        issueEpicDTOList.size() > 0

    }

    def "updateIssueTypeCode"() {
        given: '给定一个修改类型DTO'
        IssueUpdateTypeDTO issueUpdateTypeDTO = new IssueUpdateTypeDTO()
        issueUpdateTypeDTO.issueId = issueIdList[0]
        issueUpdateTypeDTO.projectId = projectId
        issueUpdateTypeDTO.typeCode = typeCode
        issueUpdateTypeDTO.objectVersionNumber = issueObjectVersionNumberList[0]
        issueUpdateTypeDTO.epicName = "测试epic"

        when: '向issue批量加入冲刺接口发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issues/update_type',
                issueUpdateTypeDTO, IssueDTO, projectId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        print(entity.body ? entity.body.toString() : null)

        and: '设置值'
        if (entity.body.objectVersionNumber) {
            issueObjectVersionNumberList[0] = entity.body.objectVersionNumber
        }

        expect: '设置期望值'
        entity.body.typeCode == expectedTypeCode

        where: '不同issue类型返回值与期望值对比'
        typeCode     | expectedTypeCode
        "task"       | "task"
        "issue_epic" | "issue_epic"
        "story"      | "story"
    }

    def "listByOptions"() {
        when: '向根据issue类型(type_code)查询issue列表(分页)接口发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issues/type_code/{typeCode}',
                null, Page, projectId, "story")

        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        print(entity.body.content ? entity.body.content.toString() : null)

        expect: '设置期望值'
        entity.body.content.size() > 0

    }

    /**
     * 导出excel的时候，SXSSFSheet的workbook.createSheet方法调用了FontManagerFactory去拿系统的"sun.font.fontmanager"属性
     * 但是在gitlab中的runner中reflect反射获取类报空指针异常，这个可能是runner中jdk的版本问题
     * @return Exception
     */
//    def "exportIssues"() {
//        given: '给定查询dto'
//        SearchDTO searchDTO = new SearchDTO()
//        searchDTO.content = '测试'
//
//        and: 'mock userFeignClient'
//        ProjectDTO projectDTO = new ProjectDTO()
//        projectDTO.name = '测试项目'
//        projectDTO.code = '测试项目Code'
//
//        when: '向根据issue类型(type_code)查询issue列表(分页)接口发请求'
//        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issues/export',
//                searchDTO, null, projectId)
//
//        then: '返回值'
//        entity.statusCode.is2xxSuccessful()
//        1 * userRepository.queryProject(_) >> projectDTO
//
//
//        expect: '期待值比较'
//        entity.headers.get("Content-Type") != null
//
//    }

    def "cloneIssueByIssueId"() {
        given: '复制issue条件DTO'
        CopyConditionDTO conditionDTO = new CopyConditionDTO()
        conditionDTO.summary = "测试复制issue"
        conditionDTO.subTask = true
        conditionDTO.issueLink = true
        conditionDTO.sprintValues = true

        when: '向复制一个issue的接口发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issues/{issueId}/clone_issue', conditionDTO, IssueDTO, projectId, issueId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        print(entity.body ? entity.body.toString() : null)

        and: '设置值'
        issueIdList.add(entity.body.issueId)
        issueObjectVersionNumberList << entity.body.objectVersionNumber

        expect: '设置期望值'
        entity.body.typeCode == expectedTypeCode

        where: '不同issue类型返回值与期望值对比'
        issueId        | expectedTypeCode
        issueIdList[0] | "story"
        issueIdList[1] | "task"
        issueIdList[2] | "issue_epic"

    }

    def "transformedSubTask"() {
        given: '复制issue条件DTO'
        IssueTransformSubTask issueTransformSubTask = new IssueTransformSubTask()
        issueTransformSubTask.issueId = issueIdList[i]
        issueTransformSubTask.objectVersionNumber = issueObjectVersionNumberList[i]
        issueTransformSubTask.parentIssueId = issueIdList[0]
        issueTransformSubTask.statusId = 1L

        when: '向任务转换为子任务的接口发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issues/transformed_sub_task', issueTransformSubTask, IssueSubDTO, projectId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        issueIdList.add(entity.body.issueId)
        issueObjectVersionNumberList << entity.body.objectVersionNumber

        expect: '设置期望值'
        entity.body.typeCode == expectedTypeCode

        where: '不同issue类型返回值与期望值对比'
        i | expectedTypeCode
        5 | "sub_task"
        6 | "sub_task"

    }

    def "listByIssueIds"() {
        given: '给定issueIds'
        def issueIds = issueIdList

        when: '向根据issue ids查询issue相关信息的接口发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issues/issue_infos', issueIds, List, projectId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<IssueInfoDTO> issueInfoDTOList = entity.body

        expect: '设置期望值'
        issueInfoDTOList.size() > 0

    }

    def "listIssueWithoutSubToTestComponent"() {
        given: '查询参数'
        SearchDTO searchDTO = new SearchDTO()

        when: '分页过滤查询issue列表提供给测试模块用'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issues/test_component/no_sub', searchDTO, Page, projectId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<IssueListDTO> issueListDTOList = entity.body.content
        expect: '设置期望值'
        issueListDTOList.size() > 0

    }

    def "queryIssueNumByTimeSlot"() {
        given: '查询条件'
        def typeCode = "story"

        when: '根据时间段查询问题类型的数量的接口发请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/issues/type/{typeCode}?timeSlot={timeSlot}', List, projectId, typeCode, timeSlot)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<IssueCreationNumDTO> issueCreationNumDTOList = entity.body

        expect: '设置期望值'
        issueCreationNumDTOList.size() > 0 == expectCount

        where: '给定参数'
        timeSlot | expectCount
        0        | false
        1        | true
        3        | true
        4        | true
        -1       | false
        -3       | false
        -4       | false


    }

    def "dragEpic"() {
        given: '拖动参数'
        EpicSequenceDTO epicSequenceDTO = new EpicSequenceDTO()
        epicSequenceDTO.epicId = issueIdList[2]
        epicSequenceDTO.objectVersionNumber = issueObjectVersionNumberList[2]
        epicSequenceDTO.beforeSequence = issueIdList[7]

        when: '分页过滤查询issue列表提供给测试模块用'
        HttpEntity<EpicSequenceDTO> requestEntity = new HttpEntity<EpicSequenceDTO>(epicSequenceDTO, null)
        def entity = restTemplate.exchange('/v1/projects/{project_id}/issues/epic_drag', HttpMethod.PUT, requestEntity, EpicDataDTO, projectId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        EpicDataDTO epicDataDTO = entity.getBody()
        if (epicDataDTO.objectVersionNumber) {
            issueObjectVersionNumberList[2] = epicDataDTO.objectVersionNumber
        }

        expect: '设置期望值'
        epicDataDTO.issueId == issueIdList[2]
    }

    def "issueStatistic"() {
        given: '查询参数'
        List<String> issueTypes = Arrays.asList("sub_task")

        when: '分页过滤查询issue列表提供给测试模块用'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issues/test_component/statistic?type={type}', issueTypes, List, projectId, type)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<PieChartDTO> pieChartDTOList = entity.body

        expect: '设置期望值'
        pieChartDTOList.size() > 0 == expectedCount

        where: '不同issue类型返回值与期望值对比'
        type        | expectedCount
        "version"   | true
        "component" | true
        "label"     | true

    }

    def "listIssueWithoutSubDetail"() {
        given: '查询参数'
        SearchDTO searchDTO = new SearchDTO()

        when: '分页过滤查询issue列表提供给测试模块用'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issues/test_component/no_sub_detail', searchDTO, Page, projectId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<IssueComponentDetailDTO> issueComponentDetailDTOList = entity.body.content

        expect: '设置期望值'
        issueComponentDetailDTOList.size() > 0

    }

    def 'listIssuesByProjectId'() {
        given:
        def type = 'sprint'
        def pageType = 'storymap'
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/issues/storymap/issues?type={type}&pageType={pageType}",
                HttpMethod.GET,
                new HttpEntity<>(),
                List.class,
                projectId,
                type,
                pageType)

        then:
        entity.statusCode.is2xxSuccessful()
        List<StoryMapIssueDTO> storyMapIssueDTOList = entity.body
        def count = 0
        for (StoryMapIssueDTO storyMapIssueDTO : storyMapIssueDTOList) {
            if (storyMapIssueDTO.sprintId != null) {
                count += 1
                storyMapIssueDTO.issueId == 1L
            }
        }
        count > 0
    }

//    def 'storymapMove'() {
//        given:
//        StoryMapMoveDTO storyMapMoveDTO = new StoryMapMoveDTO()
//        storyMapMoveDTO.issueId = issueIdList.get(0)
//        storyMapMoveDTO.objectVersionNumber = objectVersionNumber
//        storyMapMoveDTO.originEpicId = originEpicId
//        storyMapMoveDTO.epicId = epicId
//
//        when:
//        HttpEntity<StoryMapMoveDTO> storyMapMoveDTOHttpEntity = new HttpEntity<>(storyMapMoveDTO)
//        def entity = restTemplate.exchange("/v1/projects/{project_id}/issues/storymap/move",
//                HttpMethod.POST,
//                storyMapMoveDTOHttpEntity,
//                IssueDTO.class,
//                projectId)
//
//        then:
//        entity.statusCode.is2xxSuccessful()
//
//        expect:
//        entity.body.epicId == result
//
//        where:
//        originEpicId | epicId | objectVersionNumber | result
//        0L | 5L | 5L | 5L
//        5L | 0L | 6L | 0L
//        0L | 5L | 6L | null
//        0L | 0L | 7L | null
//    }

    def "updateIssueParentId"() {

        given:
        IssueCreateDTO issueCreateDTO = new IssueCreateDTO()
        issueCreateDTO.projectId = projectId
        issueCreateDTO.priorityCode = 'high'
        issueCreateDTO.reporterId = 1L
        issueCreateDTO.typeCode = 'issue_test'
        issueCreateDTO.summary = 'issue-test'
        IssueDTO issueDTO = issueService.createIssue(issueCreateDTO)
        issues.add(issueMapper.selectByPrimaryKey(issueDTO.getIssueId()))
        issueIdList.add(issueDTO.getIssueId())
        issueTestId = issueDTO.getIssueId()
        IssueUpdateParentIdDTO issueUpdateParentIdDTO = new IssueUpdateParentIdDTO()
        def subTaskIssue = issues.find {
            it.typeCode == "sub_task"
        }
        issueUpdateParentIdDTO.issueId = subTaskIssue.issueId
        issueUpdateParentIdDTO.objectVersionNumber = subTaskIssue.objectVersionNumber
        issueUpdateParentIdDTO.parentIssueId = Integer.MAX_VALUE
        def issueUpdateParentIdDTOHttpEntity = new HttpEntity<>(issueUpdateParentIdDTO)
        def resultFailure = restTemplate.exchange("/v1/projects/1/issues/update_parent",
                HttpMethod.POST,
                issueUpdateParentIdDTOHttpEntity,
                String.class,
                projectId
        )
        assert resultFailure.statusCode.is2xxSuccessful()
        JSONObject exceptionInfo = JSONObject.parse(resultFailure.body)
        assert exceptionInfo.get("failed").toString() == "true"
        assert exceptionInfo.get("code").toString() == "error.parentIssue.get"

        issueUpdateParentIdDTO.parentIssueId = issues.get(0).issueId
        issueUpdateParentIdDTO.issueId = Integer.MAX_VALUE
        issueUpdateParentIdDTOHttpEntity = new HttpEntity<>(issueUpdateParentIdDTO)
        resultFailure = restTemplate.exchange("/v1/projects/1/issues/update_parent",
                HttpMethod.POST,
                issueUpdateParentIdDTOHttpEntity,
                String.class,
                projectId
        )
        assert resultFailure.statusCode.is2xxSuccessful()
        exceptionInfo = JSONObject.parse(resultFailure.body)
        assert exceptionInfo.get("failed").toString() == "true"
        assert exceptionInfo.get("code").toString() == "error.issue.get"

        issueUpdateParentIdDTO.issueId = issues.find {
            it.typeCode != "sub_task"
        }.issueId
        issueUpdateParentIdDTOHttpEntity = new HttpEntity<>(issueUpdateParentIdDTO)
        resultFailure = restTemplate.exchange("/v1/projects/1/issues/update_parent",
                HttpMethod.POST,
                issueUpdateParentIdDTOHttpEntity,
                String.class,
                projectId
        )
        assert resultFailure.statusCode.is2xxSuccessful()
        exceptionInfo = JSONObject.parse(resultFailure.body)
        assert exceptionInfo.get("failed").toString() == "true"
        assert exceptionInfo.get("code").toString() == "error.typeCode.isSubtask"

        issueUpdateParentIdDTO.issueId = subTaskIssue.issueId
        issueUpdateParentIdDTO.parentIssueId = issues.find {
            it.typeCode == "sub_task" && it.issueId != issueUpdateParentIdDTO.issueId
        }.issueId
        issueUpdateParentIdDTOHttpEntity = new HttpEntity<>(issueUpdateParentIdDTO)
        resultFailure = restTemplate.exchange("/v1/projects/1/issues/update_parent",
                HttpMethod.POST,
                issueUpdateParentIdDTOHttpEntity,
                String.class,
                projectId
        )
        assert resultFailure.statusCode.is2xxSuccessful()
        exceptionInfo = JSONObject.parse(resultFailure.body)
        assert exceptionInfo.get("failed").toString() == "true"
        assert exceptionInfo.get("code").toString() == "error.parentIssue.isSubtask"

        issueUpdateParentIdDTO.parentIssueId = issues.find {
            it.typeCode == "issue_test"
        }.issueId
        issueUpdateParentIdDTOHttpEntity = new HttpEntity<>(issueUpdateParentIdDTO)
        resultFailure = restTemplate.exchange("/v1/projects/1/issues/update_parent",
                HttpMethod.POST,
                issueUpdateParentIdDTOHttpEntity,
                String.class,
                projectId
        )
        assert resultFailure.statusCode.is2xxSuccessful()
        exceptionInfo = JSONObject.parse(resultFailure.body)
        assert exceptionInfo.get("failed").toString() == "true"
        assert exceptionInfo.get("code").toString() == "error.parentIssue.isTest"

        issueUpdateParentIdDTO.parentIssueId = issues.find {
            it.typeCode != "issue_test" && it.typeCode != "sub_task"
        }.issueId

        when:
        issueUpdateParentIdDTOHttpEntity = new HttpEntity<>(issueUpdateParentIdDTO)
        def resultSuccess = restTemplate.exchange("/v1/projects/1/issues/update_parent",
                HttpMethod.POST,
                issueUpdateParentIdDTOHttpEntity,
                IssueUpdateParentIdDTO.class,
                projectId
        )

        then:
        resultSuccess.statusCode.is2xxSuccessful()
        resultSuccess.body.issueId == issueUpdateParentIdDTO.issueId
        resultSuccess.body.parentIssueId == issueUpdateParentIdDTO.parentIssueId

    }

    def 'querySwimLaneCode'() {

        when: '查询用户泳道接口请求'
        def entity = restTemplate.getForEntity("/v1/projects/{project_id}/issues/storymap/swim_lane", String, projectId)

        then:
        entity.statusCode.is2xxSuccessful()

        and:
        String code = entity.body

        expect:
        code == 'none'
    }

    def 'listIssueWithLinkedIssues'() {
        given:
        PageRequest pageRequest = new PageRequest()
        pageRequest.size = 10
        pageRequest.page = 0
        SearchDTO searchDTO = new SearchDTO()
        Map<String, Object> searchArgsMap = new HashMap<>()
        searchDTO.searchArgs = searchArgsMap
        Map<String, Object> advancedSearchArgsMap = new HashMap<>()
        List<String> typeCode = new ArrayList<>()
        typeCode.add("issue_test")
        advancedSearchArgsMap.put("typeCode", typeCode)
        searchDTO.advancedSearchArgs = advancedSearchArgsMap

        when:
        def entity = restTemplate.postForEntity("/v1/projects/{project_id}/issues/test_component/filter_linked?pageRequest={pageRequest}",
                searchDTO, Page.class, projectId, pageRequest)

        then:
        entity.statusCode.is2xxSuccessful()
    }

    def "cloneIssuesByVersionId"() {
        given: 'issueTestIds'
        def issueTestIds = [issueTestId]
        issueIdList = issueIdList.stream().distinct().collect(Collectors.toList())
        and: 'mockIssueMapper'
        def issueMapperMock = Mock(IssueMapper)
        issueService.setIssueMapper(issueMapperMock)
        List<IssueDetailDO> issueDetailDOList = new ArrayList<>()
        IssueDetailDO issueDetailDO = new IssueDetailDO()
        issueDetailDO.issueId = issueTestIds[0]
        issueDetailDO.summary = "XXX"
        issueDetailDO.projectId = projectId
        issueDetailDO.statusId = 1
        issueDetailDO.statusCode = 'todo'
        issueDetailDO.priorityCode = 'low'
        issueDetailDO.componentIssueRelDOList = new ArrayList<>()
        issueDetailDO.labelIssueRelDOList = new ArrayList<>()
        issueDetailDOList.add(issueDetailDO)

        when: '测试服务用，批量复制issue并生成版本信息'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issues/batch_clone_issue/{versionId}', issueTestIds, List, projectId, versionId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '判断mock交互并且设置返回值'
        1 * issueMapperMock.queryByIssueIds(projectId, issueTestIds) >> issueDetailDOList

        and: '设置值'
        List<Long> issueIds = entity.body
        issueService.setIssueMapper(issueMapper)
        List<IssueDO> issueDOList = issueMapper.selectAll()
        issueIdList.add(issueDOList.get(issueDOList.size() - 1).getIssueId())

        expect: '设置期望值'
        issueIds.size() == 1

    }

    def "batchIssueToVersionTest failed"() {
        given: '准备数据'
        def objectExpect = null
        HttpEntity<List<Long>> requestHttpEntity = new HttpEntity<List>(issueIdList)

        when: '发送请求'
        try {
            restTemplate.exchange('/v1/projects/{project_id}/issues/to_version_test/{versionId}',
                    HttpMethod.POST,
                    requestHttpEntity,
                    ResponseEntity,
                    projectId,
                    versionId)
        } catch (Exception e) {
            objectExpect = e
        }
        then: '设置值'
        objectExpect != null
    }

    def "batchIssueToVersionTest"() {
        given: '准备数据'
        ProductVersionDO productVersionDO = new ProductVersionDO()
        productVersionDO.name = '测试版本2'
        productVersionId = productVersionMapper.selectOne(productVersionDO).versionId

        SprintDO sprintDO = new SprintDO()
        sprintDO.projectId = 1L
        sprintDO.sprintName = 'sprint-test1111'
        sprintDO.statusCode = 'sprint_planning'
        sprintMapper.insert(sprintDO)
        testSprintId = sprintMapper.selectOne(sprintDO).sprintId
        IssueSprintRelDO issueSprintRelDO = new IssueSprintRelDO()
        issueSprintRelDO.sprintId = testSprintId
        issueSprintRelDO.issueId = resultId
        issueSprintRelDO.projectId = 1L
        issueSprintRelMapper.insert(issueSprintRelDO)

        IssueCreateDTO issueCreateDTO = new IssueCreateDTO()
        and: '设置issue属性'
        issueCreateDTO.typeCode = "issue_test"
        issueCreateDTO.projectId = projectId
        issueCreateDTO.description = "测试issue描述"
        issueCreateDTO.summary = "测试issue概要111111"
        issueCreateDTO.priorityCode = "hight"
        issueCreateDTO.assigneeId = 1
        issueCreateDTO.sprintId = testSprintId
        issueCreateDTO.componentIssueRelDTOList = null
        issueCreateDTO.labelIssueRelDTOList = null
        issueCreateDTO.versionIssueRelDTOList = null
        restTemplate.postForEntity('/v1/projects/{project_id}/issues', issueCreateDTO, IssueDTO, projectId)
        IssueDO issueDO = new IssueDO()
        issueDO.summary = "测试issue概要111111"
        and: "获取IssueId"
        resultId = issueMapper.selectOne(issueDO).issueId
        issueIdList.add(resultId)
        List<Long> longList = new ArrayList<>()
        longList.add(resultId)
        HttpEntity<List<Long>> requestHttpEntity = new HttpEntity<List>(longList)

        when: '发送请求'
        def entity = restTemplate.exchange('/v1/projects/{project_id}/issues/to_version_test/{versionId}',
                HttpMethod.POST,
                requestHttpEntity,
                ResponseEntity,
                projectId,
                versionId)
        then: '设置值'
        entity.statusCode.is2xxSuccessful()

        and:
        List<IssueSearchDO> issueSearchDOList = issueMapper.queryIssueByIssueIds(projectId, longList)

        expect: '设置期望值'
        issueSearchDOList.get(0).versionIds.get(0) == versionId
    }

    def "batchDeleteIssues failed"() {
        given: '准备数据'
        def objectExpect = null
        HttpEntity<List<Long>> requestHttpEntity = new HttpEntity<List>(issueIdList)

        when: '发送请求'
        try {
            restTemplate.exchange('/v1/projects/{project_id}/issues/to_version_test',
                    HttpMethod.DELETE,
                    requestHttpEntity,
                    ResponseEntity,
                    projectId)
        } catch (Exception e) {
            objectExpect = e
        }
        then: '设置值'
        objectExpect != null
    }


    def 'storymapMove'() {
        given:
        StoryMapMoveDTO storyMapMoveDTO = new StoryMapMoveDTO()
        storyMapMoveDTO.versionId = versionIds
        storyMapMoveDTO.sprintId = sprintIds
        storyMapMoveDTO.epicId = issueIdList[2]
        List<Long> epicIssueIds = new ArrayList<>()
        epicIssueIds.add(resultId)
        storyMapMoveDTO.epicIssueIds = epicIssueIds
        storyMapMoveDTO.rankIndex = true
        List<Long> sprintIssueIds = new ArrayList<>()
        sprintIssueIds.add(resultId)
        storyMapMoveDTO.sprintIssueIds = sprintIssueIds
        List<Long> versionIssueIds = new ArrayList<>()
        versionIssueIds.add(resultId)
        storyMapMoveDTO.versionIssueIds = versionIssueIds
        storyMapMoveDTO.before = true
        List<Long> issueIds = new ArrayList<Long>()
        issueIds.add(resultId)
        storyMapMoveDTO.issueIds = issueIds

        when:
        def entity = restTemplate.postForEntity("/v1/projects/{project_id}/issues/storymap/move",
                storyMapMoveDTO, null, projectId)
        then:
        entity.statusCode.is2xxSuccessful()
        expect:
        entity.body == expectObject
        where:
        versionIds       | sprintIds    | expectObject
        null             | testSprintId | null
        productVersionId | null         | null

    }

    def "batchDeleteIssues"() {
        given: '准备数据'
        List<Long> longList = new ArrayList<>()
        longList.add(resultId)
        HttpEntity<List<Long>> requestHttpEntity = new HttpEntity<List>(longList)
        and: "准备数据"
        def issueMapperMock = Mock(IssueMapper)
        issueService.setIssueMapper(issueMapperMock)
        issueMapperMock.queryIssueIdsIsTest(*_) >> longList.size()
        issueMapperMock.queryIssueSubListByIssueIds(*_) >> new ArrayList<Long>()
        issueMapperMock.batchDeleteIssues(*_) >> null

        when: '发送请求'
        def entity = restTemplate.exchange('/v1/projects/{project_id}/issues/to_version_test',
                HttpMethod.DELETE,
                requestHttpEntity,
                ResponseEntity,
                projectId)
        then: '设置值'
        entity.statusCode.is2xxSuccessful()
        and:
        issueService.setIssueMapper(issueMapper)
    }

    def 'queryIssueTestGroupByProject'() {
        when: '测试服务用，issue按照项目分组借口'
        def entity = restTemplate.getForEntity("/v1/projects/{project_id}/issues/list_issues_by_project", List.class, projectId)

        then:
        entity.statusCode.is2xxSuccessful()

        and:
        List<IssueProjectDTO> result = entity.body

        expect:
        result.size() == 1
    }

    def "deleteIssue"() {
        when: '执行方法'
        restTemplate.delete('/v1/projects/{project_id}/issues/{issueId}', projectId, issueId)

        then: '返回值'
        def result = issueMapper.selectByPrimaryKey(issueId as Long)

        expect: '期望值'
        result == null

        where: '判断issue是否删除'
        issueId << issueIdList


    }

    def "deleteData"() {
        given: '刪除模块&版本'
        IssueComponentDO issueComponentDO = new IssueComponentDO()
        issueComponentDO.name = '测试模块2'
        ProductVersionDO productVersionDO = new ProductVersionDO()
        productVersionDO.name = '测试版本'
        ProductVersionDO productVersionDO1 = new ProductVersionDO()
        productVersionDO1.name = '测试版本2'

        and: '查询模块DO&版本DO'
        IssueComponentDO queryComponent = new IssueComponentDO()
        queryComponent.name = '测试模块2'
        ProductVersionDO queryVersion = new ProductVersionDO()
        queryVersion.name = '测试版本'
        ProductVersionDO queryVersion1 = new ProductVersionDO()
        queryVersion1.name = '测试版本'

        when: '执行方法'
        issueComponentMapper.delete(issueComponentDO)
        productVersionMapper.delete(productVersionDO)
        productVersionMapper.delete(productVersionDO1)
        sprintMapper.deleteByPrimaryKey(testSprintId)

        then: '验证删除'
        issueComponentMapper.selectOne(queryComponent) == null
        productVersionMapper.selectOne(queryVersion) == null
        productVersionMapper.selectOne(queryVersion1) == null
        sprintMapper.selectByPrimaryKey(testSprintId) == null
    }
}
