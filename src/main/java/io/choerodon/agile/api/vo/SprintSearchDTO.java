package io.choerodon.agile.api.vo;

import java.math.BigDecimal;
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
    private BigDecimal todoStoryPoint;
    private BigDecimal doingStoryPoint;
    private BigDecimal doneStoryPoint;
    private List<IssueSearchDTO> issueSearchDTOList;
    private List<AssigneeIssueVO> assigneeIssues;
    private String objectVersionNumber;
    private Long piId;

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

    public List<AssigneeIssueVO> getAssigneeIssues() {
        return assigneeIssues;
    }

    public void setAssigneeIssues(List<AssigneeIssueVO> assigneeIssues) {
        this.assigneeIssues = assigneeIssues;
    }

    public String getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(String objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public BigDecimal getTodoStoryPoint() {
        return todoStoryPoint;
    }

    public void setTodoStoryPoint(BigDecimal todoStoryPoint) {
        this.todoStoryPoint = todoStoryPoint;
    }

    public BigDecimal getDoingStoryPoint() {
        return doingStoryPoint;
    }

    public void setDoingStoryPoint(BigDecimal doingStoryPoint) {
        this.doingStoryPoint = doingStoryPoint;
    }

    public BigDecimal getDoneStoryPoint() {
        return doneStoryPoint;
    }

    public void setDoneStoryPoint(BigDecimal doneStoryPoint) {
        this.doneStoryPoint = doneStoryPoint;
    }

    public void setPiId(Long piId) {
        this.piId = piId;
    }

    public Long getPiId() {
        return piId;
    }
}
