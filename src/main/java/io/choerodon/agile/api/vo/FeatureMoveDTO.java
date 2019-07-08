package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/25.
 * Email: fuqianghuang01@gmail.com
 */
public class FeatureMoveDTO {

    @ApiModelProperty(value = "问题主键id")
    private Long issueId;

    @ApiModelProperty(value = "状态id")
    private Long statusId;

    @ApiModelProperty(value = "pi主键id")
    private Long piId;

    @ApiModelProperty(value = "看板主键id")
    private Long boardId;

    @ApiModelProperty(value = "新列id")
    private Long columnId;

    @ApiModelProperty(value = "原始列id")
    private Long originColumnId;

    @ApiModelProperty(value = "版本号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "true：表示移动的feature需要与pi关联；false反之")
    private Boolean piChange;

    public void setPiId(Long piId) {
        this.piId = piId;
    }

    public Long getPiId() {
        return piId;
    }


    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public Long getOriginColumnId() {
        return originColumnId;
    }

    public void setOriginColumnId(Long originColumnId) {
        this.originColumnId = originColumnId;
    }

    public void setPiChange(Boolean piChange) {
        this.piChange = piChange;
    }

    public Boolean getPiChange() {
        return piChange;
    }
}
