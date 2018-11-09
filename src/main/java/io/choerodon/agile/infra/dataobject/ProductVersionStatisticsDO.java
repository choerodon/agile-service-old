package io.choerodon.agile.infra.dataobject;

import java.util.Date;

/**
 * Created by jian_zhang02@163.com on 2018/5/18.
 */
public class ProductVersionStatisticsDO {
    private Long versionId;
    private String name;
    private String description;
    private Date startDate;
    private Date releaseDate;
    private String statusCode;
    private String statusName;
    private Long projectId;
//    private Integer issueCount;
//    private Integer doneIssueCount;
//    private Integer doingIssueCount;
//    private Integer todoIssueCount;

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

//    public Integer getIssueCount() {
//        return issueCount;
//    }
//
//    public void setIssueCount(Integer issueCount) {
//        this.issueCount = issueCount;
//    }
//
//    public Integer getDoneIssueCount() {
//        return doneIssueCount;
//    }
//
//    public void setDoneIssueCount(Integer doneIssueCount) {
//        this.doneIssueCount = doneIssueCount;
//    }
//
//    public Integer getDoingIssueCount() {
//        return doingIssueCount;
//    }
//
//    public void setDoingIssueCount(Integer doingIssueCount) {
//        this.doingIssueCount = doingIssueCount;
//    }
//
//    public Integer getTodoIssueCount() {
//        return todoIssueCount;
//    }
//
//    public void setTodoIssueCount(Integer todoIssueCount) {
//        this.todoIssueCount = todoIssueCount;
//    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
