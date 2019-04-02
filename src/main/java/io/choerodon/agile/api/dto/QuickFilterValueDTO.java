package io.choerodon.agile.api.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/14.
 * Email: fuqianghuang01@gmail.com
 */
public class QuickFilterValueDTO {

    @ApiModelProperty(value = "快速搜索字段编码")
    private String fieldCode;

    @ApiModelProperty(value = "快速搜索表达式操作关系：and、or等")
    private String operation;

    @ApiModelProperty(value = "快速搜索值")
    private String value;

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
