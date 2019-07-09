package io.choerodon.agile.infra.dataobject;

import java.util.Date;
import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/5/16.
 */
public class SprintSearchDO {
    private Long sprintId;
    private String sprintName;
    private String sprintGoal;
    private Date startDate;
    private Date endDate;
    private String statusCode;
    private List<AssigneeIssueDTO> assigneeIssueDTOList;
    private String objectVersionNumber;
    private Long piId;
    private List<IssueSearchDO> issueSearchDOList;

    public List<IssueSearchDO> getIssueSearchDOList() {
        return issueSearchDOList;
    }

    public void setIssueSearchDOList(List<IssueSearchDO> issueSearchDOList) {
        this.issueSearchDOList = issueSearchDOList;
    }

    public List<AssigneeIssueDTO> getAssigneeIssueDTOList() {
        return assigneeIssueDTOList;
    }

    public void setAssigneeIssueDTOList(List<AssigneeIssueDTO> assigneeIssueDTOList) {
        this.assigneeIssueDTOList = assigneeIssueDTOList;
    }

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

    public String getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(String objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public void setPiId(Long piId) {
        this.piId = piId;
    }

    public Long getPiId() {
        return piId;
    }
}
