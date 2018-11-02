package io.choerodon.agile.infra.feign;

import io.choerodon.agile.api.dto.IssueTypeDTO;
import io.choerodon.agile.api.dto.PriorityDTO;
import io.choerodon.agile.api.dto.StatusInfoDTO;
import io.choerodon.agile.api.dto.StatusMapDTO;
import io.choerodon.agile.infra.dataobject.StatusForMoveDataDO;
import io.choerodon.agile.infra.feign.fallback.IssueFeignClientFallback;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
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

    @PostMapping(value = "/v1/fix_data/state_machine_scheme")
    ResponseEntity fixStateMachineScheme(@ApiParam(value = "敏捷状态数据", required = true)
                                         @RequestBody List<StatusForMoveDataDO> statusForMoveDataDOList,
                                         @RequestParam("isFixStatus") Boolean isFixStatus);

    @GetMapping("/v1/fix_data/query_priorities")
    ResponseEntity<Map<Long, Map<String, Long>>> queryPriorities();

    @GetMapping("/v1/fix_data/query_issue_types")
    ResponseEntity<Map<Long, Map<String, Long>>> queryIssueTypes();

    @GetMapping(value = "/v1/projects/{project_id}/schemes/query_state_machine_id")
    ResponseEntity<Long> queryStateMachineId(@PathVariable("project_id") Long projectId,
                                             @RequestParam("scheme_type") String schemeType,
                                             @RequestParam("issue_type_id") Long issueTypeId);

    @GetMapping(value = "/v1/projects/{project_id}/schemes/query_issue_types")
    ResponseEntity<List<IssueTypeDTO>> queryIssueTypesByProjectId(@PathVariable("project_id") Long projectId, @RequestParam("scheme_type") String schemeType);

    @PostMapping(value = "/v1/projects/{project_id}/schemes/create_status_for_agile")
    ResponseEntity<StatusInfoDTO> createStatusForAgile(@PathVariable("project_id") Long projectId,
                                                       @RequestBody StatusInfoDTO statusInfoDTO);

    @GetMapping(value = "/query_status")
    ResponseEntity<List<StatusMapDTO>> queryStatusByProjectId(@PathVariable("project_id") Long projectId,
                                                              @RequestParam("scheme_type") String schemeType);
}