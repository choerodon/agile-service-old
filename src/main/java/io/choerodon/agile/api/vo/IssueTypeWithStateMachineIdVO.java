package io.choerodon.agile.api.vo;

import com.google.common.base.MoreObjects;

/**
 * @author shinan.chen
 * @date 2018/10/29
 */
public class IssueTypeWithStateMachineIdVO {
    private Long id;

    private String name;
    private String icon;
    private String description;
    private Long organizationId;
    private String colour;
    private String typeCode;
    private Long stateMachineId;
    private Long initStatusId;
    private Boolean initialize;

    public Long getInitStatusId() {
        return initStatusId;
    }

    public void setInitStatusId(Long initStatusId) {
        this.initStatusId = initStatusId;
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

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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

    public Long getStateMachineId() {
        return stateMachineId;
    }

    public void setStateMachineId(Long stateMachineId) {
        this.stateMachineId = stateMachineId;
    }

    public Boolean getInitialize() {
        return initialize;
    }

    public void setInitialize(Boolean initialize) {
        this.initialize = initialize;
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
                .add("stateMachineId", stateMachineId)
                .add("initialize", initialize)
                .toString();
    }
}
