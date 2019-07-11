package io.choerodon.agile.infra.dataobject;

import io.choerodon.mybatis.entity.BaseDTO;
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
@Table(name = "agile_issue")
public class IssueDTO extends BaseDTO {

    /***/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
     * 应用类型
     */
    private String applyType;

    /**
     * 故事点
     */
    private BigDecimal storyPoints;

    @Transient
    private String priorityName;

    @Transient
    private String epicColor;

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
    private List<VersionIssueRelDTO> versionIssueRelDTOS;

    @Transient
    private List<IssueSprintDO> issueSprintDOS;

    @Transient
    private List<IssueComponentBriefDO> issueComponentBriefDOS;

    @Transient
    private List<LabelIssueRelDTO> labelIssueRelDTOS;

    private BigDecimal estimateTime;

    private BigDecimal remainingTime;

    private String epicName;

    private Integer epicSequence;

    private String mapRank;

    private Long priorityId;

    private Long issueTypeId;

    private Date stayDate;

    private Long featureId;

    private Date startDate;

    private Date endDate;

    private Long programId;

    private Long relateIssueId;

    @Transient
    private String featureType;

    @Transient
    private String featureRank;

    @Transient
    private Long featureRankObjectVersionNumber;

    @Transient
    private String epicRank;

    @Transient
    private Long epicRankObjectVersionNumber;

    public List<LabelIssueRelDTO> getLabelIssueRelDTOS() {
        return labelIssueRelDTOS;
    }

    public void setLabelIssueRelDTOS(List<LabelIssueRelDTO> labelIssueRelDTOS) {
        this.labelIssueRelDTOS = labelIssueRelDTOS;
    }

    public String getEpicColor() {
        return epicColor;
    }

    public void setEpicColor(String epicColor) {
        this.epicColor = epicColor;
    }

    public List<IssueComponentBriefDO> getIssueComponentBriefDOS() {
        return issueComponentBriefDOS;
    }

    public void setIssueComponentBriefDOS(List<IssueComponentBriefDO> issueComponentBriefDOS) {
        this.issueComponentBriefDOS = issueComponentBriefDOS;
    }

    public List<IssueSprintDO> getIssueSprintDOS() {
        return issueSprintDOS;
    }

    public void setIssueSprintDOS(List<IssueSprintDO> issueSprintDOS) {
        this.issueSprintDOS = issueSprintDOS;
    }

    public List<VersionIssueRelDTO> getVersionIssueRelDTOS() {
        return versionIssueRelDTOS;
    }

    public void setVersionIssueRelDTOS(List<VersionIssueRelDTO> versionIssueRelDTOS) {
        this.versionIssueRelDTOS = versionIssueRelDTOS;
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

    public void setStoryPoints(BigDecimal storyPoints) {
        this.storyPoints = storyPoints;
    }

    public BigDecimal getStoryPoints() {
        return storyPoints;
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

    public String getApplyType() {
        return applyType;
    }

    public void setApplyType(String applyType) {
        this.applyType = applyType;
    }

    public void setStayDate(Date stayDate) {
        this.stayDate = stayDate;
    }

    public Date getStayDate() {
        return stayDate;
    }

    public Long getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Long featureId) {
        this.featureId = featureId;
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

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public void setRelateIssueId(Long relateIssueId) {
        this.relateIssueId = relateIssueId;
    }

    public Long getRelateIssueId() {
        return relateIssueId;
    }

    public void setFeatureType(String featureType) {
        this.featureType = featureType;
    }

    public String getFeatureType() {
        return featureType;
    }

    public String getFeatureRank() {
        return featureRank;
    }

    public void setFeatureRank(String featureRank) {
        this.featureRank = featureRank;
    }

    public Long getFeatureRankObjectVersionNumber() {
        return featureRankObjectVersionNumber;
    }

    public void setFeatureRankObjectVersionNumber(Long featureRankObjectVersionNumber) {
        this.featureRankObjectVersionNumber = featureRankObjectVersionNumber;
    }

    public String getEpicRank() {
        return epicRank;
    }

    public void setEpicRank(String epicRank) {
        this.epicRank = epicRank;
    }

    public Long getEpicRankObjectVersionNumber() {
        return epicRankObjectVersionNumber;
    }

    public void setEpicRankObjectVersionNumber(Long epicRankObjectVersionNumber) {
        this.epicRankObjectVersionNumber = epicRankObjectVersionNumber;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

}