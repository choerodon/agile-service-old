package io.choerodon.agile.api.controller.v1;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.agile.api.vo.ProjectConfigDetailVO;
import io.choerodon.agile.app.service.ProjectConfigService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shinan.chen
 * @date 2018/10/24
 */

@RestController
@RequestMapping("/v1/projects/{project_id}/project_config")
public class ProjectConfigController {

    @Autowired
    ProjectConfigService projectConfigService;

    @Permission(type = ResourceType.PROJECT)
    @ApiOperation(value = "获取项目配置方案信息")
    @GetMapping
    public ResponseEntity<ProjectConfigDetailVO> queryById(@PathVariable("project_id") Long projectId) {
        return new ResponseEntity<>(projectConfigService.queryById(projectId), HttpStatus.OK);
    }
}
