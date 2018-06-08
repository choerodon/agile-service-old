package io.choerodon.agile.domain.agile.repository;

import io.choerodon.agile.domain.agile.entity.IssueStatusE;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
public interface IssueStatusRepository {

    IssueStatusE create(IssueStatusE issueStatusE);

    IssueStatusE update(IssueStatusE issueStatusE);

    void delete(IssueStatusE issueStatusE);

    Boolean checkSameStatus(Long projectId, String statusName);

}
