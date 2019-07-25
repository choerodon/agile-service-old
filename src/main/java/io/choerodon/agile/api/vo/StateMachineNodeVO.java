package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author peng.jiang, dinghuang123@gmail.com
 */
public class StateMachineNodeVO {
    @ApiModelProperty(value = "状态机节点id")
    private Long id;
    @ApiModelProperty(value = "状态机id")
    private Long stateMachineId;
    @ApiModelProperty(value = "节点对应的状态id")
    private Long statusId;
    @ApiModelProperty(value = "X轴位置")
    private Long positionX;
    @ApiModelProperty(value = "Y轴位置")
    private Long positionY;
    @ApiModelProperty(value = "宽度")
    private Long width;
    @ApiModelProperty(value = "高度")
    private Long height;
    @ApiModelProperty(value = "乐观锁")
    private Long objectVersionNumber;
    @ApiModelProperty(value = "节点类型（node_init/node_start/node_custom）")
    private String type;
    @ApiModelProperty(value = "全部转换到当前节点的转换id")
    private Long allStatusTransformId;
    @ApiModelProperty(value = "组织id")
    private Long organizationId;
    @ApiModelProperty(value = "状态对象")
    private StatusVO statusVO;

    /**
     * 前端要用到
     */
    @ApiModelProperty(value = "转入该节点的转换列表")
    private List<StateMachineTransformVO> intoTransform;
    @ApiModelProperty(value = "转出该节点的转换列表")
    private List<StateMachineTransformVO> outTransform;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public StatusVO getStatusVO() {
        return statusVO;
    }

    public void setStatusVO(StatusVO statusVO) {
        this.statusVO = statusVO;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<StateMachineTransformVO> getIntoTransform() {
        return intoTransform;
    }

    public void setIntoTransform(List<StateMachineTransformVO> intoTransform) {
        this.intoTransform = intoTransform;
    }

    public List<StateMachineTransformVO> getOutTransform() {
        return outTransform;
    }

    public void setOutTransform(List<StateMachineTransformVO> outTransform) {
        this.outTransform = outTransform;
    }

    public Long getAllStatusTransformId() {
        return allStatusTransformId;
    }

    public void setAllStatusTransformId(Long allStatusTransformId) {
        this.allStatusTransformId = allStatusTransformId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}
