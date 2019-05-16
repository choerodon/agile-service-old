package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.dto.ProjectRelationshipDTO;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * @author shinan.chen
 * @since 2019/4/22
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/program_info")
public class ProgramInfoController {

    @Autowired
    private ProjectInfoService projectInfoService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("获取项目群关联的团队项目信息")
    @GetMapping(value = "/team")
    public ResponseEntity<List<ProjectRelationshipDTO>> queryProgramTeamInfo(@ApiParam(value = "项目id", required = true)
                                                                             @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(projectInfoService.queryProgramTeamInfo(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.programInfo.team"));
    }
}
