package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.dto.*;
import io.choerodon.agile.app.service.TimeZoneWorkCalendarService;
import io.choerodon.agile.domain.agile.rule.TimeZoneWorkCalendarRule;
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
 * @since 2018/10/12
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/time_zone_work_calendars")
public class TimeZoneWorkCalendarController {

    @Autowired
    private TimeZoneWorkCalendarService timeZoneWorkCalendarService;
    @Autowired
    private TimeZoneWorkCalendarRule timeZoneWorkCalendarRule;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("获取时区设置")
    @GetMapping
    public ResponseEntity<TimeZoneWorkCalendarDTO> queryTimeZoneWorkCalendar(@ApiParam(value = "组织id", required = true)
                                                                             @PathVariable(name = "organization_id") Long organizationId) {
        return Optional.ofNullable(timeZoneWorkCalendarService.queryTimeZoneWorkCalendar(organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.TimeZoneWorkCalendarController.queryTimeZoneWorkCalendar"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("修改时区设置")
    @PutMapping(value = "/{timeZoneId}")
    public ResponseEntity<TimeZoneWorkCalendarDTO> updateTimeZoneWorkCalendar(@ApiParam(value = "组织id", required = true)
                                                                              @PathVariable(name = "organization_id") Long organizationId,
                                                                              @ApiParam(value = "时区id", required = true)
                                                                              @PathVariable(name = "timeZoneId") Long timeZoneId,
                                                                              @ApiParam(value = "timeZoneWorkCalendar", required = true)
                                                                              @RequestBody TimeZoneWorkCalendarUpdateDTO timeZoneWorkCalendarUpdateDTO) {
        return Optional.ofNullable(timeZoneWorkCalendarService.updateTimeZoneWorkCalendar(organizationId, timeZoneId, timeZoneWorkCalendarUpdateDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.TimeZoneWorkCalendarController.updateTimeZoneWorkCalendar"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("创建时区工作日历")
    @PostMapping("/ref/{timeZoneId}")
    public ResponseEntity<TimeZoneWorkCalendarRefDTO> createTimeZoneWorkCalendarRef(@ApiParam(value = "组织id", required = true)
                                                                                    @PathVariable(name = "organization_id") Long organizationId,
                                                                                    @ApiParam(value = "时区id", required = true)
                                                                                    @PathVariable(name = "timeZoneId") Long timeZoneId,
                                                                                    @ApiParam(value = "日期", required = true)
                                                                                    @RequestBody TimeZoneWorkCalendarRefCreateDTO timeZoneWorkCalendarRefCreateDTO) {
        timeZoneWorkCalendarRule.verifyCreateTimeZoneWorkCalendarRef(organizationId, timeZoneId);
        return Optional.ofNullable(timeZoneWorkCalendarService.createTimeZoneWorkCalendarRef(organizationId, timeZoneId, timeZoneWorkCalendarRefCreateDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.TimeZoneWorkCalendarController.createTimeZoneWorkCalendarRef"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("删除时区工作日历")
    @DeleteMapping(value = "/ref/{calendarId}")
    public ResponseEntity deleteTimeZoneWorkCalendarRef(@ApiParam(value = "组织id", required = true)
                                                        @PathVariable(name = "organization_id") Long organizationId,
                                                        @ApiParam(value = "timeZoneWorkCalendar", required = true)
                                                        @PathVariable(name = "calendarId") Long calendarId) {
        timeZoneWorkCalendarService.deleteTimeZoneWorkCalendarRef(organizationId, calendarId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("获取时区工作日历")
    @GetMapping(value = "/ref/{timeZoneId}")
    public ResponseEntity<List<TimeZoneWorkCalendarRefDTO>> queryTimeZoneWorkCalendarRefByTimeZoneId(@ApiParam(value = "组织id", required = true)
                                                                                                     @PathVariable(name = "organization_id") Long organizationId,
                                                                                                     @ApiParam(value = "时区id", required = true)
                                                                                                     @PathVariable(name = "timeZoneId") Long timeZoneId) {
        return Optional.ofNullable(timeZoneWorkCalendarService.queryTimeZoneWorkCalendarRefByTimeZoneId(organizationId, timeZoneId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.TimeZoneWorkCalendarController.queryTimeZoneWorkCalendarRefByTimeZoneId"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.ORGANIZATION_MEMBER})
    @ApiOperation("获取时区下的工作日历")
    @GetMapping(value = "/detail")
    public ResponseEntity<TimeZoneWorkCalendarRefDetailDTO> queryTimeZoneWorkCalendarDetail(@ApiParam(value = "组织id", required = true)
                                                                                            @PathVariable(name = "organization_id") Long organizationId) {
        return Optional.ofNullable(timeZoneWorkCalendarService.queryTimeZoneWorkCalendarDetail(organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.TimeZoneWorkCalendarController.queryTimeZoneWorkCalendarDetail"));
    }


}
