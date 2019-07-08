package io.choerodon.agile.api.vo;


import io.swagger.annotations.ApiModelProperty;

public class Status {

    @ApiModelProperty(value = "状态主键id")
    private Long id;

    @ApiModelProperty(value = "状态名称")
    private String name;

    @ApiModelProperty(value = "状态描述")
    private String description;

    @ApiModelProperty(value = "状态类别")
    private String type;

    @ApiModelProperty(value = "组织id")
    private Long organizationId;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

}
