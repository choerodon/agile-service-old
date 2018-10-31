package io.choerodon.agile.api.controller.v1;


import io.choerodon.agile.api.dto.WorkCalendarHolidayRefDTO;
import io.choerodon.agile.app.service.WorkCalendarHolidayRefService;
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
 * @since 2018/10/9
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/work_calendar_holiday_refs")
public class WorkCalendarHolidayRefController {

    @Autowired
    private WorkCalendarHolidayRefService workCalendarHolidayRefService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("按年份更新工作日历假期")
    @PostMapping
    public ResponseEntity updateWorkCalendarHolidayRefByYear(@ApiParam(value = "项目id", required = true)
                                                             @PathVariable(name = "organization_id") Long organizationId,
                                                             @ApiParam(value = "要更新的年份", required = true)
                                                             @RequestParam Integer year) {
        workCalendarHolidayRefService.updateWorkCalendarHolidayRefByYear(year);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Permission(level = ResourceLevel.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.ORGANIZATION_MEMBER})
    @ApiOperation("根据年份查询工作日历假期")
    @GetMapping
    public ResponseEntity<List<WorkCalendarHolidayRefDTO>> queryWorkCalendarHolidayRelByYear(@ApiParam(value = "项目id", required = true)
                                                                                             @PathVariable(name = "organization_id") Long organizationId,
                                                                                             @ApiParam(value = "要更新的年份", required = true)
                                                                                             @RequestParam Integer year) {
        return Optional.ofNullable(workCalendarHolidayRefService.queryWorkCalendarHolidayRelByYear(year))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.WorkCalendarHolidayRefController.queryWorkCalendarHolidayRelByYear"));
    }


}
