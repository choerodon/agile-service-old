package io.choerodon.agile.api.controller.v1

import com.alibaba.fastjson.JSONObject
import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.BoardDTO
import io.choerodon.agile.api.dto.IssueMoveDTO
import io.choerodon.agile.api.dto.UserSettingDTO
import io.choerodon.agile.api.eventhandler.AgileEventHandler
import io.choerodon.agile.app.service.BoardService
import io.choerodon.agile.infra.dataobject.BoardDO
import io.choerodon.agile.infra.dataobject.ColumnAndIssueDO
import io.choerodon.agile.infra.dataobject.IssueDO
import io.choerodon.agile.infra.dataobject.IssueStatusDO
import io.choerodon.agile.infra.mapper.BoardMapper
import io.choerodon.agile.infra.mapper.IssueMapper
import io.choerodon.agile.infra.mapper.IssueStatusMapper
import org.springframework.beans.factory.annotation.Autowired
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

/**
 * Created by HuangFuqiang@choerodon.io on 2018/8/7.
 * Email: fuqianghuang01@gmail.com
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class BoardControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    private BoardMapper boardMapper

    @Autowired
    private BoardService boardService

    @Autowired
    private IssueStatusMapper issueStatusMapper

    @Autowired
    AgileEventHandler agileEventHandler

    @Autowired
    private IssueMapper issueMapper

    @Shared
    def projectId = 2L
    def boardName = "board-test2"
    def changeBoardName = "boardChange-2"
    def boardNameExtra = "boardExtra-3"

    @Shared
    def boardId


    def 'createScrumBoard'() {
        given:
        BoardDO boardDO = new BoardDO()
        boardDO.name = boardNameExtra
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/board?boardName={boardName}",
                HttpMethod.POST,
                new HttpEntity<>(),
                ResponseEntity.class,
                projectId,
                boardNameExtra)
        then:
        entity.statusCode.is2xxSuccessful()
        BoardDO result = boardMapper.selectOne(boardDO)
        result != null
    }

    def 'updateScrumBoard'() {
        given:
        BoardDO boardDO = new BoardDO()
        boardDO.name = boardNameExtra
        BoardDO selectd = boardMapper.selectOne(boardDO)
        boardId = selectd.getBoardId()
        selectd.name = changeBoardName

        when:
        HttpEntity<BoardDO> boardDOHttpEntity = new HttpEntity<>(selectd)
        def entity = restTemplate.exchange("/v1/projects/{project_id}/board/{boardId}",
                HttpMethod.PUT,
                boardDOHttpEntity,
                BoardDTO.class,
                projectId,
                boardId)
        then:
        entity.statusCode.is2xxSuccessful()
        entity.body.name == changeBoardName
    }

    def 'queryScrumBoardById'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/board/{boardId}",
                HttpMethod.GET,
                new HttpEntity<>(),
                BoardDTO.class,
                projectId,
                boardId)
        then:
        entity.statusCode.is2xxSuccessful()
        entity.body.name == changeBoardName
    }

    def 'move'() {
        given:
        IssueMoveDTO issueMoveDTO = new IssueMoveDTO()
        IssueDO issue = issueMapper.selectByPrimaryKey(1L)
        issueMoveDTO.issueId = issue.getIssueId()
        issueMoveDTO.statusId = 2L
        issueMoveDTO.boardId = boardId
        issueMoveDTO.originColumnId = 1L
        issueMoveDTO.columnId = 2L
        issueMoveDTO.objectVersionNumber = 1L

        when:
        HttpEntity<IssueMoveDTO> issueMoveDTOHttpEntity = new HttpEntity<>(issueMoveDTO)
        def entity = restTemplate.exchange("/v1/projects/{project_id}/board/issue/{issueId}/move",
                HttpMethod.POST,
                issueMoveDTOHttpEntity,
                IssueMoveDTO.class,
                projectId,
                1L)
        then:
        entity.statusCode.is2xxSuccessful()
        IssueDO issueDO = issueMapper.selectByPrimaryKey(1L)
        issueDO.statusId == 2L
    }

    def 'queryByProjectId'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/board",
                HttpMethod.GET,
                new HttpEntity<>(),
                List.class,
                projectId)
        then:
        entity.statusCode.is2xxSuccessful()
        entity.body.size() == 1
    }

    def 'queryByOptions'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/board/{boardId}/all_data",
                HttpMethod.GET,
                new HttpEntity<>(),
                JSONObject.class,
                projectId,
                boardId)
        then:
        entity.statusCode.is2xxSuccessful()
        JSONObject result = entity.body
        JSONObject columnDatas = result.get("columnsData")
        List<ColumnAndIssueDO> columns = columnDatas.get("columns")
        columns.size() == 3
    }

    def 'updateUserSettingBoard'() {
        when: '发送更新请求'
        def entity = restTemplate.postForEntity("/v1/projects/{project_id}/board/user_setting/{boardId}?swimlaneBasedCode={swimlaneBasedCode}",
                null, UserSettingDTO, projectId, boardId, "assignee")
        then: '校验请求'
        entity.statusCode.is2xxSuccessful()
        and: '设置值'
        UserSettingDTO userSettingDTO = entity.body
        expect: '期望值'
        userSettingDTO.settingId != null
        userSettingDTO.swimlaneBasedCode == "assignee"
        userSettingDTO.objectVersionNumber != null
    }

    def 'queryUserSettingBoard'() {
        when: '发送查询请求'
        def entity = restTemplate.getForEntity("/v1/projects/{project_id}/board/user_setting/{boardId}",
                UserSettingDTO, projectId, boardId)
        then: '校验请求'
        entity.statusCode.is2xxSuccessful()
        and: '设置值'
        UserSettingDTO userSettingDTO = entity.body
        expect: '期望值'
        userSettingDTO.settingId != null
        userSettingDTO.swimlaneBasedCode == "assignee"
        userSettingDTO.objectVersionNumber != null
    }

    def 'deleteScrumBoard'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/board/{boardId}",
                HttpMethod.DELETE,
                new HttpEntity<>(),
                ResponseEntity.class,
                projectId,
                boardId)
        then:
        entity.statusCode.is2xxSuccessful()
        BoardDO result = boardMapper.selectByPrimaryKey(boardId)
        result == null
    }

    def 'returnData'() {
        given: '回退issue状态'
        IssueDO issueDO = issueMapper.selectByPrimaryKey(1)
        issueDO.setStatusId(1)

        when: '更新'
        issueMapper.updateByPrimaryKey(issueDO)

        then: '检查是否更新成功'
        issueMapper.selectByPrimaryKey(1).statusId == 1
    }

}
