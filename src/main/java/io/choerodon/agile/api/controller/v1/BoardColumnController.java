package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.dto.ColumnSortDTO;
import io.choerodon.agile.api.dto.ColumnWithMaxMinNumDTO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.api.dto.BoardColumnDTO;
import io.choerodon.agile.app.service.BoardColumnService;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
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

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("创建BoardColumn")
    @PostMapping
    public ResponseEntity<BoardColumnDTO> createBoardColumn(@ApiParam(value = "项目id", required = true)
                                                            @PathVariable(name = "project_id") Long projectId,
                                                            @ApiParam(value = "category code", required = true)
                                                            @RequestParam String categoryCode,
                                                            @ApiParam(value = "board column对象", required = true)
                                                            @RequestBody BoardColumnDTO boardColumnDTO) {
        return Optional.ofNullable(boardColumnService.create(projectId, categoryCode, boardColumnDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.BoardColumn.create"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("更新BoardColumn")
    @PutMapping(value = "/{columnId}")
    public ResponseEntity<BoardColumnDTO> updateBoardColumn(@ApiParam(value = "项目id", required = true)
                                                            @PathVariable(name = "project_id") Long projectId,
                                                            @ApiParam(value = "column id", required = true)
                                                            @PathVariable Long columnId,
                                                            @ApiParam(value = "board id", required = true)
                                                            @RequestParam Long boardId,
                                                            @ApiParam(value = "board column对象", required = true)
                                                            @RequestBody BoardColumnDTO boardColumnDTO) {
        return Optional.ofNullable(boardColumnService.update(projectId, columnId, boardId, boardColumnDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.BoardColumn.update"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("调整列的顺序")
    @PostMapping(value = "/column_sort")
    public ResponseEntity columnSort(@ApiParam(value = "项目id", required = true)
                                     @PathVariable(name = "project_id") Long projectId,
                                     @ApiParam(value = "ColumnSort DTO", required = true)
                                     @RequestBody ColumnSortDTO columnSortDTO) {
        boardColumnService.columnSort(projectId, columnSortDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("删除BoardColumn")
    @DeleteMapping(value = "/{columnId}")
    public ResponseEntity deleteBoardColumn(@ApiParam(value = "项目id", required = true)
                                            @PathVariable(name = "project_id") Long projectId,
                                            @ApiParam(value = "column id", required = true)
                                            @PathVariable Long columnId) {
        boardColumnService.delete(projectId, columnId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据id查询BoardColumn")
    @GetMapping(value = "/{columnId}")
    public ResponseEntity<BoardColumnDTO> queryBoardColumnById(@ApiParam(value = "项目id", required = true)
                                                               @PathVariable(name = "project_id") Long projectId,
                                                               @ApiParam(value = "column id", required = true)
                                                               @PathVariable Long columnId) {
        return Optional.ofNullable(boardColumnService.queryBoardColumnById(projectId, columnId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.BoardColumn.get"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据id更新最大最小值")
    @PostMapping(value = "/{columnId}/column_contraint")
    public ResponseEntity<BoardColumnDTO> updateColumnContraint(@ApiParam(value = "项目id", required = true)
                                                                @PathVariable(name = "project_id") Long projectId,
                                                                @ApiParam(value = "column id", required = true)
                                                                @PathVariable Long columnId,
                                                                @ApiParam(value = "ColumnWithMaxMinNumDTO", required = true)
                                                                @RequestBody ColumnWithMaxMinNumDTO columnWithMaxMinNumDTO) {
        return Optional.ofNullable(boardColumnService.updateColumnContraint(projectId, columnId, columnWithMaxMinNumDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.MaxAndMinNum.update"));
    }

//    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
//    @ApiOperation("校验状态名称是否重复")
//    @GetMapping(value = "/check")
//    public ResponseEntity<Boolean> checkStatusName(@ApiParam(value = "项目id", required = true)
//                                                   @PathVariable(name = "project_id") Long projectId,
//                                                   @ApiParam(value = "状态名称", required = true)
//                                                   @RequestParam String statusName) {
//        return Optional.ofNullable(boardColumnService.checkSameStatusName(projectId, statusName))
//                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
//                .orElseThrow(() -> new CommonException("error.status.checkStatusName"));
//    }

}
