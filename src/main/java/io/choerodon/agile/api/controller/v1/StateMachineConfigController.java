package io.choerodon.agile.api.controller.v1;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.base.BaseController;
import io.choerodon.agile.api.vo.StateMachineConfigVO;
import io.choerodon.agile.app.service.StateMachineConfigService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author peng.jiang, dinghuang123@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/state_machine_config")
public class StateMachineConfigController extends BaseController {

    @Autowired
    private StateMachineConfigService configService;

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "创建配置（草稿）")
    @PostMapping(value = "/{state_machine_id}")
    public ResponseEntity<StateMachineConfigVO> create(@PathVariable("organization_id") Long organizationId,
                                                       @PathVariable("state_machine_id") Long stateMachineId,
                                                       @RequestParam("transform_id") Long transformId,
                                                       @RequestBody StateMachineConfigVO configDTO) {
        return new ResponseEntity<>(configService.create(organizationId, stateMachineId, transformId, configDTO), HttpStatus.CREATED);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "删除配置（草稿）")
    @DeleteMapping(value = "/{config_id}")
    public ResponseEntity<Boolean> delete(@PathVariable("organization_id") Long organizationId, @PathVariable("config_id") Long configId) {
        return new ResponseEntity<>(configService.delete(organizationId, configId), HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "获取转换下的配置（草稿）")
    @GetMapping(value = "/query")
    public ResponseEntity<List<StateMachineConfigVO>> queryByTransformId(@PathVariable("organization_id") Long organizationId,
                                                                         @RequestParam Long transformId,
                                                                         @RequestParam String type) {
        return new ResponseEntity<>(configService.queryByTransformId(organizationId, transformId, type, true), HttpStatus.OK);
    }
}
