package io.choerodon.agile.domain.agile.repository;

import io.choerodon.agile.api.dto.StatusDTO;
import io.choerodon.agile.domain.agile.entity.IssueStatusE;
import io.choerodon.agile.domain.agile.event.ProjectConfig;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
public interface IssueStatusRepository {

    IssueStatusE create(IssueStatusE issueStatusE);

    IssueStatusE update(IssueStatusE issueStatusE);

    void delete(IssueStatusE issueStatusE);

    /**
     * 批量创建状态
     *
     * @param statusDTOS statusDTOS
     * @param projectIds projectIds
     * @param userId     userId
     */
    void batchCreateStatusByProjectIds(List<StatusDTO> statusDTOS, List<Long> projectIds, Long userId);

//    Boolean checkSameStatus(Long projectId, String statusName);

}
