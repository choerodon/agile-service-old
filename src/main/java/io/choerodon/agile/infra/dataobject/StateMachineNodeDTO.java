package io.choerodon.agile.infra.dataobject;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.*;

/**
 * @author peng.jiang,dinghuang123@gmail.com
 */
@Table(name = "fd_state_machine_node")
public class StateMachineNodeDTO extends BaseDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long stateMachineId;
    private Long statusId;
    private Long positionX;
    private Long positionY;
    private Long width;
    private Long height;
    private String type;
    private Long organizationId;
    /**
     * 所有状态都转换此状态的转换id
     */
    private Long allStatusTransformId;

    @Transient
    private StatusDTO status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPositionX() {
        return positionX;
    }

    public void setPositionX(Long positionX) {
        this.positionX = positionX;
    }

    public Long getPositionY() {
        return positionY;
    }

    public void setPositionY(Long positionY) {
        this.positionY = positionY;
    }

    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public StatusDTO getStatus() {
        return status;
    }

    public void setStatus(StatusDTO status) {
        this.status = status;
    }

    public Long getStateMachineId() {
        return stateMachineId;
    }

    public void setStateMachineId(Long stateMachineId) {
        this.stateMachineId = stateMachineId;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
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

    public Long getAllStatusTransformId() {
        return allStatusTransformId;
    }

    public void setAllStatusTransformId(Long allStatusTransformId) {
        this.allStatusTransformId = allStatusTransformId;
    }
}
