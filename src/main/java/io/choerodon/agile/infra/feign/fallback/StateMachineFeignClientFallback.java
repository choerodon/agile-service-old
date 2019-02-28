package io.choerodon.agile.infra.feign.fallback;

import io.choerodon.agile.api.dto.Status;
import io.choerodon.agile.api.dto.StatusMapDTO;
import io.choerodon.agile.infra.feign.StateMachineFeignClient;
import io.choerodon.core.exception.CommonException;
import io.choerodon.statemachine.dto.StateMachineTransformDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


/**
 * Created by HuangFuqiang@choerodon.io on 2018/10/23.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class StateMachineFeignClientFallback implements StateMachineFeignClient {

    @Override
    public ResponseEntity<Map<Long, Status>> batchStatusGet(List<Long> ids) {
        throw new CommonException("error.statusBatch.get");
    }

    @Override
    public ResponseEntity<Map<Long, StatusMapDTO>> queryAllStatusMap(Long organizationId) {
        throw new CommonException("error.statusMap.get");
    }

    @Override
    public ResponseEntity<StatusMapDTO> queryStatusById(Long organizationId, Long statusId) {
        throw new CommonException("error.status.get");
    }

    @Override
    public ResponseEntity<Map<Long, List<Status>>> queryAllStatus() {
        throw new CommonException("error.status.get");
    }

    @Override
    public ResponseEntity<Map<Long, Long>> queryInitStatusIds(Long organizationId, List<Long> stateMachineIds) {
        throw new CommonException("error.statusMap.queryInitStatusIds");
    }

    @Override
    public ResponseEntity<StateMachineTransformDTO> queryDeployTransformForAgile(Long organizationId, Long tansformId) {
        throw new CommonException("error.stateMachineFegin.queryDeployTransformForAgile");

    }
}
