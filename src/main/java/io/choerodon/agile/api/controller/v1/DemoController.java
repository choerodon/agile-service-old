package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.app.service.DemoService;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/01/07.
 * Email: fuqianghuang01@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/demo")
public class DemoController {

    @Autowired
    private DemoService demoService;

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("demo初始化")
    @PostMapping
    public ResponseEntity demoInit(@ApiParam(value = "项目id", required = true)
                                   @PathVariable(name = "project_id") Long projectId) {
        demoService.demoInit(projectId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("demo删除数据")
    @DeleteMapping
    public ResponseEntity demoDelete(@ApiParam(value = "项目id", required = true)
                                     @PathVariable(name = "project_id") Long projectId) {
        demoService.demoDelete(projectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
