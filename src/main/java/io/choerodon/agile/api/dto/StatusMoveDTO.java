package io.choerodon.agile.api.dto;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/16.
 * Email: fuqianghuang01@gmail.com
 */
public class StatusMoveDTO {

    private Integer position;

    private Long columnId;

    private Long originColumnId;

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
