package io.choerodon.agile.api.dto;


import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;


public class PriorityDTO implements Serializable {

    @ApiModelProperty(value = "优先级主键id")
    private Long id;

    @ApiModelProperty(value = "优先级名称")
    private String name;

    @ApiModelProperty(value = "优先级描述")
    private String description;

    @ApiModelProperty(value = "优先级颜色")
    private String colour;

    @ApiModelProperty(value = "优先级所属组织id")
    private Long organizationId;

    @ApiModelProperty(value = "是否默认")
    private Boolean isDefault;

    @ApiModelProperty(value = "版本号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "优先级排序字段")
    private BigDecimal sequence;

    @ApiModelProperty(value = "优先级是否启用")
    private Boolean enable;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public BigDecimal getSequence() {
        return sequence;
    }

    public void setSequence(BigDecimal sequence) {
        this.sequence = sequence;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
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

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public Boolean getDefault() {
        return isDefault;
    }
}
