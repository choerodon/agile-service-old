package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.dto.ProjectDefaultSettingDTO;
import io.choerodon.agile.api.dto.ProjectInfoDTO;
import io.choerodon.agile.app.service.ProjectInfoService;
import io.choerodon.agile.domain.agile.rule.ProjectInfoRule;
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
 * @since 2018/6/15
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/project_info")
public class ProjectInfoController {

    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private ProjectInfoRule projectInfoRule;

    @Permission(level = ResourceLevel.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation("更新projectInfo")
    @PutMapping
    public ResponseEntity<ProjectInfoDTO> updateProjectInfo(@ApiParam(value = "项目id", required = true)
                                                            @PathVariable(name = "project_id") Long projectId,
                                                            @ApiParam(value = "projectInfo对象", required = true)
                                                            @RequestBody ProjectInfoDTO projectInfoDTO) {
        projectInfoRule.verifyUpdateData(projectInfoDTO, projectId);
        return Optional.ofNullable(projectInfoService.updateProjectInfo(projectInfoDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.projectInfo.update"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据项目id查询projectInfo")
    @GetMapping
    public ResponseEntity<ProjectInfoDTO> queryProjectInfoByProjectId(@ApiParam(value = "项目id", required = true)
                                                                      @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(projectInfoService.queryProjectInfoByProjectId(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.projectInfo.queryProjectInfoByProjectId"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据项目id查询项目默认设置")
    @GetMapping
    public ResponseEntity<ProjectDefaultSettingDTO> queryProjectDefaultSettingByProjectId(@ApiParam(value = "项目id", required = true)
                                                                                          @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(projectInfoService.queryProjectDefaultSettingByProjectId(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.projectInfo.queryProjectDefaultSettingByProjectId"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("项目code重名校验")
    @PostMapping("check")
    public ResponseEntity<Boolean> checkProjectCode(@ApiParam(value = "项目id", required = true)
                                                    @PathVariable(name = "project_id") Long projectId,
                                                    @RequestParam String projectName) {
        return Optional.ofNullable(projectInfoService.checkProjectCode(projectName))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.projectInfo.checkProjectCode"));
    }
}
