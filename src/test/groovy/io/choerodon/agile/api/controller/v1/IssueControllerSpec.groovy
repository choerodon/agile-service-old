package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.api.dto.StoryMapIssueDTO
import com.alibaba.fastjson.JSONObject
import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.ComponentIssueRelDTO
import io.choerodon.agile.api.dto.EpicDataDTO
import io.choerodon.agile.api.dto.IssueComponentDTO
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
import io.choerodon.agile.api.dto.SprintDetailDTO
import io.choerodon.agile.api.dto.VersionIssueRelDTO
import io.choerodon.agile.api.eventhandler.AgileEventHandler
import io.choerodon.agile.app.service.IssueService
import io.choerodon.agile.domain.agile.repository.UserRepository
import io.choerodon.agile.infra.dataobject.IssueDO
import io.choerodon.agile.infra.dataobject.UserDO
import io.choerodon.agile.infra.dataobject.UserMessageDO
import io.choerodon.agile.infra.mapper.IssueMapper
import io.choerodon.agile.infra.mapper.IssueSprintRelMapper
import io.choerodon.agile.infra.mapper.ProjectInfoMapper
import io.choerodon.agile.infra.mapper.SprintMapper
import io.choerodon.asgard.saga.feign.SagaClient
import io.choerodon.core.domain.Page
import io.choerodon.event.producer.execute.EventProducerTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
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
    def issueIdList = []
    @Shared
    def sprintId = null
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

    def '创建模块api测试'() {
        given: '给一个创建模块的DTO'
        IssueComponentDTO issueComponentDTO = new IssueComponentDTO()

        and: '设置模块属性'
        issueComponentDTO.projectId = projectId
        issueComponentDTO.name = "测试模块"
        issueComponentDTO.description = "测试模块描述"
        issueComponentDTO.managerId = 1
        issueComponentDTO.defaultAssigneeRole = "模块负责人"

        when: '向开始创建issue的接口发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/component', issueComponentDTO, IssueComponentDTO, projectId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        println "创建模块的执行结果：${entity.body?.toString()}"

        and: '设置模块值'
        if (entity.body.componentId) {
            componentId = entity.body.componentId
        }

        expect: '验证结果'
        entity.body.description == "测试模块描述"
        entity.body.name == "测试模块"
        entity.body.managerId == 1
        entity.body.defaultAssigneeRole == "模块负责人"

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
        issueCreateDTO.assigneeId = 1

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
        labelIssueRelDTO1.labelName = "测试标签1"
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
        versionIssueRelDTO1.name = "测试版本2"
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
        issueListDTOList.size() != 0

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
        issueNumDTOList.size() != 0
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
        issueNumDTOList.size() != 0
    }

    def '查询epic'() {
        when: '向开始查询查询epic的接口发请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/issues/epics',
                List, projectId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<EpicDataDTO> epicDataDTOList = entity.body
        expect: '设置期望值'
        epicDataDTOList.size() != 0
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
        issueSearchDTOList.size() != 0
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
        issueSearchDTOList.size() != 0
    }

    def '创建冲刺'() {
        when: '向创建冲刺的接口发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/sprint',
                null, SprintDetailDTO, projectId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        SprintDetailDTO issueDetailDTO = entity.body
        if (issueDetailDTO.sprintId) {
            sprintId = issueDetailDTO.sprintId
        }
        expect: '设置期望值'
        issueDetailDTO.projectId == projectId
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
        count == 1
    }

//    def "issue批量加入冲刺"() {
//        given: '给定一个issue数组列表'
//        List<Long> issueIds = issueIdList.subList(0,2)
//        and: '移动issue的对象'
//        MoveIssueDTO moveIssueDTO = new MoveIssueDTO()
//        moveIssueDTO.before = true
//        moveIssueDTO.issueIds = issueIds
//        when: '向issue批量加入冲刺接口发请求'
//        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issues/to_sprint/{sprintId}',
//                moveIssueDTO, List, projectId, sprintId)
//
//        then: '返回值'
//        entity.statusCode.is2xxSuccessful()
//
//        and: '设置值'
//        List<IssueSearchDTO> issueSearchDTOList = entity.body
//        expect: '设置期望值'
//        issueSearchDTOList.size() == 2
//
//    }

    def "删除issue"() {
        when: '执行方法'
        restTemplate.delete('/v1/projects/{project_id}/issues/{issueId}', projectId, issueId)

        then: '返回值'
        issueMapper.selectByPrimaryKey(issueId) == result

        where: '判断issue是否删除'
        issueId        | result
        issueIdList[0] | null
        issueIdList[1] | null
        issueIdList[2] | null
        issueIdList[3] | null

    }


}
