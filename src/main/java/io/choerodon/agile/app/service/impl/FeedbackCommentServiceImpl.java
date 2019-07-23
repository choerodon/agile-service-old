package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.api.validator.FeedbackCommentValidator;
import io.choerodon.agile.app.service.FeedbackCommentService;
import io.choerodon.agile.app.service.IFeedbackCommentService;
import io.choerodon.agile.app.service.UserService;
import io.choerodon.agile.infra.common.utils.SendEmailUtil;
import io.choerodon.agile.infra.dataobject.FeedbackAttachmentDTO;
import io.choerodon.agile.infra.dataobject.FeedbackCommentDTO;
import io.choerodon.agile.infra.dataobject.UserMessageDTO;
import io.choerodon.agile.infra.mapper.FeedbackAttachmentMapper;
import io.choerodon.agile.infra.mapper.FeedbackCommentMapper;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/23.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class FeedbackCommentServiceImpl implements FeedbackCommentService {

    @Autowired
    private FeedbackCommentMapper feedbackCommentMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private SendEmailUtil sendEmailUtil;

    @Autowired
    private FeedbackCommentValidator feedbackCommentValidator;

    @Autowired
    private FeedbackAttachmentMapper feedbackAttachmentMapper;

    @Autowired
    private IFeedbackCommentService iFeedbackCommentService;

    @Override
    public FeedbackCommentDTO createFeedbackComment(Long projectId, FeedbackCommentDTO feedbackCommentDTO) {
        feedbackCommentValidator.checkFeedbackCommentCreate(feedbackCommentDTO);
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        feedbackCommentDTO.setUserId(customUserDetails.getUserId());
        FeedbackCommentDTO result = iFeedbackCommentService.createBase(feedbackCommentDTO);
        if (feedbackCommentDTO.getBeRepliedId() != null && !feedbackCommentDTO.getWithin()) {
            sendEmailUtil.feedbackCommentReplied(feedbackCommentDTO.getBeRepliedId(), feedbackCommentDTO.getContent());
        }
        return result;
    }

    @Override
    public Map<Long, List<FeedbackCommentDTO>> queryListByFeedbackId(Long projectId, Long feedbackId) {
        List<FeedbackCommentDTO> feedbackCommentDTOList = feedbackCommentMapper.selectByOptions(projectId, feedbackId);
        if (feedbackCommentDTOList != null && !feedbackCommentDTOList.isEmpty()) {
            List<Long> userIds = new ArrayList<>();
            feedbackCommentDTOList.forEach(feedbackComment -> {
                userIds.add(feedbackComment.getUserId());
                userIds.add(feedbackComment.getBeRepliedId());
            });
            Map<Long, UserMessageDTO> userMessageMap = userService.queryUsersMap(userIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList()), false);
            List<FeedbackAttachmentDTO> feedbackAttachmentDTOList = feedbackAttachmentMapper.selectByFeedbackId(projectId, feedbackId, "comment");
            Map<Long, List<FeedbackAttachmentDTO>> attachmentMap = feedbackAttachmentDTOList.stream().collect(Collectors.groupingBy(FeedbackAttachmentDTO::getCommentId));
            feedbackCommentDTOList.forEach(feedbackComment -> {
                feedbackComment.setUser(userMessageMap.get(feedbackComment.getUserId()) != null ? userMessageMap.get(feedbackComment.getUserId()) : null);
                feedbackComment.setBeRepliedUser(userMessageMap.get(feedbackComment.getBeRepliedId()) != null ? userMessageMap.get(feedbackComment.getBeRepliedId()) : null);
                feedbackComment.setFeedbackAttachmentDTOList(attachmentMap.get(feedbackComment.getId()));
            });
            Map<Long, List<FeedbackCommentDTO>> commentMap = feedbackCommentDTOList.stream().collect(Collectors.groupingBy(FeedbackCommentDTO::getParentId));
            return commentMap;
        } else {
            return new HashMap<>();
        }
    }

    @Override
    public void deleteById(Long projectId, Long id) {
        FeedbackCommentDTO feedbackCommentDTO = new FeedbackCommentDTO();
        feedbackCommentDTO.setProjectId(projectId);
        feedbackCommentDTO.setId(id);
        iFeedbackCommentService.deleteBase(feedbackCommentDTO);
    }

    @Override
    public FeedbackCommentDTO updateComment(Long projectId, FeedbackCommentDTO feedbackCommentDTO) {
        feedbackCommentValidator.checkFeedbackCommentUpdate(feedbackCommentDTO);
        FeedbackCommentDTO updateDTO = new FeedbackCommentDTO();
        updateDTO.setId(feedbackCommentDTO.getId());
        updateDTO.setProjectId(feedbackCommentDTO.getProjectId());
        updateDTO.setContent(feedbackCommentDTO.getContent());
        updateDTO.setObjectVersionNumber(feedbackCommentDTO.getObjectVersionNumber());
        return iFeedbackCommentService.updateBase(feedbackCommentDTO);
    }

}
