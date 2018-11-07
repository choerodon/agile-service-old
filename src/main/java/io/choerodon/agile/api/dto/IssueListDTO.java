package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;
import io.choerodon.agile.infra.dataobject.IssueComponentBriefDO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 */
public class IssueListDTO implements Serializable {

    private Long issueId;

    private String issueNum;

    private String typeCode;

    private String summary;

    private Long assigneeId;

    private Long reporterId;

    private Long projectId;

    private Long issueTypeId;

    private String assigneeName;

    private String reporterName;

    private String reporterImageUrl;

    private String assigneeImageUrl;

    private String epicName;

    private Long epicId;

    private Integer storyPoints;

    private Boolean addIssue;

    private BigDecimal remainingTime;

    private PriorityDTO priorityDTO;

    private StatusMapDTO statusMapDTO;

    private IssueTypeDTO issueTypeDTO;

    private Date lastUpdateDate;

    private List<VersionIssueRelDTO> versionIssueRelDTOS;

    private List<IssueSprintDTO> issueSprintDTOS;

    private List<IssueComponentBriefDTO> issueComponentBriefDTOS;

    public List<IssueSprintDTO> getIssueSprintDTOS() {
        return issueSprintDTOS;
    }

    public void setIssueSprintDTOS(List<IssueSprintDTO> issueSprintDTOS) {
        this.issueSprintDTOS = issueSprintDTOS;
    }

    public List<IssueComponentBriefDTO> getIssueComponentBriefDTOS() {
        return issueComponentBriefDTOS;
    }

    public void setIssueComponentBriefDTOS(List<IssueComponentBriefDTO> issueComponentBriefDTOS) {
        this.issueComponentBriefDTOS = issueComponentBriefDTOS;
    }

    public Long getEpicId() {
        return epicId;
    }

    public void setEpicId(Long epicId) {
        this.epicId = epicId;
    }

    public Long getIssueTypeId() {
        return issueTypeId;
    }

    public void setIssueTypeId(Long issueTypeId) {
        this.issueTypeId = issueTypeId;

    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
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

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public String getReporterImageUrl() {
        return reporterImageUrl;
    }

    public void setReporterImageUrl(String reporterImageUrl) {
        this.reporterImageUrl = reporterImageUrl;
    }

    public String getAssigneeImageUrl() {
        return assigneeImageUrl;
    }

    public void setAssigneeImageUrl(String assigneeImageUrl) {
        this.assigneeImageUrl = assigneeImageUrl;
    }

    public Integer getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(Integer storyPoints) {
        this.storyPoints = storyPoints;
    }

    public Boolean getAddIssue() {
        return addIssue;
    }

    public void setAddIssue(Boolean addIssue) {
        this.addIssue = addIssue;
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

    public void setPriorityDTO(PriorityDTO priorityDTO) {
        this.priorityDTO = priorityDTO;
    }

    public PriorityDTO getPriorityDTO() {
        return priorityDTO;
    }

    public void setStatusMapDTO(StatusMapDTO statusMapDTO) {
        this.statusMapDTO = statusMapDTO;
    }

    public StatusMapDTO getStatusMapDTO() {
        return statusMapDTO;
    }

    public void setIssueTypeDTO(IssueTypeDTO issueTypeDTO) {
        this.issueTypeDTO = issueTypeDTO;
    }

    public IssueTypeDTO getIssueTypeDTO() {
        return issueTypeDTO;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
