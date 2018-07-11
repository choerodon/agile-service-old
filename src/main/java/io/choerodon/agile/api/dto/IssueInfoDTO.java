package io.choerodon.agile.api.dto;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/7/11.
 * Email: fuqianghuang01@gmail.com
 */
public class IssueInfoDTO {

    private Long issueId;

    private String issueNum;

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getIssueNum() {
        return issueNum;
    }

    public void setIssueNum(String issueNum) {
        this.issueNum = issueNum;
    }
}
