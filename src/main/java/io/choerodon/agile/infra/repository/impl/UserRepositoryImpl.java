package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.domain.agile.repository.UserRepository;
import io.choerodon.agile.infra.dataobject.UserDO;
import io.choerodon.agile.infra.dataobject.UserMessageDO;
import io.choerodon.agile.infra.feign.UserFeignClient;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/28
 */
@Component
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private UserFeignClient userFeignClient;

    @Override
    public String queryUserNameByOption(Long userId, Boolean withId) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        if (userId == null || userId == 0) {
            return null;
        } else {
            UserDO userDO = userFeignClient.query(customUserDetails.getOrganizationId(), userId).getBody();
            if (withId) {
                return userDO.getLoginName() + "-" + userDO.getRealName();
            } else {
                return userDO.getRealName();
            }
        }
    }

    @Override
    public Map<Long, UserMessageDO> queryUsersMap(List<Long> assigneeIdList, Boolean withLoginName) {
        Map<Long, UserMessageDO> userMessageMap = new HashMap<>();
        if (assigneeIdList != null && !assigneeIdList.isEmpty()) {
            Long[] assigneeIds = new Long[assigneeIdList.size()];
            assigneeIdList.toArray(assigneeIds);
            List<UserDO> userDOS = userFeignClient.listUsersByIds(assigneeIds).getBody();
            if (withLoginName) {
                userDOS.forEach(userDO -> userMessageMap.put(userDO.getId(), new UserMessageDO(userDO.getLoginName() + userDO.getRealName(), userDO.getImageUrl())));
            } else {
                userDOS.forEach(userDO -> userMessageMap.put(userDO.getId(), new UserMessageDO(userDO.getRealName(), userDO.getImageUrl())));
            }
        }
        return userMessageMap;
    }
}
