package io.choerodon.agile.api.dto;

import java.math.BigDecimal;

/**
 * Created by jian_zhang02@163.com on 2018/6/2.
 */
public class AssigneeIssueDTO {
    private Long sprintId;
    private Long assigneeId;
    private String assigneeName;
    private String imageUrl;
    private BigDecimal totalRemainingTime;
    private BigDecimal totalStoryPoints;
    private Integer issueCount;
    private BigDecimal remainingStoryPoints;
    private Integer remainingIssueCount;
    private BigDecimal remainingTime;

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

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getIssueCount() {
        return issueCount;
    }

    public void setIssueCount(Integer issueCount) {
        this.issueCount = issueCount;
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

    public void setTotalStoryPoints(BigDecimal totalStoryPoints) {
        this.totalStoryPoints = totalStoryPoints;
    }

    public BigDecimal getTotalStoryPoints() {
        return totalStoryPoints;
    }

    public void setRemainingStoryPoints(BigDecimal remainingStoryPoints) {
        this.remainingStoryPoints = remainingStoryPoints;
    }

    public BigDecimal getRemainingStoryPoints() {
        return remainingStoryPoints;
    }
}
