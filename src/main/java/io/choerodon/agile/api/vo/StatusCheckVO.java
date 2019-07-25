package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/11/30.
 * Email: fuqianghuang01@gmail.com
 */
public class StatusCheckVO {
    @ApiModelProperty(value = "状态是否存在")
    private Boolean statusExist;
    @ApiModelProperty(value = "状态id")
    private Long id;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "状态类型（todo/doing/done/none/prepare）")
    private String type;

    public void setStatusExist(Boolean statusExist) {
        this.statusExist = statusExist;
    }

    public Boolean getStatusExist() {
        return statusExist;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
