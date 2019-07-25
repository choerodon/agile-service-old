package io.choerodon.agile.api.vo;

import io.choerodon.mybatis.entity.BaseDTO;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author shinan.chen
 * @since 2019/4/1
 */
public class FieldOptionVO extends BaseDTO {
    @ApiModelProperty(value = "字段选项id")
    private Long id;
    @ApiModelProperty(value = "字段id")
    private Long fieldId;
    @ApiModelProperty(value = "选项值编码")
    private String code;
    @ApiModelProperty(value = "选项值")
    private String value;
    @ApiModelProperty(value = "父选项id")
    private Long parentId;
    @ApiModelProperty(value = "排序")
    private Integer sequence;
    @ApiModelProperty(value = "是否启用")
    private Boolean enabled;
    @ApiModelProperty(value = "组织id")
    private Long organizationId;
    @ApiModelProperty(value = "是否默认值")
    private Boolean isDefault;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}
