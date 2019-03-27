package io.choerodon.agile.api.dto;

/**
 * Created by Zenger on 2019/3/27.
 */
public class IssueFeatureDTO {

    private Long issueId;

    private String summary;

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
