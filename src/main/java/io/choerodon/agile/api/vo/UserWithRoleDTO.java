package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author flyleft
 * @date 2018/5/30
 */
public class UserWithRoleDTO extends UserDTO {

    @ApiModelProperty(value = "角色列表")
    private List<RoleDTO> roles;

    public List<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDTO> roles) {
        this.roles = roles;
    }
}
