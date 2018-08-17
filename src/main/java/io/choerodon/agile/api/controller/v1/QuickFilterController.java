package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.app.service.QuickFilterFieldService;
import io.choerodon.agile.app.service.QuickFilterService;
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

    @Permission(level = ResourceLevel.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation("创建quick filter")
    @PostMapping
    public ResponseEntity<QuickFilterDTO> create(@ApiParam(value = "项目id", required = true)
                                                  @PathVariable(name = "project_id") Long projectId,
                                                  @ApiParam(value = "quick filter object", required = true)
                                                  @RequestBody QuickFilterDTO quickFilterDTO) {
        return Optional.ofNullable(quickFilterService.create(projectId, quickFilterDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.quickFilter.create"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("修改quick filter")
    @PutMapping(value = "/{filterId}")
    public ResponseEntity<QuickFilterDTO> update(@ApiParam(value = "项目id", required = true)
                                                  @PathVariable(name = "project_id") Long projectId,
                                                  @ApiParam(value = "filter id", required = true)
                                                  @PathVariable Long filterId,
                                                  @ApiParam(value = "quick filter object", required = true)
                                                  @RequestBody QuickFilterDTO quickFilterDTO) {
        return Optional.ofNullable(quickFilterService.update(projectId, filterId, quickFilterDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.quickFilter.update"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("修改quick filter")
    @DeleteMapping(value = "/{filterId}")
    public ResponseEntity<QuickFilterDTO> deleteById(@ApiParam(value = "项目id", required = true)
                                                      @PathVariable(name = "project_id") Long projectId,
                                                      @ApiParam(value = "filter id", required = true)
                                                      @PathVariable Long filterId) {
        quickFilterService.deleteById(projectId, filterId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据id查询quick filter")
    @GetMapping(value = "/{filterId}")
    public ResponseEntity<QuickFilterDTO> queryById(@ApiParam(value = "项目id", required = true)
                                                     @PathVariable(name = "project_id") Long projectId,
                                                     @ApiParam(value = "filter id", required = true)
                                                     @PathVariable Long filterId) {
        return Optional.ofNullable(quickFilterService.queryById(projectId, filterId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.quickFilter.get"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询quick filter列表")
    @GetMapping
    public ResponseEntity<List<QuickFilterDTO>> listByProjectId(@ApiParam(value = "项目id", required = true)
                                                                 @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(quickFilterService.listByProjectId(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.quickFilter.list"));
    }


    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询quick filter field列表")
    @GetMapping("/fields")
    public ResponseEntity<List<QuickFilterFieldDTO>> list(@ApiParam(value = "项目id", required = true)
                                                          @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(quickFilterFieldService.list(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.quickFilterField.list"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "拖动过滤位置")
    @PutMapping(value = "/10.211.97.63")
    public ResponseEntity<QuickFilterDTO> dragFilter(@ApiParam(value = "项目id", required = true)
                                                             @PathVariable(name = "project_id") Long projectId,
                                                             @ApiParam(value = "排序对象", required = true)
                                                             @RequestBody QuickFilterSequenceDTO quickFilterSequenceDTO) {
        return Optional.ofNullable(quickFilterService.dragFilter(projectId, quickFilterSequenceDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException(DRAG_ERROR));
    }


}
