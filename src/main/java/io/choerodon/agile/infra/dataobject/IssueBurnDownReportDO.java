package io.choerodon.agile.infra.dataobject;

import io.choerodon.agile.api.dto.PriorityDTO;
import io.choerodon.agile.infra.common.utils.StringUtil;

import java.util.Date;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/9/5
 */
public class IssueBurnDownReportDO {

    private Long issueId;

    private String issueNum;

    private String typeCode;

    private Long statusId;

    private String summary;

//    private String priorityCode;

    private Long assigneeId;

    private Long projectId;

    private Long id;

    private Integer storyPoints;

//    private String priorityName;

    private String statusCode;

    private Date addDate;

    private Date doneDate;

    private Boolean completed;

    private String name;

    private Long issueTypeId;

    private Long priorityId;

    public Long getIssueTypeId() {
        return issueTypeId;
    }

    public void setIssueTypeId(Long issueTypeId) {
        this.issueTypeId = issueTypeId;
    }

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

//    public String getPriorityCode() {
//        return priorityCode;
//    }
//
//    public void setPriorityCode(String priorityCode) {
//        this.priorityCode = priorityCode;
//    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(Integer storyPoints) {
        this.storyPoints = storyPoints;
    }

//    public String getPriorityName() {
//        return priorityName;
//    }
//
//    public void setPriorityName(String priorityName) {
//        this.priorityName = priorityName;
//    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public Date getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(Date doneDate) {
        this.doneDate = doneDate;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
