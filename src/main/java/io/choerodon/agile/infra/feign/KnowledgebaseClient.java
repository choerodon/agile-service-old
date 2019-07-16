package io.choerodon.agile.infra.feign;

import io.choerodon.agile.api.vo.WorkSpaceVO;
import io.choerodon.agile.infra.dataobject.WorkSpaceDTO;
import io.choerodon.agile.infra.feign.fallback.KnowledgebaseClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "knowledgebase-service", fallback = KnowledgebaseClientFallback.class)
public interface KnowledgebaseClient {

    @GetMapping("/v1/fix_data/all_project_space")
    ResponseEntity<List<WorkSpaceDTO>> queryAllSpaceByProject();

    @PostMapping(value = "/v1/projects/{project_id}/work_space/query_by_space_ids")
    ResponseEntity<List<WorkSpaceVO>> querySpaceByIds(@PathVariable(value = "project_id") Long projectId,
                                                      @RequestBody List<Long> spaceIds);
}
