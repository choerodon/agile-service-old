package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/3/29
 */
public class ObjectSchemeFieldUpdateVO {
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "默认值")
    private String defaultValue;
    @ApiModelProperty(value = "额外配置（是否当前时间/是否包括小数）")
    private Boolean extraConfig;
    @ApiModelProperty(value = "是否必填")
    private Boolean required;
    @ApiModelProperty(value = "上下文")
    private String[] context;
    @ApiModelProperty(value = "乐观锁")
    private Long objectVersionNumber;
    @ApiModelProperty(value = "字段选项列表")
    private List<FieldOptionUpdateVO> fieldOptions;

    public List<FieldOptionUpdateVO> getFieldOptions() {
        return fieldOptions;
    }

    public void setFieldOptions(List<FieldOptionUpdateVO> fieldOptions) {
        this.fieldOptions = fieldOptions;
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

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Boolean getExtraConfig() {
        return extraConfig;
    }

    public void setExtraConfig(Boolean extraConfig) {
        this.extraConfig = extraConfig;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String[] getContext() {
        return context;
    }

    public void setContext(String[] context) {
        this.context = context;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
