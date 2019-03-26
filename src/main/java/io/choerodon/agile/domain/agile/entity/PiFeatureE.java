package io.choerodon.agile.domain.agile.entity;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/26.
 * Email: fuqianghuang01@gmail.com
 */
public class PiFeatureE {

    public PiFeatureE() {}

    public PiFeatureE(Long issueId, Long piId, Long programId) {
        this.issueId = issueId;
        this.piId = piId;
        this.programId = programId;
    }

    private Long issueId;

    private Long piId;

    private Long programId;

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getPiId() {
        return piId;
    }

    public void setPiId(Long piId) {
        this.piId = piId;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }
}
