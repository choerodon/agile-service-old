package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.app.service.TimeZoneWorkCalendarService;
import io.choerodon.agile.domain.agile.rule.SprintRule;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import com.github.pagehelper.PageInfo;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.agile.app.service.SprintService;
import io.choerodon.mybatis.annotation.SortDefault;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.base.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.*;

/**
 * Created by jian_zhang02@163.com on 2018/5/14.
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/sprint")
public class SprintController {

    @Autowired
    private SprintService sprintService;

    @Autowired
    private TimeZoneWorkCalendarService timeZoneWorkCalendarService;

    @Autowired
    private SprintRule sprintRule;

    private static final String CREATE_ERROR = "error.sprint.create";
    private static final String UPDATE_ERROR = "error.sprint.update";
    private static final String DELETE_ERROR = "error.sprint.delete";
    private static final String QUERY_ERROR = "error.spring.query";
    private static final String QUERY_NAME_ERROR = "error.sprintName.query";
    private static final String OPEN_ERROR = "error.sprint.open";
    private static final String CLOSE_ERROR = "error.sprint.close";
    private static final String QUERY_SPRINT_MESSAGE_ERROR = "error.sprintMessage.query";

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "创建冲刺")
    @PostMapping
    public ResponseEntity<SprintDetailDTO> createSprint(@ApiParam(value = "项目id", required = true)
                                                        @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(sprintService.createSprint(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException(CREATE_ERROR));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
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

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
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

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "联合查询sprint及其issue")
    @PostMapping(value = "/issues")
    public ResponseEntity<Map<String, Object>> queryByProjectId(@ApiParam(value = "项目id", required = true)
                                                                @PathVariable(name = "project_id") Long projectId,
                                                                @ApiParam(value = "组织id", required = true)
                                                                @RequestParam Long organizationId,
                                                                @ApiParam(value = "查询参数", required = false)
                                                                @RequestBody(required = false) Map<String, Object> searchParamMap,
                                                                @ApiParam(value = "quick filter")
                                                                @RequestParam(required = false) List<Long> quickFilterIds,
                                                                @ApiParam(value = "经办人搜索", required = false)
                                                                @RequestParam(required = false) List<Long> assigneeFilterIds) {
        return Optional.ofNullable(sprintService.queryByProjectId(projectId, searchParamMap, quickFilterIds, organizationId, assigneeFilterIds))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException(QUERY_ERROR));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
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

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
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

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
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

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
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

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
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

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @CustomPageRequest
    @ApiOperation(value = "根据状态查已完成冲刺issue信息")
    @GetMapping(value = "/{sprintId}/issues")
    public ResponseEntity<PageInfo<IssueListDTO>> queryIssueByOptions(@ApiParam(value = "项目id", required = true)
                                                                  @PathVariable(name = "project_id") Long projectId,
                                                                  @ApiParam(value = "冲刺id", required = true)
                                                                  @PathVariable Long sprintId,
                                                                  @ApiParam(value = "状态", required = true)
                                                                  @RequestParam String status,
                                                                  @ApiParam(value = "组织id", required = true)
                                                                  @RequestParam Long organizationId,
                                                                  @ApiParam(value = "分页信息", required = true)
                                                                  @SortDefault(value = "issue_id", direction = Sort.Direction.DESC)
                                                                  @ApiIgnore PageRequest pageRequest) {
        return Optional.ofNullable(sprintService.queryIssueByOptions(projectId, sprintId, status, pageRequest, organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException(QUERY_ERROR));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "查询项目下创建冲刺时当前默认的名称")
    @GetMapping(value = "/current_create_name")
    public ResponseEntity<String> queryCurrentSprintCreateName(@ApiParam(value = "项目id", required = true)
                                                               @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(sprintService.queryCurrentSprintCreateName(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.CurrentSprintCreateName.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "根据项目id和冲刺名称创建冲刺")
    @PostMapping(value = "/create")
    public ResponseEntity<SprintDetailDTO> createBySprintName(@ApiParam(value = "项目id", required = true)
                                                              @PathVariable(name = "project_id") Long projectId,
                                                              @ApiParam(value = "冲刺名称", required = true)
                                                              @RequestParam String sprintName) {
        return Optional.ofNullable(sprintService.createBySprintName(projectId, sprintName))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.createBySprintName.post"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "查询未关闭的冲刺")
    @GetMapping(value = "/unclosed")
    public ResponseEntity<List<SprintUnClosedDTO>> queryUnClosedSprint(@ApiParam(value = "项目id", required = true)
                                                                       @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(sprintService.queryUnClosedSprint(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.SprintUnClosedDTOList.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "查询活跃冲刺")
    @GetMapping(value = "/active/{organization_id}")
    public ResponseEntity<ActiveSprintDTO> queryActiveSprint(@ApiParam(value = "项目id", required = true)
                                                             @PathVariable(name = "project_id") Long projectId,
                                                             @ApiParam(value = "组织id", required = true)
                                                             @PathVariable(name = "organization_id") Long organizationId) {
        return Optional.ofNullable(sprintService.queryActiveSprint(projectId, organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.activeSprint.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "查询冲刺的时间范围内非工作日(包含周六周天)")
    @GetMapping(value = "/query_non_workdays/{sprint_id}/{organization_id}")
    public ResponseEntity<List<String>> queryNonWorkdays(@ApiParam(value = "项目id", required = true)
                                                         @PathVariable(name = "project_id") Long projectId,
                                                         @ApiParam(value = "冲刺", required = true)
                                                         @PathVariable(name = "sprint_id") Long sprintId,
                                                         @ApiParam(value = "组织id", required = true)
                                                         @PathVariable(name = "organization_id") Long organizationId) {
        return Optional.ofNullable(sprintService.queryNonWorkdays(projectId, sprintId, organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.queryNonWorkdays.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("获取冲刺有关于组织层时区设置")
    @GetMapping(value = "/time_zone_detail/{organization_id}")
    public ResponseEntity<TimeZoneWorkCalendarRefDetailDTO> queryTimeZoneWorkCalendarDetail(@ApiParam(value = "项目id", required = true)
                                                                                            @PathVariable(name = "project_id") Long projectId,
                                                                                            @ApiParam(value = "组织id", required = true)
                                                                                            @PathVariable(name = "organization_id") Long organizationId,
                                                                                            @ApiParam(value = "组织id", required = true)
                                                                                            @RequestParam(name = "year") Integer year) {
        return Optional.ofNullable(timeZoneWorkCalendarService.queryTimeZoneWorkCalendarDetail(organizationId, year))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.TimeZoneWorkCalendarController.queryTimeZoneWorkCalendarDetail"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("冲刺重名校验")
    @GetMapping(value = "/check_name")
    public ResponseEntity<Boolean> checkName(@ApiParam(value = "项目id", required = true)
                                             @PathVariable(name = "project_id") Long projectId,
                                             @ApiParam(value = "sprint name", required = true)
                                             @RequestParam String sprintName) {
        return Optional.ofNullable(sprintService.checkName(projectId, sprintName))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.sprintName.check"));
    }


}
