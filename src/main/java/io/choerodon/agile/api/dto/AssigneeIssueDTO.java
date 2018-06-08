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
    private Integer totalStoryPoints;
    private Integer issueCount;

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

    public Integer getTotalStoryPoints() {
        return totalStoryPoints;
    }

    public void setTotalStoryPoints(Integer totalStoryPoints) {
        this.totalStoryPoints = totalStoryPoints;
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
}
