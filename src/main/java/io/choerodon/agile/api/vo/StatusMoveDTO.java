package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/16.
 * Email: fuqianghuang01@gmail.com
 */
public class StatusMoveDTO {

    @ApiModelProperty(value = "状态位置")
    private Integer position;

    @ApiModelProperty(value = "目标列的id")
    private Long columnId;

    @ApiModelProperty(value = "原始列的id")
    private Long originColumnId;

    @ApiModelProperty(value = "版本号")
    private Long statusObjectVersionNumber;

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public Long getStatusObjectVersionNumber() {
        return statusObjectVersionNumber;
    }

    public void setStatusObjectVersionNumber(Long statusObjectVersionNumber) {
        this.statusObjectVersionNumber = statusObjectVersionNumber;
    }

    public void setOriginColumnId(Long originColumnId) {
        this.originColumnId = originColumnId;
    }

    public Long getOriginColumnId() {
        return originColumnId;
    }
}
