package io.choerodon.agile.infra.dataobject;

import io.choerodon.agile.api.vo.IssueTypeVO;
import io.choerodon.agile.api.vo.PriorityVO;
import io.choerodon.agile.api.vo.StatusVO;
import io.choerodon.agile.infra.utils.StringUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 */
public class IssueDetailDTO {

    private Long issueId;

    private String issueNum;

    private String typeCode;

    private Long statusId;

    private String summary;

    private Long reporterId;

    private String description;

    private Long assigneeId;

    private Long projectId;

    private Long epicId;

    private Long parentIssueId;

    private BigDecimal storyPoints;

    private Long objectVersionNumber;

    private Long priorityId;

    private Long issueTypeId;

    private List<VersionIssueRelDTO> versionIssueRelDTOList;

    private List<LabelIssueRelDTO> labelIssueRelDTOList;

    private List<ComponentIssueRelDTO> componentIssueRelDTOList;

    private List<IssueLinkDTO> issueLinkDTOList;

    private SprintNameDTO activeSprint;

    private List<SprintNameDTO> closeSprint;

    private PiNameDTO activePi;

    private List<PiNameDTO> closePi;

    private List<IssueCommentDTO> issueCommentDTOList;

    private List<IssueAttachmentDTO> issueAttachmentDTOList;

    private List<IssueDTO> subIssueDTOList;

    private List<IssueDTO> subBugDOList;

    private Date creationDate;

    private Date lastUpdateDate;

    private BigDecimal estimateTime;

    private BigDecimal remainingTime;

    private String epicName;

    private String issueEpicName;

    private String color;

    private String epicColor;

    private String rank;

    private String parentIssueNum;

    private PriorityVO priorityVO;

    private IssueTypeVO issueTypeVO;

    private StatusVO statusVO;

    private Long createdBy;

    private String applyType;

    private String issueTypeCode;

    private FeatureDTO featureDTO;

    private Long featureId;

    private String featureName;

    private Long relateIssueId;

    private String relateIssueNum;

    public String getIssueTypeCode() {
        return issueTypeCode;
    }

    public void setIssueTypeCode(String issueTypeCode) {
        this.issueTypeCode = issueTypeCode;
    }

    public StatusVO getStatusVO() {
        return statusVO;
    }

    public void setStatusVO(StatusVO statusVO) {
        this.statusVO = statusVO;
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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
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

    public List<IssueCommentDTO> getIssueCommentDTOList() {
        return issueCommentDTOList;
    }

    public void setIssueCommentDTOList(List<IssueCommentDTO> issueCommentDTOList) {
        this.issueCommentDTOList = issueCommentDTOList;
    }

    public List<IssueAttachmentDTO> getIssueAttachmentDTOList() {
        return issueAttachmentDTOList;
    }

    public void setIssueAttachmentDTOList(List<IssueAttachmentDTO> issueAttachmentDTOList) {
        this.issueAttachmentDTOList = issueAttachmentDTOList;
    }

    public List<IssueDTO> getSubIssueDTOList() {
        return subIssueDTOList;
    }

    public void setSubIssueDTOList(List<IssueDTO> subIssueDTOList) {
        this.subIssueDTOList = subIssueDTOList;
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

    public void setIssueEpicName(String issueEpicName) {
        this.issueEpicName = issueEpicName;
    }

    public String getIssueEpicName() {
        return issueEpicName;
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

    public SprintNameDTO getActiveSprint() {
        return activeSprint;
    }

    public void setActiveSprint(SprintNameDTO activeSprint) {
        this.activeSprint = activeSprint;
    }

    public List<SprintNameDTO> getCloseSprint() {
        return closeSprint;
    }

    public void setCloseSprint(List<SprintNameDTO> closeSprint) {
        this.closeSprint = closeSprint;
    }

    public void setPriorityId(Long priorityId) {
        this.priorityId = priorityId;
    }

    public Long getPriorityId() {
        return priorityId;
    }

    public void setPriorityVO(PriorityVO priorityVO) {
        this.priorityVO = priorityVO;
    }

    public PriorityVO getPriorityVO() {
        return priorityVO;
    }

    public void setIssueTypeId(Long issueTypeId) {
        this.issueTypeId = issueTypeId;
    }

    public Long getIssueTypeId() {
        return issueTypeId;
    }

    public void setIssueTypeVO(IssueTypeVO issueTypeVO) {
        this.issueTypeVO = issueTypeVO;
    }

    public IssueTypeVO getIssueTypeVO() {
        return issueTypeVO;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public String getApplyType() {
        return applyType;
    }

    public void setApplyType(String applyType) {
        this.applyType = applyType;
    }

    public void setFeatureDTO(FeatureDTO featureDTO) {
        this.featureDTO = featureDTO;
    }

    public FeatureDTO getFeatureDTO() {
        return featureDTO;
    }

    public Long getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Long featureId) {
        this.featureId = featureId;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public void setActivePi(PiNameDTO activePi) {
        this.activePi = activePi;
    }

    public PiNameDTO getActivePi() {
        return activePi;
    }

    public void setClosePi(List<PiNameDTO> closePi) {
        this.closePi = closePi;
    }

    public List<PiNameDTO> getClosePi() {
        return closePi;
    }

    public void setRelateIssueId(Long relateIssueId) {
        this.relateIssueId = relateIssueId;
    }

    public Long getRelateIssueId() {
        return relateIssueId;
    }

    public void setSubBugDOList(List<IssueDTO> subBugDOList) {
        this.subBugDOList = subBugDOList;
    }

    public List<IssueDTO> getSubBugDOList() {
        return subBugDOList;
    }

    public void setRelateIssueNum(String relateIssueNum) {
        this.relateIssueNum = relateIssueNum;
    }

    public String getRelateIssueNum() {
        return relateIssueNum;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
