package io.choerodon.agile.api.controller.v1

import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.BoardColumnDTO
import io.choerodon.agile.api.dto.ColumnSortDTO
import io.choerodon.agile.api.dto.ColumnWithMaxMinNumDTO
import io.choerodon.agile.app.service.BoardColumnService
import io.choerodon.agile.infra.dataobject.BoardColumnDO
import io.choerodon.agile.infra.dataobject.BoardDO
import io.choerodon.agile.infra.dataobject.IssueStatusDO
import io.choerodon.agile.infra.mapper.BoardColumnMapper
import io.choerodon.agile.infra.mapper.BoardMapper
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


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(AgileTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class BoardColumnControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    private BoardColumnService boardColumnService

    @Autowired
    private BoardColumnMapper boardColumnMapper

    @Autowired
    private BoardMapper boardMapper

    @Autowired
    private IssueStatusMapper issueStatusMapper

    @Shared
    def projectId = 1L
    def categoryCode = 'todo'
    def columnId = 1L
    def boardId = 1L

    def setup() {
        def boardName = "board-" + System.currentTimeMillis()
        BoardDO boardDO = new BoardDO();
        boardDO.projectId = projectId
        boardDO.name = boardName
        boardMapper.insert(boardDO)

        def statusName = "statusName"
        IssueStatusDO issueStatusDO = new IssueStatusDO();
        issueStatusDO.projectId = projectId
        issueStatusDO.name = statusName
        issueStatusDO.categoryCode = categoryCode
        issueStatusDO.enable = true
        issueStatusMapper.insert(issueStatusDO)
    }

    def 'createBoardColumn'() {
        given:
        def boardColumnName = "board-" + System.currentTimeMillis()
        BoardColumnDTO boardColumnDTO = new BoardColumnDTO();
        boardColumnDTO.projectId = projectId
        boardColumnDTO.boardId = 1L
        boardColumnDTO.categoryCode = categoryCode
        boardColumnDTO.sequence = 1
        boardColumnDTO.name = boardColumnName
        boardColumnDTO.minNum = 1
        boardColumnDTO.maxNum = 1

        when:
        HttpEntity<BoardColumnDTO> boardColumnDTOHttpEntity = new HttpEntity<>(boardColumnDTO)
        def entity = restTemplate.exchange("/v1/projects/{project_id}/board_column?categoryCode={categoryCode}",
                HttpMethod.POST,
                boardColumnDTOHttpEntity,
                BoardColumnDTO.class,
                projectId,
                categoryCode)
        then:
        entity.statusCode.is2xxSuccessful()
        entity.body.name == boardColumnName
    }

    def 'updateBoardColumn'() {
        given: '更新列名称'
        def boardColumnName2 = "board-" + System.currentTimeMillis()
        BoardColumnDTO boardColumnDTO = new BoardColumnDTO();
        boardColumnDTO.projectId = projectId
        boardColumnDTO.name = boardColumnName2
        boardColumnDTO.objectVersionNumber = 1L
        boardColumnDTO.columnId = 1L
        boardColumnDTO.boardId = boardId

        when:
        HttpEntity<BoardColumnDTO> boardColumnDTOHttpEntity = new HttpEntity<>(boardColumnDTO)
        def entity = restTemplate.exchange("/v1/projects/{project_id}/board_column/{columnId}?boardId={boardId}",
                HttpMethod.PUT,
                boardColumnDTOHttpEntity,
                BoardColumnDTO.class,
                projectId,
                columnId,
                boardId)
        then:
        entity.statusCode.is2xxSuccessful()
        entity.body.columnId == 1L
        entity.body.name == boardColumnName2
    }

    def 'columnSort'() {
        given:
        ColumnSortDTO columnSortDTO = new ColumnSortDTO()
        columnSortDTO.boardId = 1L
        columnSortDTO.projectId = 1L
        columnSortDTO.columnId = 1L
        columnSortDTO.sequence = 2
        columnSortDTO.objectVersionNumber = 2L

        when:
        HttpEntity<ColumnSortDTO> columnSortDTOHttpEntity = new HttpEntity<>(columnSortDTO)
        def entity = restTemplate.exchange("/v1/projects/{project_id}/board_column/column_sort",
                HttpMethod.POST,
                columnSortDTOHttpEntity,
                ResponseEntity.class,
                projectId)

        then:
        entity.statusCode.is2xxSuccessful()

    }

    def 'queryBoardColumnById'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/board_column/{columnId}",
                HttpMethod.GET,
                new HttpEntity<Object>(),
                BoardColumnDTO.class,
                projectId,
                columnId)
        then:
        entity.statusCode.is2xxSuccessful()
        entity.body.columnId == 1L
    }

    def 'updateColumnContraint'() {
        given:
        ColumnWithMaxMinNumDTO columnWithMaxMinNumDTO = new ColumnWithMaxMinNumDTO()
        columnWithMaxMinNumDTO.projectId = projectId
        columnWithMaxMinNumDTO.columnId = 1L
        columnWithMaxMinNumDTO.objectVersionNumber = 3L
        columnWithMaxMinNumDTO.boardId = 1L
        columnWithMaxMinNumDTO.maxNum = 3
        columnWithMaxMinNumDTO.minNum = 2

        when:
        HttpEntity<ColumnWithMaxMinNumDTO> columnWithMaxMinNumDTOHttpEntity = new HttpEntity<>(columnWithMaxMinNumDTO);
        def entity = restTemplate.exchange("/v1/projects/{project_id}/board_column/{columnId}/column_contraint",
                HttpMethod.POST,
                columnWithMaxMinNumDTOHttpEntity,
                BoardColumnDTO.class,
                projectId,
                columnId)
        then:
        entity.statusCode.is2xxSuccessful()
        entity.body.minNum == 2
        entity.body.maxNum == 3
    }

    def 'deleteBoardColumn'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/board_column/{columnId}",
                HttpMethod.DELETE, new HttpEntity<>(),
                ResponseEntity.class,
                projectId,
                columnId)
        then:
        entity.statusCode.is2xxSuccessful()
        BoardColumnDO result = boardColumnMapper.selectByPrimaryKey(columnId)
        result == null
    }

    def 'checkStatusName'() {
        given:
        def statusName = name

        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/board_column/check?statusName={statusName}",
                HttpMethod.GET,
                new HttpEntity<>(),
                Boolean.class,
                projectId,
                statusName)
        then:
        entity.statusCode.is2xxSuccessful()

        expect:
        entity.body.booleanValue() == expectResult

        where:
        name         | expectResult
        "statusName" | true
        "columnName" | false
    }
}
