package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * Created by jian_zhang02@163.com on 2018/5/14.
 */

public class ProductVersionPageVO {

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

    @ApiModelProperty(value = "版本状态code")
    private String statusCode;

    @ApiModelProperty(value = "版本状态名称")
    private String status;

    @ApiModelProperty(value = "版本排序字段")
    private Integer sequence;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "版本号")
    private Long objectVersionNumber;

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

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
}
