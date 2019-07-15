package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.vo.ProjectInfoVO;
import io.choerodon.agile.api.validator.ProjectInfoValidator;
import io.choerodon.agile.app.service.ProjectInfoService;
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
    private ProjectInfoValidator projectInfoValidator;

    @Permission(type = ResourceType.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation("更新projectInfo")
    @PutMapping
    public ResponseEntity<ProjectInfoVO> updateProjectInfo(@ApiParam(value = "项目id", required = true)
                                                            @PathVariable(name = "project_id") Long projectId,
                                                           @ApiParam(value = "projectInfo对象", required = true)
                                                            @RequestBody ProjectInfoVO projectInfoVO) {
        projectInfoValidator.verifyUpdateData(projectInfoVO, projectId);
        return Optional.ofNullable(projectInfoService.updateProjectInfo(projectInfoVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.projectInfo.update"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据项目id查询projectInfo")
    @GetMapping
    public ResponseEntity<ProjectInfoVO> queryProjectInfoByProjectId(@ApiParam(value = "项目id", required = true)
                                                                      @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(projectInfoService.queryProjectInfoByProjectId(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.projectInfo.queryProjectInfoByProjectId"));
    }

}
