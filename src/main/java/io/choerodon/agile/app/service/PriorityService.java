package io.choerodon.agile.app.service;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/3/4
 */
public interface PriorityService {
    Long checkPriorityDelete(Long organizationId, Long priorityId, List<Long> projectIds);

    void batchChangeIssuePriority(Long organizationId, Long priorityId, Long changePriorityId, Long userId, List<Long> projectIds);
}
