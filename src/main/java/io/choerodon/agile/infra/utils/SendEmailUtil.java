package io.choerodon.agile.infra.utils;

import io.choerodon.agile.infra.feign.NotifyFeignClient;
import io.choerodon.core.notify.NoticeSendDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SendEmailUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(SendEmailUtil.class);

    @Autowired
    private NotifyFeignClient notifyFeignClient;

    @Async
    public void feedbackDoing(String emailAddress, String summary, String description) {
        NoticeSendDTO noticeSendDTO = new NoticeSendDTO();
        noticeSendDTO.setCode("feedback-doing");

        // 设置内容
        Map<String, Object> params = new HashMap<>();
        params.put("summary", summary);
        params.put("description", description);
        noticeSendDTO.setParams(params);

        // 设置发送的目标用户
        List<NoticeSendDTO.User> userList = new ArrayList<>();
        NoticeSendDTO.User user = new NoticeSendDTO.User();
        user.setEmail(emailAddress);
        userList.add(user);
        noticeSendDTO.setTargetUsers(userList);
        try {
            notifyFeignClient.postEmail(noticeSendDTO);
        } catch (Exception e) {
            LOGGER.error("反馈处理中的Email发送失败", e);
        }
    }

    @Async
    public void feedbackDone(String emailAddress, String summary, String description) {
        NoticeSendDTO noticeSendDTO = new NoticeSendDTO();
        noticeSendDTO.setCode("feedback-done");

        // 设置内容
        Map<String, Object> params = new HashMap<>();
        params.put("summary", summary);
        params.put("description", description);
        noticeSendDTO.setParams(params);

        // 设置发送的目标用户
        List<NoticeSendDTO.User> userList = new ArrayList<>();
        NoticeSendDTO.User user = new NoticeSendDTO.User();
        user.setEmail(emailAddress);
        userList.add(user);
        noticeSendDTO.setTargetUsers(userList);
        try {
            notifyFeignClient.postEmail(noticeSendDTO);
        } catch (Exception e) {
            LOGGER.error("反馈已完成的Email发送失败", e);
        }
    }

    @Async
    public void feedbackCommentReplied(String emailAddress, String content) {
        NoticeSendDTO noticeSendDTO = new NoticeSendDTO();
        noticeSendDTO.setCode("feedback-replied");
        // 设置内容
        Map<String, Object> params = new HashMap<>();
        params.put("content", content);
        noticeSendDTO.setParams(params);

        // 设置发送的目标用户
        List<NoticeSendDTO.User> userList = new ArrayList<>();
        NoticeSendDTO.User user = new NoticeSendDTO.User();
        user.setEmail(emailAddress);
        userList.add(user);
        noticeSendDTO.setTargetUsers(userList);
        try {
            notifyFeignClient.postEmail(noticeSendDTO);
        } catch (Exception e) {
            LOGGER.error("回复评论的Email发送失败", e);
        }
    }

}
