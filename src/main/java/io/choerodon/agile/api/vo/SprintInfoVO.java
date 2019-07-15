package io.choerodon.agile.api.vo;


import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/9/4.
 * Email: fuqianghuang01@gmail.com
 */
public class SprintInfoVO {

    @ApiModelProperty(value = "冲刺id")
    private Long sprintId;

    @ApiModelProperty(value = "冲刺名称")
    private String sprintName;

    @ApiModelProperty(value = "冲刺状态code")
    private String statusCode;

    @ApiModelProperty(value = "冲刺目标")
    private String sprintGoal;

    @ApiModelProperty(value = "冲刺开始时间")
    private Date startDate;

    @ApiModelProperty(value = "冲刺结束时间")
    private Date endDate;

    @ApiModelProperty(value = "冲刺剩余时间（天）")
    private Integer dayRemain;

    @ApiModelProperty(value = "冲刺总天数")
    private Integer dayTotal;

    @ApiModelProperty(value = "问题数量")
    private Integer issueCount;

    private List<AssigneeIssueVO> assigneeIssueVOList;

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

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getSprintGoal() {
        return sprintGoal;
    }

    public void setSprintGoal(String sprintGoal) {
        this.sprintGoal = sprintGoal;
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

    public List<AssigneeIssueVO> getAssigneeIssueVOList() {
        return assigneeIssueVOList;
    }

    public void setAssigneeIssueVOList(List<AssigneeIssueVO> assigneeIssueVOList) {
        this.assigneeIssueVOList = assigneeIssueVOList;
    }

    public void setDayRemain(Integer dayRemain) {
        this.dayRemain = dayRemain;
    }

    public Integer getDayRemain() {
        return dayRemain;
    }

    public void setDayTotal(Integer dayTotal) {
        this.dayTotal = dayTotal;
    }

    public Integer getDayTotal() {
        return dayTotal;
    }

    public void setIssueCount(Integer issueCount) {
        this.issueCount = issueCount;
    }

    public Integer getIssueCount() {
        return issueCount;
    }
}
