package io.choerodon.agile.infra.feign;

import io.choerodon.agile.api.dto.IssueTypeDTO;
import io.choerodon.agile.api.dto.PriorityDTO;
import io.choerodon.agile.api.dto.Status;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/10/23.
 * Email: fuqianghuang01@gmail.com
 */
@FeignClient(value = "issue-service", fallback = IssueFeignClient.class)
public interface IssueFeignClient {

    @PostMapping("/v1/init_prority")
    ResponseEntity<Map<Long, Map<String, Long>>> initProrityByOrganization(@ApiParam(value = "组织ids", required = true)
                                                                           @RequestBody List<Long> organizationIds);

    @GetMapping("/v1/organizations/{organization_id}/priority/{id}")
    ResponseEntity<PriorityDTO> queryById(@ApiParam(value = "组织id", required = true)
                                          @PathVariable("organization_id") Long organizationId,
                                          @ApiParam(value = "id", required = true)
                                          @PathVariable("id") Long id);

    @GetMapping("/v1/organizations/{organization_id}/priority/list")
    ResponseEntity<Map<Long, PriorityDTO>> queryByOrganizationId(@ApiParam(value = "组织id", required = true)
                                                                 @PathVariable("organization_id") Long organizationId);

    @GetMapping(value = "/v1/organizations/{organization_id}/issue_type/init_data")
    ResponseEntity<Map<Long, Map<String, Long>>> initIssueTypeData(@PathVariable("organization_id") Long organizationId,
                                                                   @RequestBody List<Long> orgIds);

    @GetMapping(value = "/v1/organizations/{organization_id}/issue_type/type_map")
    ResponseEntity<Map<Long, IssueTypeDTO>> listIssueTypeMap(@PathVariable("organization_id") Long organizationId);

    @GetMapping(value = "/v1/organizations/{organization_id}/issue_type/{id}")
    ResponseEntity<IssueTypeDTO> queryIssueTypeById(@PathVariable("organization_id") Long organizationId,
                                                    @PathVariable("id") Long issueTypeId);

}