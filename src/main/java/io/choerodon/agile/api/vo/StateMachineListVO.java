package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author shinan.chen
 * @date 2019/7/9
 */
public class StateMachineListVO {
    @ApiModelProperty(value = "状态机id")
    private Long id;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "状态机状态（state_machine_draft/state_machine_active/state_machine_create）")
    private String status;
    @ApiModelProperty(value = "组织id")
    private Long organizationId;
    @ApiModelProperty(value = "是否默认状态机")
    private Boolean isDefault;
    @ApiModelProperty(value = "状态机方案列表")
    List<StateMachineSchemeVO> stateMachineSchemeVOS;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public List<StateMachineSchemeVO> getStateMachineSchemeVOS() {
        return stateMachineSchemeVOS;
    }

    public void setStateMachineSchemeVOS(List<StateMachineSchemeVO> stateMachineSchemeVOS) {
        this.stateMachineSchemeVOS = stateMachineSchemeVOS;
    }
}
