package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/19.
 * Email: fuqianghuang01@gmail.com
 */
public class LookupTypeWithValuesVO {
    @ApiModelProperty(value = "类型编码")
    private String typeCode;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "乐观锁")
    private Long objectVersionNumber;
    @ApiModelProperty(value = "值列表")
    private List<LookupValueVO> lookupValues;

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

    public void setLookupValues(List<LookupValueVO> lookupValues) {
        this.lookupValues = lookupValues;
    }

    public List<LookupValueVO> getLookupValues() {
        return lookupValues;
    }
}
