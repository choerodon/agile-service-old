package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.dto.ComponentForListDTO;
import io.choerodon.agile.api.dto.IssueDTO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.api.dto.IssueComponentDTO;
import io.choerodon.agile.app.service.IssueComponentService;
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
@RequestMapping(value = "/v1/projects/{project_id}/component")
public class IssueComponentController {

    @Autowired
    private IssueComponentService issueComponentService;

    @Permission(level = ResourceLevel.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation("创建component")
    @PostMapping
    public ResponseEntity<IssueComponentDTO> createComponent(@ApiParam(value = "项目id", required = true)
                                                             @PathVariable(name = "project_id") Long projectId,
                                                             @ApiParam(value = "components对象", required = true)
                                                             @RequestBody IssueComponentDTO issueComponentDTO) {
        return Optional.ofNullable(issueComponentService.create(projectId, issueComponentDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.component.create"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation("修改component")
    @PutMapping(value = "/{id}")
    public ResponseEntity<IssueComponentDTO> updateComponent(@ApiParam(value = "项目id", required = true)
                                                             @PathVariable(name = "project_id") Long projectId,
                                                             @ApiParam(value = "component id", required = true)
                                                             @PathVariable Long id,
                                                             @ApiParam(value = "components对象", required = true)
                                                             @RequestBody IssueComponentDTO issueComponentDTO) {
        return Optional.ofNullable(issueComponentService.update(projectId, id, issueComponentDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.component.update"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation("删除component")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity deleteComponent(@ApiParam(value = "项目id", required = true)
                                          @PathVariable(name = "project_id") Long projectId,
                                          @ApiParam(value = "component id", required = true)
                                          @PathVariable Long id,
                                          @ApiParam(value = "relate component id", required = false)
                                          @RequestParam(required = false) Long relateComponentId) {
        issueComponentService.delete(projectId, id, relateComponentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据id查询component")
    @GetMapping(value = "/{id}")
    public ResponseEntity<IssueComponentDTO> queryComponentById(@ApiParam(value = "项目id", required = true)
                                                                @PathVariable(name = "project_id") Long projectId,
                                                                @ApiParam(value = "component id", required = true)
                                                                @PathVariable Long id) {
        return Optional.ofNullable(issueComponentService.queryComponentsById(projectId, id))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.component.get"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据project id查询component")
    @GetMapping
    public ResponseEntity<List<ComponentForListDTO>> listByProjectId(@ApiParam(value = "项目id", required = true)
                                                                     @PathVariable(name = "project_id") Long projectId,
                                                                     @ApiParam(value = "当前模块id", required = false)
                                                                     @RequestParam(required = false) Long componentId) {
        return Optional.ofNullable(issueComponentService.queryComponentByProjectId(projectId, componentId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.componentList.get"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据id查询component下的issues")
    @GetMapping(value = "/{id}/issues")
    public ResponseEntity<List<IssueDTO>> listByOptions(@ApiParam(value = "项目id", required = true)
                                                        @PathVariable(name = "project_id") Long projectId,
                                                        @ApiParam(value = "component id", required = true)
                                                        @PathVariable Long id) {
        return Optional.ofNullable(issueComponentService.queryIssuesByComponentId(projectId, id))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.issues.get"));
    }

}
