package io.choerodon.agile.api.controller.v1;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.app.service.PriorityService;
import io.choerodon.agile.app.service.ProjectConfigService;
import io.choerodon.agile.infra.utils.ProjectUtil;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.base.BaseController;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
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
 * 根据项目id获取对应数据
 *
 * @author shinan.chen
 * @date 2018/10/24
 */

@RestController
@RequestMapping(value = "/v1/projects/{project_id}")
public class SchemeController extends BaseController {

    @Autowired
    private ProjectConfigService projectConfigService;
    @Autowired
    private PriorityService priorityService;
    @Autowired
    private ProjectUtil projectUtil;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "查询项目的问题类型列表")
    @GetMapping(value = "/schemes/query_issue_types")
    public ResponseEntity<List<IssueTypeVO>> queryIssueTypesByProjectId(@PathVariable("project_id") Long projectId, @RequestParam("apply_type") String applyType) {
        return new ResponseEntity<>(projectConfigService.queryIssueTypesByProjectId(projectId, applyType), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "查询项目的问题类型列表，带对应的状态机id")
    @GetMapping(value = "/schemes/query_issue_types_with_sm_id")
    public ResponseEntity<List<IssueTypeWithStateMachineIdVO>> queryIssueTypesWithStateMachineIdByProjectId(@PathVariable("project_id") Long projectId, @RequestParam("apply_type") String applyType) {
        return new ResponseEntity<>(projectConfigService.queryIssueTypesWithStateMachineIdByProjectId(projectId, applyType), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "查询项目下某个问题类型拥有的转换（包含可以转换到的状态）")
    @GetMapping(value = "/schemes/query_transforms")
    public ResponseEntity<List<TransformVO>> queryTransformsByProjectId(@PathVariable("project_id") Long projectId,
                                                                        @RequestParam("current_status_id") Long currentStatusId,
                                                                        @RequestParam("issue_id") Long issueId,
                                                                        @RequestParam("issue_type_id") Long issueTypeId,
                                                                        @RequestParam("apply_type") String applyType) {
        return new ResponseEntity<>(projectConfigService.queryTransformsByProjectId(projectId, currentStatusId, issueId, issueTypeId, applyType), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "查询项目下所有问题类型所有状态对应的转换")
    @GetMapping(value = "/schemes/query_transforms_map")
    public ResponseEntity<Map<Long, Map<Long, List<TransformVO>>>> queryTransformsMapByProjectId(@PathVariable("project_id") Long projectId,
                                                                                                 @RequestParam("apply_type") String applyType) {
        return new ResponseEntity<>(projectConfigService.queryTransformsMapByProjectId(projectId, applyType), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "查询项目下某个问题类型的所有状态")
    @GetMapping(value = "/schemes/query_status_by_issue_type_id")
    public ResponseEntity<List<StatusVO>> queryStatusByIssueTypeId(@PathVariable("project_id") Long projectId,
                                                                   @RequestParam("issue_type_id") Long issueTypeId,
                                                                   @RequestParam("apply_type") String applyType) {
        return new ResponseEntity<>(projectConfigService.queryStatusByIssueTypeId(projectId, issueTypeId, applyType), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "查询项目下的所有状态")
    @GetMapping(value = "/schemes/query_status_by_project_id")
    public ResponseEntity<List<StatusVO>> queryStatusByProjectId(@PathVariable("project_id") Long projectId,
                                                                 @RequestParam("apply_type") String applyType) {
        return new ResponseEntity<>(projectConfigService.queryStatusByProjectId(projectId, applyType), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "查询项目的问题类型对应的状态机id")
    @GetMapping(value = "/schemes/query_state_machine_id")
    public ResponseEntity<Long> queryStateMachineId(@PathVariable("project_id") Long projectId,
                                                    @RequestParam("apply_type") String applyType,
                                                    @RequestParam("issue_type_id") Long issueTypeId) {
        return new ResponseEntity<>(projectConfigService.queryStateMachineId(projectId, applyType, issueTypeId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "【敏捷】新增状态")
    @PostMapping(value = "/schemes/create_status_for_agile")
    public ResponseEntity<StatusVO> createStatusForAgile(@PathVariable("project_id") Long projectId,
                                                         @RequestParam String applyType,
                                                         @RequestBody StatusVO statusVO) {
        return new ResponseEntity<>(projectConfigService.createStatusForAgile(projectId, applyType, statusVO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "【敏捷】校验是否能新增状态")
    @GetMapping(value = "/schemes/check_create_status_for_agile")
    public ResponseEntity<Boolean> checkCreateStatusForAgile(@PathVariable("project_id") Long projectId,
                                                             @RequestParam String applyType) {
        return new ResponseEntity<>((Boolean) projectConfigService.checkCreateStatusForAgile(projectId, applyType).get("flag"), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "【敏捷】校验是否能删除状态")
    @GetMapping(value = "/schemes/check_remove_status_for_agile")
    public ResponseEntity<Boolean> checkRemoveStatusForAgile(@PathVariable("project_id") Long projectId,
                                                             @RequestParam("status_id") Long statusId,
                                                             @RequestParam String applyType) {
        return new ResponseEntity<>(projectConfigService.checkRemoveStatusForAgile(projectId, statusId, applyType), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "根据项目id查询组织默认优先级")
    @GetMapping("/priority/default")
    public ResponseEntity<PriorityVO> queryDefaultByOrganizationId(@ApiParam(value = "项目id", required = true)
                                                                   @PathVariable("project_id") Long projectId) {
        Long organizationId = projectUtil.getOrganizationId(projectId);
        return Optional.ofNullable(priorityService.queryDefaultByOrganizationId(organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.priority.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "根据项目id查询组织优先级列表")
    @GetMapping("/priority/list_by_org")
    public ResponseEntity<List<PriorityVO>> queryByOrganizationIdList(@ApiParam(value = "项目id", required = true)
                                                                      @PathVariable("project_id") Long projectId) {
        Long organizationId = projectUtil.getOrganizationId(projectId);
        return Optional.ofNullable(priorityService.queryByOrganizationIdList(organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.priorityList.get"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "查询工作流第一个状态")
    @GetMapping("/status/query_first_status")
    public ResponseEntity<Long> queryWorkFlowFirstStatus(@ApiParam(value = "项目id", required = true)
                                                         @PathVariable("project_id") Long projectId,
                                                         @ApiParam(value = "applyType", required = true)
                                                         @RequestParam String applyType,
                                                         @ApiParam(value = "issueTypeId", required = true)
                                                         @RequestParam Long issueTypeId,
                                                         @ApiParam(value = "organizationId", required = true)
                                                         @RequestParam Long organizationId) {
        return Optional.ofNullable(projectConfigService.queryWorkFlowFirstStatus(projectId, applyType, issueTypeId, organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.firstStatus.get"));
    }
}
