package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/13.
 * Email: fuqianghuang01@gmail.com
 */
public class QuickFilterFieldDTO {

    @ApiModelProperty(value = "快速搜索字段编码，如assignee、component等")
    private String fieldCode;

    @ApiModelProperty(value = "快速搜索字段类型，如long、decimal等")
    private String type;

    @ApiModelProperty(value = "快速搜索字段名称")
    private String name;

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
