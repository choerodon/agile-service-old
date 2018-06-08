package io.choerodon.agile.api.dto;

import java.util.Date;
import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/5/16.
 */
public class SprintSearchDTO {
    private Long sprintId;
    private String sprintName;
    private String sprintGoal;
    private Date startDate;
    private Date endDate;
    private String statusCode;
    private Integer issueCount;
    private Integer todoStoryPoint;
    private Integer doingStoryPoint;
    private Integer doneStoryPoint;
    private List<IssueSearchDTO> issueSearchDTOList;
    private List<AssigneeIssueDTO> assigneeIssues;
    private String objectVersionNumber;

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public String getSprintName() {
        return sprintName;
    }

    public void setSprintName(String sprintName) {
        this.sprintName = sprintName;
    }

    public String getSprintGoal() {
        return sprintGoal;
    }

    public void setSprintGoal(String sprintGoal) {
        this.sprintGoal = sprintGoal;
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

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Integer getIssueCount() {
        return issueCount;
    }

    public void setIssueCount(Integer issueCount) {
        this.issueCount = issueCount;
    }

    public List<IssueSearchDTO> getIssueSearchDTOList() {
        return issueSearchDTOList;
    }

    public void setIssueSearchDTOList(List<IssueSearchDTO> issueSearchDTOList) {
        this.issueSearchDTOList = issueSearchDTOList;
    }

    public Integer getTodoStoryPoint() {
        return todoStoryPoint;
    }

    public void setTodoStoryPoint(Integer todoStoryPoint) {
        this.todoStoryPoint = todoStoryPoint;
    }

    public Integer getDoingStoryPoint() {
        return doingStoryPoint;
    }

    public void setDoingStoryPoint(Integer doingStoryPoint) {
        this.doingStoryPoint = doingStoryPoint;
    }

    public Integer getDoneStoryPoint() {
        return doneStoryPoint;
    }

    public void setDoneStoryPoint(Integer doneStoryPoint) {
        this.doneStoryPoint = doneStoryPoint;
    }

    public List<AssigneeIssueDTO> getAssigneeIssues() {
        return assigneeIssues;
    }

    public void setAssigneeIssues(List<AssigneeIssueDTO> assigneeIssues) {
        this.assigneeIssues = assigneeIssues;
    }

    public String getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(String objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
