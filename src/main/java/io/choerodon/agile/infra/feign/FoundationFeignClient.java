package io.choerodon.agile.infra.feign;

import io.choerodon.agile.infra.feign.fallback.FoundationFeignClientFallback;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(value = "foundation-service", fallback = FoundationFeignClientFallback.class)
public interface FoundationFeignClient {

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

    @PostMapping(value = "/v1/projects/{project_id}/object_scheme_field/query_field_name_map")
    ResponseEntity<Map<String, String>> queryFieldNameMap(@ApiParam(value = "项目id", required = true)
                                                          @PathVariable("project_id") Long projectId,
                                                          @ApiParam(value = "组织id", required = true)
                                                          @RequestParam("organizationId") Long organizationId,
                                                          @ApiParam(value = "方案编码", required = true)
                                                          @RequestParam("schemeCode") String schemeCode,
                                                          @ApiParam(value = "字段编码列表", required = true)
                                                          @RequestBody List<String> fieldCodes);
}
