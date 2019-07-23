package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.app.service.FeedbackCommentService;
import io.choerodon.agile.infra.dataobject.FeedbackCommentDTO;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/23.
 * Email: fuqianghuang01@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/feedback_comment")
public class FeedbackCommentController {

    @Autowired
    private FeedbackCommentService feedbackCommentService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("创建评论")
    @PostMapping
    public ResponseEntity<FeedbackCommentDTO> createFeedbackComment(@ApiParam(value = "项目id", required = true)
                                                                    @PathVariable(name = "project_id") Long projectId,
                                                                    @ApiParam(value = "feedback comment vo", required = true)
                                                                    @RequestBody FeedbackCommentDTO feedbackCommentDTO) {
        return Optional.ofNullable(feedbackCommentService.createFeedbackComment(projectId, feedbackCommentDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.feedbackComment.create"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("根据feedbackId查询评论")
    @GetMapping
    public ResponseEntity<Map<Long, List<FeedbackCommentDTO>>> queryListByFeedbackId(@ApiParam(value = "项目id", required = true)
                                                                             @PathVariable(name = "project_id") Long projectId,
                                                                                     @ApiParam(value = "feedback id", required = true)
                                                                             @RequestParam Long feedbackId) {
        return Optional.ofNullable(feedbackCommentService.queryListByFeedbackId(projectId, feedbackId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.feedbackComment.create"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("删除评论")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@ApiParam(value = "项目id", required = true)
                                     @PathVariable(name = "project_id") Long projectId,
                                     @ApiParam(value = "comment id", required = true)
                                     @PathVariable Long id) {
        feedbackCommentService.deleteById(projectId, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("更新评论内容")
    @PutMapping
    public ResponseEntity<FeedbackCommentDTO> updateComment(@ApiParam(value = "项目id", required = true)
                                                            @PathVariable(name = "project_id") Long projectId,
                                                            @ApiParam(value = "feedback comment", required = true)
                                                            @RequestBody FeedbackCommentDTO feedbackCommentDTO) {
        return Optional.ofNullable(feedbackCommentService.updateComment(projectId, feedbackCommentDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.feedbackComment.update"));
    }
}
