package io.choerodon.agile.infra.repository;

import io.choerodon.agile.domain.agile.entity.IssueComponentE;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */

public interface IssueComponentRepository {

    IssueComponentE create(IssueComponentE issueComponentE);

    IssueComponentE update(IssueComponentE issueComponentE);

    void delete(Long id);
}
