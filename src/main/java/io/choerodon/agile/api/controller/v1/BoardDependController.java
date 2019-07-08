package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.vo.BoardDependCreateDTO;
import io.choerodon.agile.api.vo.BoardDependDTO;
import io.choerodon.agile.app.service.BoardDependService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author shinan.chen
 * @since 2019/5/13
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/board_depend")
public class BoardDependController {
    @Autowired
    private BoardDependService boardDependService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("创建公告板特性依赖")
    @PostMapping
    public ResponseEntity<BoardDependDTO> create(@ApiParam(value = "项目id", required = true)
                                                 @PathVariable(name = "project_id") Long projectId,
                                                 @ApiParam(value = "createDTO", required = true)
                                                 @RequestBody BoardDependCreateDTO createDTO) {
        return Optional.ofNullable(boardDependService.create(projectId, createDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.boardDepend.create"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("删除公告板特性依赖")
    @DeleteMapping(value = "/{boardDependId}")
    public ResponseEntity deleteById(@ApiParam(value = "项目id", required = true)
                                     @PathVariable(name = "project_id") Long projectId,
                                     @ApiParam(value = "boardDependId", required = true)
                                     @PathVariable Long boardDependId) {
        boardDependService.deleteById(projectId, boardDependId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
