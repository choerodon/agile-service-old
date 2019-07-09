package io.choerodon.agile.infra.feign.fallback;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.infra.feign.IssueFeignClient;
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
    public ResponseEntity<Map<Long, IssueTypeVO>> listIssueTypeMap(Long organizationId) {
        throw new CommonException("error.issueTypeMap.get");
    }

    @Override
    public ResponseEntity<IssueTypeVO> queryIssueTypeById(Long organizationId, Long issueTypeId) {
        throw new CommonException("error.issueType.get");
    }

    @Override
    public ResponseEntity<Long> queryStateMachineId(Long projectId, String applyType, Long issueTypeId) {
        throw new CommonException("error.stateMachineId.get");
    }

    @Override
    public ResponseEntity<List<IssueTypeVO>> queryIssueTypesByProjectId(Long projectId, String applyType) {
        throw new CommonException("error.queryIssueTypesByProjectId.get");
    }

    @Override
    public ResponseEntity<StatusInfoVO> createStatusForAgile(Long projectId, String applyType, StatusInfoVO statusInfoVO) {
        throw new CommonException("error.status.create");
    }

    @Override
    public ResponseEntity<List<StatusMapVO>> queryStatusByProjectId(Long projectId, String applyType) {
        throw new CommonException("error.status.queryStatusByProjectId");
    }

    @Override
    public ResponseEntity<List<IssueTypeWithStateMachineIdDTO>> queryIssueTypesWithStateMachineIdByProjectId(Long projectId, String applyType) {
        throw new CommonException("error.status.queryIssueTypesWithStateMachineIdByProjectId");
    }

    @Override
    public ResponseEntity removeStatusForAgile(Long projectId, Long statusId, String applyType) {
        throw new CommonException("error.status.remove");
    }

    @Override
    public ResponseEntity<Boolean> updateDeployProgress(Long organizationId, Long schemeId, Integer deployProgress) {
        throw new CommonException("error.issue.updateDeployProgress");
    }

    @Override
    public ResponseEntity<PriorityDTO> queryDefaultByOrganizationId(Long organizationId) {
        throw new CommonException("error.defaultPriority.get");
    }

    @Override
    public ResponseEntity<List<TransformDTO>> queryTransformsByProjectId(Long projectId, Long currentStatusId, Long issueId, Long issueTypeId, String applyType) {
        throw new CommonException("error.transform.get");
    }

    @Override
    public ResponseEntity<List<PriorityDTO>> queryByOrganizationIdList(Long organizationId) {
        throw new CommonException("error.priorityList.get");
    }

    @Override
    public ResponseEntity<List<IssueTypeVO>> queryByOrgId(Long organizationId) {
        throw new CommonException("error.issueTypeList.get");
    }
}
