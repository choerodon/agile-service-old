package io.choerodon.agile.infra.common.utils;

import io.choerodon.agile.api.dto.WsSendDTO;
import io.choerodon.agile.infra.feign.NotifyFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/10/8.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class SiteMsgUtil {

    @Autowired
    private NotifyFeignClient notifyFeignClient;

    public void issueCreate(Long userId,String userName, String summary) {
        WsSendDTO wsSendDTO = new WsSendDTO();
        wsSendDTO.setId(userId);
        wsSendDTO.setCode("issueCreate");
        wsSendDTO.setTemplateCode("issueCreate-preset");
        Map<String, Object> params = new HashMap<>();
        params.put("userName", userName);
        params.put("summary", summary);
        wsSendDTO.setParams(params);
        notifyFeignClient.postPm(wsSendDTO);
    }

    public void issueAssignee(Long userId, String userName, String summary) {
        WsSendDTO wsSendDTO = new WsSendDTO();
        wsSendDTO.setId(userId);
        wsSendDTO.setCode("issueAssignee");
        wsSendDTO.setTemplateCode("issueAssignee-preset");
        Map<String, Object> params = new HashMap<>();
        params.put("userName", userName);
        params.put("summary", summary);
        wsSendDTO.setParams(params);
        notifyFeignClient.postPm(wsSendDTO);
    }

    public void issueSolve(Long userId, String userName, String summary) {
        WsSendDTO wsSendDTO = new WsSendDTO();
        wsSendDTO.setId(userId);
        wsSendDTO.setCode("issueSolve");
        wsSendDTO.setTemplateCode("issueSolve-preset");
        Map<String, Object> params = new HashMap<>();
        params.put("userName", userName);
        params.put("summary", summary);
        wsSendDTO.setParams(params);
        notifyFeignClient.postPm(wsSendDTO);
    }

}
