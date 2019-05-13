package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.infra.common.annotation.DataLog;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.domain.agile.entity.IssueAttachmentE;
import io.choerodon.agile.infra.repository.IssueAttachmentRepository;
import io.choerodon.agile.infra.dataobject.IssueAttachmentDO;
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
        IssueAttachmentDO issueAttachmentDO = ConvertHelper.convert(issueAttachmentE, IssueAttachmentDO.class);
        if (issueAttachmentMapper.insert(issueAttachmentDO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        return ConvertHelper.convert(issueAttachmentMapper.selectByPrimaryKey(issueAttachmentDO.getAttachmentId()), IssueAttachmentE.class);
    }

    @Override
    @DataLog(type = "deleteAttachment")
    public Boolean deleteById(Long attachmentId) {
        IssueAttachmentDO issueAttachmentDO = issueAttachmentMapper.selectByPrimaryKey(attachmentId);
        if (issueAttachmentDO == null) {
            throw new CommonException("error.attachment.get");
        }
        if (issueAttachmentMapper.delete(issueAttachmentDO) != 1) {
            throw new CommonException("error.attachment.delete");
        }
        return true;
    }

}