package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.app.service.IssueAccessDataService;
import io.choerodon.agile.app.service.PriorityService;
import io.choerodon.agile.infra.mapper.IssueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/3/4
 */
@Service
public class PriorityServiceImpl implements PriorityService {
    @Autowired
    IssueMapper issueMapper;
//    @Autowired
//    IssueRepository issueRepository;
    @Autowired
    private IssueAccessDataService issueAccessDataService;

    @Override
    public Long checkPriorityDelete(Long organizationId, Long priorityId, List<Long> projectIds) {
        if (projectIds == null || projectIds.isEmpty()) {
            return 0L;
        } else {
            return issueMapper.checkPriorityDelete(priorityId, projectIds);
        }
    }

    @Override
    public void batchChangeIssuePriority(Long organizationId, Long priorityId, Long changePriorityId, Long userId, List<Long> projectIds) {
        if (projectIds != null && !projectIds.isEmpty()) {
            issueAccessDataService.batchUpdateIssuePriority(organizationId, priorityId, changePriorityId, userId, projectIds);
        }
    }
}
