package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.app.service.IFeedbackCommentService;
import io.choerodon.agile.infra.common.annotation.FeedbackDataLog;
import io.choerodon.agile.infra.dataobject.FeedbackCommentDTO;
import io.choerodon.agile.infra.mapper.FeedbackCommentMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/23.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class IFeedbackCommentServiceImpl implements IFeedbackCommentService {

    @Autowired
    private FeedbackCommentMapper feedbackCommentMapper;

    @Override
    @FeedbackDataLog(type = "createComment")
    public FeedbackCommentDTO createBase(FeedbackCommentDTO feedbackCommentDTO) {
        if (feedbackCommentMapper.insert(feedbackCommentDTO) != 1) {
            throw new CommonException("error.feedbackComment.insert");
        }
        return feedbackCommentMapper.selectByPrimaryKey(feedbackCommentDTO.getId());
    }

    @Override
    @FeedbackDataLog(type = "deleteComment")
    public void deleteBase(FeedbackCommentDTO feedbackCommentDTO) {
        if (feedbackCommentMapper.delete(feedbackCommentDTO) != 1) {
            throw new CommonException("error.feedbackComment.delete");
        }
    }

    @Override
    @FeedbackDataLog(type = "updateComment")
    public FeedbackCommentDTO updateBase(FeedbackCommentDTO feedbackCommentDTO) {
        if (feedbackCommentMapper.updateByPrimaryKeySelective(feedbackCommentDTO) != 1) {
            throw new CommonException("error.feedbackComment.update");
        }
        return feedbackCommentMapper.selectByPrimaryKey(feedbackCommentDTO.getId());
    }
}
