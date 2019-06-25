package io.choerodon.agile.infra.feign.fallback;

import io.choerodon.agile.infra.dataobject.WorkSpaceDO;
import io.choerodon.agile.infra.feign.KnowledgebaseClient;
import io.choerodon.core.exception.CommonException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KnowledgebaseClientFallback implements KnowledgebaseClient {

    @Override
    public ResponseEntity<List<WorkSpaceDO>> queryAllSpaceByProject() {
        throw new CommonException("error.workspace.get");
    }
}
