package io.choerodon.agile.api.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/01/07.
 * Email: fuqianghuang01@gmail.com
 */
public class TransformDTO {

    @ApiModelProperty(value = "转换id")
    private Long id;

    @ApiModelProperty(value = "转换名称")
    private String name;

    @ApiModelProperty(value = "转换所属状态机id")
    private Long stateMachineId;

    @ApiModelProperty(value = "终点状态")
    private Long endStatusId;

    @ApiModelProperty(value = "状态DTO")
    private StatusDTO statusDTO;

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

    public StatusDTO getStatusDTO() {
        return statusDTO;
    }

    public void setStatusDTO(StatusDTO statusDTO) {
        this.statusDTO = statusDTO;
    }
}
