package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * @author shinan.chen
 * @since 2019/4/8
 */
public class PageFieldViewParamVO {
    @ApiModelProperty(value = "上下文")
    @NotNull(message = "error.param.contextNotNull")
    private String context;
    @ApiModelProperty(value = "页面编码")
    @NotNull(message = "error.param.pageCodeNotNull")
    private String pageCode;
    @ApiModelProperty(value = "方案编码")
    @NotNull(message = "error.param.schemeCodeNotNull")
    private String schemeCode;

    @NotNull
    public String getSchemeCode() {
        return schemeCode;
    }

    public void setSchemeCode(@NotNull String schemeCode) {
        this.schemeCode = schemeCode;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getPageCode() {
        return pageCode;
    }

    public void setPageCode(String pageCode) {
        this.pageCode = pageCode;
    }
}
