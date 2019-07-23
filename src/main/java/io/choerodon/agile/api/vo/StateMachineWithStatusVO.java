package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2018/11/20
 */
public class StateMachineWithStatusVO {
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
    @ApiModelProperty(value = "状态机中的状态列表")
    private List<StatusVO> statusVOS;

    public StateMachineWithStatusVO() {
    }

    public StateMachineWithStatusVO(Long id, String name, String description, String status, Long organizationId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.organizationId = organizationId;
    }

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

    public List<StatusVO> getStatusVOS() {
        return statusVOS;
    }

    public void setStatusVOS(List<StatusVO> statusVOS) {
        this.statusVOS = statusVOS;
    }
}
