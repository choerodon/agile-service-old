package io.choerodon.agile.app.service;


import io.choerodon.agile.api.dto.PriorityDistributeDTO;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/9/4.
 * Email: fuqianghuang01@gmail.com
 */
public interface IterativeWorktableService {

    List<PriorityDistributeDTO> queryPriorityDistribute(Long projectId, Long sprintId);

}
