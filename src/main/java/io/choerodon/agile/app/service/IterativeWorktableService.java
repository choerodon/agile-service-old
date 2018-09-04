package io.choerodon.agile.app.service;


import io.choerodon.agile.api.dto.PriorityDistributeDTO;
import io.choerodon.agile.api.dto.SprintInfoDTO;
import io.choerodon.agile.api.dto.StatusCategoryDTO;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/9/4.
 * Email: fuqianghuang01@gmail.com
 */
public interface IterativeWorktableService {

    List<PriorityDistributeDTO> queryPriorityDistribute(Long projectId, Long sprintId);

    List<StatusCategoryDTO> queryStatusCategoryDistribute(Long projectId, Long sprintId);

    SprintInfoDTO querySprintInfo(Long projectId, Long sprintId);

}
