package io.choerodon.agile.app.service;

import io.choerodon.agile.infra.dataobject.FeedbackCommentDTO;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/23.
 * Email: fuqianghuang01@gmail.com
 */
public interface IFeedbackCommentService {

    FeedbackCommentDTO createBase(FeedbackCommentDTO feedbackCommentDTO);

    void deleteBase(FeedbackCommentDTO feedbackCommentDTO);

    FeedbackCommentDTO updateBase(FeedbackCommentDTO feedbackCommentDTO);
}
