package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/11/27.
 * Email: fuqianghuang01@gmail.com
 */
public class StatusSearchVO {
    @ApiModelProperty(value = "查询名称")
    private String name;
    @ApiModelProperty(value = "查询描述")
    private String description;
    @ApiModelProperty(value = "查询状态类型（todo/doing/done/none/prepare）")
    private String type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getParam() {
        return param;
    }
}
