package io.choerodon.agile.api.dto;


import java.util.Date;
import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/9/4.
 * Email: fuqianghuang01@gmail.com
 */
public class SprintInfoDTO {

    private Long sprintId;

    private String sprintName;

    private String statusCode;

    private String sprintGoal;

    private Date startDate;

    private Date endDate;

    private Integer dayRemain;

    private Integer dayTotal;

    private List<AssigneeIssueDTO> assigneeIssueDTOList;

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

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
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

    public List<AssigneeIssueDTO> getAssigneeIssueDTOList() {
        return assigneeIssueDTOList;
    }

    public void setAssigneeIssueDTOList(List<AssigneeIssueDTO> assigneeIssueDTOList) {
        this.assigneeIssueDTOList = assigneeIssueDTOList;
    }

    public void setDayRemain(Integer dayRemain) {
        this.dayRemain = dayRemain;
    }

    public Integer getDayRemain() {
        return dayRemain;
    }

    public void setDayTotal(Integer dayTotal) {
        this.dayTotal = dayTotal;
    }

    public Integer getDayTotal() {
        return dayTotal;
    }
}
