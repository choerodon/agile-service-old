package io.choerodon.agile.infra.repository.impl;

import io.choerodon.agile.api.dto.ProjectDTO;
import io.choerodon.agile.api.dto.UserDTO;
import io.choerodon.agile.domain.agile.repository.UserRepository;
import io.choerodon.agile.infra.dataobject.UserDO;
import io.choerodon.agile.infra.dataobject.UserMessageDO;
import io.choerodon.agile.infra.feign.UserFeignClient;
import io.choerodon.core.domain.Page;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
    public UserDO queryUserNameByOption(Long userId, Boolean withId) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        if (userId == null || userId == 0) {
            return new UserDO();
        } else {
            UserDO userDO = userFeignClient.query(customUserDetails.getOrganizationId(), userId).getBody();
            if (withId) {
                userDO.setRealName(userDO.getLoginName() + userDO.getRealName());
                return userDO;
            } else {
                return userDO;
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
                userDOS.forEach(userDO -> userMessageMap.put(userDO.getId(), new UserMessageDO(userDO.getLoginName() + userDO.getRealName(), userDO.getImageUrl(),userDO.getEmail())));
            } else {
                userDOS.forEach(userDO -> userMessageMap.put(userDO.getId(), new UserMessageDO(userDO.getRealName(), userDO.getImageUrl(),userDO.getEmail())));
            }
        }
        return userMessageMap;
    }

    @Override
    public List<UserDTO> queryUsersByNameAndProjectId(Long projectId, String name) {
        ResponseEntity<Page<UserDTO>> userList = userFeignClient.list(projectId, new PageRequest(), name);
        if (userList != null) {
            return userList.getBody().getContent();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public ProjectDTO queryProject(Long projectId) {
        return userFeignClient.queryProject(projectId).getBody();
    }
}
