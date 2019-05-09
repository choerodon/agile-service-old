package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.dto.SprintWorkCalendarDTO;
import io.choerodon.agile.api.dto.WorkCalendarRefCreateDTO;
import io.choerodon.agile.api.dto.WorkCalendarRefDTO;
import io.choerodon.agile.app.service.WorkCalendarRefService;
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

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * @author shinan.chen
 * @date 2019/4/28
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/work_calendar_ref")
public class WorkCalendarRefController {

    @Autowired
    private WorkCalendarRefService workCalendarRefService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "创建冲刺工作日历")
    @PostMapping(value = "/sprint/{sprint_id}")
    public ResponseEntity<WorkCalendarRefDTO> createSprintWorkCalendarRef(@ApiParam(value = "项目id", required = true)
                                                                          @PathVariable(name = "project_id") Long projectId,
                                                                          @ApiParam(value = "冲刺id", required = true)
                                                                          @PathVariable(name = "sprint_id") Long sprintId,
                                                                          @ApiParam(value = "创建冲刺工作日对象", required = true)
                                                                          @RequestBody @Valid WorkCalendarRefCreateDTO workCalendarRefCreateDTO) {
        return Optional.ofNullable(workCalendarRefService.createWorkCalendarRef(projectId, sprintId, workCalendarRefCreateDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.workCalendarRef.create"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("获取冲刺工作日历设置")
    @GetMapping(value = "/sprint")
    public ResponseEntity<SprintWorkCalendarDTO> querySprintWorkCalendarRefs(@ApiParam(value = "项目id", required = true)
                                                                             @PathVariable(name = "project_id") Long projectId,
                                                                             @ApiParam(value = "年份", required = true)
                                                                             @RequestParam(name = "year") Integer year) {
        return Optional.ofNullable(workCalendarRefService.querySprintWorkCalendarRefs(projectId, year))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.workCalendarRef.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "创建项目工作日历")
    @PostMapping(value = "/project")
    public ResponseEntity<WorkCalendarRefDTO> createProjectWorkCalendarRef(@ApiParam(value = "项目id", required = true)
                                                                           @PathVariable(name = "project_id") Long projectId,
                                                                           @ApiParam(value = "创建项目工作日对象", required = true)
                                                                           @RequestBody @Valid WorkCalendarRefCreateDTO workCalendarRefCreateDTO) {
        return Optional.ofNullable(workCalendarRefService.createWorkCalendarRef(projectId, null, workCalendarRefCreateDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.workCalendarRef.create"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("获取项目工作日历设置")
    @GetMapping(value = "/project")
    public ResponseEntity<List<WorkCalendarRefDTO>> queryProjectWorkCalendarRefs(@ApiParam(value = "项目id", required = true)
                                                                                 @PathVariable(name = "project_id") Long projectId,
                                                                                 @ApiParam(value = "年份", required = true)
                                                                                 @RequestParam(name = "year") Integer year) {
        return Optional.ofNullable(workCalendarRefService.queryProjectWorkCalendarRefs(projectId, year))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.workCalendarRef.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("删除项目工作日历")
    @DeleteMapping(value = "/{calendar_id}")
    public ResponseEntity deleteProjectWorkCalendarRef(@ApiParam(value = "项目id", required = true)
                                                       @PathVariable(name = "project_id") Long projectId,
                                                       @ApiParam(value = "calendar_id", required = true)
                                                       @PathVariable(name = "calendar_id") Long calendarId) {
        workCalendarRefService.deleteWorkCalendarRef(projectId, calendarId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
