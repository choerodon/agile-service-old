package io.choerodon.agile.app.service;

import io.choerodon.agile.infra.dataobject.IssueAttachmentDTO;

public interface IIssueAttachmentService {

    IssueAttachmentDTO createBase(IssueAttachmentDTO issueAttachmentDTO);

    Boolean deleteBase(Long attachmentId);
}
