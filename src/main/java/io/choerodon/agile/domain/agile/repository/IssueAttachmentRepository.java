package io.choerodon.agile.domain.agile.repository;

import io.choerodon.agile.domain.agile.entity.IssueAttachmentE;


/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/16.
 * Email: fuqianghuang01@gmail.com
 */
public interface IssueAttachmentRepository {

    IssueAttachmentE create(IssueAttachmentE issueAttachmentE);

    Boolean deleteById(Long attachmentId);

}