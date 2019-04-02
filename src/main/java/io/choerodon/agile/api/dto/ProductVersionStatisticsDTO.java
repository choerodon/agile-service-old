package io.choerodon.agile.api.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/5/18.
 */
public class ProductVersionStatisticsDTO {

    @ApiModelProperty(value = "版本主键id")
    private Long versionId;

    @ApiModelProperty(value = "版本名称")
    private String name;

    @ApiModelProperty(value = "版本描述")
    private String description;

    @ApiModelProperty(value = "版本开始时间")
    private Date startDate;

    @ApiModelProperty(value = "版本预计发布时间")
    private Date expectReleaseDate;

    @ApiModelProperty(value = "版本发布时间")
    private Date releaseDate;

    @ApiModelProperty(value = "版本状态")
    private String statusCode;

    @ApiModelProperty(value = "版本状态名称")
    private String statusName;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "版本下的问题计数")
    private Integer issueCount;

    @ApiModelProperty(value = "版本下的已完成问题计数")
    private Integer doneIssueCount;

    @ApiModelProperty(value = "已完成状态列表")
    private List<StatusMapDTO> doneStatuses;

    @ApiModelProperty(value = "进行中状态列表")
    private List<StatusMapDTO> doingStatuses;

    @ApiModelProperty(value = "待处理状态列表")
    private List<StatusMapDTO> todoStatuses;

    @ApiModelProperty(value = "版本下的进行中问题计数")
    private Integer doingIssueCount;

    @ApiModelProperty(value = "版本下的待处理问题计数")
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
