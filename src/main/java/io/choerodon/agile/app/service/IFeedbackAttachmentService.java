package io.choerodon.agile.app.service;


import io.choerodon.agile.infra.dataobject.FeedbackAttachmentDTO;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/23.
 * Email: fuqianghuang01@gmail.com
 */
public interface IFeedbackAttachmentService {

    FeedbackAttachmentDTO createBase(FeedbackAttachmentDTO feedbackAttachmentDTO);

    Boolean deleteBase(FeedbackAttachmentDTO feedbackAttachmentDTO);
}
