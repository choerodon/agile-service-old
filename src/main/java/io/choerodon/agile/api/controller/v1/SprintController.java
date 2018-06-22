package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.dto.*;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.agile.app.service.SprintService;
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

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by jian_zhang02@163.com on 2018/5/14.
 */
@RestController
@RequestMapping(value = "/v1/project/{project_id}/sprint")
public class SprintController {

    @Autowired
    private SprintService sprintService;

    private static final String CREATE_ERROR = "error.sprint.create";
    private static final String UPDATE_ERROR = "error.sprint.update";
    private static final String DELETE_ERROR = "error.sprint.delete";
    private static final String QUERY_ERROR = "error.spring.query";
    private static final String QUERY_NAME_ERROR = "error.sprintName.query";
    private static final String OPEN_ERROR = "error.sprint.open";
    private static final String CLOSE_ERROR = "error.sprint.close";
    private static final String QUERY_SPRINT_MESSAGE_ERROR = "error.sprintMessage.query";

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "创建冲刺")
    @PostMapping
    public ResponseEntity<SprintDetailDTO> createSprint(@ApiParam(value = "项目id", required = true)
                                                        @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(sprintService.createSprint(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException(CREATE_ERROR));
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "更新冲刺部分字段")
    @PutMapping
    public ResponseEntity<SprintDetailDTO> updateSprint(@ApiParam(value = "项目id", required = true)
                                                        @PathVariable(name = "project_id") Long projectId,
                                                        @ApiParam(value = "冲刺DTO对象", required = true)
                                                        @RequestBody @Valid SprintUpdateDTO sprintUpdateDTO) {
        return Optional.ofNullable(sprintService.updateSprint(projectId, sprintUpdateDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException(UPDATE_ERROR));
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation("根据id删除冲刺")
    @DeleteMapping(value = "/{sprintId}")
    public ResponseEntity<Boolean> deleteSprint(@ApiParam(value = "项目id", required = true)
                                                @PathVariable(name = "project_id") Long projectId,
                                                @ApiParam(value = "sprintId", required = true)
                                                @PathVariable Long sprintId) {
        return Optional.ofNullable(sprintService.deleteSprint(projectId, sprintId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.NO_CONTENT))
                .orElseThrow(() -> new CommonException(DELETE_ERROR));
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "联合查询sprint及其issue")
    @PostMapping(value = "/issues")
    public ResponseEntity<Map<String, Object>> queryByProjectId(@ApiParam(value = "项目id", required = true)
                                                                @PathVariable(name = "project_id") Long projectId,
                                                                @ApiParam(value = "查询参数", required = false)
                                                                @RequestBody(required = false) Map<String, Object> searchParamMap,
                                                                @ApiParam(value = "quick filter", required = false)
                                                                @RequestParam(required = false) List<Long> quickFilterIds) {
        return Optional.ofNullable(sprintService.queryByProjectId(projectId, searchParamMap, quickFilterIds))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException(QUERY_ERROR));
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "查询冲刺名")
    @PostMapping(value = "/names")
    public ResponseEntity<List<SprintNameDTO>> queryNameByOptions(@ApiParam(value = "项目id", required = true)
                                                                  @PathVariable(name = "project_id") Long projectId,
                                                                  @ApiParam(value = "状态列表", required = false)
                                                                  @RequestBody(required = false) List<String> sprintStatusCodes) {
        return Optional.ofNullable(sprintService.queryNameByOptions(projectId, sprintStatusCodes))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException(QUERY_NAME_ERROR));
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "开启冲刺")
    @PostMapping(value = "/start")
    public ResponseEntity<SprintDetailDTO> startSprint(@ApiParam(value = "项目id", required = true)
                                                       @PathVariable(name = "project_id") Long projectId,
                                                       @ApiParam(value = "冲刺DTO对象", required = true)
                                                       @RequestBody @Valid SprintUpdateDTO sprintUpdateDTO) {
        return Optional.ofNullable(sprintService.startSprint(projectId, sprintUpdateDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException(OPEN_ERROR));
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "完成冲刺")
    @PostMapping(value = "/complete")
    public ResponseEntity<Boolean> completeSprint(@ApiParam(value = "项目id", required = true)
                                                  @PathVariable(name = "project_id") Long projectId,
                                                  @ApiParam(value = "完成冲刺对象", required = true)
                                                  @RequestBody @Valid SprintCompleteDTO sprintCompleteDTO) {
        return Optional.ofNullable(sprintService.completeSprint(projectId, sprintCompleteDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException(CLOSE_ERROR));
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "查询sprint名及issue统计信息")
    @GetMapping(value = "/{sprintId}/names")
    public ResponseEntity<SprintCompleteMessageDTO> queryCompleteMessageBySprintId(@ApiParam(value = "项目id", required = true)
                                                                                   @PathVariable(name = "project_id") Long projectId,
                                                                                   @ApiParam(value = "冲刺id", required = true)
                                                                                   @PathVariable Long sprintId) {
        return Optional.ofNullable(sprintService.queryCompleteMessageBySprintId(projectId, sprintId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException(QUERY_SPRINT_MESSAGE_ERROR));
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "根据sprintId查询冲刺信息")
    @GetMapping(value = "/{sprintId}")
    public ResponseEntity<SprintDetailDTO> querySprintById(@ApiParam(value = "项目id", required = true)
                                                           @PathVariable(name = "project_id") Long projectId,
                                                           @ApiParam(value = "冲刺id", required = true)
                                                           @PathVariable Long sprintId) {
        return Optional.ofNullable(sprintService.querySprintById(projectId, sprintId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException(QUERY_ERROR));
    }

    @Permission(level = ResourceLevel.PROJECT)
    @CustomPageRequest
    @ApiOperation(value = "根据状态查已完成冲刺issue信息")
    @GetMapping(value = "/{sprintId}/issues")
    public ResponseEntity<Page<IssueListDTO>> queryIssueByOptions(@ApiParam(value = "项目id", required = true)
                                                                  @PathVariable(name = "project_id") Long projectId,
                                                                  @ApiParam(value = "冲刺id", required = true)
                                                                  @PathVariable Long sprintId,
                                                                  @ApiParam(value = "状态", required = true)
                                                                  @RequestParam String status,
                                                                  @ApiParam(value = "分页信息", required = true)
                                                                  @SortDefault(value = "issue_id", direction = Sort.Direction.DESC)
                                                                  @ApiIgnore PageRequest pageRequest) {
        return Optional.ofNullable(sprintService.queryIssueByOptions(projectId, sprintId, status, pageRequest))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException(QUERY_ERROR));
    }


}
