package io.choerodon.agile.api.controller.v1;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.IssueMoveDTO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.api.dto.BoardDTO;
import io.choerodon.agile.app.service.BoardService;
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
@RequestMapping(value = "/v1/project/{project_id}/board")
public class BoardController {

    @Autowired
    private BoardService boardService;

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation("创建scrum board,创建默认列，关联项目状态")
    @PostMapping
    public ResponseEntity createScrumBoard(@ApiParam(value = "项目id", required = true)
                                           @PathVariable(name = "project_id") Long projectId,
                                           @ApiParam(value = "board name", required = true)
                                           @RequestParam String boardName) {
        boardService.create(projectId, boardName);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Permission(level = ResourceLevel.PROJECT)
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

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation("删除scrum board")
    @DeleteMapping(value = "/{boardId}")
    public ResponseEntity deleteScrumBoard(@ApiParam(value = "项目id", required = true)
                                           @PathVariable(name = "project_id") Long projectId,
                                           @ApiParam(value = "agile board id", required = true)
                                           @PathVariable Long boardId) {
        boardService.delete(projectId, boardId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.PROJECT)
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

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation("all data")
    @GetMapping(value = "/{boardId}/all_data")
    public ResponseEntity<JSONObject> queryByOptions(@ApiParam(value = "项目id", required = true)
                                                     @PathVariable(name = "project_id") Long projectId,
                                                     @ApiParam(value = "agile board id", required = true)
                                                     @PathVariable Long boardId,
                                                     @ApiParam(value = "search item，my problem", required = false)
                                                     @RequestParam(required = false) Long assigneeId,
                                                     @ApiParam(value = "search item，only story", required = false)
                                                     @RequestParam(required = false) Boolean onlyStory,
                                                     @ApiParam(value = "quick filter", required = false)
                                                     @RequestParam(required = false) List<Long> quickFilterIds) {
        return Optional.ofNullable(boardService.queryAllData(projectId, boardId, assigneeId, onlyStory, quickFilterIds))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.board.get"));
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation("init board,勿调用")
    @PostMapping(value = "/init_board")
    public ResponseEntity initBoard(@ApiParam(value = "项目id", required = true)
                                    @PathVariable(name = "project_id") Long projectId,
                                    @ApiParam(value = "board name", required = true)
                                    @RequestParam String boardName) {
        boardService.initBoard(projectId, boardName);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation("移动issue")
    @PostMapping(value = "/issue/{issueId}/move")
    public ResponseEntity<IssueMoveDTO> move(@ApiParam(value = "项目id", required = true)
                                             @PathVariable(name = "project_id") Long projectId,
                                             @ApiParam(value = "issue id", required = true)
                                             @PathVariable Long issueId,
                                             @ApiParam(value = "issue move object", required = true)
                                             @RequestBody IssueMoveDTO issueMoveDTO) {
        return Optional.ofNullable(boardService.move(projectId, issueId, issueMoveDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.issue.update"));
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation("根据projectId查询项目下的board")
    @GetMapping
    public ResponseEntity<List<BoardDTO>> queryByProjectId(@ApiParam(value = "项目id", required = true)
                                                           @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(boardService.queryByProjectId(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.boardList.get"));
    }

}