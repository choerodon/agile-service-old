package io.choerodon.agile.domain.agile.repository;

import io.choerodon.agile.api.dto.ProjectDTO;
import io.choerodon.agile.api.dto.RoleAssignmentSearchDTO;
import io.choerodon.agile.api.dto.RoleDTO;
import io.choerodon.agile.api.dto.UserDTO;
import io.choerodon.agile.infra.dataobject.UserDO;
import io.choerodon.agile.infra.dataobject.UserMessageDO;
import io.choerodon.core.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/28
 */
public interface UserRepository {

    /**
     * 查询
     *
     * @param userId userId
     * @param withId withId
     * @return userDO
     */
    UserDO queryUserNameByOption(Long userId, Boolean withId);

    Map<Long, UserMessageDO> queryUsersMap(List<Long> assigneeIds, Boolean withLoginName);

    /**
     * 根据项目id和名称查询用户信息
     *
     * @param projectId projectId
     * @param name      name
     * @return UserDTO
     */
    List<UserDTO> queryUsersByNameAndProjectId(Long projectId, String name);


    /**
     * 根据项目id查询项目信息
     *
     * @param projectId projectId
     * @return ProjectDTO
     */
    ProjectDTO queryProject(Long projectId);

    List<RoleDTO> listRolesWithUserCountOnProjectLevel(Long sourceId, RoleAssignmentSearchDTO roleAssignmentSearchDTO);

    Page<UserDTO>  pagingQueryUsersByRoleIdOnProjectLevel(int page, int size, Long roleId, Long sourceId, RoleAssignmentSearchDTO roleAssignmentSearchDTO);

    List<UserDO> listUsersByIds(Long[] ids);
}
