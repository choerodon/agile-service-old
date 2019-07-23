package io.choerodon.agile.api.vo;

import com.google.common.base.MoreObjects;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * @author shinan.chen
 * @date 2018/8/8
 */
public class IssueTypeVO {
    @ApiModelProperty(value = "问题类型id")
    private Long id;
    @ApiModelProperty(value = "名称")
    @NotNull(message = "error.name.null")
    private String name;
    @ApiModelProperty(value = "图标")
    private String icon;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "组织id")
    private Long organizationId;
    @ApiModelProperty(value = "颜色")
    private String colour;
    @ApiModelProperty(value = "类型编码")
    private String typeCode;
    @ApiModelProperty(value = "是否初始化")
    private Boolean initialize;
    @ApiModelProperty(value = "乐观锁")
    private Long objectVersionNumber;

    /**
     * 用于做状态机方案的配置时
     */
    @ApiModelProperty(value = "状态机名称")
    private String stateMachineName;
    @ApiModelProperty(value = "状态机id")
    private Long stateMachineId;

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

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getStateMachineName() {
        return stateMachineName;
    }

    public void setStateMachineName(String stateMachineName) {
        this.stateMachineName = stateMachineName;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public Boolean getInitialize() {
        return initialize;
    }

    public void setInitialize(Boolean initialize) {
        this.initialize = initialize;
    }

    public Long getStateMachineId() {
        return stateMachineId;
    }

    public void setStateMachineId(Long stateMachineId) {
        this.stateMachineId = stateMachineId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("icon", icon)
                .add("description", description)
                .add("organizationId", organizationId)
                .add("colour", colour)
                .add("typeCode", typeCode)
                .add("initialize", initialize)
                .add("objectVersionNumber", objectVersionNumber)
                .add("stateMachineName", stateMachineName)
                .add("stateMachineId", stateMachineId)
                .toString();
    }
}
