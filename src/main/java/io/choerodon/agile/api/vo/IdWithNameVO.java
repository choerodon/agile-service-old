package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

public class IdWithNameVO {

    public IdWithNameVO(Long userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "名称")
    private String name;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
