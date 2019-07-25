package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author shinan.chen
 * @date 2018/10/9
 */
public class ConfigCodeVO {
    @ApiModelProperty(value = "配置编码")
    private String code;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "配置类型（config_condition/config_validator/config_trigger/config_action）")
    private String type;
    @ApiModelProperty(value = "微服务名称")
    private String service;

    public ConfigCodeVO() {
    }

    public ConfigCodeVO(String code, String name, String description, String type) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.type = type;
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

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    @Override
    public String toString() {
        return "ConfigCodeDTO{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", service='" + service + '\'' +
                '}';
    }
}
