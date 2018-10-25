package io.choerodon.agile.infra.dataobject;

import io.choerodon.agile.api.dto.PriorityDTO;
import io.choerodon.agile.infra.common.utils.StringUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 */
public class IssueDetailDO {

    private Long issueId;

    private String issueNum;

    private String typeCode;

    private Long statusId;

    private String summary;

    private String priorityCode;

    private Long reporterId;

    private String statusCode;

    private String description;

    private Long assigneeId;

    private Long projectId;

    private Long epicId;

    private Long parentIssueId;

    private Integer storyPoints;

    private Long objectVersionNumber;

    private Long priorityId;

    private List<VersionIssueRelDO> versionIssueRelDOList;

    private List<LabelIssueRelDO> labelIssueRelDOList;

    private List<ComponentIssueRelDO> componentIssueRelDOList;

    private List<IssueLinkDO> issueLinkDOList;

    private SprintNameDO activeSprint;

    private List<SprintNameDO> closeSprint;

    private List<IssueCommentDO> issueCommentDOList;

    private List<IssueAttachmentDO> issueAttachmentDOList;

    private List<IssueDO> subIssueDOList;

    private Date creationDate;

    private Date lastUpdateDate;

//    private String priorityName;

    private String statusName;

    private BigDecimal estimateTime;

    private BigDecimal remainingTime;

    private String epicName;

    private String color;

    private String epicColor;

    private String rank;

    private String parentIssueNum;

    private PriorityDTO priorityDTO;

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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public List<VersionIssueRelDO> getVersionIssueRelDOList() {
        return versionIssueRelDOList;
    }

    public void setVersionIssueRelDOList(List<VersionIssueRelDO> versionIssueRelDOList) {
        this.versionIssueRelDOList = versionIssueRelDOList;
    }

    public List<LabelIssueRelDO> getLabelIssueRelDOList() {
        return labelIssueRelDOList;
    }

    public void setLabelIssueRelDOList(List<LabelIssueRelDO> labelIssueRelDOList) {
        this.labelIssueRelDOList = labelIssueRelDOList;
    }

    public List<ComponentIssueRelDO> getComponentIssueRelDOList() {
        return componentIssueRelDOList;
    }

    public void setComponentIssueRelDOList(List<ComponentIssueRelDO> componentIssueRelDOList) {
        this.componentIssueRelDOList = componentIssueRelDOList;
    }

    public List<IssueLinkDO> getIssueLinkDOList() {
        return issueLinkDOList;
    }

    public void setIssueLinkDOList(List<IssueLinkDO> issueLinkDOList) {
        this.issueLinkDOList = issueLinkDOList;
    }

    public List<IssueCommentDO> getIssueCommentDOList() {
        return issueCommentDOList;
    }

    public void setIssueCommentDOList(List<IssueCommentDO> issueCommentDOList) {
        this.issueCommentDOList = issueCommentDOList;
    }

    public List<IssueAttachmentDO> getIssueAttachmentDOList() {
        return issueAttachmentDOList;
    }

    public void setIssueAttachmentDOList(List<IssueAttachmentDO> issueAttachmentDOList) {
        this.issueAttachmentDOList = issueAttachmentDOList;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public List<IssueDO> getSubIssueDOList() {
        return subIssueDOList;
    }

    public void setSubIssueDOList(List<IssueDO> subIssueDOList) {
        this.subIssueDOList = subIssueDOList;
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

//    public String getPriorityName() {
//        return priorityName;
//    }
//
//    public void setPriorityName(String priorityName) {
//        this.priorityName = priorityName;
//    }

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

    public String getEpicName() {
        return epicName;
    }

    public void setEpicName(String epicName) {
        this.epicName = epicName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getEpicColor() {
        return epicColor;
    }

    public void setEpicColor(String epicColor) {
        this.epicColor = epicColor;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getParentIssueNum() {
        return parentIssueNum;
    }

    public void setParentIssueNum(String parentIssueNum) {
        this.parentIssueNum = parentIssueNum;
    }

    public SprintNameDO getActiveSprint() {
        return activeSprint;
    }

    public void setActiveSprint(SprintNameDO activeSprint) {
        this.activeSprint = activeSprint;
    }

    public List<SprintNameDO> getCloseSprint() {
        return closeSprint;
    }

    public void setCloseSprint(List<SprintNameDO> closeSprint) {
        this.closeSprint = closeSprint;
    }

    public void setPriorityId(Long priorityId) {
        this.priorityId = priorityId;
    }

    public Long getPriorityId() {
        return priorityId;
    }

    public void setPriorityDTO(PriorityDTO priorityDTO) {
        this.priorityDTO = priorityDTO;
    }

    public PriorityDTO getPriorityDTO() {
        return priorityDTO;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
