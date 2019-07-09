package io.choerodon.agile.infra.dataobject;

import io.choerodon.agile.infra.common.utils.StringUtil;

import java.math.BigDecimal;

/**
 * Created by jian_zhang02@163.com on 2018/6/2.
 */
public class AssigneeIssueDTO {

    private Long sprintId;
    private Long assigneeId;
    private BigDecimal totalRemainingTime;
    private BigDecimal totalStoryPoints;
    private BigDecimal remainingStoryPoints;
    private Integer issueCount;
    private Integer remainingIssueCount;
    private BigDecimal remainingTime;

    public void setRemainingStoryPoints(BigDecimal remainingStoryPoints) {
        this.remainingStoryPoints = remainingStoryPoints;
    }

    public BigDecimal getRemainingStoryPoints() {
        return remainingStoryPoints;
    }

    public Integer getRemainingIssueCount() {
        return remainingIssueCount;
    }

    public void setRemainingIssueCount(Integer remainingIssueCount) {
        this.remainingIssueCount = remainingIssueCount;
    }

    public BigDecimal getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(BigDecimal remainingTime) {
        this.remainingTime = remainingTime;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public BigDecimal getTotalRemainingTime() {
        return totalRemainingTime;
    }

    public void setTotalRemainingTime(BigDecimal totalRemainingTime) {
        this.totalRemainingTime = totalRemainingTime;
    }

    public void setTotalStoryPoints(BigDecimal totalStoryPoints) {
        this.totalStoryPoints = totalStoryPoints;
    }

    public BigDecimal getTotalStoryPoints() {
        return totalStoryPoints;
    }

    public Integer getIssueCount() {
        return issueCount;
    }

    public void setIssueCount(Integer issueCount) {
        this.issueCount = issueCount;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
