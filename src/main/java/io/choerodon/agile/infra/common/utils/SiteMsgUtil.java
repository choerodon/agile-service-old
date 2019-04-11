package io.choerodon.agile.infra.common.utils;

import io.choerodon.agile.infra.feign.NotifyFeignClient;
import io.choerodon.core.notify.NoticeSendDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/10/8.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class SiteMsgUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(SiteMsgUtil.class);

    private static final String ASSIGNEENAME = "assigneeName";
    private static final String SUMMARY = "summary";
    private static final String URL = "url";

    @Autowired
    private NotifyFeignClient notifyFeignClient;

    public void issueCreate(List<Long> userIds,String userName, String summary, String url, Long reporterId, Long projectId) {
        NoticeSendDTO noticeSendDTO = new NoticeSendDTO();
        noticeSendDTO.setCode("issueCreate");
        Map<String, Object> params = new HashMap<>();
        params.put(ASSIGNEENAME, userName);
        params.put(SUMMARY, summary);
        params.put(URL, url);
        noticeSendDTO.setParams(params);
        List<NoticeSendDTO.User> userList = new ArrayList<>();
        for (Long id : userIds) {
            NoticeSendDTO.User user = new NoticeSendDTO.User();
            user.setId(id);
            userList.add(user);
        }
        noticeSendDTO.setTargetUsers(userList);
        NoticeSendDTO.User fromUser = new NoticeSendDTO.User();
        fromUser.setId(reporterId);
        noticeSendDTO.setFromUser(fromUser);
        noticeSendDTO.setSourceId(projectId);
        try {
            notifyFeignClient.postNotice(noticeSendDTO);
        } catch (Exception e) {
            LOGGER.error("创建issue消息发送失败", e);
        }
    }

    public void issueAssignee(List<Long> userIds, String userName, String summary, String url, Long assigneeId, Long projectId) {
        NoticeSendDTO noticeSendDTO = new NoticeSendDTO();
        noticeSendDTO.setCode("issueAssignee");
        Map<String, Object> params = new HashMap<>();
        params.put(ASSIGNEENAME, userName);
        params.put(SUMMARY, summary);
        params.put(URL, url);
        noticeSendDTO.setParams(params);
        List<NoticeSendDTO.User> userList = new ArrayList<>();
        for (Long id : userIds) {
            NoticeSendDTO.User user = new NoticeSendDTO.User();
            user.setId(id);
            userList.add(user);
        }
        noticeSendDTO.setTargetUsers(userList);
        NoticeSendDTO.User fromUser = new NoticeSendDTO.User();
        fromUser.setId(assigneeId);
        noticeSendDTO.setFromUser(fromUser);
        noticeSendDTO.setSourceId(projectId);
        try {
            notifyFeignClient.postNotice(noticeSendDTO);
        } catch (Exception e) {
            LOGGER.error("分配issue消息发送失败", e);
        }
    }

    public void issueSolve(List<Long> userIds, String userName, String summary, String url, Long assigneeId, Long projectId) {
        NoticeSendDTO noticeSendDTO = new NoticeSendDTO();
        noticeSendDTO.setCode("issueSolve");
        Map<String, Object> params = new HashMap<>();
        params.put(ASSIGNEENAME, userName);
        params.put(SUMMARY, summary);
        params.put(URL, url);
        noticeSendDTO.setParams(params);
        List<NoticeSendDTO.User> userList = new ArrayList<>();
        for (Long id : userIds) {
            NoticeSendDTO.User user = new NoticeSendDTO.User();
            user.setId(id);
            userList.add(user);
        }
        noticeSendDTO.setTargetUsers(userList);
        NoticeSendDTO.User fromUser = new NoticeSendDTO.User();
        fromUser.setId(assigneeId);
        noticeSendDTO.setFromUser(fromUser);
        noticeSendDTO.setSourceId(projectId);
        try {
            notifyFeignClient.postNotice(noticeSendDTO);
        } catch (Exception e) {
            LOGGER.error("完成issue消息发送失败", e);
        }
    }

}
