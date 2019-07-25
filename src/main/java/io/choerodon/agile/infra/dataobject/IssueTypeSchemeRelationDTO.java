package io.choerodon.agile.infra.dataobject;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/11/29.
 * Email: fuqianghuang01@gmail.com
 */
public class IssueTypeSchemeRelationDTO {

    private Long issueTypeSchemeId;

    private String issueTypeSchemeName;

    public Long getIssueTypeSchemeId() {
        return issueTypeSchemeId;
    }

    public void setIssueTypeSchemeId(Long issueTypeSchemeId) {
        this.issueTypeSchemeId = issueTypeSchemeId;
    }

    public String getIssueTypeSchemeName() {
        return issueTypeSchemeName;
    }

    public void setIssueTypeSchemeName(String issueTypeSchemeName) {
        this.issueTypeSchemeName = issueTypeSchemeName;
    }
}
