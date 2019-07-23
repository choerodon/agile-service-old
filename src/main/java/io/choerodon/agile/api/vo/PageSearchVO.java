package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author shinan.chen
 * @since 2019/3/29
 */
public class PageSearchVO {
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "方案编码")
    private String schemeCode;
    @ApiModelProperty(value = "页面编码")
    private String pageCode;
    @ApiModelProperty(value = "其他参数")
    private String param;

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

    public String getSchemeCode() {
        return schemeCode;
    }

    public void setSchemeCode(String schemeCode) {
        this.schemeCode = schemeCode;
    }

    public String getPageCode() {
        return pageCode;
    }

    public void setPageCode(String pageCode) {
        this.pageCode = pageCode;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }
}
