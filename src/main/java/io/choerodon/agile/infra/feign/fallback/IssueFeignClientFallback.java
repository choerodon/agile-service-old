package io.choerodon.agile.infra.feign.fallback;

import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.infra.feign.IssueFeignClient;
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
public class IssueFeignClientFallback implements IssueFeignClient {

    @Override
    public ResponseEntity<PriorityVO> queryById(Long organizationId, Long id) {
        throw new CommonException("error.priority.get");
    }

    @Override
    public ResponseEntity<Map<Long, PriorityVO>> queryByOrganizationId(Long organizationId) {
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
    public ResponseEntity<List<IssueTypeWithStateMachineIdVO>> queryIssueTypesWithStateMachineIdByProjectId(Long projectId, String applyType) {
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
    public ResponseEntity<PriorityVO> queryDefaultByOrganizationId(Long organizationId) {
        throw new CommonException("error.defaultPriority.get");
    }

    @Override
    public ResponseEntity<List<TransformVO>> queryTransformsByProjectId(Long projectId, Long currentStatusId, Long issueId, Long issueTypeId, String applyType) {
        throw new CommonException("error.transform.get");
    }

    @Override
    public ResponseEntity<List<PriorityVO>> queryByOrganizationIdList(Long organizationId) {
        throw new CommonException("error.priorityList.get");
    }

    @Override
    public ResponseEntity<List<IssueTypeVO>> queryByOrgId(Long organizationId) {
        throw new CommonException("error.issueTypeList.get");
    }

    @Override
    public ResponseEntity<Map<Long, Status>> batchStatusGet(List<Long> ids) {
        throw new CommonException("error.statusBatch.get");
    }

    @Override
    public ResponseEntity<Map<Long, StatusMapVO>> queryAllStatusMap(Long organizationId) {
        throw new CommonException("error.statusMap.get");
    }

    @Override
    public ResponseEntity<StatusMapVO> queryStatusById(Long organizationId, Long statusId) {
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

    @Override
    public ResponseEntity<Map<String, Object>> listQuery(Long projectId, Long organizationId, String schemeCode) {
        throw new CommonException("error.foundation.listQuery");
    }

    @Override
    public ResponseEntity<Map<Long, Map<String, String>>> queryFieldValueWithIssueIds(Long organizationId, Long projectId, List<Long> instanceIds) {
        throw new CommonException("error.foundation.CodeValue");
    }

    @Override
    public ResponseEntity<List<FieldDataLogVO>> queryDataLogByInstanceId(Long projectId, Long instanceId, String schemeCode) {
        throw new CommonException("error.foundation.queryDataLogByInstanceId");
    }

    @Override
    public ResponseEntity<List<Long>> sortIssueIdsByFieldValue(Long organizationId, Long projectId, String pageRequestString) {
        throw new CommonException("error.foundation.sortIssueIdsByFieldValue");
    }
}
