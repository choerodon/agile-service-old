package io.choerodon.agile.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.agile.api.vo.ProjectVO;
import io.choerodon.agile.api.vo.RoleAssignmentSearchVO;
import io.choerodon.agile.api.vo.RoleVO;
import io.choerodon.agile.api.vo.UserVO;
import io.choerodon.agile.infra.dataobject.UserDTO;
import io.choerodon.agile.infra.dataobject.UserMessageDTO;

import java.util.List;
import java.util.Map;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/28
 */
public interface UserService {

    /**
     * 查询
     *
     * @param userId userId
     * @param withId withId
     * @return userDO
     */
    UserDTO queryUserNameByOption(Long userId, Boolean withId);

    Map<Long, UserMessageDTO> queryUsersMap(List<Long> assigneeIds, Boolean withLoginName);

    /**
     * 根据项目id和名称查询用户信息
     *
     * @param projectId projectId
     * @param name      name
     * @return UserVO
     */
    List<UserVO> queryUsersByNameAndProjectId(Long projectId, String name);


    /**
     * 根据项目id查询项目信息
     *
     * @param projectId projectId
     * @return ProjectVO
     */
    ProjectVO queryProject(Long projectId);

    List<RoleVO> listRolesWithUserCountOnProjectLevel(Long sourceId, RoleAssignmentSearchVO roleAssignmentSearchVO);

    PageInfo<UserVO> pagingQueryUsersByRoleIdOnProjectLevel(int page, int size, Long roleId, Long sourceId, RoleAssignmentSearchVO roleAssignmentSearchVO);

    List<UserDTO> listUsersByIds(Long[] ids);

    ProjectVO getGroupInfoByEnableProject(Long organizationId, Long projectId);
}
