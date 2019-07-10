package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.vo.WorkLogVO;
import io.choerodon.agile.app.service.WorkLogService;
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
 * Created by HuangFuqiang@choerodon.io on 2018/5/18.
 * Email: fuqianghuang01@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/work_log")
public class WorkLogController {

    @Autowired
    private WorkLogService workLogService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("创建work log")
    @PostMapping
    public ResponseEntity<WorkLogVO> createWorkLog(@ApiParam(value = "项目id", required = true)
                                                    @PathVariable(name = "project_id") Long projectId,
                                                   @ApiParam(value = "work log object", required = true)
                                                    @RequestBody WorkLogVO workLogVO) {
        return Optional.ofNullable(workLogService.createWorkLog(projectId, workLogVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.workLog.create"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("修改work log")
    @PatchMapping(value = "/{logId}")
    public ResponseEntity<WorkLogVO> updateWorkLog(@ApiParam(value = "项目id", required = true)
                                                    @PathVariable(name = "project_id") Long projectId,
                                                   @ApiParam(value = "log id", required = true)
                                                    @PathVariable Long logId,
                                                   @ApiParam(value = "work log object", required = true)
                                                    @RequestBody WorkLogVO workLogVO) {
        return Optional.ofNullable(workLogService.updateWorkLog(projectId, logId, workLogVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.workLog.update"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation("删除work log")
    @DeleteMapping(value = "/{logId}")
    public ResponseEntity deleteWorkLog(@ApiParam(value = "项目id", required = true)
                                        @PathVariable(name = "project_id") Long projectId,
                                        @ApiParam(value = "log id", required = true)
                                        @PathVariable Long logId) {
        workLogService.deleteWorkLog(projectId, logId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据logId查询work log")
    @GetMapping(value = "/{logId}")
    public ResponseEntity queryWorkLogById(@ApiParam(value = "项目id", required = true)
                                           @PathVariable(name = "project_id") Long projectId,
                                           @ApiParam(value = "log id", required = true)
                                           @PathVariable Long logId) {
        return Optional.ofNullable(workLogService.queryWorkLogById(projectId, logId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.workLog.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据issue id查询work log列表")
    @GetMapping(value = "/issue/{issueId}")
    public ResponseEntity<List<WorkLogVO>> queryWorkLogListByIssueId(@ApiParam(value = "项目id", required = true)
                                                                      @PathVariable(name = "project_id") Long projectId,
                                                                     @ApiParam(value = "issue id", required = true)
                                                                      @PathVariable Long issueId) {
        return Optional.ofNullable(workLogService.queryWorkLogListByIssueId(projectId, issueId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.workLogList.get"));
    }

}
