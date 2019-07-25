package io.choerodon.agile.api.vo;

import io.choerodon.mybatis.entity.BaseDTO;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * @author shinan.chen
 * @since 2019/4/8
 */
public class FieldValueVO extends BaseDTO {
    @ApiModelProperty(value = "字段值id")
    private Long id;
    @ApiModelProperty(value = "实例对象id")
    private Long instanceId;
    @ApiModelProperty(value = "字段id")
    private Long fieldId;
    @ApiModelProperty(value = "字段类型")
    private String fieldType;
    @ApiModelProperty(value = "字段选项id")
    private Long optionId;
    @ApiModelProperty(value = "字段选项值")
    private String optionValue;
    @ApiModelProperty(value = "字符串值")
    private String stringValue;
    @ApiModelProperty(value = "数值")
    private String numberValue;
    @ApiModelProperty(value = "文本值")
    private String textValue;
    @ApiModelProperty(value = "时间值")
    private Date dateValue;
    @ApiModelProperty(value = "项目id")
    private Long projectId;
    @ApiModelProperty(value = "方案编码：用于区分不同方案字段，否则可能instanceId一样")
    private String schemeCode;

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getNumberValue() {
        return numberValue;
    }

    public void setNumberValue(String numberValue) {
        this.numberValue = numberValue;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getSchemeCode() {
        return schemeCode;
    }

    public void setSchemeCode(String schemeCode) {
        this.schemeCode = schemeCode;
    }
}
