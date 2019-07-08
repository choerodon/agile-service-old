package io.choerodon.agile.infra.feign;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.infra.feign.fallback.IssueFeignClientFallback;
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
    ResponseEntity<PriorityDTO> queryById(@ApiParam(value = "组织id", required = true)
                                          @PathVariable("organization_id") Long organizationId,
                                          @ApiParam(value = "id", required = true)
                                          @PathVariable("id") Long id);

    @GetMapping("/v1/organizations/{organization_id}/priority/list")
    ResponseEntity<Map<Long, PriorityDTO>> queryByOrganizationId(@ApiParam(value = "组织id", required = true)
                                                                 @PathVariable("organization_id") Long organizationId);


    @GetMapping(value = "/v1/organizations/{organization_id}/issue_type/type_map")
    ResponseEntity<Map<Long, IssueTypeDTO>> listIssueTypeMap(@PathVariable("organization_id") Long organizationId);

    @GetMapping(value = "/v1/organizations/{organization_id}/issue_type/{id}")
    ResponseEntity<IssueTypeDTO> queryIssueTypeById(@PathVariable("organization_id") Long organizationId,
                                                    @PathVariable("id") Long issueTypeId);

    @GetMapping(value = "/v1/projects/{project_id}/schemes/query_state_machine_id")
    ResponseEntity<Long> queryStateMachineId(@PathVariable("project_id") Long projectId,
                                             @RequestParam("apply_type") String applyType,
                                             @RequestParam("issue_type_id") Long issueTypeId);

    @GetMapping(value = "/v1/projects/{project_id}/schemes/query_issue_types")
    ResponseEntity<List<IssueTypeDTO>> queryIssueTypesByProjectId(@PathVariable("project_id") Long projectId, @RequestParam("apply_type") String applyType);

    @PostMapping(value = "/v1/projects/{project_id}/schemes/create_status_for_agile")
    ResponseEntity<StatusInfoDTO> createStatusForAgile(@PathVariable("project_id") Long projectId,
                                                       @RequestParam("applyType") String applyType,
                                                       @RequestBody StatusInfoDTO statusInfoDTO);

    @GetMapping(value = "/v1/projects/{project_id}/schemes/query_status_by_project_id")
    ResponseEntity<List<StatusMapDTO>> queryStatusByProjectId(@PathVariable("project_id") Long projectId,
                                                              @RequestParam("apply_type") String applyType);

    @GetMapping(value = "/v1/projects/{project_id}/schemes/query_issue_types_with_sm_id")
    ResponseEntity<List<IssueTypeWithStateMachineIdDTO>> queryIssueTypesWithStateMachineIdByProjectId(@PathVariable("project_id") Long projectId, @RequestParam("apply_type") String applyType);

    @DeleteMapping(value = "/v1/projects/{project_id}/schemes/remove_status_for_agile")
    ResponseEntity removeStatusForAgile(@PathVariable("project_id") Long projectId,
                                        @RequestParam("status_id") Long statusId,
                                        @RequestParam("applyType") String applyType);

    @PutMapping(value = "/v1/organizations/{organization_id}/state_machine_scheme/update_deploy_progress/{scheme_id}")
    ResponseEntity<Boolean> updateDeployProgress(@PathVariable("organization_id") Long organizationId,
                                                 @PathVariable("scheme_id") Long schemeId,
                                                 @RequestParam("deployProgress") Integer deployProgress);

    @GetMapping("/v1/organizations/{organization_id}/priority/default")
    ResponseEntity<PriorityDTO> queryDefaultByOrganizationId(@ApiParam(value = "组织id", required = true)
                                                             @PathVariable("organization_id") Long organizationId);

    @GetMapping(value = "/v1/projects/{project_id}/schemes/query_transforms")
    ResponseEntity<List<TransformDTO>> queryTransformsByProjectId(@PathVariable("project_id") Long projectId,
                                                                  @RequestParam("current_status_id") Long currentStatusId,
                                                                  @RequestParam("issue_id") Long issueId,
                                                                  @RequestParam("issue_type_id") Long issueTypeId,
                                                                  @RequestParam("apply_type") String applyType);

    @GetMapping("/v1/organizations/{organization_id}/priority/list_by_org")
    ResponseEntity<List<PriorityDTO>> queryByOrganizationIdList(@ApiParam(value = "组织id", required = true)
                                                                @PathVariable("organization_id") Long organizationId);

    @GetMapping(value = "/v1/organizations/{organization_id}/issue_type/types")
    ResponseEntity<List<IssueTypeDTO>> queryByOrgId(@PathVariable("organization_id") Long organizationId);
}