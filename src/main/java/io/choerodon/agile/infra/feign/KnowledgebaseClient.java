package io.choerodon.agile.infra.feign;

import io.choerodon.agile.api.vo.WorkSpaceDTO;
import io.choerodon.agile.infra.dataobject.WorkSpaceDO;
import io.choerodon.agile.infra.feign.fallback.FoundationFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "knowledgebase-service", fallback = FoundationFeignClientFallback.class)
public interface KnowledgebaseClient {

    @GetMapping("/v1/fix_data/all_project_space")
    ResponseEntity<List<WorkSpaceDO>> queryAllSpaceByProject();

    @PostMapping(value = "/v1/projects/{project_id}/work_space/query_by_space_ids")
    ResponseEntity<List<WorkSpaceDTO>> querySpaceByIds(@PathVariable(value = "project_id") Long projectId,
                                                       @RequestBody List<Long> spaceIds);
}
