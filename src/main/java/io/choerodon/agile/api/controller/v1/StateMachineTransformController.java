package io.choerodon.agile.api.controller.v1;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.base.BaseController;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.agile.api.validator.StateMachineTransformValidator;
import io.choerodon.agile.api.vo.StateMachineTransformVO;
import io.choerodon.agile.app.service.StateMachineTransformService;
import io.choerodon.agile.infra.dataobject.StateMachineTransformDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


/**
 * @author peng.jiang, dinghuang123@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/state_machine_transform")
public class StateMachineTransformController extends BaseController {

    @Autowired
    private StateMachineTransformService transformService;
    @Autowired
    private StateMachineTransformValidator transformValidator;

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "创建转换（草稿）")
    @PostMapping
    public ResponseEntity<StateMachineTransformVO> create(@PathVariable("organization_id") Long organizationId,
                                                          @RequestParam Long stateMachineId,
                                                          @RequestBody StateMachineTransformVO transformDTO) {
        transformValidator.createValidate(transformDTO);
        return new ResponseEntity<>(transformService.create(organizationId, stateMachineId, transformDTO), HttpStatus.CREATED);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "更新转换（草稿）")
    @PutMapping(value = "/{transform_id}")
    public ResponseEntity<StateMachineTransformVO> update(@PathVariable("organization_id") Long organizationId,
                                                          @PathVariable("transform_id") Long transformId,
                                                          @RequestParam Long stateMachineId,
                                                          @RequestBody StateMachineTransformVO transformDTO) {
        transformValidator.updateValidate(transformDTO);
        return new ResponseEntity<>(transformService.update(organizationId, stateMachineId, transformId, transformDTO), HttpStatus.CREATED);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "删除转换（草稿）")
    @DeleteMapping(value = "/{transform_id}")
    public ResponseEntity<Boolean> delete(@PathVariable("organization_id") Long organizationId,
                                          @RequestParam Long stateMachineId,
                                          @PathVariable("transform_id") Long transformId) {
        return new ResponseEntity<>(transformService.delete(organizationId, stateMachineId, transformId), HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "根据id获取转换（草稿）")
    @GetMapping(value = "/{transform_id}")
    public ResponseEntity<StateMachineTransformVO> queryById(@PathVariable("organization_id") Long organizationId,
                                                             @PathVariable("transform_id") Long transformId) {
        return new ResponseEntity<>(transformService.queryById(organizationId, transformId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "创建【全部】转换，所有节点均可转换到当前节点（草稿）")
    @PostMapping(value = "/create_type_all")
    public ResponseEntity<StateMachineTransformVO> createAllStatusTransform(@PathVariable("organization_id") Long organizationId,
                                                                            @RequestParam("state_machine_id") Long stateMachineId,
                                                                            @RequestParam("end_node_id") Long endNodeId) {
        return new ResponseEntity<>(transformService.createAllStatusTransform(organizationId, stateMachineId, endNodeId), HttpStatus.CREATED);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "删除【全部】转换（草稿）")
    @DeleteMapping(value = "/delete_type_all/{transform_id}")
    public ResponseEntity<Boolean> deleteAllStatusTransform(@PathVariable("organization_id") Long organizationId,
                                                            @PathVariable("transform_id") Long transformId) {
        return new ResponseEntity<>(transformService.deleteAllStatusTransform(organizationId, transformId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "更改条件策略")
    @GetMapping(value = "/update_condition_strategy/{transform_id}")
    public ResponseEntity<Boolean> updateConditionStrategy(@PathVariable("organization_id") Long organizationId,
                                                           @PathVariable("transform_id") Long transformId,
                                                           @RequestParam("condition_strategy") String conditionStrategy) {
        return new ResponseEntity<>(transformService.updateConditionStrategy(organizationId, transformId, conditionStrategy), HttpStatus.CREATED);
    }

    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.ORGANIZATION_MEMBER})
    @ApiOperation(value = "校验转换名字在起始终点相同条件下是否未被使用")
    @GetMapping(value = "/check_name")
    public ResponseEntity<Boolean> checkName(@PathVariable("organization_id") Long organizationId,
                                             @RequestParam("startNodeId") Long startNodeId,
                                             @RequestParam("endNodeId") Long endNodeId,
                                             @RequestParam("stateMachineId") Long stateMachineId,
                                             @RequestParam("name") String name) {
        return Optional.ofNullable(transformService.checkName(organizationId, stateMachineId, startNodeId, endNodeId, name))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.transformName.check"));
    }

    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.ORGANIZATION_MEMBER})
    @ApiOperation(value = "敏捷获取转换")
    @GetMapping(value = "/query_deploy_transform")
    public ResponseEntity<StateMachineTransformDTO> queryDeployTransformForAgile(@PathVariable("organization_id") Long organizationId,
                                                                                 @RequestParam("transformId") Long transformId) {
        return Optional.ofNullable(transformService.queryDeployTransformForAgile(organizationId, transformId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.stateMachineTransform.queryDeployTransformForAgile"));
    }
}
