package io.choerodon.agile.api.controller.v1;


import io.choerodon.agile.api.dto.BoardColumnDTO;
import io.choerodon.agile.api.dto.PriorityDistributeDTO;
import io.choerodon.agile.api.dto.StatusCategoryDTO;
import io.choerodon.agile.app.service.IterativeWorktableService;
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
 * Created by HuangFuqiang@choerodon.io on 2018/9/4.
 * Email: fuqianghuang01@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/iterative_worktable")
public class IterativeWorktableController {

    @Autowired
    private IterativeWorktableService iterativeWorktableService;

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("迭代冲刺台查询issue优先级分布情况")
    @GetMapping(value = "/priority")
    public ResponseEntity<List<PriorityDistributeDTO>> queryPriorityDistribute(@ApiParam(value = "项目id", required = true)
                                                                               @PathVariable(name = "project_id") Long projectId,
                                                                               @ApiParam(value = "冲刺id", required = true)
                                                                               @RequestParam Long sprintId) {
        return Optional.ofNullable(iterativeWorktableService.queryPriorityDistribute(projectId, sprintId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.PriorityDistribute.get"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("迭代冲刺台查询issue的状态分布")
    @GetMapping(value = "/status")
    public ResponseEntity<List<StatusCategoryDTO>> queryStatusCategoryDistribute(@ApiParam(value = "项目id", required = true)
                                                                                 @PathVariable(name = "project_id") Long projectId,
                                                                                 @ApiParam(value = "冲刺id", required = true)
                                                                                 @RequestParam Long sprintId) {
        return Optional.ofNullable(iterativeWorktableService.queryStatusCategoryDistribute(projectId, sprintId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.StatusDistribute.get"));
    }

}
