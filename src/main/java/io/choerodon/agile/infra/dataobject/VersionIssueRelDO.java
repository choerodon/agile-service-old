package io.choerodon.agile.infra.dataobject;


import io.choerodon.agile.infra.common.utils.StringUtil;
import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 16:21:18
 */
@Table(name = "agile_version_issue_rel")
public class VersionIssueRelDO extends BaseDTO{

    /**
     * version id
     */
    @NotNull(message = "error.version_issue_rel.version_idNotNull")
    private Long versionId;

    /**
     * issue id
     */
    @NotNull(message = "error.version_issue_rel.issue_idNotNull")
    private Long issueId;

    @Transient
    private String name;

    @Transient
    private String statusCode;

    private String relationType;

    private Long projectId;

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

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

}