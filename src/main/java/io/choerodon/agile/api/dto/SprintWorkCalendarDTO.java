package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/18
 */
public class SprintWorkCalendarDTO {

    @ApiModelProperty(value = "冲刺下的日历变更")
    private List<WorkCalendarRefDTO> workCalendarRefDTOS;

    @ApiModelProperty(value = "冲刺id")
    private Long sprintId;

    @ApiModelProperty(value = "开始时间")
    private Date startDate;

    @ApiModelProperty(value = "冲刺名称")
    private String sprintName;

    @ApiModelProperty(value = "冲刺目标")
    private String sprintGoal;

    @ApiModelProperty(value = "冲刺结束时间")
    private Date endDate;

    @ApiModelProperty(value = "冲刺实际结束时间")
    private Date actualEndDate;

    @ApiModelProperty(value = "冲刺状态")
    private String statusCode;

    public List<WorkCalendarRefDTO> getWorkCalendarRefDTOS() {
        return workCalendarRefDTOS;
    }

    public void setWorkCalendarRefDTOS(List<WorkCalendarRefDTO> workCalendarRefDTOS) {
        this.workCalendarRefDTOS = workCalendarRefDTOS;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getSprintName() {
        return sprintName;
    }

    public void setSprintName(String sprintName) {
        this.sprintName = sprintName;
    }

    public String getSprintGoal() {
        return sprintGoal;
    }

    public void setSprintGoal(String sprintGoal) {
        this.sprintGoal = sprintGoal;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getActualEndDate() {
        return actualEndDate;
    }

    public void setActualEndDate(Date actualEndDate) {
        this.actualEndDate = actualEndDate;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
