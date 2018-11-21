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
    private Date releaseDate;
    private String statusCode;
    private String statusName;
    private Long projectId;
    private Integer issueCount;
    private Integer doneIssueCount;
    private List<Long> doneStatusIds;
    private List<Long> doingStatusIds;
    private List<Long> todoStatusIds;
    private Integer doingIssueCount;
    private Integer todoIssueCount;

    public List<Long> getDoneStatusIds() {
        return doneStatusIds;
    }

    public void setDoneStatusIds(List<Long> doneStatusIds) {
        this.doneStatusIds = doneStatusIds;
    }

    public List<Long> getDoingStatusIds() {
        return doingStatusIds;
    }

    public void setDoingStatusIds(List<Long> doingStatusIds) {
        this.doingStatusIds = doingStatusIds;
    }

    public List<Long> getTodoStatusIds() {
        return todoStatusIds;
    }

    public void setTodoStatusIds(List<Long> todoStatusIds) {
        this.todoStatusIds = todoStatusIds;
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
//
    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
//
//    public List<IssueCountDTO> getTodoCategoryIssueCount() {
//        return todoCategoryIssueCount;
//    }
//
//    public void setTodoCategoryIssueCount(List<IssueCountDTO> todoCategoryIssueCount) {
//        this.todoCategoryIssueCount = todoCategoryIssueCount;
//    }
//
//    public List<IssueCountDTO> getDoingCategoryIssueCount() {
//        return doingCategoryIssueCount;
//    }
//
//    public void setDoingCategoryIssueCount(List<IssueCountDTO> doingCategoryIssueCount) {
//        this.doingCategoryIssueCount = doingCategoryIssueCount;
//    }
//
//    public List<IssueCountDTO> getDoneCategoryIssueCount() {
//        return doneCategoryIssueCount;
//    }
//
//    public void setDoneCategoryIssueCount(List<IssueCountDTO> doneCategoryIssueCount) {
//        this.doneCategoryIssueCount = doneCategoryIssueCount;
//    }
}
