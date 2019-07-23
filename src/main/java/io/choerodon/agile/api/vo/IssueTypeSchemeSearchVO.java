package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/11/29.
 * Email: fuqianghuang01@gmail.com
 */
public class IssueTypeSchemeSearchVO {

    @ApiModelProperty(value = "查询名称")
    private String name;
    @ApiModelProperty(value = "查询描述")
    private String description;
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

    public void setParam(String param) {
        this.param = param;
    }

    public String getParam() {
        return param;
    }
}
