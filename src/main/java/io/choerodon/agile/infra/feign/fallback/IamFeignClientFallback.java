package io.choerodon.agile.infra.feign.fallback;

import com.github.pagehelper.PageInfo;
import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.infra.dataobject.ApplicationDTO;
import io.choerodon.agile.infra.dataobject.UserDTO;
import io.choerodon.agile.infra.feign.IamFeignClient;
import io.choerodon.core.exception.CommonException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/24
 */
@Component
public class IamFeignClientFallback implements IamFeignClient {

    private static final String QUERY_ERROR = "error.UserFeign.query";
    private static final String BATCH_QUERY_ERROR = "error.UserFeign.queryList";

    @Override
    public ResponseEntity<UserDTO> query(Long organizationId, Long id) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<List<UserDTO>> listUsersByIds(Long[] ids, Boolean onlyEnabled) {
        throw new CommonException(BATCH_QUERY_ERROR);
    }

    @Override
    public ResponseEntity<ProjectVO> queryProject(Long id) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<PageInfo<UserVO>> list(Long id, String param) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<List<RoleVO>> listRolesWithUserCountOnProjectLevel(Long sourceId, RoleAssignmentSearchVO roleAssignmentSearchVO) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<PageInfo<UserVO>> pagingQueryUsersByRoleIdOnProjectLevel(int page, int size, Long roleId, Long sourceId, RoleAssignmentSearchVO roleAssignmentSearchVO) {
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

    @Override
    public ResponseEntity<ApplicationDTO> getApplicationByToken(@RequestBody ApplicationDTO applicationDTO) {
        throw new CommonException("error.application.get");
    }

    @Override
    public ResponseEntity<ApplicationDTO> queryByApplicationId(Long organizationId, Long id, Boolean withDescendants) {
        throw new CommonException("error.applicationInfo.get");
    }

    @Override
    public ResponseEntity<PageInfo<ProjectVO>> queryProjectsByOrgId(Long organizationId, Integer page, Integer size) {
        throw new CommonException("error.iamServiceFeignFallback.queryProjectsByOrgId");
    }
}