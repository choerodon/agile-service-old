package io.choerodon.agile.infra.feign;

import com.github.pagehelper.PageInfo;
import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.infra.dataobject.ApplicationDTO;
import io.choerodon.agile.infra.dataobject.UserDTO;
import io.choerodon.agile.infra.feign.fallback.IamFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/24
 */
@Component
@FeignClient(value = "iam-service", fallback = IamFeignClientFallback.class)
public interface IamFeignClient {

    /**
     * 查询用户信息
     *
     * @param organizationId organizationId
     * @param id             id
     * @return UserDTO
     */
    @GetMapping(value = "/v1/organizations/{organization_id}/users/{id}")
    ResponseEntity<UserDTO> query(@PathVariable(name = "organization_id") Long organizationId,
                                  @PathVariable("id") Long id);

    @PostMapping(value = "/v1/users/ids")
    ResponseEntity<List<UserDTO>> listUsersByIds(@RequestBody Long[] ids,
                                                 @RequestParam(name = "only_enabled") Boolean onlyEnabled);

    /**
     * 按照Id查询项目
     *
     * @param id 要查询的项目ID
     * @return 查询到的项目
     */
    @GetMapping(value = "/v1/projects/{id}")
    ResponseEntity<ProjectVO> queryProject(@PathVariable("id") Long id);

    /**
     * 根据projectId和param模糊查询loginName和realName两列
     *
     * @param id    id
     * @param param param
     * @return UserVO
     */
    @GetMapping(value = "/v1/projects/{id}/users")
    ResponseEntity<PageInfo<UserVO>> list(@PathVariable("id") Long id,
                                          @RequestParam("param") String param);

    @PostMapping(value = "/v1/projects/{project_id}/role_members/users/count")
    ResponseEntity<List<RoleVO>> listRolesWithUserCountOnProjectLevel(
            @PathVariable(name = "project_id") Long sourceId,
            @RequestBody(required = false) @Valid RoleAssignmentSearchVO roleAssignmentSearchVO);

    @PostMapping(value = "/v1/projects/{project_id}/role_members/users")
    ResponseEntity<PageInfo<UserVO>> pagingQueryUsersByRoleIdOnProjectLevel(
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size,
            @RequestParam(name = "role_id") Long roleId,
            @PathVariable(name = "project_id") Long sourceId,
            @RequestBody(required = false) @Valid RoleAssignmentSearchVO roleAssignmentSearchVO);

    @GetMapping(value = "/v1/organizations/{organization_id}")
    ResponseEntity<OrganizationVO> query(@PathVariable(name = "organization_id") Long id);

    @GetMapping(value = "/v1/organizations/{organization_id}/project_relations/{parent_id}")
    ResponseEntity<List<ProjectRelationshipVO>> getProjUnderGroup(@PathVariable(name = "organization_id") Long orgId,
                                                                  @PathVariable(name = "parent_id") Long id,
                                                                  @RequestParam(name = "only_select_enable") Boolean onlySelectEnable);

    @GetMapping(value = "/v1/organizations/{organization_id}/projects/{project_id}/program")
    ResponseEntity<ProjectVO> getGroupInfoByEnableProject(@PathVariable(name = "organization_id") Long organizationId,
                                                          @PathVariable(name = "project_id") Long projectId);

    @PostMapping(value = "/v1/projects/{project_id}/role_members/users/roles")
    ResponseEntity<PageInfo<UserWithRoleVO>> pagingQueryUsersWithProjectLevelRoles(
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size,
            @PathVariable(name = "project_id") Long sourceId,
            @RequestBody(required = false) @Valid RoleAssignmentSearchVO roleAssignmentSearchVO,
            @RequestParam(name = "doPage") boolean doPage);

    @PostMapping(value = "/v1/applications/token")
    ResponseEntity<ApplicationDTO> getApplicationByToken(@RequestBody ApplicationDTO applicationDTO);

    @GetMapping("/v1/organizations/{organization_id}/applications/{id}")
    ResponseEntity<ApplicationDTO> queryByApplicationId(@PathVariable("organization_id") Long organizationId,
                                                        @PathVariable("id") Long id,
                                                        @RequestParam(defaultValue = "true", name = "with_descendants") Boolean withDescendants);

    /**
     * 根据组织id查询所有项目
     *
     * @param organizationId
     * @param page
     * @param size
     * @return
     */
    @GetMapping(value = "/v1/organizations/{organization_id}/projects")
    ResponseEntity<PageInfo<ProjectVO>> queryProjectsByOrgId(@PathVariable("organization_id") Long organizationId,
                                                             @RequestParam(value = "page", required = false) Integer page,
                                                             @RequestParam(value = "size", required = false) Integer size);
}

