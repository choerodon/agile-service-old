package io.choerodon.agile.infra.feign.fallback;

import io.choerodon.agile.api.dto.IssueTypeDTO;
import io.choerodon.agile.api.dto.PriorityDTO;
import io.choerodon.agile.api.dto.Status;
import io.choerodon.agile.api.dto.StatusDTO;
import io.choerodon.agile.infra.dataobject.StatusForMoveDataDO;
import io.choerodon.agile.infra.feign.IssueFeignClient;
import io.choerodon.agile.infra.feign.StateMachineFeignClient;
import io.choerodon.core.exception.CommonException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


/**
 * Created by HuangFuqiang@choerodon.io on 2018/10/23.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class IssueFeignClientFallback implements IssueFeignClient {

    @Override
    public ResponseEntity<PriorityDTO> queryById(Long organizationId, Long id) {
        throw new CommonException("error.priority.get");
    }

    @Override
    public ResponseEntity<Map<Long, PriorityDTO>> queryByOrganizationId(Long organizationId) {
        throw new CommonException("error.priorityList.get");
    }

    @Override
    public ResponseEntity<Map<Long, IssueTypeDTO>> listIssueTypeMap(Long organizationId) {
        throw new CommonException("error.issueTypeMap.get");
    }

    @Override
    public ResponseEntity<IssueTypeDTO> queryIssueTypeById(Long organizationId, Long issueTypeId) {
        throw new CommonException("error.issueType.get");
    }

    @Override
    public ResponseEntity fixStateMachineScheme(List<StatusForMoveDataDO> statusForMoveDataDOList) {
        throw new CommonException("error.status.init");
    }

    @Override
    public ResponseEntity<Map<Long, Map<String, Long>>> queryPriorities() {
        throw new CommonException("error.priority.get");
    }

    @Override
    public ResponseEntity<Map<Long, Map<String, Long>>> queryIssueTypes() {
        throw new CommonException("error.issueType.get");
    }

    @Override
    public ResponseEntity<Long> queryStateMachineId(Long projectId, String schemeType, Long issueTypeId) {
        throw new CommonException("error.stateMachineId.get");
    }
}
