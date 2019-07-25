package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/01/07.
 * Email: fuqianghuang01@gmail.com
 */
public class TransformVO {
    @ApiModelProperty(value = "转换id")
    private Long id;
    @ApiModelProperty(value = "转换名称")
    private String name;
    @ApiModelProperty(value = "转换所属状态机id")
    private Long stateMachineId;
    @ApiModelProperty(value = "终点状态")
    private Long endStatusId;
    @ApiModelProperty(value = "转换类型")
    private String type;
    @ApiModelProperty(value = "开始节点id")
    private Long startNodeId;
    @ApiModelProperty(value = "目标状态类型")
    private String statusType;
    @ApiModelProperty(value = "状态DTO")
    private StatusVO statusVO;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getStartNodeId() {
        return startNodeId;
    }

    public void setStartNodeId(Long startNodeId) {
        this.startNodeId = startNodeId;
    }

    public String getStatusType() {
        return statusType;
    }

    public void setStatusType(String statusType) {
        this.statusType = statusType;
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

    public Long getStateMachineId() {
        return stateMachineId;
    }

    public void setStateMachineId(Long stateMachineId) {
        this.stateMachineId = stateMachineId;
    }

    public Long getEndStatusId() {
        return endStatusId;
    }

    public void setEndStatusId(Long endStatusId) {
        this.endStatusId = endStatusId;
    }

    public StatusVO getStatusVO() {
        return statusVO;
    }

    public void setStatusVO(StatusVO statusVO) {
        this.statusVO = statusVO;
    }
}
