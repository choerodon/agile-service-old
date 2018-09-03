package io.choerodon.agile.api.controller.v1

import com.alibaba.fastjson.JSONObject
import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.CumulativeFlowDiagramDTO
import io.choerodon.agile.api.dto.CumulativeFlowFilterDTO
import io.choerodon.agile.api.dto.IssueCreateDTO
import io.choerodon.agile.api.dto.IssueDTO
import io.choerodon.agile.api.dto.IssueListDTO
import io.choerodon.agile.api.dto.PieChartDTO
import io.choerodon.agile.api.dto.ReportIssueDTO
import io.choerodon.agile.api.dto.SprintDetailDTO
import io.choerodon.agile.api.dto.SprintUpdateDTO
import io.choerodon.agile.api.dto.VelocitySprintDTO
import io.choerodon.agile.app.service.IssueService
import io.choerodon.agile.app.service.SprintService
import io.choerodon.agile.domain.agile.repository.UserRepository
import io.choerodon.agile.infra.common.utils.MybatisFunctionTestUtil
import io.choerodon.agile.infra.dataobject.GroupDataChartDO
import io.choerodon.agile.infra.dataobject.GroupDataChartListDO
import io.choerodon.agile.infra.dataobject.IssueDO
import io.choerodon.agile.infra.dataobject.SprintDO
import io.choerodon.agile.infra.dataobject.UserDO
import io.choerodon.agile.infra.dataobject.UserMessageDO
import io.choerodon.agile.infra.mapper.BoardColumnMapper
import io.choerodon.agile.infra.mapper.IssueMapper
import io.choerodon.agile.infra.mapper.SprintMapper
import io.choerodon.agile.infra.mapper.VersionIssueRelMapper
import io.choerodon.core.domain.Page
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

    @Autowired
    private BoardColumnMapper boardColumnMapper

    @Autowired
    private SprintMapper sprintMapper

    @Autowired
    private IssueMapper issueMapper

    @Autowired
    private VersionIssueRelMapper versionIssueRelMapper

    @Shared
    def projectId = 1

    @Shared
    def epicId = 1

    @Shared
    def boardId = 1

    @Shared
    def versionId = 1

    @Shared
    def sprintId = null

    @Shared
    def issueId = null

    @Shared
    def startDate = MybatisFunctionTestUtil.dataSubFunction(new Date(), -1)

    @Shared
    def endDate = MybatisFunctionTestUtil.dataSubFunction(startDate, -10)

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
        sprintUpdateDTO.startDate = startDate
        sprintUpdateDTO.endDate = endDate

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

    def 'queryBurnDownCoordinate'() {
        when: '向开始查询燃尽图坐标信息的接口发请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/reports/{sprintId}/burn_down_report/coordinate?type={type}',
                JSONObject, projectId, sprintId, type)

        then: '接口是否请求成功'
        entity.statusCode.is2xxSuccessful()

        and: '设置返回值值'
        JSONObject object = entity.body
        TreeMap<String, Integer> report = object.get("coordinate") as TreeMap<String, Integer>

        expect: '验证期望值'
        report.size() == expectSize
        object.get("expectCount") == exceptCount

        where: '设置期望值'
        type                     || expectSize | exceptCount
        'storyPoints'            || 1          | 0
        'remainingEstimatedTime' || 1          | 0
        'issueCount'             || 1          | 1

    }

    def 'queryCumulativeFlowDiagram'() {
        given: '查询参数'
        CumulativeFlowFilterDTO cumulativeFlowFilterDTO = new CumulativeFlowFilterDTO()
        cumulativeFlowFilterDTO.startDate = startDate
        cumulativeFlowFilterDTO.endDate = endDate
        cumulativeFlowFilterDTO.boardId = 1

        and: '加入列'
        cumulativeFlowFilterDTO.columnIds = boardColumnMapper.queryColumnIdsByBoardId(boardId, projectId)

        when: '向开始查看项目累积流量图的接口发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/reports/cumulative_flow_diagram',
                cumulativeFlowFilterDTO, List, projectId)

        then: '接口是否请求成功'
        entity.statusCode.is2xxSuccessful()

        and: '设置返回值值'
        List<CumulativeFlowDiagramDTO> result = entity.body

        expect: '验证期望值'
        result.size() == 3

    }

    def 'queryIssueByOptions'() {
        when: '向根据状态查版本下issue列表的接口发请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/reports/{versionId}/issues?' +
                'status={status}&type={type}', Page, projectId, versionId, status, type)

        then: '接口是否请求成功'
        entity.statusCode.is2xxSuccessful()

        and: '设置返回值值'
        List<IssueListDTO> result = entity.body.content

        expect: '验证期望值'
        result.size() == expectSize

        where: '设置期望值'
        status                  | type                     || expectSize
        'done'                  | 'storyPoints'            || 0
        'done'                  | 'remainingEstimatedTime' || 0
        'done'                  | 'issueCount'             || 0
        'unfinished'            | 'storyPoints'            || 1
        'unfinished'            | 'remainingEstimatedTime' || 0
        'unfinished'            | 'issueCount'             || 1
        'unfinishedUnestimated' | 'storyPoints'            || 0
        'unfinishedUnestimated' | 'remainingEstimatedTime' || 1
        'unfinishedUnestimated' | 'issueCount'             || 0

    }

    def 'queryVersionLineChart'() {
        when: '向版本报告图信息的接口发请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/reports/{versionId}?type={type}', Map, projectId, versionId, type)

        then: '接口是否请求成功'
        entity.statusCode.is2xxSuccessful()

        and: '设置返回值值'
        Map<String, Object> result = entity.body
        Object productVersionDO = result.get("version")
        List<Object> versionReportDTOList = result.get("versionReport") as List<Object>

        expect: '验证期望值'
        productVersionDO != null
        versionReportDTOList.size() == expectSize

        where: '设置期望值'
        type                     || expectSize
        'storyPoints'            || 1
        'remainingEstimatedTime' || 1
        'issueCount'             || 1

    }

    def 'queryVelocityChart'() {
        when: '向速度图的接口发请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/reports/velocity_chart?type={type}', List, projectId, type)

        then: '接口是否请求成功'
        entity.statusCode.is2xxSuccessful()

        and: '设置返回值值'
        List<VelocitySprintDTO> velocitySprintDTOList = entity.body

        expect: '验证期望值'
        velocitySprintDTOList.size() == expectSize

        where: '设置期望值'
        type          || expectSize
        'issue_count' || 2
        'story_point' || 2
        'remain_time' || 2

    }

    def 'queryPieChart'() {
        when: '向查询饼图的接口发请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/reports/pie_chart?fieldName={fieldName}', List, projectId, fieldName)

        then: '接口是否请求成功'
        entity.statusCode.is2xxSuccessful()

        and: '设置返回值值'
        List<PieChartDTO> pieChartDTOList = entity.body

        expect: '验证期望值'
        pieChartDTOList.size() == expectSize

        where: '设置期望值'
        fieldName      || expectSize
        'assignee'     || 1
        'component'    || 1
        'typeCode'     || 2
        'version'      || 2
        'priorityCode' || 2
        'statusCode'   || 1
        'sprint'       || 3
        'epic'         || 1
        'resolution'   || 1

    }

    def 'queryEpicChart'() {
        when: '向史诗图的接口发请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/reports/epic_chart?epicId={epicId}&type={type}', List, projectId, epicId, type)

        then: '接口是否请求成功'
        entity.statusCode.is2xxSuccessful()

        and: '设置返回值值'
        List<GroupDataChartDO> groupDataChartDOList = entity.body

        expect: '验证期望值'
        groupDataChartDOList.size() == expectSize

        where: '设置期望值'
        type          || expectSize
        'issue_count' || 0
        'story_point' || 0
        'remain_time' || 0
    }

    def 'epic_issue_list'() {
        when: '向史诗图问题列表的接口发请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/reports/epic_issue_list?epicId={epicId}', List, projectId, epicId)

        then: '接口是否请求成功'
        entity.statusCode.is2xxSuccessful()

        and: '设置返回值值'
        List<GroupDataChartListDO> groupDataChartListDOList = entity.body

        expect: '验证期望值'
        groupDataChartListDOList.size() == 1
    }

    def 'version_chart'() {
        when: '向版本图重构api的接口发请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/reports/version_chart?versionId={versionId}&type={type}', List, projectId, versionId, type)

        then: '接口是否请求成功'
        entity.statusCode.is2xxSuccessful()

        and: '设置返回值值'
        List<GroupDataChartDO> groupDataChartDOList = entity.body

        expect: '验证期望值'
        groupDataChartDOList.size() == expectSize

        where: '设置期望值'
        type          || expectSize
        'issue_count' || 0
        'story_point' || 0
        'remain_time' || 0
    }

    def 'version_issue_list'() {
        when: '版本图问题列表重构api'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/reports/version_issue_list?versionId={versionId}', List, projectId, versionId)

        then: '接口是否请求成功'
        entity.statusCode.is2xxSuccessful()

        and: '设置返回值值'
        List<GroupDataChartListDO> groupDataChartListDOList = entity.body

        expect: '验证期望值'
        groupDataChartListDOList.size() == 1

    }

    def 'deleteData'() {
        given: '删除数据DO'
        SprintDO sprintDO = new SprintDO()
        sprintDO.sprintId = sprintId
        sprintDO.projectId = projectId
        IssueDO issueDO = new IssueDO()
        issueDO.issueId = issueId
        issueDO.projectId = projectId

        when: '删除数据'
        sprintMapper.delete(sprintDO)
        issueService.deleteIssue(projectId, issueId)

        then: '验证'
        sprintMapper.selectByPrimaryKey(sprintDO) == null
        issueMapper.selectByPrimaryKey(issueDO) == null

    }

}
