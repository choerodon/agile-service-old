package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.app.service.IFeedbackAttachmentService;
import io.choerodon.agile.infra.annotation.FeedbackDataLog;
import io.choerodon.agile.infra.dataobject.FeedbackAttachmentDTO;
import io.choerodon.agile.infra.mapper.FeedbackAttachmentMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/23.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class IFeedbackAttachmentServiceImpl implements IFeedbackAttachmentService {

    @Autowired
    private FeedbackAttachmentMapper feedbackAttachmentMapper;

    @Override
    @FeedbackDataLog(type = "createAttachment")
    public FeedbackAttachmentDTO createBase(FeedbackAttachmentDTO feedbackAttachmentDTO) {
        if (feedbackAttachmentMapper.insert(feedbackAttachmentDTO) != 1) {
            throw new CommonException("error.feedbackAttachment.insert");
        }
        return feedbackAttachmentMapper.selectByPrimaryKey(feedbackAttachmentDTO.getId());
    }

    @Override
    @FeedbackDataLog(type = "deleteAttachment")
    public Boolean deleteBase(FeedbackAttachmentDTO feedbackAttachmentDTO) {
        if (feedbackAttachmentMapper.delete(feedbackAttachmentDTO) != 1) {
            return false;
        }
        return true;
    }
}
