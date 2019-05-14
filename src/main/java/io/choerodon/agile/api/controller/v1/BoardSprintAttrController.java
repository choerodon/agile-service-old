package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.dto.BoardSprintAttrDTO;
import io.choerodon.agile.app.service.BoardSprintAttrService;
import io.choerodon.core.exception.CommonException;
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
 * @author shinan.chen
 * @since 2019/5/14
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/board_sprint_attr")
public class BoardSprintAttrController {
    @Autowired
    private BoardSprintAttrService boardSprintAttrService;

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("增加公告板冲刺列宽")
    @GetMapping(value = "/add_column_width")
    public ResponseEntity<BoardSprintAttrDTO> addColumnWidth(@ApiParam(value = "项目id", required = true)
                                                             @PathVariable(name = "project_id") Long projectId,
                                                             @ApiParam(value = "sprintId", required = true)
                                                             @RequestParam Long sprintId) {
        return Optional.ofNullable(boardSprintAttrService.addColumnWidth(projectId, sprintId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.boardSprintAttr.addColumnWidth"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("减少公告板冲刺列宽")
    @GetMapping(value = "/reduce_column_width")
    public ResponseEntity<BoardSprintAttrDTO> reduceColumnWidth(@ApiParam(value = "项目id", required = true)
                                                                @PathVariable(name = "project_id") Long projectId,
                                                                @ApiParam(value = "sprintId", required = true)
                                                                @RequestParam Long sprintId) {
        return Optional.ofNullable(boardSprintAttrService.reduceColumnWidth(projectId, sprintId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.boardSprintAttr.reduceColumnWidth"));
    }
}
