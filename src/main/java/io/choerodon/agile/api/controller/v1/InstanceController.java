package io.choerodon.agile.api.controller.v1;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.base.BaseController;
import io.choerodon.agile.infra.statemachineclient.dto.ExecuteResult;
import io.choerodon.agile.api.vo.StateMachineTransformVO;
import io.choerodon.agile.api.vo.event.TransformInfo;
import io.choerodon.agile.app.service.InstanceService;
import io.choerodon.agile.infra.cache.InstanceCache;
import io.choerodon.agile.infra.statemachineclient.dto.InputDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @date 2018/9/17
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/instances")
public class InstanceController extends BaseController {

    @Autowired
    private InstanceService instanceService;
    @Autowired
    private InstanceCache instanceCache;

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "创建状态机实例")
    @PostMapping(value = "/start_instance")
    public ResponseEntity<ExecuteResult> startInstance(@PathVariable("organization_id") Long organizationId,
                                                       @RequestParam("service_code") String serviceCode,
                                                       @RequestParam("state_machine_id") Long stateMachineId,
                                                       @RequestBody InputDTO inputDTO) {
        ExecuteResult result = instanceService.startInstance(organizationId, serviceCode, stateMachineId, inputDTO);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "执行状态转换，并返回转换后的状态")
    @PostMapping(value = "/execute_transform")
    public ResponseEntity<ExecuteResult> executeTransform(@PathVariable("organization_id") Long organizationId,
                                                          @RequestParam("service_code") String serviceCode,
                                                          @RequestParam("state_machine_id") Long stateMachineId,
                                                          @RequestParam("current_status_id") Long currentStatusId,
                                                          @RequestParam("transform_id") Long transformId,
                                                          @RequestBody InputDTO inputDTO) {
        ExecuteResult result = instanceService.executeTransform(organizationId, serviceCode, stateMachineId, currentStatusId, transformId, inputDTO);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "获取当前状态拥有的转换列表，feign调用对应服务的条件验证")
    @GetMapping(value = "/transform_list")
    public ResponseEntity<List<TransformInfo>> queryListTransform(@PathVariable("organization_id") Long organizationId,
                                                                  @RequestParam("service_code") String serviceCode,
                                                                  @RequestParam("state_machine_id") Long stateMachineId,
                                                                  @RequestParam("instance_id") Long instanceId,
                                                                  @RequestParam("current_status_id") Long currentStateId) {
        return new ResponseEntity<>(instanceService.queryListTransform(organizationId, serviceCode, stateMachineId, instanceId, currentStateId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "获取状态机的初始状态")
    @GetMapping(value = "/query_init_status_id")
    public ResponseEntity<Long> queryInitStatusId(@PathVariable("organization_id") Long organizationId,
                                                  @RequestParam("state_machine_id") Long stateMachineId) {
        return new ResponseEntity<>(instanceService.queryInitStatusId(organizationId, stateMachineId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "获取状态机对应的初始状态Map")
    @GetMapping(value = "/query_init_status_ids")
    public ResponseEntity<Map<Long, Long>> queryInitStatusIds(@PathVariable("organization_id") Long organizationId,
                                                              @RequestParam("state_machine_id") List<Long> stateMachineIds) {
        return new ResponseEntity<>(instanceService.queryInitStatusIds(organizationId, stateMachineIds), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "创建实例时，获取状态机的初始转换，包括转换的configs")
    @GetMapping(value = "/query_init_transform")
    public ResponseEntity<StateMachineTransformVO> queryInitTransform(@PathVariable("organization_id") Long organizationId,
                                                                      @RequestParam("state_machine_id") Long stateMachineId) {
        return new ResponseEntity<>(instanceService.queryInitTransform(organizationId, stateMachineId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "手动清理状态机实例")
    @GetMapping(value = "/cleanInstance")
    @Transactional(rollbackFor = Exception.class)
    public void cleanInstance(@PathVariable("organization_id") Long organizationId,
                              @RequestParam("is_clean_all") Boolean isCleanAll) {
        if (isCleanAll) {
            instanceCache.cleanAllInstances();
        } else {
            instanceCache.cleanInstanceTask();
        }
    }
}