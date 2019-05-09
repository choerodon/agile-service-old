package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.dto.IssueBranchDTO;
import io.choerodon.agile.app.service.IssueBranchService;
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
 * Created by HuangFuqiang@choerodon.io on 2018/5/19.
 * Email: fuqianghuang01@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/issue_branch")
public class IssueBranchController {

    @Autowired
    private IssueBranchService issueBranchService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("创建branch")
    @PostMapping
    public ResponseEntity<IssueBranchDTO> createIssueBranch(@ApiParam(value = "项目id", required = true)
                                                            @PathVariable(name = "project_id") Long projectId,
                                                            @ApiParam(value = "issue branch object", required = true)
                                                            @RequestBody IssueBranchDTO issueBranchDTO) {
        return Optional.ofNullable(issueBranchService.create(projectId, issueBranchDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.issueBranch.create"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("修改branch")
    @PatchMapping(value = "/{branchId}")
    public ResponseEntity<IssueBranchDTO> updateIssueBranch(@ApiParam(value = "项目id", required = true)
                                                            @PathVariable(name = "project_id") Long projectId,
                                                            @ApiParam(value = "分支id", required = true)
                                                            @PathVariable Long branchId,
                                                            @ApiParam(value = "issue branch object", required = true)
                                                            @RequestBody IssueBranchDTO issueBranchDTO) {
        return Optional.ofNullable(issueBranchService.update(projectId, branchId, issueBranchDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.issueBranch.update"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("删除branch")
    @DeleteMapping(value = "/{branchId}")
    public ResponseEntity deleteIssueBranch(@ApiParam(value = "项目id", required = true)
                                            @PathVariable(name = "project_id") Long projectId,
                                            @ApiParam(value = "分支id", required = true)
                                            @PathVariable Long branchId) {
        issueBranchService.delete(projectId, branchId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据branchId查询branch")
    @GetMapping(value = "/{branchId}")
    public ResponseEntity<IssueBranchDTO> queryIssueBranchById(@ApiParam(value = "项目id", required = true)
                                                               @PathVariable(name = "project_id") Long projectId,
                                                               @ApiParam(value = "分支id", required = true)
                                                               @PathVariable Long branchId) {
        return Optional.ofNullable(issueBranchService.queryIssueBranchById(projectId, branchId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.issueBranch.get"));
    }

}
