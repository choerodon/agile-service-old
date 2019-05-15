package io.choerodon.agile.infra.repository;

import io.choerodon.agile.domain.agile.entity.IssueBranchE;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/19.
 * Email: fuqianghuang01@gmail.com
 */
public interface IssueBranchRepository {

    IssueBranchE create(IssueBranchE issueBranchE);

    IssueBranchE update(IssueBranchE issueBranchE);

    void delete(Long branchId);

}
