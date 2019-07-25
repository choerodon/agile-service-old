package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author shinan.chen
 * @since 2019/6/12
 */
public class FieldDataLogCreateVO {
    @ApiModelProperty(value = "实例对象id")
    private Long instanceId;
    @ApiModelProperty(value = "字段id")
    private Long fieldId;

    @ApiModelProperty(value = "旧值")
    private String oldValue;
    @ApiModelProperty(value = "旧值str")
    private String oldString;
    @ApiModelProperty(value = "新值")
    private String newValue;
    @ApiModelProperty(value = "新值str")
    private String newString;

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getOldString() {
        return oldString;
    }

    public void setOldString(String oldString) {
        this.oldString = oldString;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getNewString() {
        return newString;
    }

    public void setNewString(String newString) {
        this.newString = newString;
    }
}
