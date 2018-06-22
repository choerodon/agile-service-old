package io.choerodon.agile.infra.dataobject;

/**
 * Created by jian_zhang02@163.com on 2018/6/22.
 */
public class SprintReportIssueSearchDO {
    private Long issueId;
    private String categoryCode;
    private String name;
    private Boolean completed;
    private String newStatusId;
    private String newCategoryCode;
    private String newName;
    private Boolean newCompleted;
    private String oldStatusId;
    private String oldCategoryCode;
    private String oldName;
    private Boolean oldCompleted;
    private String storyPoints;
    private String issueNum;
    private String typeCode;
    private Long statusId;
    private String summary;
    private String priorityCode;
    private String priorityName;
    private Long assigneeId;
    private Long projectId;

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public String getNewStatusId() {
        return newStatusId;
    }

    public void setNewStatusId(String newStatusId) {
        this.newStatusId = newStatusId;
    }

    public String getNewCategoryCode() {
        return newCategoryCode;
    }

    public void setNewCategoryCode(String newCategoryCode) {
        this.newCategoryCode = newCategoryCode;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public Boolean getNewCompleted() {
        return newCompleted;
    }

    public void setNewCompleted(Boolean newCompleted) {
        this.newCompleted = newCompleted;
    }

    public String getOldStatusId() {
        return oldStatusId;
    }

    public void setOldStatusId(String oldStatusId) {
        this.oldStatusId = oldStatusId;
    }

    public String getOldCategoryCode() {
        return oldCategoryCode;
    }

    public void setOldCategoryCode(String oldCategoryCode) {
        this.oldCategoryCode = oldCategoryCode;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public Boolean getOldCompleted() {
        return oldCompleted;
    }

    public void setOldCompleted(Boolean oldCompleted) {
        this.oldCompleted = oldCompleted;
    }

    public String getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(String storyPoints) {
        this.storyPoints = storyPoints;
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

    public String getPriorityName() {
        return priorityName;
    }

    public void setPriorityName(String priorityName) {
        this.priorityName = priorityName;
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
}
