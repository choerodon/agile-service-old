package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.dto.ComponentForListDTO;
import io.choerodon.agile.api.dto.IssueDTO;
import io.choerodon.agile.api.dto.SearchDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.api.dto.IssueComponentDTO;
import io.choerodon.agile.app.service.IssueComponentService;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

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
    @CustomPageRequest
    @PostMapping(value = "/query_all")
    public ResponseEntity<Page<ComponentForListDTO>> listByProjectId(@ApiParam(value = "项目id", required = true)
                                                                     @PathVariable(name = "project_id") Long projectId,
                                                                     @ApiParam(value = "当前模块id")
                                                                     @RequestParam(required = false) Long componentId,
                                                                     @ApiParam(value = "是否包含测试")
                                                                     @RequestParam(required = false, name = "no_issue_test", defaultValue = "false") Boolean noIssueTest,
                                                                     @ApiParam(value = "查询参数")
                                                                     @RequestBody(required = false) SearchDTO searchDTO,
                                                                     @ApiParam(value = "分页信息", required = true)
                                                                     @SortDefault(value = "component_id", direction = Sort.Direction.DESC)
                                                                     @ApiIgnore PageRequest pageRequest) {
        return Optional.ofNullable(issueComponentService.queryComponentByProjectId(projectId, componentId, noIssueTest, searchDTO, pageRequest))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.componentList.get"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据project id查询component,测试服务用")
    @GetMapping
    public ResponseEntity<List<ComponentForListDTO>> listByProjectIdForTest(@ApiParam(value = "项目id", required = true)
                                                                            @PathVariable(name = "project_id") Long projectId,
                                                                            @ApiParam(value = "当前模块id")
                                                                            @RequestParam(required = false) Long componentId,
                                                                            @ApiParam(value = "是否包含测试")
                                                                            @RequestParam(required = false, name = "no_issue_test", defaultValue = "false")
                                                                                    Boolean noIssueTest) {
        return Optional.ofNullable(issueComponentService.listByProjectIdForTest(projectId, componentId, noIssueTest))
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
