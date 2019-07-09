package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.infra.common.annotation.DataLog;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.domain.agile.entity.IssueAttachmentE;
import io.choerodon.agile.infra.repository.IssueAttachmentRepository;
import io.choerodon.agile.infra.dataobject.IssueAttachmentDTO;
import io.choerodon.agile.infra.mapper.IssueAttachmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 20:08:38
 */
@Component
public class IssueAttachmentRepositoryImpl implements IssueAttachmentRepository {

    private static final String INSERT_ERROR = "error.IssueAttachment.create";

    @Autowired
    private IssueAttachmentMapper issueAttachmentMapper;

    @Override
    @DataLog(type = "createAttachment")
    public IssueAttachmentE create(IssueAttachmentE issueAttachmentE) {
        IssueAttachmentDTO issueAttachmentDTO = ConvertHelper.convert(issueAttachmentE, IssueAttachmentDTO.class);
        if (issueAttachmentMapper.insert(issueAttachmentDTO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        return ConvertHelper.convert(issueAttachmentMapper.selectByPrimaryKey(issueAttachmentDTO.getAttachmentId()), IssueAttachmentE.class);
    }

    @Override
    @DataLog(type = "deleteAttachment")
    public Boolean deleteById(Long attachmentId) {
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