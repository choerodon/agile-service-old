package io.choerodon.agile.app.service;

import java.util.List;

import io.choerodon.agile.api.vo.*;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/9/4.
 * Email: fuqianghuang01@gmail.com
 */
public interface IterativeWorktableService {

    List<PriorityDistributeVO> queryPriorityDistribute(Long projectId, Long sprintId, Long organizationId);

    List<StatusCategoryVO> queryStatusCategoryDistribute(Long projectId, Long sprintId, Long organizationId);

    SprintInfoVO querySprintInfo(Long projectId, Long sprintId, Long organizationId);

    List<AssigneeDistributeVO> queryAssigneeDistribute(Long projectId, Long sprintId);

    List<IssueTypeDistributeVO> queryIssueTypeDistribute(Long projectId, Long sprintId, Long organizationId);
}
