package io.choerodon.agile.infra.feign.fallback;

import io.choerodon.agile.api.vo.WorkSpaceVO;
import io.choerodon.agile.infra.dataobject.WorkSpaceDTO;
import io.choerodon.agile.infra.feign.KnowledgebaseClient;
import io.choerodon.core.exception.CommonException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KnowledgebaseClientFallback implements KnowledgebaseClient {

    @Override
    public ResponseEntity<List<WorkSpaceDTO>> queryAllSpaceByProject() {
        throw new CommonException("error.workspace.get");
    }

    @Override
    public ResponseEntity<List<WorkSpaceVO>> querySpaceByIds(Long projectId, List<Long> spaceIds) {
        throw new CommonException("error.spaceList.get");
    }
}
