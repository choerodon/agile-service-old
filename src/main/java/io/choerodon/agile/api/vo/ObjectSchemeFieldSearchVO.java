package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author shinan.chen
 * @since 2019/3/29
 */
public class ObjectSchemeFieldSearchVO {
    @ApiModelProperty(value = "上下文")
    private String context;
    @ApiModelProperty(value = "方案编码")
    private String schemeCode;
    @ApiModelProperty(value = "字段编码")
    private String code;
    @ApiModelProperty(value = "名称")
    private String name;

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getSchemeCode() {
        return schemeCode;
    }

    public void setSchemeCode(String schemeCode) {
        this.schemeCode = schemeCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
