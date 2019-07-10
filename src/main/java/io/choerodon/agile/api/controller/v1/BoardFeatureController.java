package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.app.service.BoardFeatureService;
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
@RequestMapping(value = "/v1/projects/{project_id}/board_feature")
public class BoardFeatureController {
    @Autowired
    private BoardFeatureService boardFeatureService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("创建公告板特性")
    @PostMapping
    public ResponseEntity<BoardFeatureInfoVO> create(@ApiParam(value = "项目id", required = true)
                                                      @PathVariable(name = "project_id") Long projectId,
                                                     @ApiParam(value = "createDTO", required = true)
                                                      @RequestBody BoardFeatureCreateVO createVO) {
        return Optional.ofNullable(boardFeatureService.create(projectId, createVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.boardFeature.create"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("移动公告板特性")
    @PutMapping(value = "/{boardFeatureId}")
    public ResponseEntity<BoardFeatureInfoVO> update(@ApiParam(value = "项目id", required = true)
                                                      @PathVariable(name = "project_id") Long projectId,
                                                     @ApiParam(value = "boardFeatureId", required = true)
                                                      @PathVariable Long boardFeatureId,
                                                     @ApiParam(value = "updateDTO", required = true)
                                                      @RequestBody BoardFeatureUpdateVO updateVO) {
        return Optional.ofNullable(boardFeatureService.update(projectId, boardFeatureId, updateVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.boardFeature.update"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("删除公告板特性")
    @DeleteMapping(value = "/{boardFeatureId}")
    public ResponseEntity deleteById(@ApiParam(value = "项目id", required = true)
                                     @PathVariable(name = "project_id") Long projectId,
                                     @ApiParam(value = "boardFeatureId", required = true)
                                     @PathVariable Long boardFeatureId) {
        boardFeatureService.deleteById(projectId, boardFeatureId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("获取公告板所有信息")
    @PostMapping(value = "/query_board_info")
    public ResponseEntity<ProgramBoardInfoDTO> queryBoardInfo(@ApiParam(value = "项目id", required = true)
                                                              @PathVariable(name = "project_id") Long projectId,
                                                              @RequestBody ProgramBoardFilterDTO boardFilter) {
        return Optional.ofNullable(boardFeatureService.queryBoardInfo(projectId, boardFilter))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.boardFeature.queryBoardInfo"));
    }
}
