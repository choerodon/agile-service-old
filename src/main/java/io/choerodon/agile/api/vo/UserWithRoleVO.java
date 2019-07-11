package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author flyleft
 * @date 2018/5/30
 */
public class UserWithRoleVO extends UserDTO {

    @ApiModelProperty(value = "角色列表")
    private List<RoleVO> roles;

    public List<RoleVO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleVO> roles) {
        this.roles = roles;
    }
}
