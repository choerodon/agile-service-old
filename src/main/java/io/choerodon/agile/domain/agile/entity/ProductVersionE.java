package io.choerodon.agile.domain.agile.entity;

import io.choerodon.core.exception.CommonException;

import java.util.Date;

/**
 * Created by jian_zhang02@163.com on 2018/5/14.
 */

public class ProductVersionE {
    private Long versionId;
    private String name;
    private String description;
    private Date startDate;
    private Date releaseDate;
    private String statusCode;
    private String status;
    private Long projectId;
    private Long objectVersionNumber;

    private static final String VERSION_DATE_ERROR = "error.versionDate.startAfterReleaseDate";

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public void checkDate() {
        if (this.startDate != null && this.releaseDate != null && this.startDate.after(this.releaseDate)) {
            throw new CommonException(VERSION_DATE_ERROR);
        }
    }
}