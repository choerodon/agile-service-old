package io.choerodon.agile.app.service;

import java.util.List;

import io.choerodon.agile.api.dto.AssigneeDistributeDTO;
import io.choerodon.agile.api.dto.PriorityDistributeDTO;
import io.choerodon.agile.api.dto.SprintInfoDTO;
import io.choerodon.agile.api.dto.StatusCategoryDTO;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/9/4.
 * Email: fuqianghuang01@gmail.com
 */
public interface IterativeWorktableService {

    List<PriorityDistributeDTO> queryPriorityDistribute(Long projectId, Long sprintId);

    List<StatusCategoryDTO> queryStatusCategoryDistribute(Long projectId, Long sprintId);

    SprintInfoDTO querySprintInfo(Long projectId, Long sprintId);

    List<AssigneeDistributeDTO> queryAssigneeDistribute(Long projectId, Long sprintId);
}
