package io.choerodon.agile.infra.feign.fallback;

import io.choerodon.agile.api.dto.FieldDataLogDTO;
import io.choerodon.agile.infra.feign.FoundationFeignClient;
import io.choerodon.core.exception.CommonException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by WangZhe@choerodon.io on 2019-05-16.
 * Email: ettwz@hotmail.com
 */

@Component
public class FoundationFeignClientFallback implements FoundationFeignClient {
    @Override
    public ResponseEntity<Map<String, Object>> listQuery(Long projectId, Long organizationId, String schemeCode) {
        throw new CommonException("error.foundation.listQuery");
    }

    @Override
    public ResponseEntity<Map<Long, Map<String, String>>> queryFieldValueWithIssueIds(Long organizationId, Long projectId, List<Long> instanceIds) {
        throw new CommonException("error.foundation.CodeValue");
    }

    @Override
    public ResponseEntity<List<FieldDataLogDTO>> queryDataLogByInstanceId(Long projectId, Long instanceId, String schemeCode) {
        throw new CommonException("error.foundation.queryDataLogByInstanceId");
    }
}
