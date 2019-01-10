package io.choerodon.agile.api.dto;

import java.util.Date;
import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/5/18.
 */
public class ProductVersionStatisticsDTO {
    private Long versionId;
    private String name;
    private String description;
    private Date startDate;
    private Date expectReleaseDate;
    private Date releaseDate;
    private String statusCode;
    private String statusName;
    private Long projectId;
    private Integer issueCount;
    private Integer doneIssueCount;
    private List<StatusMapDTO> doneStatuses;
    private List<StatusMapDTO> doingStatuses;
    private List<StatusMapDTO> todoStatuses;
    private Integer doingIssueCount;
    private Integer todoIssueCount;

    public List<StatusMapDTO> getDoneStatuses() {
        return doneStatuses;
    }

    public void setDoneStatuses(List<StatusMapDTO> doneStatuses) {
        this.doneStatuses = doneStatuses;
    }

    public List<StatusMapDTO> getDoingStatuses() {
        return doingStatuses;
    }

    public void setDoingStatuses(List<StatusMapDTO> doingStatuses) {
        this.doingStatuses = doingStatuses;
    }

    public List<StatusMapDTO> getTodoStatuses() {
        return todoStatuses;
    }

    public void setTodoStatuses(List<StatusMapDTO> todoStatuses) {
        this.todoStatuses = todoStatuses;
    }

    public Integer getIssueCount() {
        return issueCount;
    }

    public void setIssueCount(Integer issueCount) {
        this.issueCount = issueCount;
    }

    public Integer getDoneIssueCount() {
        return doneIssueCount;
    }

    public void setDoneIssueCount(Integer doneIssueCount) {
        this.doneIssueCount = doneIssueCount;
    }

    public Integer getDoingIssueCount() {
        return doingIssueCount;
    }

    public void setDoingIssueCount(Integer doingIssueCount) {
        this.doingIssueCount = doingIssueCount;
    }

    public Integer getTodoIssueCount() {
        return todoIssueCount;
    }

    public void setTodoIssueCount(Integer todoIssueCount) {
        this.todoIssueCount = todoIssueCount;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public void setExpectReleaseDate(Date expectReleaseDate) {
        this.expectReleaseDate = expectReleaseDate;
    }

    public Date getExpectReleaseDate() {
        return expectReleaseDate;
    }
}
