package io.choerodon.agile.api.vo;

import io.choerodon.agile.infra.dataobject.StateMachineInfoDTO;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/11/27.
 * Email: fuqianghuang01@gmail.com
 */
public class StatusWithInfoVO {
    @ApiModelProperty(value = "状态id")
    private Long id;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "状态类型（todo/doing/done/none/prepare）")
    private String type;
    @ApiModelProperty(value = "编码")
    private String code;
    @ApiModelProperty(value = "组织id")
    private Long organizationId;
    @ApiModelProperty(value = "乐观锁")
    private Long objectVersionNumber;
    @ApiModelProperty(value = "用到该状态的状态机列表")
    private List<StateMachineInfoDTO> stateMachineInfoList;

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

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public void setStateMachineInfoList(List<StateMachineInfoDTO> stateMachineInfoList) {
        this.stateMachineInfoList = stateMachineInfoList;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<StateMachineInfoDTO> getStateMachineInfoList() {
        return stateMachineInfoList;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }
}
