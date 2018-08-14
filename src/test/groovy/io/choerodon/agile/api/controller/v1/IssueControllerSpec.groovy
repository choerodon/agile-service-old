package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.api.dto.CopyConditionDTO
import io.choerodon.agile.api.dto.EpicSequenceDTO
import io.choerodon.agile.api.dto.IssueCreationNumDTO
import io.choerodon.agile.api.dto.IssueEpicDTO
import io.choerodon.agile.api.dto.IssueInfoDTO
import io.choerodon.agile.api.dto.IssueTransformSubTask
import io.choerodon.agile.api.dto.IssueUpdateTypeDTO
import io.choerodon.agile.api.dto.MoveIssueDTO
import io.choerodon.agile.api.dto.PieChartDTO
import io.choerodon.agile.api.dto.ProjectDTO
import io.choerodon.agile.api.dto.StoryMapIssueDTO
import com.alibaba.fastjson.JSONObject
import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.ComponentIssueRelDTO
import io.choerodon.agile.api.dto.EpicDataDTO
import io.choerodon.agile.api.dto.IssueCreateDTO
import io.choerodon.agile.api.dto.IssueDTO
import io.choerodon.agile.api.dto.IssueLinkDTO
import io.choerodon.agile.api.dto.IssueListDTO
import io.choerodon.agile.api.dto.IssueNumDTO
import io.choerodon.agile.api.dto.IssueSearchDTO
import io.choerodon.agile.api.dto.IssueSubCreateDTO
import io.choerodon.agile.api.dto.IssueSubDTO
import io.choerodon.agile.api.dto.LabelIssueRelDTO
import io.choerodon.agile.api.dto.SearchDTO
import io.choerodon.agile.api.dto.VersionIssueRelDTO
import io.choerodon.agile.api.eventhandler.AgileEventHandler
import io.choerodon.agile.app.service.IssueService
import io.choerodon.agile.domain.agile.repository.UserRepository
import io.choerodon.agile.infra.dataobject.IssueComponentDetailDTO
import io.choerodon.agile.infra.dataobject.IssueDO
import io.choerodon.agile.infra.dataobject.UserDO
import io.choerodon.agile.infra.dataobject.UserMessageDO
import io.choerodon.agile.infra.feign.UserFeignClient
import io.choerodon.agile.infra.mapper.DataLogMapper
import io.choerodon.agile.infra.mapper.IssueMapper
import io.choerodon.agile.infra.mapper.IssueSprintRelMapper
import io.choerodon.agile.infra.mapper.ProjectInfoMapper
import io.choerodon.agile.infra.mapper.SprintMapper
import io.choerodon.asgard.saga.feign.SagaClient
import io.choerodon.core.domain.Page
import io.choerodon.event.producer.execute.EventProducerTemplate
import org.apache.http.HttpHeaders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.test.context.ActiveProfiles
import org.springframework.util.MultiValueMap
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
    def issueIdList = []
    @Shared
    def sprintId = 1
    @Shared
    def versionId = 1
    @Shared
    def issueObjectVersionNumberList = []

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

    def '创建issue'() {
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
        issueIdList << entity.body.issueId
        issueObjectVersionNumberList << entity.body.objectVersionNumber

        expect: '设置期望值'
        entity.body.typeCode == expectedTypeCode

        where: '不同issue类型返回值与期望值对比'
        typeCode     | expectedTypeCode
        "story"      | "story"
        "task"       | "task"
        "issue_epic" | "issue_epic"
    }

    def '创建issue子任务'() {
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
        issueIdList << entity.body.issueId
        issueObjectVersionNumberList << entity.body.objectVersionNumber
        expect: '设置期望值'
        entity.body.typeCode == "sub_task"
        entity.body.parentIssueId == issueIdList[0]
        entity.body.description == "测试issue描述"
        entity.body.summary == "测试issue概要"
        entity.body.priorityCode == "hight"

    }

    def '更新issue'() {
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

    def '查询单个issue'() {
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

    def '查询单个子任务'() {
        when: '向开始查询单个issue子任务的接口发请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/issues/sub_issue/{issueId}', IssueSubDTO, projectId, issueIdList[3])

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        expect: '设置期望值'
        entity.body.typeCode == "sub_task"

    }

    def '分页过滤查询issue列表(不包含子任务,不含测试任务)'() {
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

    def '分页搜索查询issue列表(包含子任务)'() {
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

    def '分页搜索查询issue列表'() {
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

    def '查询epic'() {
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

    def 'issue批量加入版本'() {
        when: '向issue批量加入版本接口发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issues/to_version/{versionId}',
                issueIdList, List, projectId, 1)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<IssueSearchDTO> issueSearchDTOList = entity.body
        expect: '设置期望值'
        issueSearchDTOList.size() > 0
    }

    def 'issue批量加入epic'() {
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

    def "issue批量加入冲刺"() {
        given: '给定一个issue数组列表'
        List<Long> issueIds = Arrays.asList(issueIdList[0]) as List<Long>
        and: '移动issue的对象'
        MoveIssueDTO moveIssueDTO = new MoveIssueDTO()
        moveIssueDTO.before = true
        moveIssueDTO.issueIds = issueIds
        when: '向issue批量加入冲刺接口发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issues/to_sprint/{sprintId}',
                moveIssueDTO, List, projectId, sprintId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<IssueSearchDTO> issueSearchDTOList = entity.body
        expect: '设置期望值'
        issueSearchDTOList.size() > 0

    }

    def "查询当前项目下的epic，提供给列表下拉"() {
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

    def "更改issue类型"() {
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

    def "根据issue类型(type_code)查询issue列表(分页)"() {
        when: '向根据issue类型(type_code)查询issue列表(分页)接口发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issues/type_code/{typeCode}',
                null, Page, projectId, "story")

        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        print(entity.body.content ? entity.body.content.toString() : null)

        expect: '设置期望值'
        entity.body.content.size() > 0

    }

    def "导出issue列表"() {
        given: '给定查询dto'
        SearchDTO searchDTO = new SearchDTO()
        searchDTO.content = '测试'

        and: 'mock userFeignClient'
        ProjectDTO projectDTO = new ProjectDTO()
        projectDTO.name = '测试项目'
        projectDTO.code = '测试项目Code'
        userRepository.queryProject(_) >> projectDTO

        when: '向根据issue类型(type_code)查询issue列表(分页)接口发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issues/export',
                searchDTO, null, projectId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        expect: '期待值比较'
        entity.headers.get("Content-Length").size() > 0

    }

    def "导出issue详情"() {
        given: 'mock userFeignClient'
        ProjectDTO projectDTO = new ProjectDTO()
        projectDTO.name = '测试项目'
        projectDTO.code = '测试项目Code'
        userRepository.queryProject(_) >> projectDTO

        when: '向根据issue类型(type_code)查询issue列表(分页)接口发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issues/export/{issueId}',
                null, null, projectId, issueIdList[0])

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        expect: '期待值比较'
        entity.headers.get("Content-Length").size() > 0

    }

    def "复制一个issue"() {
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
        issueIdList << entity.body.issueId
        issueObjectVersionNumberList << entity.body.objectVersionNumber

        expect: '设置期望值'
        entity.body.typeCode == expectedTypeCode

        where: '不同issue类型返回值与期望值对比'
        issueId        | expectedTypeCode
        issueIdList[0] | "story"
        issueIdList[1] | "task"
        issueIdList[2] | "issue_epic"

    }

    def "任务转换为子任务"() {
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
        issueIdList << entity.body.issueId
        issueObjectVersionNumberList << entity.body.objectVersionNumber

        expect: '设置期望值'
        entity.body.typeCode == expectedTypeCode

        where: '不同issue类型返回值与期望值对比'
        i | expectedTypeCode
        5 | "sub_task"
        6 | "sub_task"

    }

    def "根据issue ids查询issue相关信息"() {
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

    def "分页过滤查询issue列表提供给测试模块用"() {
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

    def "根据时间段查询问题类型的数量"() {
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
        1        | false
        3        | false
        4        | false
        -1       | true
        -3       | true
        -4       | true


    }

    def "拖动epic位置"() {
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

    def "统计issue相关信息（测试模块用）"() {
        given: '查询参数'
        List<String> issueTypes = Arrays.asList("sub_task")

        when: '分页过滤查询issue列表提供给测试模块用'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issues/test_component/statistic?type={type}', issueTypes, List, projectId, type)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<PieChartDTO> pieChartDTOList = entity.body

        expect: '设置期望值'
        pieChartDTOList.size() == expectedCount

        where: '不同issue类型返回值与期望值对比'
        type        | expectedCount
        "version"   | 3
        "component" | 3
        "label"     | 2

    }

    def "分页过滤查询issue列表(不包含子任务，包含详情),测试模块用"() {
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

    def '故事地图查询issues,type:sprint, version, none, pageType:storymap,backlog'() {
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

    def "删除issue"() {
        when: '执行方法'
        restTemplate.delete('/v1/projects/{project_id}/issues/{issueId}', projectId, issueId)

        then: '返回值'
        def result = issueMapper.selectByPrimaryKey(issueId)

        expect: '期望值'
        result == null

        where: '判断issue是否删除'
        issueId << issueIdList


    }

}
