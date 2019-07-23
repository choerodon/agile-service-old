package io.choerodon.agile.api.controller.v1;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.agile.api.vo.FieldDataLogVO;
import io.choerodon.agile.app.service.FieldDataLogService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/4/8
 */
@RestController
@RequestMapping("/v1/projects/{project_id}/data_log")
public class FieldDataLogController {

    @Autowired
    private FieldDataLogService fieldDataLogService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "获取某个实体的所有自定义字段操作日志")
    @GetMapping("/list")
    public ResponseEntity<List<FieldDataLogVO>> queryByInstanceId(@ApiParam(value = "项目id", required = true)
                                                                   @PathVariable("project_id") Long projectId,
                                                                  @ApiParam(value = "字段id", required = true)
                                                                   @RequestParam Long instanceId,
                                                                  @ApiParam(value = "方案编码", required = true)
                                                                   @RequestParam String schemeCode) {
        return new ResponseEntity<>(fieldDataLogService.queryByInstanceId(projectId, instanceId, schemeCode), HttpStatus.OK);
    }
}
