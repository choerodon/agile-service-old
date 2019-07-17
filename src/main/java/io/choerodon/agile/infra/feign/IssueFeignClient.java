package io.choerodon.agile.infra.feign;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.infra.feign.fallback.IssueFeignClientFallback;
import io.choerodon.statemachine.dto.StateMachineTransformDTO;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/10/23.
 * Email: fuqianghuang01@gmail.com
 */
@FeignClient(value = "issue-service", fallback = IssueFeignClientFallback.class)
public interface IssueFeignClient {

    @GetMapping("/v1/organizations/{organization_id}/priority/{id}")
    ResponseEntity<PriorityVO> queryById(@ApiParam(value = "组织id", required = true)
                                          @PathVariable("organization_id") Long organizationId,
                                         @ApiParam(value = "id", required = true)
                                          @PathVariable("id") Long id);

    @GetMapping("/v1/organizations/{organization_id}/priority/list")
    ResponseEntity<Map<Long, PriorityVO>> queryByOrganizationId(@ApiParam(value = "组织id", required = true)
                                                                 @PathVariable("organization_id") Long organizationId);


    @GetMapping(value = "/v1/organizations/{organization_id}/issue_type/type_map")
    ResponseEntity<Map<Long, IssueTypeVO>> listIssueTypeMap(@PathVariable("organization_id") Long organizationId);

    @GetMapping(value = "/v1/organizations/{organization_id}/issue_type/{id}")
    ResponseEntity<IssueTypeVO> queryIssueTypeById(@PathVariable("organization_id") Long organizationId,
                                                   @PathVariable("id") Long issueTypeId);

    @GetMapping(value = "/v1/projects/{project_id}/schemes/query_state_machine_id")
    ResponseEntity<Long> queryStateMachineId(@PathVariable("project_id") Long projectId,
                                             @RequestParam("apply_type") String applyType,
                                             @RequestParam("issue_type_id") Long issueTypeId);

    @GetMapping(value = "/v1/projects/{project_id}/schemes/query_issue_types")
    ResponseEntity<List<IssueTypeVO>> queryIssueTypesByProjectId(@PathVariable("project_id") Long projectId, @RequestParam("apply_type") String applyType);

    @PostMapping(value = "/v1/projects/{project_id}/schemes/create_status_for_agile")
    ResponseEntity<StatusInfoVO> createStatusForAgile(@PathVariable("project_id") Long projectId,
                                                      @RequestParam("applyType") String applyType,
                                                      @RequestBody StatusInfoVO statusInfoVO);

    @GetMapping(value = "/v1/projects/{project_id}/schemes/query_status_by_project_id")
    ResponseEntity<List<StatusMapVO>> queryStatusByProjectId(@PathVariable("project_id") Long projectId,
                                                             @RequestParam("apply_type") String applyType);

    @GetMapping(value = "/v1/projects/{project_id}/schemes/query_issue_types_with_sm_id")
    ResponseEntity<List<IssueTypeWithStateMachineIdVO>> queryIssueTypesWithStateMachineIdByProjectId(@PathVariable("project_id") Long projectId, @RequestParam("apply_type") String applyType);

    @DeleteMapping(value = "/v1/projects/{project_id}/schemes/remove_status_for_agile")
    ResponseEntity removeStatusForAgile(@PathVariable("project_id") Long projectId,
                                        @RequestParam("status_id") Long statusId,
                                        @RequestParam("applyType") String applyType);

    @PutMapping(value = "/v1/organizations/{organization_id}/state_machine_scheme/update_deploy_progress/{scheme_id}")
    ResponseEntity<Boolean> updateDeployProgress(@PathVariable("organization_id") Long organizationId,
                                                 @PathVariable("scheme_id") Long schemeId,
                                                 @RequestParam("deployProgress") Integer deployProgress);

    @GetMapping("/v1/organizations/{organization_id}/priority/default")
    ResponseEntity<PriorityVO> queryDefaultByOrganizationId(@ApiParam(value = "组织id", required = true)
                                                             @PathVariable("organization_id") Long organizationId);

