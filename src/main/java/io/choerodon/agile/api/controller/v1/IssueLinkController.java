package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.vo.IssueLinkCreateVO;
import io.choerodon.agile.api.vo.IssueLinkVO;
import io.choerodon.agile.app.service.IssueLinkService;
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
 * @author dinghuang123@gmail.com
 * @since 2018/6/14
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/issue_links")
public class IssueLinkController {

    @Autowired
    private IssueLinkService issueLinkService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("创建issueLink")
    @PostMapping(value = "/{issueId}")
    public ResponseEntity<List<IssueLinkVO>> createIssueLinkList(@ApiParam(value = "项目id", required = true)
                                                                  @PathVariable(name = "project_id") Long projectId,
                                                                 @ApiParam(value = "issueId", required = true)
                                                                  @PathVariable Long issueId,
                                                                 @ApiParam(value = "issueLink创建对象", required = true)
                                                                  @RequestBody List<IssueLinkCreateVO> issueLinkCreateVOList) {
        return Optional.ofNullable(issueLinkService.createIssueLinkList(issueLinkCreateVOList, issueId, projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.IssueLink.createIssueLink"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("删除issueLink")
    @DeleteMapping(value = "/{issueLinkId}")
    public ResponseEntity deleteIssueLink(@ApiParam(value = "项目id", required = true)
                                          @PathVariable(name = "project_id") Long projectId,
                                          @ApiParam(value = "issueLinkId", required = true)
                                          @PathVariable Long issueLinkId) {
        issueLinkService.deleteIssueLink(issueLinkId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据issueId查询issueLink")
    @GetMapping(value = "/{issueId}")
    public ResponseEntity<List<IssueLinkVO>> listIssueLinkByIssueId(@ApiParam(value = "项目id", required = true)
                                                                     @PathVariable(name = "project_id") Long projectId,
                                                                    @ApiParam(value = "issueId", required = true)
                                                                     @PathVariable Long issueId,
                                                                    @ApiParam(value = "是否包含测试任务")
                                                                     @RequestParam(required = false,name = "no_issue_test",defaultValue = "false")
                                                                                 Boolean noIssueTest) {
        return Optional.ofNullable(issueLinkService.listIssueLinkByIssueId(issueId, projectId, noIssueTest))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.IssueLink.listIssueLinkByIssueId"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据issueId查询issueLink,外接测试项目")
    @PostMapping(value = "/issues")
    public ResponseEntity<List<IssueLinkVO>> listIssueLinkByBatch(@ApiParam(value = "项目id", required = true)
                                                                    @PathVariable(name = "project_id") Long projectId,
                                                                  @ApiParam(value = "issueIds", required = true)
                                                                    @RequestBody List<Long> issueIds) {
        return Optional.ofNullable(issueLinkService.listIssueLinkByBatch(projectId, issueIds))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.IssueLink.listIssueLinkByBatch"));
    }
}
