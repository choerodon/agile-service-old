package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.app.service.FeedbackAttachmentService;
import io.choerodon.agile.infra.dataobject.FeedbackAttachmentDTO;
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

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/23.
 * Email: fuqianghuang01@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/feedback_attachment")
public class FeedbackAttachmentController {

    @Autowired
    private FeedbackAttachmentService feedbackAttachmentService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("上传附件")
    @PostMapping
    public ResponseEntity<List<FeedbackAttachmentDTO>> uploadAttachment(@ApiParam(value = "项目id", required = true)
                                                                        @PathVariable(name = "project_id") Long projectId,
                                                                        @ApiParam(value = "feedback id", required = true)
                                                                        @RequestParam Long feedbackId,
                                                                        @ApiParam(value = "comment id", required = false)
                                                                        @RequestParam(required = false) Long commentId,
                                                                        HttpServletRequest request) {
        return Optional.ofNullable(feedbackAttachmentService.create(projectId, feedbackId, commentId, request))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.attachment.upload"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("删除附件")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity deleteAttachment(@ApiParam(value = "项目id", required = true)
                                           @PathVariable(name = "project_id") Long projectId,
                                           @ApiParam(value = "附件id", required = true)
                                           @PathVariable Long id) {
        feedbackAttachmentService.delete(projectId, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("上传附件，直接返回地址")
    @PostMapping(value = "/upload_for_address")
    public ResponseEntity<List<String>> uploadForAddress(@ApiParam(value = "project id", required = true)
                                                         @PathVariable(name = "project_id") Long projectId,
                                                         HttpServletRequest request) {
        return Optional.ofNullable(feedbackAttachmentService.uploadForAddress(projectId, request))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.attachment.upload"));
    }
}
