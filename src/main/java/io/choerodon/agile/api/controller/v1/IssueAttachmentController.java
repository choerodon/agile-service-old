package io.choerodon.agile.api.controller.v1;

import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.api.dto.IssueAttachmentDTO;
import io.choerodon.agile.app.service.IssueAttachmentService;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
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
 * Created by HuangFuqiang@choerodon.io on 2018/5/16.
 * Email: fuqianghuang01@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/issue_attachment")
public class IssueAttachmentController {

    @Autowired
    private IssueAttachmentService issueAttachmentService;

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("上传附件")
    @PostMapping
    public ResponseEntity<List<IssueAttachmentDTO>> uploadAttachment(@ApiParam(value = "项目id", required = true)
                                                                      @PathVariable(name = "project_id") Long projectId,
                                                                      @ApiParam(value = "issue id", required = true)
                                                                      @RequestParam Long issueId,
                                                                      HttpServletRequest request) {
        return Optional.ofNullable(issueAttachmentService.create(projectId, issueId, request))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.attachment.upload"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("删除附件")
    @DeleteMapping(value = "/{issueAttachmentId}")
    public ResponseEntity deleteAttachment(@ApiParam(value = "项目id", required = true)
                                            @PathVariable(name = "project_id") Long projectId,
                                            @ApiParam(value = "附件id", required = true)
                                            @PathVariable Long issueAttachmentId) {
        issueAttachmentService.delete(projectId, issueAttachmentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("上传附件，直接返回地址")
    @PostMapping(value = "/upload_for_address")
    public ResponseEntity<List<String>> uploadForAddress(@ApiParam(value = "project id", required = true)
                                                          @PathVariable(name = "project_id") Long projectId,
                                                          HttpServletRequest request) {
        return Optional.ofNullable(issueAttachmentService.uploadForAddress(projectId, request))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.attachment.upload"));
    }

}
