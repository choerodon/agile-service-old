package io.choerodon.agile.infra.dataobject;

import io.choerodon.agile.infra.common.utils.StringUtil;

import java.util.Date;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/30
 */
public class IssueComponentDetailDO {

    private Long issueId;

    private String typeCode;

    private String summary;

    private Long statusId;

    private String priorityCode;

    private Long assigneeId;

    private Long projectId;

    private String priorityName;

    private String statusCode;

    private String statusName;

    private String issueNum;

    private Long reporterId;

    private Date lastUpdateDate;

    private Date creationDate;

    private String epicName;

    private String epicColor;

    private List<VersionIssueRelDO> versionIssueRelDOList;

    private List<LabelIssueRelDO> labelIssueRelDOList;

    private List<ComponentIssueRelDO> componentIssueRelDOList;

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
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

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public String getPriorityCode() {
        return priorityCode;
    }

    public void setPriorityCode(String priorityCode) {
        this.priorityCode = priorityCode;
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

    public String getIssueNum() {
        return issueNum;
    }

    public void setIssueNum(String issueNum) {
        this.issueNum = issueNum;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getEpicName() {
        return epicName;
    }

    public void setEpicName(String epicName) {
        this.epicName = epicName;
    }

    public String getEpicColor() {
        return epicColor;
    }

    public void setEpicColor(String epicColor) {
        this.epicColor = epicColor;
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

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
