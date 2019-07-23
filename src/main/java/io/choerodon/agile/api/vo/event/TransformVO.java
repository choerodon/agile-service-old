package io.choerodon.agile.api.vo.event;

import io.choerodon.agile.api.vo.StatusVO;

/**
 * @author shinan.chen
 * @date 2018/10/24
 */
public class TransformVO {
    private Long id;
    private String name;
    private Long stateMachineId;
    private Long endStatusId;
    private String type;
    private Long startNodeId;

    private String statusType;
    private StatusVO statusVO;

    public Long getStartNodeId() {
        return startNodeId;
    }

    public void setStartNodeId(Long startNodeId) {
        this.startNodeId = startNodeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
