package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.vo.IssueLabelVO;
import io.choerodon.agile.app.service.IssueLabelService;
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
 * 敏捷开发Issue标签
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:04:00
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/issue_labels")
public class IssueLabelController {

    @Autowired
    private IssueLabelService issueLabelService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询issue标签列表")
    @GetMapping
    public ResponseEntity<List<IssueLabelVO>> listIssueLabel(@ApiParam(value = "项目id", required = true)
                                                              @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(issueLabelService.listIssueLabel(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.IssueLabel.queryIssueLabelList"));
    }
}