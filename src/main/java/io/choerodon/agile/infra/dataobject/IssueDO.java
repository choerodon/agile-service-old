package io.choerodon.agile.infra.dataobject;


import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.agile.infra.common.utils.StringUtil;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 敏捷开发Issue
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 20:30:48
 */
@ModifyAudit
@VersionAudit
@Table(name = "agile_issue")
public class IssueDO extends AuditDomain {

    /***/
    @Id
    @GeneratedValue
    private Long issueId;

    /**
     * issue编号
     */
    private String issueNum;

    private String rank;

    /**
     * 类型code
     */
    @NotNull(message = "error.issue.type_codeNotNull")
    private String typeCode;

    /**
     * 状态code
     */
    @NotNull(message = "error.issue.statusIdNotNull")
    private Long statusId;

    /**
     * 概要
     */
    private String summary;

    /**
     * 优先级code
     */
    @NotNull(message = "error.issue.priority_codeNotNull")
    private String priorityCode;

    /**
     * issue负责人id
     */
    @NotNull(message = "error.issue.reporter_idNotNull")
    private Long reporterId;

    /**
     * 描述
     */
    private String description;

    /**
     * 受让人id
     */
    private Long assigneeId;

    /**
     * 项目id
     */
    @NotNull(message = "error.issue.project_idNotNull")
    private Long projectId;

    /**
     * epic的id
     */
    private Long epicId;

    /**
     * 父issue的id
     */
    private Long parentIssueId;

    /**
     * 故事点
     */
    private Integer storyPoints;

    @Transient
    private String priorityName;

    @Transient
    private Long sprintId;

    @Transient
    private String statusCode;

    @Transient
    private String statusName;

    @Transient
    private String typeName;

    private String colorCode;

    @Transient
    private String color;

    @Transient
    private Boolean addIssue;

    @Transient
    private Date addDate;

    @Transient
    private Date doneDate;

    @Transient
    private Boolean completed;

    @Transient
    private List<VersionIssueRelDO> versionIssueRelDOS;

    private BigDecimal estimateTime;

    private BigDecimal remainingTime;

    private String epicName;

    private Integer epicSequence;

    private String mapRank;

    private Long priorityId;

    private Long issueTypeId;

    public List<VersionIssueRelDO> getVersionIssueRelDOS() {
        return versionIssueRelDOS;
    }

    public void setVersionIssueRelDOS(List<VersionIssueRelDO> versionIssueRelDOS) {
        this.versionIssueRelDOS = versionIssueRelDOS;
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

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPriorityCode() {
        return priorityCode;
    }

    public void setPriorityCode(String priorityCode) {
        this.priorityCode = priorityCode;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getEpicId() {
        return epicId;
    }

    public void setEpicId(Long epicId) {
        this.epicId = epicId;
    }

    public Long getParentIssueId() {
        return parentIssueId;
    }

    public void setParentIssueId(Long parentIssueId) {
        this.parentIssueId = parentIssueId;
    }

    public Integer getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(Integer storyPoints) {
        this.storyPoints = storyPoints;
    }

    public String getPriorityName() {
        return priorityName;
    }

    public void setPriorityName(String priorityName) {
        this.priorityName = priorityName;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getEpicName() {
        return epicName;
    }

    public void setEpicName(String epicName) {
        this.epicName = epicName;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public Boolean getAddIssue() {
        return addIssue;
    }

    public void setAddIssue(Boolean addIssue) {
        this.addIssue = addIssue;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getEpicSequence() {
        return epicSequence;
    }

    public void setEpicSequence(Integer epicSequence) {
        this.epicSequence = epicSequence;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Date getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(Date doneDate) {
        this.doneDate = doneDate;
    }

    public void setMapRank(String mapRank) {
        this.mapRank = mapRank;
    }

    public String getMapRank() {
        return mapRank;
    }

    public void setPriorityId(Long priorityId) {
        this.priorityId = priorityId;
    }

    public Long getPriorityId() {
        return priorityId;
    }

    public void setIssueTypeId(Long issueTypeId) {
        this.issueTypeId = issueTypeId;
    }

    public Long getIssueTypeId() {
        return issueTypeId;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

}