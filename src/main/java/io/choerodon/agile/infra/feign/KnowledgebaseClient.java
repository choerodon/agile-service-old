package io.choerodon.agile.infra.feign;

import io.choerodon.agile.infra.dataobject.WorkSpaceDO;
import io.choerodon.agile.infra.feign.fallback.KnowledgebaseClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(value = "knowledgebase-service", fallback = KnowledgebaseClientFallback.class)
public interface KnowledgebaseClient {

    @GetMapping("/v1/fix_data/all_project_space")
    ResponseEntity<List<WorkSpaceDO>> queryAllSpaceByProject();
}
