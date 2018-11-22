package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.app.service.StateMachineService;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @since 2018/11/21
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/state_machine")
public class StateMachineController {

    @Autowired
    private StateMachineService stateMachineService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("【内部调用】校验是否可以删除状态机的节点")
    @PostMapping("/check_delete_node")
    public ResponseEntity<Map<String, Object>> checkDeleteNode(@ApiParam(value = "组织id", required = true)
                                                   @PathVariable(name = "organization_id") Long organizationId,
                                                   @ApiParam(value = "状态id", required = true)
                                                   @RequestParam(value = "status_id") Long statusId,
                                                   @RequestBody Map<Long, List<Long>> issueTypeIdsMap) {

        return new ResponseEntity<>(stateMachineService.checkDeleteNode(organizationId, statusId, issueTypeIdsMap), HttpStatus.CREATED);
    }
}
