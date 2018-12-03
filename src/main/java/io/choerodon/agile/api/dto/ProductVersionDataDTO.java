package io.choerodon.agile.api.dto;

import java.util.Date;

/**
 * Created by jian_zhang02@163.com on 2018/5/16.
 */
public class ProductVersionDataDTO {
    private Long versionId;
    private String name;
    private String description;
    private Date startDate;
    private Date expectReleaseDate;
    private Date releaseDate;
    private String statusCode;
    private Integer issueCount;
    private Integer doneIssueCount;
    private Integer notEstimate;
    private Integer totalEstimate;
    private Long projectId;
    private Long objectVersionNumber;
    private Integer sequence;

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

    public void setExpectReleaseDate(Date expectReleaseDate) {
        this.expectReleaseDate = expectReleaseDate;
    }

    public Date getExpectReleaseDate() {
        return expectReleaseDate;
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

    public Integer getNotEstimate() {
        return notEstimate;
    }

    public void setNotEstimate(Integer notEstimate) {
        this.notEstimate = notEstimate;
    }

    public Integer getTotalEstimate() {
        return totalEstimate;
    }

    public void setTotalEstimate(Integer totalEstimate) {
        this.totalEstimate = totalEstimate;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
}
