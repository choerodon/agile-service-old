package io.choerodon.agile.api.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/4/25.
 * Email: fuqianghuang01@gmail.com
 */
public class SprintCalendarDTO {

    @ApiModelProperty(value = "冲刺id")
    private Long sprintId;

    @ApiModelProperty(value = "冲刺名称")
    private String sprintName;

    @ApiModelProperty(value = "冲刺开始时间")
    private Date startDate;

    @ApiModelProperty(value = "冲刺结束时间")
    private Date endDate;

    @ApiModelProperty(value = "冲刺状态")
    private String statusCode;

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public String getSprintName() {
        return sprintName;
    }

    public void setSprintName(String sprintName) {
        this.sprintName = sprintName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
