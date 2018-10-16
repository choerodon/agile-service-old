package io.choerodon.agile.app.service;

import java.util.List;

import io.choerodon.agile.api.dto.*;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/9/4.
 * Email: fuqianghuang01@gmail.com
 */
public interface IterativeWorktableService {

    List<PriorityDistributeDTO> queryPriorityDistribute(Long projectId, Long sprintId);

    List<StatusCategoryDTO> queryStatusCategoryDistribute(Long projectId, Long sprintId);

    SprintInfoDTO querySprintInfo(Long projectId, Long sprintId, Long organizationId);

    List<AssigneeDistributeDTO> queryAssigneeDistribute(Long projectId, Long sprintId);

    List<IssueTypeDistributeDTO> queryIssueTypeDistribute(Long projectId, Long sprintId);
}
