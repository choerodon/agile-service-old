package io.choerodon.agile.app.service.impl;

import io.choerodon.agile.app.service.FeedbackDataLogService;
import io.choerodon.agile.app.service.UserService;
import io.choerodon.agile.infra.dataobject.FeedbackDataLogDTO;
import io.choerodon.agile.infra.dataobject.UserMessageDTO;
import io.choerodon.agile.infra.mapper.FeedbackDataLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/23.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class FeedbackDataLogServiceImpl implements FeedbackDataLogService {

    @Autowired
    private FeedbackDataLogMapper feedbackDataLogMapper;

    @Autowired
    private UserService userService;

    @Override
    public List<FeedbackDataLogDTO> listByFeedbackId(Long projectId, Long feedbackId) {
        List<FeedbackDataLogDTO> feedbackDataLogDTOList = feedbackDataLogMapper.selectByOptions(projectId, feedbackId);
        if (feedbackDataLogDTOList != null && !feedbackDataLogDTOList.isEmpty()) {
            List<Long> userIds = feedbackDataLogDTOList.stream().map(FeedbackDataLogDTO::getCreatedBy).collect(Collectors.toList());
            Map<Long, UserMessageDTO> userMessageMap = userService.queryUsersMap(userIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList()), false);
            for (FeedbackDataLogDTO feedbackDataLogDTO : feedbackDataLogDTOList) {
                feedbackDataLogDTO.setCreated(userMessageMap.get(feedbackDataLogDTO.getCreatedBy()));
            }
        }
        return feedbackDataLogDTOList;
    }
}
