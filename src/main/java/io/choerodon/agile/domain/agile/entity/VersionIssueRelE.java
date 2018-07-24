package io.choerodon.agile.domain.agile.entity;


import io.choerodon.agile.infra.common.utils.StringUtil;
import io.choerodon.core.oauth.DetailsHelper;

import java.util.Date;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 16:21:18
 */
public class VersionIssueRelE {

    private static final String STATUS_CODE_PLANNING = "version_planning";

    private Long versionId;

    private Long issueId;

    private String name;

    private String statusCode;

    private Long projectId;

    private String relationType;

    private Date creationDate;

    private Long createdBy;

    private List<Long> issueIds;

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public List<Long> getIssueIds() {
        return issueIds;
    }

    public void setIssueIds(List<Long> issueIds) {
        this.issueIds = issueIds;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

    public ProductVersionE createProductVersionE() {
        ProductVersionE productVersionE = new ProductVersionE();
        productVersionE.setStatusCode(STATUS_CODE_PLANNING);
        productVersionE.setName(this.name);
        productVersionE.setProjectId(this.projectId);
        return productVersionE;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public void createBatchIssueToVersionE(Long projectId, Long versionId, List<Long> issueIds) {
        this.projectId = projectId;
        this.versionId = versionId;
        this.issueIds = issueIds;
        this.creationDate = new Date();
        this.createdBy = DetailsHelper.getUserDetails().getUserId();
    }
}