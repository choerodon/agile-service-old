package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.dto.TimeZoneWorkCalendarCreateDTO;
import io.choerodon.agile.api.dto.TimeZoneWorkCalendarDTO;
import io.choerodon.agile.app.service.TimeZoneWorkCalendarService;
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

    @Permission(level = ResourceLevel.ORGANIZATION, roles = InitRoleCode.ORGANIZATION_ADMINISTRATOR)
    @ApiOperation("创建时区设置")
    @PostMapping
    public ResponseEntity<TimeZoneWorkCalendarDTO> createTimeZoneWorkCalendar(@ApiParam(value = "项目id", required = true)
                                                                              @PathVariable(name = "organization_id") Long organizationId,
                                                                              @ApiParam(value = "创建TimeZoneWorkCalendar对象", required = true)
                                                                              @RequestBody TimeZoneWorkCalendarCreateDTO timeZoneWorkCalendarCreateDTO) {
        return Optional.ofNullable(timeZoneWorkCalendarService.createTimeZoneWorkCalendar(organizationId, timeZoneWorkCalendarCreateDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.TimeZoneWorkCalendarController.createTimeZoneWorkCalendar"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION, roles = InitRoleCode.ORGANIZATION_ADMINISTRATOR)
    @ApiOperation("修改时区设置")
    @PutMapping
    public ResponseEntity<TimeZoneWorkCalendarDTO> updateTimeZoneWorkCalendar(@ApiParam(value = "项目id", required = true)
                                                                              @PathVariable(name = "organization_id") Long organizationId,
                                                                              @ApiParam(value = "timeZoneWorkCalendar", required = true)
                                                                              @RequestBody TimeZoneWorkCalendarDTO timeZoneWorkCalendarDTO) {
        return Optional.ofNullable(timeZoneWorkCalendarService.updateTimeZoneWorkCalendar(organizationId, timeZoneWorkCalendarDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.TimeZoneWorkCalendarController.TimeZoneWorkCalendar"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION, roles = InitRoleCode.ORGANIZATION_ADMINISTRATOR)
    @ApiOperation("删除时区设置")
    @DeleteMapping(value = "/{timeZoneId}")
    public ResponseEntity deleteTimeZoneWorkCalendar(@ApiParam(value = "项目id", required = true)
                                                     @PathVariable(name = "organization_id") Long organizationId,
                                                     @ApiParam(value = "timeZoneWorkCalendar", required = true)
                                                     @PathVariable(name = "timeZoneId") Long timeZoneId) {
        timeZoneWorkCalendarService.deleteTimeZoneWorkCalendar(organizationId, timeZoneId);
        return new ResponseEntity(HttpStatus.OK);
    }


}
