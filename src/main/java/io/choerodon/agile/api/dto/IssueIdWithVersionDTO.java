package io.choerodon.agile.api.dto;

public class IssueIdWithVersionDTO {

    private Long versionId;

    private Long issueId;

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getIssueId() {
        return issueId;
    }
}
