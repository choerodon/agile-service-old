package io.choerodon.agile.api.controller.v1

import com.alibaba.fastjson.JSONObject
import io.choerodon.agile.AgileTestConfiguration
import io.choerodon.agile.api.dto.BoardColumnDTO
import io.choerodon.agile.api.dto.ColumnSortDTO
import io.choerodon.agile.api.dto.ColumnWithMaxMinNumDTO
import io.choerodon.agile.app.service.BoardColumnService
import io.choerodon.agile.infra.dataobject.BoardColumnDO
import io.choerodon.agile.infra.dataobject.IssueStatusDO
import io.choerodon.agile.infra.mapper.BoardColumnMapper
import io.choerodon.agile.infra.mapper.BoardMapper
import io.choerodon.agile.infra.mapper.IssueStatusMapper
import org.springframework.beans.BeanUtils
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

    @Shared
    def categoryCode = 'todo'

    @Shared
    def boardId = 1L

    @Shared
    def boardColumnName = "column-create"

    @Shared
    def boardColumnName2 = "column-update"

    @Shared
    def columnId2


    def 'createBoardColumn'() {
        given:
        BoardColumnDTO boardColumnDTO = new BoardColumnDTO();
        boardColumnDTO.projectId = projectId
        boardColumnDTO.boardId = boardId
        boardColumnDTO.categoryCode = categoryCode
        boardColumnDTO.sequence = 1
        boardColumnDTO.name = boardColumnName
        boardColumnDTO.minNum = 1
        boardColumnDTO.maxNum = 1

        when:
        HttpEntity<BoardColumnDTO> boardColumnDTOHttpEntity = new HttpEntity<>(boardColumnDTO)
        def entity = restTemplate.exchange("/v1/projects/{project_id}/board_column?categoryCode={categoryCode}&applyType=agile",
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
        BoardColumnDO columnDO = new BoardColumnDO()
        columnDO.name = boardColumnName
        columnId2 = boardColumnMapper.selectOne(columnDO).columnId
        BoardColumnDTO boardColumnDTO = new BoardColumnDTO()
        boardColumnDTO.projectId = projectId
        boardColumnDTO.name = boardColumnName2
        boardColumnDTO.objectVersionNumber = 1L
        boardColumnDTO.columnId = columnId2
        boardColumnDTO.boardId = boardId

        when:
        HttpEntity<BoardColumnDTO> boardColumnDTOHttpEntity = new HttpEntity<>(boardColumnDTO)
        def entity = restTemplate.exchange("/v1/projects/{project_id}/board_column/{columnId}?boardId={boardId}",
                HttpMethod.PUT,
                boardColumnDTOHttpEntity,
                BoardColumnDTO.class,
                projectId,
                columnId2,
                boardId)
        then:
        entity.statusCode.is2xxSuccessful()
        print(entity.body)
        entity.body.name == boardColumnName2
    }

    def 'updateBoardColumn unSuccess'() {
        given:
        BoardColumnDO columnDO = boardColumnMapper.selectByPrimaryKey(columnId2)
        columnDO.objectVersionNumber = 0L
        BoardColumnDTO boardColumnDTO = new BoardColumnDTO()
        BeanUtils.copyProperties(columnDO, boardColumnDTO)

        when:
        HttpEntity<BoardColumnDTO> boardColumnDTOHttpEntity = new HttpEntity<>(boardColumnDTO)
        def entity = restTemplate.exchange("/v1/projects/{project_id}/board_column/{columnId}?boardId={boardId}",
                HttpMethod.PUT,
                boardColumnDTOHttpEntity,
                String.class,
                projectId,
                columnId2,
                boardId)
        then:
        entity.statusCode.is2xxSuccessful()
        JSONObject exceptionInfo = JSONObject.parse(entity.body)
        exceptionInfo.get("failed").toString() == "true"
        exceptionInfo.get("code").toString() == "error.BoardColumn.update"

    }

    def 'columnSort'() {
        given:
        ColumnSortDTO columnSortDTO = new ColumnSortDTO()
        columnSortDTO.boardId = boardId
        columnSortDTO.projectId = projectId
        columnSortDTO.columnId = columnId2
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
                columnId2)
        then:
        entity.statusCode.is2xxSuccessful()
        entity.body.name == boardColumnName2
    }

    def 'updateColumnContraint'() {
        given:
        ColumnWithMaxMinNumDTO columnWithMaxMinNumDTO = new ColumnWithMaxMinNumDTO()
        columnWithMaxMinNumDTO.projectId = projectId
        columnWithMaxMinNumDTO.columnId = columnId2
        columnWithMaxMinNumDTO.objectVersionNumber = 3L
        columnWithMaxMinNumDTO.boardId = boardId
        columnWithMaxMinNumDTO.maxNum = 3
        columnWithMaxMinNumDTO.minNum = 2

        when:
        HttpEntity<ColumnWithMaxMinNumDTO> columnWithMaxMinNumDTOHttpEntity = new HttpEntity<>(columnWithMaxMinNumDTO);
        def entity = restTemplate.exchange("/v1/projects/{project_id}/board_column/{columnId}/column_contraint",
                HttpMethod.POST,
                columnWithMaxMinNumDTOHttpEntity,
                BoardColumnDTO.class,
                projectId,
                columnId2)
        then:
        entity.statusCode.is2xxSuccessful()
        entity.body.minNum == 2
        entity.body.maxNum == 3
    }

    def 'updateColumnContraint unSuccess'() {
        given:
        ColumnWithMaxMinNumDTO columnWithMaxMinNumDTO = new ColumnWithMaxMinNumDTO()
        columnWithMaxMinNumDTO.projectId = projectId
        columnWithMaxMinNumDTO.columnId = columnId2
        columnWithMaxMinNumDTO.objectVersionNumber = 4L
        columnWithMaxMinNumDTO.boardId = boardId
        columnWithMaxMinNumDTO.maxNum = 2
        columnWithMaxMinNumDTO.minNum = 3

        when:
        HttpEntity<ColumnWithMaxMinNumDTO> columnWithMaxMinNumDTOHttpEntity = new HttpEntity<>(columnWithMaxMinNumDTO);
        def entity = restTemplate.exchange("/v1/projects/{project_id}/board_column/{columnId}/column_contraint",
                HttpMethod.POST,
                columnWithMaxMinNumDTOHttpEntity,
                String.class,
                projectId,
                columnId2)
        then:
        entity.statusCode.is2xxSuccessful()
        JSONObject exceptionInfo = JSONObject.parse(entity.body)
        exceptionInfo.get("failed").toString() == "true"
        exceptionInfo.get("code").toString() == "error.num.minNumCannotUpToMaxNum"
    }

    def 'deleteBoardColumn'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/board_column/{columnId}",
                HttpMethod.DELETE, new HttpEntity<>(),
                ResponseEntity.class,
                projectId,
                columnId2)
        then:
        entity.statusCode.is2xxSuccessful()
        BoardColumnDO result = boardColumnMapper.selectByPrimaryKey(columnId2)
        result == null
    }

    def 'deleteBoardColumn unSuccess'() {
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/board_column/{columnId}",
                HttpMethod.DELETE, new HttpEntity<>(),
                String.class,
                projectId,
                0L)
        then:
        entity.statusCode.is2xxSuccessful()
        JSONObject exceptionInfo = JSONObject.parse(entity.body)
        exceptionInfo.get("failed").toString() == "true"
        exceptionInfo.get("code").toString() == "error.column.isNull"
    }

    def 'deleteData'() {
        given:
        IssueStatusDO issueStatusDO = new IssueStatusDO()
        issueStatusDO.setProjectId(projectId)
        issueStatusDO.setName("column-create")
        issueStatusMapper.delete(issueStatusDO)
    }

//    def 'checkStatusName'() {
//        given:
//        def statusName = name
//
//        when:
//        def entity = restTemplate.exchange("/v1/projects/{project_id}/board_column/check?statusName={statusName}",
//                HttpMethod.GET,
//                new HttpEntity<>(),
//                Boolean.class,
//                projectId,
//                statusName)
//        then:
//        entity.statusCode.is2xxSuccessful()
//
//        expect:
//        entity.body.booleanValue() == expectResult
//
//        where:
//        name         | expectResult
//        "待处理" | true
//        "columnName" | false
//    }
}
