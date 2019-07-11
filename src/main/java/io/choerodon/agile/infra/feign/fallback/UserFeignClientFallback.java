package io.choerodon.agile.infra.feign.fallback;

import com.github.pagehelper.PageInfo;
import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.infra.dataobject.UserDO;
import io.choerodon.agile.infra.feign.UserFeignClient;
import io.choerodon.core.exception.CommonException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/24
 */
@Component
public class UserFeignClientFallback implements UserFeignClient {

    private static final String QUERY_ERROR = "error.UserFeign.query";
    private static final String BATCH_QUERY_ERROR = "error.UserFeign.queryList";

    @Override
    public ResponseEntity<UserDO> query(Long organizationId, Long id) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<List<UserDO>> listUsersByIds(Long[] ids, Boolean onlyEnabled) {
        throw new CommonException(BATCH_QUERY_ERROR);
    }

    @Override
    public ResponseEntity<ProjectVO> queryProject(Long id) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<PageInfo<UserDTO>> list(Long id, String param) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<List<RoleVO>> listRolesWithUserCountOnProjectLevel(Long sourceId, RoleAssignmentSearchVO roleAssignmentSearchVO) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<PageInfo<UserDTO>> pagingQueryUsersByRoleIdOnProjectLevel(int page, int size, Long roleId, Long sourceId, RoleAssignmentSearchVO roleAssignmentSearchVO) {
        throw new CommonException("error.users.get");
    }

    @Override
    public ResponseEntity<OrganizationVO> query(Long id) {
        throw new CommonException("error.organization.get");
    }

    @Override
    public ResponseEntity<List<ProjectRelationshipVO>> getProjUnderGroup(Long orgId, Long id, Boolean onlySelectEnable) {
        throw new CommonException("error.projUnderGroup.get");
    }

    @Override
    public ResponseEntity<ProjectVO> getGroupInfoByEnableProject(Long organizationId, Long projectId) {
        throw new CommonException("error.groupInfo.get");
    }

    @Override
    public ResponseEntity<PageInfo<UserWithRoleVO>> pagingQueryUsersWithProjectLevelRoles(int page, int size, Long sourceId, @Valid RoleAssignmentSearchVO roleAssignmentSearchVO, boolean doPage) {
        throw new CommonException("error.usersWithRoles.get");
    }
}