    @GetMapping(value = "/v1/projects/{project_id}/schemes/query_transforms")
    ResponseEntity<List<TransformVO>> queryTransformsByProjectId(@PathVariable("project_id") Long projectId,
                                                                 @RequestParam("current_status_id") Long currentStatusId,
                                                                 @RequestParam("issue_id") Long issueId,
                                                                 @RequestParam("issue_type_id") Long issueTypeId,
                                                                 @RequestParam("apply_type") String applyType);

    @GetMapping("/v1/organizations/{organization_id}/priority/list_by_org")
    ResponseEntity<List<PriorityVO>> queryByOrganizationIdList(@ApiParam(value = "组织id", required = true)
                                                                @PathVariable("organization_id") Long organizationId);

    @GetMapping(value = "/v1/organizations/{organization_id}/issue_type/types")
    ResponseEntity<List<IssueTypeVO>> queryByOrgId(@PathVariable("organization_id") Long organizationId);

    @PostMapping(value = "/v1/status/batch")
    ResponseEntity<Map<Long, Status>> batchStatusGet(@ApiParam(value = "状态ids", required = true)
                                                     @RequestBody List<Long> ids);

    @GetMapping(value = "/v1/organizations/{organization_id}/status/list_map")
    ResponseEntity<Map<Long, StatusMapVO>> queryAllStatusMap(@PathVariable("organization_id") Long organizationId);

    @GetMapping(value = "/v1/organizations/{organization_id}/status/{status_id}")
    ResponseEntity<StatusMapVO> queryStatusById(@PathVariable("organization_id") Long organizationId,
                                                @PathVariable("status_id") Long statusId);

    @GetMapping({"/v1/organizations/{organization_id}/instances/query_init_status_ids"})
    ResponseEntity<Map<Long, Long>> queryInitStatusIds(@PathVariable("organization_id") Long organizationId, @RequestParam("state_machine_id") List<Long> stateMachineIds);

    @GetMapping({"/v1/organizations/{organization_id}/state_machine_transform/query_deploy_transform"})
    ResponseEntity<StateMachineTransformDTO> queryDeployTransformForAgile(@PathVariable("organization_id") Long organizationId, @RequestParam("transformId") Long transformId);

    @GetMapping("/v1/projects/{project_id}/object_scheme_field/list")
    ResponseEntity<Map<String, Object>> listQuery(@ApiParam(value = "项目id", required = true)
                                                  @PathVariable("project_id") Long projectId,
                                                  @ApiParam(value = "组织id", required = true)
                                                  @RequestParam("organizationId") Long organizationId,
                                                  @ApiParam(value = "方案编码", required = true)
                                                  @RequestParam("schemeCode") String schemeCode);

    @PostMapping("/v1/projects/{project_id}/field_value/query/instanceIds")
    ResponseEntity<Map<Long, Map<String, String>>> queryFieldValueWithIssueIds(@ApiParam(value = "组织id", required = true)
                                                                               @RequestParam("organizationId") Long organizationId,
                                                                               @ApiParam(value = "项目id", required = true)
                                                                               @PathVariable("project_id") Long projectId,
                                                                               @ApiParam(value = "实例ids", required = true)
                                                                               @RequestBody List<Long> instanceIds);

    @GetMapping("/v1/projects/{project_id}/data_log/list")
    ResponseEntity<List<FieldDataLogVO>> queryDataLogByInstanceId(@ApiParam(value = "项目id", required = true)
                                                                  @PathVariable("project_id") Long projectId,
                                                                  @ApiParam(value = "字段id", required = true)
                                                                  @RequestParam("instanceId") Long instanceId,
                                                                  @ApiParam(value = "方案编码", required = true)
                                                                  @RequestParam("schemeCode") String schemeCode);

    @PostMapping("/v1/projects/{project_id}/field_value/sort/getInstanceIds")
    ResponseEntity<List<Long>> sortIssueIdsByFieldValue(@ApiParam(value = "组织id", required = true)
                                                        @RequestParam("organizationId") Long organizationId,
                                                        @ApiParam(value = "项目id", required = true)
                                                        @PathVariable("project_id") Long projectId,
                                                        @ApiParam(value = "分页信息", required = true)
                                                        @RequestBody String pageRequestString);
}