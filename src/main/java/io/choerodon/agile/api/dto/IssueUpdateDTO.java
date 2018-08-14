package io.choerodon.agile.api.dto;


import io.choerodon.agile.infra.common.annotation.Update;
import io.choerodon.agile.infra.common.utils.StringUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 */
public class IssueUpdateDTO {

    private Long issueId;

    private String issueNum;

    private Long statusId;

    private String summary;

    private String priorityCode;

    private Long reporterId;

    private String description;

    private Long assigneeId;

    private Long epicId;

    private Long sprintId;

    private Long parentIssueId;

    private Integer storyPoints;

    @Update(temp = true)
    private String versionType;

    private List<VersionIssueRelDTO> versionIssueRelDTOList;

    private List<LabelIssueRelDTO> labelIssueRelDTOList;

    private List<ComponentIssueRelDTO> componentIssueRelDTOList;

    private List<IssueLinkDTO> issueLinkDTOList;

    private List<IssueAttachmentDTO> issueAttachmentDTOList;

    private Long objectVersionNumber;

    private BigDecimal estimateTime;

    private BigDecimal remainingTime;

    private String colorCode;

    private String epicName;

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

    public Long getEpicId() {
        return epicId;
    }

    public void setEpicId(Long epicId) {
        this.epicId = epicId;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
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

    public List<VersionIssueRelDTO> getVersionIssueRelDTOList() {
        return versionIssueRelDTOList;
    }

    public void setVersionIssueRelDTOList(List<VersionIssueRelDTO> versionIssueRelDTOList) {
        this.versionIssueRelDTOList = versionIssueRelDTOList;
    }

    public List<LabelIssueRelDTO> getLabelIssueRelDTOList() {
        return labelIssueRelDTOList;
    }

    public void setLabelIssueRelDTOList(List<LabelIssueRelDTO> labelIssueRelDTOList) {
        this.labelIssueRelDTOList = labelIssueRelDTOList;
    }

    public List<ComponentIssueRelDTO> getComponentIssueRelDTOList() {
        return componentIssueRelDTOList;
    }

    public void setComponentIssueRelDTOList(List<ComponentIssueRelDTO> componentIssueRelDTOList) {
        this.componentIssueRelDTOList = componentIssueRelDTOList;
    }

    public List<IssueLinkDTO> getIssueLinkDTOList() {
        return issueLinkDTOList;
    }

    public void setIssueLinkDTOList(List<IssueLinkDTO> issueLinkDTOList) {
        this.issueLinkDTOList = issueLinkDTOList;
    }

    public List<IssueAttachmentDTO> getIssueAttachmentDTOList() {
        return issueAttachmentDTOList;
    }

    public void setIssueAttachmentDTOList(List<IssueAttachmentDTO> issueAttachmentDTOList) {
        this.issueAttachmentDTOList = issueAttachmentDTOList;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public BigDecimal getEstimateTime() {
        return estimateTime;
    }

    public void setEstimateTime(BigDecimal estimateTime) {
        this.estimateTime = estimateTime;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getEpicName() {
        return epicName;
    }

    public void setEpicName(String epicName) {
        this.epicName = epicName;
    }

    public BigDecimal getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(BigDecimal remainingTime) {
        this.remainingTime = remainingTime;
    }

    public void setVersionType(String versionType) {
        this.versionType = versionType;
    }

    public String getVersionType() {
        return versionType;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
