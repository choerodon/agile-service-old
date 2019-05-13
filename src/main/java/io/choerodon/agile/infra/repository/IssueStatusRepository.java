package io.choerodon.agile.infra.repository;

import io.choerodon.agile.domain.agile.entity.IssueStatusE;
import io.choerodon.agile.domain.agile.event.AddStatusWithProject;

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
     * @param addStatusWithProjects addStatusWithProjects
     * @param userId                userId
     */
    void batchCreateStatusByProjectIds(List<AddStatusWithProject> addStatusWithProjects, Long userId);

}
