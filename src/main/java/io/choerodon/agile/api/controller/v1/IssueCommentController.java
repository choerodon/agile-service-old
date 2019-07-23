package io.choerodon.agile.api.controller.v1;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.api.validator.IssueCommentValidator;
import io.choerodon.agile.app.service.IssueCommentService;
import io.choerodon.agile.infra.utils.VerifyUpdateUtil;
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
import java.util.Optional;

/**
 * 敏捷开发Issue评论
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:59:45
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/issue_comment")
public class IssueCommentController {

    @Autowired
    private IssueCommentService issueCommentService;
    @Autowired
    private IssueCommentValidator issueCommentValidator;
    @Autowired
    private VerifyUpdateUtil verifyUpdateUtil;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("创建issue评论")
    @PostMapping
    public ResponseEntity<IssueCommentVO> createIssueComment(@ApiParam(value = "项目id", required = true)
                                                              @PathVariable(name = "project_id") Long projectId,
                                                             @ApiParam(value = "创建issue评论对象", required = true)
                                                              @RequestBody IssueCommentCreateVO issueCommentCreateVO) {
        issueCommentValidator.verifyCreateData(issueCommentCreateVO);
        return Optional.ofNullable(issueCommentService.createIssueComment(projectId, issueCommentCreateVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.IssueComment.createIssueComment"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("更新issue评论")
    @PostMapping(value = "/update")
    public ResponseEntity<IssueCommentVO> updateIssueComment(@ApiParam(value = "项目id", required = true)
                                                              @PathVariable(name = "project_id") Long projectId,
                                                             @ApiParam(value = "更新issue对象", required = true)
                                                              @RequestBody JSONObject issueCommentUpdate) {
        issueCommentValidator.verifyUpdateData(projectId, issueCommentUpdate);
        IssueCommentUpdateVO issueCommentUpdateVO = new IssueCommentUpdateVO();
        List<String> stringList = verifyUpdateUtil.verifyUpdateData(issueCommentUpdate, issueCommentUpdateVO);
        return Optional.ofNullable(issueCommentService.updateIssueComment(issueCommentUpdateVO, stringList,projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.IssueComment.updateIssueComment"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("通过issueId查询issue评论列表")
    @GetMapping(value = "/{issueId}")
    public ResponseEntity<List<IssueCommentVO>> queryIssueCommentList(@ApiParam(value = "项目id", required = true)
                                                                       @PathVariable(name = "project_id") Long projectId,
                                                                      @ApiParam(value = "issueId", required = true)
                                                                       @PathVariable Long issueId) {
        return Optional.ofNullable(issueCommentService.queryIssueCommentList(projectId, issueId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.IssueComment.queryIssueCommentList"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("通过commentId删除")
    @DeleteMapping(value = "/{commentId}")
    public ResponseEntity deleteIssueComment(@ApiParam(value = "项目id", required = true)
                                             @PathVariable(name = "project_id") Long projectId,
                                             @ApiParam(value = "commentId", required = true)
                                             @PathVariable Long commentId) {
        issueCommentService.deleteIssueComment(projectId, commentId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}