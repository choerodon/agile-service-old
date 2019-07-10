package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.vo.DataLogCreateVO;
import io.choerodon.agile.api.vo.DataLogVO;
import io.choerodon.agile.app.service.DataLogService;
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
 * Created by HuangFuqiang@choerodon.io on 2018/6/14.
 * Email: fuqianghuang01@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/data_log")
public class DataLogController {

    @Autowired
    private DataLogService dataLogService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("创建DataLog")
    @PostMapping
    public ResponseEntity<DataLogVO> createDataLog(@ApiParam(value = "项目id", required = true)
                                                     @PathVariable(name = "project_id") Long projectId,
                                                   @ApiParam(value = "data log object", required = true)
                                                     @RequestBody DataLogCreateVO createVO) {
        return Optional.ofNullable(dataLogService.create(projectId, createVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.dataLog.create"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询DataLog列表")
    @GetMapping
    public ResponseEntity<List<DataLogVO>> listByIssueId(@ApiParam(value = "项目id", required = true)
                                                          @PathVariable(name = "project_id") Long projectId,
                                                         @ApiParam(value = "issue id", required = true)
                                                          @RequestParam Long issueId) {
        return Optional.ofNullable(dataLogService.listByIssueId(projectId, issueId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.dataLogList.get"));
    }

}
