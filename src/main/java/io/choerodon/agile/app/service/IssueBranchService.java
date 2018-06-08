package io.choerodon.agile.app.service;

import io.choerodon.agile.api.dto.IssueBranchDTO;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/19.
 * Email: fuqianghuang01@gmail.com
 */
public interface IssueBranchService {

    IssueBranchDTO create(Long projectId, IssueBranchDTO issueBranchDTO);

    IssueBranchDTO update(Long projectId, Long branchId, IssueBranchDTO issueBranchDTO);

    void delete(Long projectId, Long branchId);

    IssueBranchDTO queryIssueBranchById(Long projectId, Long branchId);
}
