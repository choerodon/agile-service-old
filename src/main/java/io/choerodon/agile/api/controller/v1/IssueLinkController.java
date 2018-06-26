package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.dto.IssueLinkCreateDTO;
import io.choerodon.agile.api.dto.IssueLinkDTO;
import io.choerodon.agile.app.service.IssueLinkService;
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
 * @author dinghuang123@gmail.com
 * @since 2018/6/14
 */
@RestController
@RequestMapping(value = "/v1/project/{project_id}/issue_links")
public class IssueLinkController {

    @Autowired
    private IssueLinkService issueLinkService;

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("创建issueLink")
    @PostMapping(value = "/{issueId}")
    public ResponseEntity<List<IssueLinkDTO>> createIssueLinkList(@ApiParam(value = "项目id", required = true)
                                                                  @PathVariable(name = "project_id") Long projectId,
                                                                  @ApiParam(value = "issueId", required = true)
                                                                  @PathVariable Long issueId,
                                                                  @ApiParam(value = "issueLink创建对象", required = true)
                                                                  @RequestBody List<IssueLinkCreateDTO> issueLinkCreateDTOList) {
        return Optional.ofNullable(issueLinkService.createIssueLinkList(issueLinkCreateDTOList, issueId, projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.IssueLink.createIssueLink"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("删除issueLink")
    @DeleteMapping(value = "/{issueLinkId}")
    public ResponseEntity deleteIssueLink(@ApiParam(value = "项目id", required = true)
                                          @PathVariable(name = "project_id") Long projectId,
                                          @ApiParam(value = "issueLinkId", required = true)
                                          @PathVariable Long issueLinkId) {
        issueLinkService.deleteIssueLink(issueLinkId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据issueId查询issueLink")
    @GetMapping(value = "/{issueId}")
    public ResponseEntity<List<IssueLinkDTO>> listIssueLinkByIssueId(@ApiParam(value = "项目id", required = true)
                                                                      @PathVariable(name = "project_id") Long projectId,
                                                                      @ApiParam(value = "issueId", required = true)
                                                                      @PathVariable Long issueId) {
        return Optional.ofNullable(issueLinkService.listIssueLinkByIssueId(issueId, projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.IssueLink.listIssueLinkByIssueId"));
    }
}
