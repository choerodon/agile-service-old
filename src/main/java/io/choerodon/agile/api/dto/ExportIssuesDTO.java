package io.choerodon.agile.api.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by jian_zhang02@163.com on 2018/6/12.
 */
public class ExportIssuesDTO {
    private Long issueId;
    private String issueNum;
    private String summary;
    private String typeName;
    private String projectCode;
    private Long assigneeId;
    private String assigneeName;
    private Long reporterId;
    private String reporterName;
    private String statusName;
    private String description;
    private String sprintName;
    private String closeSprintName;
    private Date creationDate;
    private Date lastUpdateDate;
    private String priorityName;
    private String subTask;
    private BigDecimal estimateTime;
    private BigDecimal remainingTime;
    private String fixVersionName;
    private String influenceVersionName;
    private String projectName;
    private String versionName;

    private String solution;
    private BigDecimal sumEstimateTime;
    private BigDecimal sumRemainingTime;
    private String epicName;
    private Integer storyPoints;
    private String componentName;
    private String labelName;
    private String resolution;

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getIssueNum() {
        return issueNum;
    }

    public void setIssueNum(String issueNum) {
        this.issueNum = issueNum;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSprintName() {
        return sprintName;
    }

    public void setSprintName(String sprintName) {
        this.sprintName = sprintName;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getPriorityName() {
        return priorityName;
    }

    public void setPriorityName(String priorityName) {
        this.priorityName = priorityName;
    }

    public String getSubTask() {
        return subTask;
    }

    public void setSubTask(String subTask) {
        this.subTask = subTask;
    }

    public BigDecimal getEstimateTime() {
        return estimateTime;
    }

    public void setEstimateTime(BigDecimal estimateTime) {
        this.estimateTime = estimateTime;
    }

    public BigDecimal getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(BigDecimal remainingTime) {
        this.remainingTime = remainingTime;
    }

    public String getCloseSprintName() {
        return closeSprintName;
    }

    public void setCloseSprintName(String closeSprintName) {
        this.closeSprintName = closeSprintName;
    }

    public String getFixVersionName() {
        return fixVersionName;
    }

    public void setFixVersionName(String fixVersionName) {
        this.fixVersionName = fixVersionName;
    }

    public String getInfluenceVersionName() {
        return influenceVersionName;
    }

    public void setInfluenceVersionName(String influenceVersionName) {
        this.influenceVersionName = influenceVersionName;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public BigDecimal getSumEstimateTime() {
        return sumEstimateTime;
    }

    public void setSumEstimateTime(BigDecimal sumEstimateTime) {
        this.sumEstimateTime = sumEstimateTime;
    }

    public BigDecimal getSumRemainingTime() {
        return sumRemainingTime;
    }

    public void setSumRemainingTime(BigDecimal sumRemainingTime) {
        this.sumRemainingTime = sumRemainingTime;
    }

    public String getEpicName() {
        return epicName;
    }

    public void setEpicName(String epicName) {
        this.epicName = epicName;
    }

    public Integer getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(Integer storyPoints) {
        this.storyPoints = storyPoints;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }
}
