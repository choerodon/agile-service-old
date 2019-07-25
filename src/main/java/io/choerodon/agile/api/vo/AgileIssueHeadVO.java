package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by WangZhe@choerodon.io on 2019-07-02.
 * Email: ettwz@hotmail.com
 */
public class AgileIssueHeadVO {
    @ApiModelProperty(value = "字段名称")
    private String title;
    @ApiModelProperty(value = "字段编码")
    private String code;
    @ApiModelProperty(value = "字段排序名称")
    private String sortId;
    @ApiModelProperty(value = "字段类型")
    private String fieldType;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSortId() {
        return sortId;
    }

    public void setSortId(String sortId) {
        this.sortId = "foundation." + sortId;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }
}
