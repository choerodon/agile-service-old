package io.choerodon.agile.api.vo;

import io.choerodon.mybatis.entity.BaseDTO;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * @author shinan.chen
 * @since 2019/4/1
 */
public class FieldOptionUpdateVO extends BaseDTO {
    @ApiModelProperty(value = "字段选项id")
    private Long id;
    @ApiModelProperty(value = "选项值编码")
    @NotNull(message = "error.fieldOption.codeNotNull")
    private String code;
    @ApiModelProperty(value = "选项值")
    @NotNull(message = "error.fieldOption.valueNotNull")
    private String value;
    @ApiModelProperty(value = "父选项id")
    private Long parentId;
    @ApiModelProperty(value = "是否启用")
    @NotNull(message = "error.fieldOption.enabledNotNull")
    private Boolean enabled;
    @ApiModelProperty(value = "选项状态（normal/update/add）")
    private String status;
    @ApiModelProperty(value = "排序")
    private Integer sequence;
    @ApiModelProperty(value = "是否默认值")
    private Boolean isDefault;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
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

    @NotNull
    public String getValue() {
        return value;
    }

    public void setValue(@NotNull String value) {
        this.value = value;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @NotNull
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(@NotNull Boolean enabled) {
        this.enabled = enabled;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
}
