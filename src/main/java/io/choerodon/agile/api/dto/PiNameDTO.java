package io.choerodon.agile.api.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/4/2.
 * Email: fuqianghuang01@gmail.com
 */
public class PiNameDTO {

    @ApiModelProperty(value = "pi主键id")
    private Long id;

    @ApiModelProperty(value = "pi编码")
    private String code;

    @ApiModelProperty(value = "pi名称")
    private String name;

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

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
