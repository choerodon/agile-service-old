package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.app.service.PriorityService;
import io.choerodon.agile.app.service.StateMachineService;
import io.choerodon.agile.api.vo.event.ProjectConfig;
import io.choerodon.agile.api.vo.event.StateMachineSchemeDeployCheckIssue;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author shinan.chen
 * @since 2018/11/21
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}")
public class OrganizationController {

    @Autowired
    private StateMachineService stateMachineService;
    @Autowired
    private PriorityService priorityService;

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation("【内部调用】校验是否可以删除状态机的节点")
    @PostMapping("/state_machine/check_delete_node")
    public ResponseEntity<Map<String, Object>> checkDeleteNode(@ApiParam(value = "组织id", required = true)
                                                               @PathVariable(name = "organization_id") Long organizationId,
                                                               @ApiParam(value = "状态id", required = true)
                                                               @RequestParam(value = "status_id") Long statusId,
                                                               @RequestBody List<ProjectConfig> projectConfigs) {

        return new ResponseEntity<>(stateMachineService.checkDeleteNode(organizationId, statusId, projectConfigs), HttpStatus.CREATED);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation("【内部调用】查询状态机方案变更后对issue的影响")
    @PostMapping("/state_machine/check_state_machine_scheme_change")
    public ResponseEntity<Map<Long, Long>> checkStateMachineSchemeChange(@ApiParam(value = "组织id", required = true)
                                                                         @PathVariable(name = "organization_id") Long organizationId,
                                                                         @RequestBody StateMachineSchemeDeployCheckIssue deployCheckIssue) {

        return new ResponseEntity<>(stateMachineService.checkStateMachineSchemeChange(organizationId, deployCheckIssue), HttpStatus.CREATED);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation("【内部调用】校验删除优先级")
    @PostMapping("/issues/check_priority_delete")
    public ResponseEntity<Long> checkPriorityDelete(@ApiParam(value = "组织id", required = true)
                                                    @PathVariable(name = "organization_id") Long organizationId,
                                                    @ApiParam(value = "priorityId", required = true)
                                                    @RequestParam(value = "priority_id") Long priorityId,
                                                    @RequestBody List<Long> projectIds) {
        return Optional.ofNullable(priorityService.checkPriorityDelete(organizationId, priorityId, projectIds))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.priority.checkPriorityDelete"));
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation("【内部调用】批量更新issue的优先级")
    @PostMapping("/issues/batch_change_issue_priority")
    public ResponseEntity batchChangeIssuePriority(@ApiParam(value = "组织id", required = true)
                                                   @PathVariable(name = "organization_id") Long organizationId,
                                                   @ApiParam(value = "priorityId", required = true)
                                                   @RequestParam(value = "priority_id") Long priorityId,
                                                   @ApiParam(value = "changePriorityId", required = true)
                                                   @RequestParam(value = "change_priority_id") Long changePriorityId,
                                                   @RequestParam(value = "user_id") Long userId,
                                                   @RequestBody List<Long> projectIds) {
        priorityService.batchChangeIssuePriority(organizationId, priorityId, changePriorityId, userId, projectIds);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
