package io.choerodon.agile.infra.common.utils;

import io.choerodon.agile.api.dto.NoticeSendDTO;
import io.choerodon.agile.api.dto.WsSendDTO;
import io.choerodon.agile.infra.feign.NotifyFeignClient;
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

    private static final String USERNAME = "userName";
    private static final String SUMMARY = "summary";
    private static final String URL = "url";

    @Autowired
    private NotifyFeignClient notifyFeignClient;

    public void issueCreate(List<Long> userIds,String userName, String summary, String url, Long reporterId) {
        NoticeSendDTO noticeSendDTO = new NoticeSendDTO();
        noticeSendDTO.setCode("issueCreate");
        Map<String, Object> params = new HashMap<>();
        params.put(USERNAME, userName);
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
        notifyFeignClient.postNotice(noticeSendDTO);
    }

    public void issueAssignee(List<Long> userIds, String userName, String summary, String url, Long assigneeId) {
        NoticeSendDTO noticeSendDTO = new NoticeSendDTO();
        noticeSendDTO.setCode("issueAssignee");
        Map<String, Object> params = new HashMap<>();
        params.put(USERNAME, userName);
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
        notifyFeignClient.postNotice(noticeSendDTO);
    }

    public void issueSolve(List<Long> userIds, String userName, String summary, String url, Long assigneeId) {
        NoticeSendDTO noticeSendDTO = new NoticeSendDTO();
        noticeSendDTO.setCode("issueSolve");
        Map<String, Object> params = new HashMap<>();
        params.put(USERNAME, userName);
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
        notifyFeignClient.postNotice(noticeSendDTO);
    }

}
