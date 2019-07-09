package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.vo.IssueStatusDTO
import io.choerodon.agile.api.vo.StatusInfoDTO
import io.choerodon.agile.api.vo.StatusMoveDTO
import io.choerodon.agile.infra.dataobject.ColumnStatusRelDTO
import io.choerodon.agile.infra.dataobject.IssueStatusDO
import io.choerodon.agile.infra.feign.IssueFeignClient
import io.choerodon.agile.infra.mapper.ColumnStatusRelMapper
import io.choerodon.agile.infra.mapper.IssueStatusMapper
import org.mockito.Matchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
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

/**
 * Created by HuangFuqiang@choerodon.io on 2018/8/7.
 * Email: fuqianghuang01@gmail.com
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class IssueStatusControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    IssueStatusMapper issueStatusMapper

    @Autowired
    ColumnStatusRelMapper columnStatusRelMapper

    @Shared
    Long projectId = 1L

    @Shared
    String statusName = "status_test"

    @Shared
    String statusNameChange = "status_test_change"

    @Shared
    String categoryCode = "todo"

    @Shared
    Long statusId

    @Autowired
    private IssueFeignClient issueFeignClient

    @Shared
    Long boardId = 1L

    def setup() {
        StatusInfoDTO statusInfoDTO = new StatusInfoDTO()
        statusInfoDTO.setId(10001L)
        statusInfoDTO.setName(statusName)
        Mockito.when(issueFeignClient.createStatusForAgile(Matchers.anyLong(), Matchers.anyString(), Matchers.any(StatusInfoDTO.class))).thenReturn(new ResponseEntity<>(statusInfoDTO, HttpStatus.OK));
    }

    def 'createStatus'() {
        given:
        IssueStatusDTO issueStatusDTO = new IssueStatusDTO()
        issueStatusDTO.projectId = projectId
        issueStatusDTO.name = statusName
        issueStatusDTO.categoryCode = categoryCode
        issueStatusDTO.enable = true

        when:
        HttpEntity<IssueStatusDTO> issueStatusDTOHttpEntity = new HttpEntity<>(issueStatusDTO)
        def entity = restTemplate.exchange("/v1/projects/{project_id}/issue_status?applyType=agile",
                HttpMethod.POST,
                issueStatusDTOHttpEntity,
                IssueStatusDTO.class,
                projectId)

        then:
        entity.statusCode.is2xxSuccessful()
        entity.body != null
        entity.body.getName() == statusName

    }

    def 'listUnCorrespondStatus'() {

        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/issue_status/list_by_options?boardId={boardId}&applyType=agile",
                HttpMethod.GET,
                new HttpEntity<>(),
                List.class,
                projectId,
                boardId
        )

        then:
        entity.statusCode.is2xxSuccessful()
        entity.body.size() == 0
    }

    def 'moveStatusToColumn'() {
        given:
        IssueStatusDO issueStatusDO = new IssueStatusDO()
        issueStatusDO.setName(statusName)
        issueStatusDO.projectId = projectId
        statusId = issueStatusMapper.selectOne(issueStatusDO).id
        StatusMoveDTO statusMoveDTO = new StatusMoveDTO()
        statusMoveDTO.columnId = 1L
        statusMoveDTO.originColumnId = 0L
        statusMoveDTO.position = 1
        statusMoveDTO.statusObjectVersionNumber = 1L

        and:
        ColumnStatusRelDTO columnStatusRelDO = new ColumnStatusRelDTO()
        columnStatusRelDO.projectId = projectId
        columnStatusRelDO.columnId = 1L
        columnStatusRelDO.statusId = statusId
        columnStatusRelDO.position = 1

        when:
        HttpEntity<StatusMoveDTO> statusMoveDTOHttpEntity = new HttpEntity<>(statusMoveDTO)
        def entity = restTemplate.exchange("/v1/projects/{project_id}/issue_status/{id}/move_to_column",
                HttpMethod.POST,
                statusMoveDTOHttpEntity,
                IssueStatusDTO.class,
                projectId,
                statusId
        )

        then:
        entity.statusCode.is2xxSuccessful()
        ColumnStatusRelDTO result = columnStatusRelMapper.selectOne(columnStatusRelDO)
        result != null

    }

    def 'moveStatusToUnCorrespond'() {
        given:
        StatusMoveDTO statusMoveDTO = new StatusMoveDTO()
        statusMoveDTO.columnId = 1L

        and:
        ColumnStatusRelDTO columnStatusRelDO = new ColumnStatusRelDTO()
        columnStatusRelDO.projectId = projectId
        columnStatusRelDO.columnId = 1L
        columnStatusRelDO.statusId = statusId
        columnStatusRelDO.position = 1

        when:
        HttpEntity<StatusMoveDTO> statusMoveDTOHttpEntity = new HttpEntity<>(statusMoveDTO)
        def entity = restTemplate.exchange("/v1/projects/{project_id}/issue_status/{id}/move_to_uncorrespond",
                HttpMethod.POST,
                statusMoveDTOHttpEntity,
                IssueStatusDTO.class,
                projectId,
                statusId
        )

        then:
        entity.statusCode.is2xxSuccessful()
        ColumnStatusRelDTO result = columnStatusRelMapper.selectOne(columnStatusRelDO)
        result == null

    }

    def 'updateStatus'() {
        given:
        IssueStatusDTO issueStatusDTO = new IssueStatusDTO()
        issueStatusDTO.projectId = projectId
        issueStatusDTO.id = statusId
        issueStatusDTO.completed = true
        issueStatusDTO.objectVersionNumber = 1L
        issueStatusDTO.setStatusId(issueStatusMapper.selectByPrimaryKey(statusId).getStatusId())

        when:
        HttpEntity<IssueStatusDTO> issueStatusDTOHttpEntity = new HttpEntity<>(issueStatusDTO)
        def entity = restTemplate.exchange("/v1/projects/{project_id}/issue_status/{id}",
                HttpMethod.PUT,
                issueStatusDTOHttpEntity,
                IssueStatusDTO.class,
                projectId,
                statusId)

        then:
        entity.statusCode.is2xxSuccessful()
        entity.body.completed
    }

    def 'listStatusByProjectId'() {

        when: '查询项目下的issue状态'
        def entity = restTemplate.getForEntity("/v1/projects/{project_id}/issue_status/list",
                List,
                projectId)

        then:
        entity.statusCode.is2xxSuccessful()
        expect:
        entity.body.size() == 4
    }

    def 'createStatusByStateMachine'() {
        given: '准备数据'
        IssueStatusDTO issueStatusDTO = new IssueStatusDTO()
        issueStatusDTO.statusId = statusId
        issueStatusDTO.projectId = projectId
        issueStatusDTO.objectVersionNumber = 2L
        issueStatusDTO.name = "other"
        HttpEntity<IssueStatusDTO> issueStatusDTOHttpEntity = new HttpEntity<>(issueStatusDTO)
        when: '状态机服务回写状态信息'
        def entity = restTemplate.exchange("/v1/projects/{project_id}/issue_status/back_update",
                HttpMethod.POST,
                issueStatusDTOHttpEntity,
                IssueStatusDTO.class,
                projectId)

        then:
        entity.statusCode.is2xxSuccessful()
        and:
        IssueStatusDTO result = entity.body

        expect:
        result.statusId == statusId
    }

    def 'deleteStatus'() {

        when: '删除未对应的状态'
        def entity = restTemplate.exchange("/v1/projects/{project_id}/issue_status/{id}?applyType=agile",
                HttpMethod.DELETE,
                new HttpEntity<>(),
                ResponseEntity.class,
                projectId,
                statusId
        )

        then:
        entity.statusCode.is2xxSuccessful()
    }
}
