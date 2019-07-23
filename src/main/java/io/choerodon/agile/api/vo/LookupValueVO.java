package io.choerodon.agile.api.vo;


import io.swagger.annotations.ApiModelProperty;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/09/27.
 * Email: fuqianghuang01@gmail.com
 */
public class LookupValueVO {
    @ApiModelProperty(value = "值编码")
    private String valueCode;
    @ApiModelProperty(value = "类型编码")
    private String typeCode;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "乐观锁")
    private Long objectVersionNumber;

    public String getValueCode() {
        return valueCode;
    }

    public void setValueCode(String valueCode) {
        this.valueCode = valueCode;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}