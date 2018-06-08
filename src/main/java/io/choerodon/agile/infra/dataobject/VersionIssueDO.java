package io.choerodon.agile.infra.dataobject;

/**
 * Created by jian_zhang02@163.com on 2018/6/5.
 */
public class VersionIssueDO {
    private Long issueId;
    private String relationType;

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }
}
