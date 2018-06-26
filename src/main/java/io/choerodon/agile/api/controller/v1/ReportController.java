package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.dto.ReportIssueDTO;
import io.choerodon.agile.app.service.ReportService;
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
 * @since 2018/6/19
 */
@RestController
@RequestMapping(value = "/v1/project/{project_id}/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询冲刺对应的燃尽图报告信息")
    @GetMapping(value = "/{sprintId}/burn_down_report")
    public ResponseEntity<List<ReportIssueDTO>> queryBurnDownReport(@ApiParam(value = "项目id", required = true)
                                                                        @PathVariable(name = "project_id") Long projectId,
                                                                        @ApiParam(value = "sprintId", required = true)
                                                                        @PathVariable Long sprintId,
                                                                        @ApiParam(value = "类型(storyPoints、remainingEstimatedTime、issueCount)", required = true)
                                                                        @RequestParam String type) {
        return Optional.ofNullable(reportService.queryBurnDownReport(projectId, sprintId, type))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.report.queryBurnDownReport"));
    }

}
