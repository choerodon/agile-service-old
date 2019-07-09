package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.vo.ColumnSortVO;
import io.choerodon.agile.api.vo.ColumnWithMaxMinNumVO;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.api.vo.BoardColumnVO;
import io.choerodon.agile.app.service.BoardColumnService;
import io.choerodon.core.iam.InitRoleCode;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/board_column")
public class BoardColumnController {

    @Autowired
    private BoardColumnService boardColumnService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation("创建BoardColumn")
    @PostMapping
    public ResponseEntity<BoardColumnVO> createBoardColumn(@ApiParam(value = "项目id", required = true)
                                                            @PathVariable(name = "project_id") Long projectId,
                                                           @ApiParam(value = "category code", required = true)
                                                            @RequestParam String categoryCode,
                                                           @ApiParam(value = "apply type", required = true)
                                                            @RequestParam String applyType,
                                                           @ApiParam(value = "board column对象", required = true)
                                                            @RequestBody BoardColumnVO boardColumnVO) {
        return Optional.ofNullable(boardColumnService.create(projectId, categoryCode, applyType, boardColumnVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.BoardColumn.create"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation("更新BoardColumn")
    @PutMapping(value = "/{columnId}")
    public ResponseEntity<BoardColumnVO> updateBoardColumn(@ApiParam(value = "项目id", required = true)
                                                            @PathVariable(name = "project_id") Long projectId,
                                                           @ApiParam(value = "column id", required = true)
                                                            @PathVariable Long columnId,
                                                           @ApiParam(value = "board id", required = true)
                                                            @RequestParam Long boardId,
                                                           @ApiParam(value = "board column对象", required = true)
                                                            @RequestBody BoardColumnVO boardColumnVO) {
        return Optional.ofNullable(boardColumnService.update(projectId, columnId, boardId, boardColumnVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.BoardColumn.update"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation("调整列的顺序")
    @PostMapping(value = "/column_sort")
    public ResponseEntity columnSort(@ApiParam(value = "项目id", required = true)
                                     @PathVariable(name = "project_id") Long projectId,
                                     @ApiParam(value = "ColumnSort DTO", required = true)
                                     @RequestBody ColumnSortVO columnSortVO) {
        boardColumnService.columnSort(projectId, columnSortVO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation("调整项目群列的顺序")
    @PostMapping(value = "/program/column_sort")
    public ResponseEntity columnSortByProgram(@ApiParam(value = "项目id", required = true)
                                              @PathVariable(name = "project_id") Long projectId,
                                              @ApiParam(value = "ColumnSort DTO", required = true)
                                              @RequestBody ColumnSortVO columnSortVO) {
        boardColumnService.columnSortByProgram(projectId, columnSortVO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation("删除BoardColumn")
    @DeleteMapping(value = "/{columnId}")
    public ResponseEntity deleteBoardColumn(@ApiParam(value = "项目id", required = true)
                                            @PathVariable(name = "project_id") Long projectId,
                                            @ApiParam(value = "column id", required = true)
                                            @PathVariable Long columnId) {
        boardColumnService.delete(projectId, columnId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation("项目群删除BoardColumn")
    @DeleteMapping(value = "/program/{columnId}")
    public ResponseEntity deleteProgramBoardColumn(@ApiParam(value = "项目id", required = true)
                                                   @PathVariable(name = "project_id") Long projectId,
                                                   @ApiParam(value = "column id", required = true)
                                                   @PathVariable Long columnId) {
        boardColumnService.deleteProgramBoardColumn(projectId, columnId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据id查询BoardColumn")
    @GetMapping(value = "/{columnId}")
    public ResponseEntity<BoardColumnVO> queryBoardColumnById(@ApiParam(value = "项目id", required = true)
                                                               @PathVariable(name = "project_id") Long projectId,
                                                              @ApiParam(value = "column id", required = true)
                                                               @PathVariable Long columnId) {
        return Optional.ofNullable(boardColumnService.queryBoardColumnById(projectId, columnId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.BoardColumn.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据id更新最大最小值")
    @PostMapping(value = "/{columnId}/column_contraint")
    public ResponseEntity<BoardColumnVO> updateColumnContraint(@ApiParam(value = "项目id", required = true)
                                                                @PathVariable(name = "project_id") Long projectId,
                                                               @ApiParam(value = "column id", required = true)
                                                                @PathVariable Long columnId,
                                                               @ApiParam(value = "ColumnWithMaxMinNumVO", required = true)
                                                                @RequestBody ColumnWithMaxMinNumVO columnWithMaxMinNumVO) {
        return Optional.ofNullable(boardColumnService.updateColumnContraint(projectId, columnId, columnWithMaxMinNumVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.MaxAndMinNum.update"));
    }

}
