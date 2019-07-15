package io.choerodon.agile.infra.feign;

import io.choerodon.agile.api.vo.Status;
import io.choerodon.agile.api.vo.StatusMapVO;
import io.choerodon.agile.infra.feign.fallback.StateMachineFeignClientFallback;
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
@FeignClient(value = "state-machine-service", fallback = StateMachineFeignClientFallback.class)
public interface StateMachineFeignClient {

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

    @GetMapping({"/v1/organizations/{organization_id}/state_machine_transforms/query_deploy_transform"})
    ResponseEntity<StateMachineTransformDTO> queryDeployTransformForAgile(@PathVariable("organization_id") Long organizationId, @RequestParam("transformId") Long transformId);
}