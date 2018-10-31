package io.choerodon.agile.infra.feign;

import io.choerodon.agile.api.dto.Status;
import io.choerodon.agile.api.dto.StatusInfoDTO;
import io.choerodon.agile.api.dto.StatusMapDTO;
import io.choerodon.agile.infra.feign.fallback.StateMachineFeignClientFallback;
import io.swagger.annotations.ApiOperation;
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
@FeignClient(value = "state-machine-service", fallback = StateMachineFeignClientFallback.class)
public interface StateMachineFeignClient {

    @PostMapping(value = "/v1/status/batch")
    ResponseEntity<Map<Long, Status>> batchStatusGet(@ApiParam(value = "状态ids", required = true)
                                                     @RequestBody List<Long> ids);

    @GetMapping(value = "/v1/organizations/{organization_id}/status/list_map")
    ResponseEntity<Map<Long, StatusMapDTO>> queryAllStatusMap(@PathVariable("organization_id") Long organizationId);

    @GetMapping(value = "/v1/organizations/{organization_id}/status/{status_id}")
    ResponseEntity<StatusInfoDTO> queryStatusById(@PathVariable("organization_id") Long organizationId,
                                                  @PathVariable("status_id") Long statusId);

    @GetMapping(value = "/v1/fix_data/query_status")
    ResponseEntity<Map<Long, List<Status>>> queryAllStatus();
}