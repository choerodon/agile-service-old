package io.choerodon.agile.api.validator;

import io.choerodon.agile.infra.dataobject.FeedbackCommentDTO;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/23.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class FeedbackCommentValidator {

    public void checkFeedbackCommentCreate(FeedbackCommentDTO feedbackCommentDTO) {
        if (feedbackCommentDTO.getFeedbackId() == null) {
            throw new CommonException("error.feedbackComment.isNull");
        }
        if (feedbackCommentDTO.getUserId() == null) {
            throw new CommonException("error.userId.isNull");
        }
        if (feedbackCommentDTO.getContent() == null) {
            throw new CommonException("error.content.isNull");
        }
        if (feedbackCommentDTO.getProjectId() == null) {
            throw new CommonException("error.projectId.isNull");
        }
        if (feedbackCommentDTO.getWithin() == null) {
            throw new CommonException("error.within.isNull");
        }
    }

    public void checkFeedbackCommentUpdate(FeedbackCommentDTO feedbackCommentDTO) {
        if (feedbackCommentDTO.getId() == null) {
            throw new CommonException("error.id.isNull");
        }
        if (feedbackCommentDTO.getContent() == null) {
            throw new CommonException("error.content.isNull");
        }
        if (feedbackCommentDTO.getObjectVersionNumber() == null) {
            throw new CommonException("error.objectVersionNumber.isNull");
        }
    }

}
