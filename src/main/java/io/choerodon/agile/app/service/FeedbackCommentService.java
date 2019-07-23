package io.choerodon.agile.app.service;


import io.choerodon.agile.infra.dataobject.FeedbackCommentDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/23.
 * Email: fuqianghuang01@gmail.com
 */
public interface FeedbackCommentService {

    FeedbackCommentDTO createFeedbackComment(Long projectId, FeedbackCommentDTO feedbackCommentDTO);

    Map<Long, List<FeedbackCommentDTO>> queryListByFeedbackId(Long projectId, Long feedbackId);

    void deleteById(Long projectId, Long id);

    FeedbackCommentDTO updateComment(Long projectId, FeedbackCommentDTO feedbackCommentDTO);

}
