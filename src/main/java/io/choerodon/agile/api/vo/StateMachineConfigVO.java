package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author peng.jiang, dinghuang123@gmail.com
 */
public class StateMachineConfigVO {
    @ApiModelProperty(value = "状态机配置id")
    private Long id;
    @ApiModelProperty(value = "转换id")
    private Long transformId;
    @ApiModelProperty(value = "状态机id")
    private Long stateMachineId;
    @ApiModelProperty(value = "配置编码")
    private String code;
    @ApiModelProperty(value = "配置类型（config_condition/config_validator/config_trigger/config_action）")
    private String type;
    @ApiModelProperty(value = "组织id")
    private Long organizationId;
    @ApiModelProperty(value = "配置编码名称")
    private String codeName;
    @ApiModelProperty(value = "配置编码描述")
    private String codeDescription;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTransformId() {
        return transformId;
    }

    public void setTransformId(Long transformId) {
        this.transformId = transformId;
    }

    public Long getStateMachineId() {
        return stateMachineId;
    }

    public void setStateMachineId(Long stateMachineId) {
        this.stateMachineId = stateMachineId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public String getCodeDescription() {
        return codeDescription;
    }

    public void setCodeDescription(String codeDescription) {
        this.codeDescription = codeDescription;
    }
}
