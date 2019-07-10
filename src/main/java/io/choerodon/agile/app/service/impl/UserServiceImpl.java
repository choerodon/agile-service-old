package io.choerodon.agile.app.service.impl;

import com.github.pagehelper.PageInfo;
import io.choerodon.agile.api.vo.ProjectVO;
import io.choerodon.agile.api.vo.RoleAssignmentSearchDTO;
import io.choerodon.agile.api.vo.RoleDTO;
import io.choerodon.agile.api.vo.UserDTO;
import io.choerodon.agile.app.service.UserService;
import io.choerodon.agile.infra.common.utils.ConvertUtil;
import io.choerodon.agile.infra.dataobject.UserDO;
import io.choerodon.agile.infra.dataobject.UserMessageDO;
import io.choerodon.agile.infra.feign.UserFeignClient;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
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
public class UserServiceImpl implements UserService {

    private final UserFeignClient userFeignClient;

    @Autowired
    public UserServiceImpl(UserFeignClient userFeignClient) {
        this.userFeignClient = userFeignClient;
    }

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
        if (assigneeIdList == null) {
            return new HashMap<>();
        }
        Map<Long, UserMessageDO> userMessageMap = new HashMap<>(assigneeIdList.size());
        if (!assigneeIdList.isEmpty()) {
            Long[] assigneeIds = new Long[assigneeIdList.size()];
            assigneeIdList.toArray(assigneeIds);
            List<UserDO> userDOS = userFeignClient.listUsersByIds(assigneeIds, false).getBody();
            if (withLoginName) {
                userDOS.forEach(userDO -> userMessageMap.put(userDO.getId(), new UserMessageDO(userDO.getLoginName() + userDO.getRealName(), userDO.getLoginName(), userDO.getRealName(), userDO.getImageUrl(), userDO.getEmail())));
            } else {
                userDOS.forEach(userDO -> userMessageMap.put(userDO.getId(), new UserMessageDO(userDO.getRealName(), userDO.getLoginName(), userDO.getRealName(), userDO.getImageUrl(), userDO.getEmail())));
            }
        }
        return userMessageMap;
    }

    @Override
    public List<UserDTO> queryUsersByNameAndProjectId(Long projectId, String name) {
        ResponseEntity<PageInfo<UserDTO>> userList = userFeignClient.list(projectId, name);
        if (userList != null) {
            return userList.getBody().getList();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public ProjectVO queryProject(Long projectId) {
        return userFeignClient.queryProject(projectId).getBody();
    }

    @Override
    public List<RoleDTO> listRolesWithUserCountOnProjectLevel(Long sourceId, RoleAssignmentSearchDTO roleAssignmentSearchDTO) {
        ResponseEntity<List<RoleDTO>> roles = userFeignClient.listRolesWithUserCountOnProjectLevel(sourceId, roleAssignmentSearchDTO);
        return roles != null ? roles.getBody() : new ArrayList<>();
    }

    @Override
    public PageInfo<UserDTO> pagingQueryUsersByRoleIdOnProjectLevel(int page, int size, Long roleId, Long sourceId, RoleAssignmentSearchDTO roleAssignmentSearchDTO) {
        ResponseEntity<PageInfo<UserDTO>> users = userFeignClient.pagingQueryUsersByRoleIdOnProjectLevel(page, size, roleId, sourceId, roleAssignmentSearchDTO);
        return users != null ? users.getBody() : new PageInfo<>(new ArrayList<>());
    }

    @Override
    public List<UserDO> listUsersByIds(Long[] ids) {
        ResponseEntity<List<UserDO>> users = userFeignClient.listUsersByIds(ids, false);
        return users != null ? users.getBody() : new ArrayList<>();
    }

    @Override
    public ProjectVO getGroupInfoByEnableProject(Long organizationId, Long projectId) {
        ResponseEntity<ProjectVO> projectDTOResponseEntity = userFeignClient.getGroupInfoByEnableProject(ConvertUtil.getOrganizationId(projectId), projectId);
        return projectDTOResponseEntity != null ? projectDTOResponseEntity.getBody() : null;
    }

}
