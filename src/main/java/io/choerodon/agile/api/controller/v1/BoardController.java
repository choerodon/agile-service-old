package io.choerodon.agile.api.controller.v1;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.BoardDTO;
import io.choerodon.agile.api.dto.IssueMoveDTO;
import io.choerodon.agile.api.dto.UserSettingDTO;
import io.choerodon.agile.app.service.BoardService;
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

import java.util.List;
import java.util.Optional;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/board")
public class BoardController {

    @Autowired
    private BoardService boardService;

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("创建scrum board,创建默认列，关联项目状态")
    @PostMapping
    public ResponseEntity createScrumBoard(@ApiParam(value = "项目id", required = true)
                                           @PathVariable(name = "project_id") Long projectId,
                                           @ApiParam(value = "board name", required = true)
                                           @RequestParam String boardName) {
        boardService.create(projectId, boardName);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("更新scrum board")
    @PutMapping(value = "/{boardId}")
    public ResponseEntity<BoardDTO> updateScrumBoard(@ApiParam(value = "项目id", required = true)
                                                     @PathVariable(name = "project_id") Long projectId,
                                                     @ApiParam(value = "agile board id", required = true)
                                                     @PathVariable Long boardId,
                                                     @ApiParam(value = "ScrumBoard对象", required = true)
                                                     @RequestBody BoardDTO boardDTO) {
        return Optional.ofNullable(boardService.update(projectId, boardId, boardDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.board.update"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation("删除scrum board")
    @DeleteMapping(value = "/{boardId}")
    public ResponseEntity deleteScrumBoard(@ApiParam(value = "项目id", required = true)
                                           @PathVariable(name = "project_id") Long projectId,
                                           @ApiParam(value = "agile board id", required = true)
                                           @PathVariable Long boardId) {
        boardService.delete(projectId, boardId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据id查询scrum board")
    @GetMapping(value = "/{boardId}")
    public ResponseEntity<BoardDTO> queryScrumBoardById(@ApiParam(value = "项目id", required = true)
                                                        @PathVariable(name = "project_id") Long projectId,
                                                        @ApiParam(value = "agile board id", required = true)
                                                        @PathVariable Long boardId) {
        return Optional.ofNullable(boardService.queryScrumBoardById(projectId, boardId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.board.get"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("移动issue")
    @PostMapping(value = "/issue/{issueId}/move")
    public ResponseEntity<IssueMoveDTO> move(@ApiParam(value = "项目id", required = true)
                                             @PathVariable(name = "project_id") Long projectId,
                                             @ApiParam(value = "issue id", required = true)
                                             @PathVariable Long issueId,
                                             @ApiParam(value = "转换id", required = true)
                                             @RequestParam Long transformId,
                                             @ApiParam(value = "issue move object", required = true)
                                             @RequestBody IssueMoveDTO issueMoveDTO) {
        return Optional.ofNullable(boardService.move(projectId, issueId, transformId, issueMoveDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.issue.update"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据projectId查询项目下的board")
    @GetMapping
    public ResponseEntity<List<BoardDTO>> queryByProjectId(@ApiParam(value = "项目id", required = true)
                                                           @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(boardService.queryByProjectId(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.boardList.get"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据projectId查询项目下的用户的board设置")
    @GetMapping(value = "/user_setting/{boardId}")
    public ResponseEntity<UserSettingDTO> queryUserSettingBoard(@ApiParam(value = "项目id", required = true)
                                                                @PathVariable(name = "project_id") Long projectId,
                                                                @ApiParam(value = "agile board id", required = true)
                                                                @PathVariable Long boardId) {
        return Optional.ofNullable(boardService.queryUserSettingBoard(projectId, boardId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.userSettingBoard.get"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("更新用户泳道设置")
    @PostMapping(value = "/user_setting/{boardId}")
    public ResponseEntity<UserSettingDTO> updateUserSettingBoard(@ApiParam(value = "项目id", required = true)
                                                                 @PathVariable(name = "project_id") Long projectId,
                                                                 @ApiParam(value = "agile board id", required = true)
                                                                 @PathVariable Long boardId,
                                                                 @ApiParam(value = "swimlaneBasedCode", required = true)
                                                                 @RequestParam String swimlaneBasedCode) {
        return Optional.ofNullable(boardService.updateUserSettingBoard(projectId, boardId, swimlaneBasedCode))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.userSettingBoard.update"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("all data , Refactoring")
    @GetMapping(value = "/{boardId}/all_data/{organization_id}")
    public ResponseEntity<JSONObject> queryByOptions(@ApiParam(value = "项目id", required = true)
                                                     @PathVariable(name = "project_id") Long projectId,
                                                     @ApiParam(value = "agile board id", required = true)
                                                     @PathVariable Long boardId,
                                                     @ApiParam(value = "search item，my problem", required = false)
                                                     @RequestParam(required = false) Long assigneeId,
                                                     @ApiParam(value = "search item，only story", required = false)
                                                     @RequestParam(required = false) Boolean onlyStory,
                                                     @ApiParam(value = "quick filter", required = false)
                                                     @RequestParam(required = false) List<Long> quickFilterIds,
                                                     @ApiParam(value = "组织id", required = true)
                                                     @PathVariable(name = "organization_id") Long organizationId,
                                                     @ApiParam(value = "经办人搜索", required = false)
                                                     @RequestParam(required = false) List<Long> assigneeFilterIds) {
        return Optional.ofNullable(boardService.queryAllData(projectId, boardId, assigneeId, onlyStory, quickFilterIds, organizationId, assigneeFilterIds))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.board.get"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("校验看板名称重复性")
    @GetMapping("/check_name")
    public ResponseEntity<Boolean> checkName(@ApiParam(value = "项目id", required = true)
                                             @PathVariable(name = "project_id") Long projectId,
                                             @ApiParam(value = "board name", required = true)
                                             @RequestParam String boardName) {
        return Optional.ofNullable(boardService.checkName(projectId, boardName))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.checkName.get"));

    }

}
