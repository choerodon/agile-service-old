package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.app.service.IIssueAttachmentService;
import io.choerodon.agile.infra.annotation.DataLog;
import io.choerodon.agile.infra.dataobject.IssueAttachmentDTO;
import io.choerodon.agile.infra.mapper.IssueAttachmentMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IIssueAttachmentServiceImpl implements IIssueAttachmentService {

    private static final String INSERT_ERROR = "error.IssueAttachment.create";

    @Autowired
    private IssueAttachmentMapper issueAttachmentMapper;

    @Override
    @DataLog(type = "createAttachment")
    public IssueAttachmentDTO createBase(IssueAttachmentDTO issueAttachmentDTO) {
        if (issueAttachmentMapper.insert(issueAttachmentDTO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        return issueAttachmentMapper.selectByPrimaryKey(issueAttachmentDTO.getAttachmentId());
    }

    @Override
    @DataLog(type = "deleteAttachment")
    public Boolean deleteBase(Long attachmentId) {
        IssueAttachmentDTO issueAttachmentDTO = issueAttachmentMapper.selectByPrimaryKey(attachmentId);
        if (issueAttachmentDTO == null) {
            throw new CommonException("error.attachment.get");
        }
        if (issueAttachmentMapper.delete(issueAttachmentDTO) != 1) {
            throw new CommonException("error.attachment.delete");
        }
        return true;
    }

}
