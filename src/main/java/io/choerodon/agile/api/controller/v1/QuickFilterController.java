package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.app.service.QuickFilterFieldService;
import io.choerodon.agile.app.service.QuickFilterService;
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
 * Created by HuangFuqiang@choerodon.io on 2018/6/13.
 * Email: fuqianghuang01@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/quick_filter")
public class QuickFilterController {

    @Autowired
    private QuickFilterService quickFilterService;

    @Autowired
    private QuickFilterFieldService quickFilterFieldService;

    private static final String DRAG_ERROR = "error.filter.dragVersion";

    @Permission(type = ResourceType.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation("创建quick filter")
    @PostMapping
    public ResponseEntity<QuickFilterVO> create(@ApiParam(value = "项目id", required = true)
                                                 @PathVariable(name = "project_id") Long projectId,
                                                @ApiParam(value = "quick filter object", required = true)
                                                 @RequestBody QuickFilterVO quickFilterVO) {
        return Optional.ofNullable(quickFilterService.create(projectId, quickFilterVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.quickFilter.create"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("修改quick filter")
    @PutMapping(value = "/{filterId}")
    public ResponseEntity<QuickFilterVO> update(@ApiParam(value = "项目id", required = true)
                                                 @PathVariable(name = "project_id") Long projectId,
                                                @ApiParam(value = "filter id", required = true)
                                                 @PathVariable Long filterId,
                                                @ApiParam(value = "quick filter object", required = true)
                                                 @RequestBody QuickFilterVO quickFilterVO) {
        return Optional.ofNullable(quickFilterService.update(projectId, filterId, quickFilterVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.quickFilter.update"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("删除quick filter")
    @DeleteMapping(value = "/{filterId}")
    public ResponseEntity<QuickFilterVO> deleteById(@ApiParam(value = "项目id", required = true)
                                                     @PathVariable(name = "project_id") Long projectId,
                                                    @ApiParam(value = "filter id", required = true)
                                                     @PathVariable Long filterId) {
        quickFilterService.deleteById(projectId, filterId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据id查询quick filter")
    @GetMapping(value = "/{filterId}")
    public ResponseEntity<QuickFilterVO> queryById(@ApiParam(value = "项目id", required = true)
                                                    @PathVariable(name = "project_id") Long projectId,
                                                   @ApiParam(value = "filter id", required = true)
                                                    @PathVariable Long filterId) {
        return Optional.ofNullable(quickFilterService.queryById(projectId, filterId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.quickFilter.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询quick filter列表")
    @PostMapping(value = "/query_all")
    public ResponseEntity<List<QuickFilterVO>> listByProjectId(@ApiParam(value = "项目id", required = true)
                                                                @PathVariable(name = "project_id") Long projectId,
                                                               @RequestBody(required = false) QuickFilterSearchVO quickFilterSearchVO) {
        return Optional.ofNullable(quickFilterService.listByProjectId(projectId, quickFilterSearchVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.quickFilter.list"));
    }


    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询quick filter field列表")
    @GetMapping("/fields")
    public ResponseEntity<List<QuickFilterFieldVO>> list(@ApiParam(value = "项目id", required = true)
                                                          @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(quickFilterFieldService.list(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.quickFilterField.list"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "拖动过滤位置")
    @PutMapping(value = "/drag")
    public ResponseEntity<QuickFilterVO> dragFilter(@ApiParam(value = "项目id", required = true)
                                                     @PathVariable(name = "project_id") Long projectId,
                                                    @ApiParam(value = "排序对象", required = true)
                                                     @RequestBody QuickFilterSequenceVO quickFilterSequenceVO) {
        return Optional.ofNullable(quickFilterService.dragFilter(projectId, quickFilterSequenceVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException(DRAG_ERROR));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("快速搜索重名校验")
    @GetMapping("/check_name")
    public ResponseEntity<Boolean> checkName(@ApiParam(value = "项目id", required = true)
                                             @PathVariable(name = "project_id") Long projectId,
                                             @ApiParam(value = "快速搜索名称", required = true)
                                             @RequestParam String quickFilterName) {
        return Optional.ofNullable(quickFilterService.checkName(projectId, quickFilterName))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.checkName.get"));
    }


}
