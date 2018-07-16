package io.choerodon.agile.infra.dataobject;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/7/16.
 * Email: fuqianghuang01@gmail.com
 */
public class IssueCreationNumDO {

    private String creationDay;

    private Integer issueCount;

    public void setCreationDay(String creationDay) {
        this.creationDay = creationDay;
    }

    public String getCreationDay() {
        return creationDay;
    }

    public Integer getIssueCount() {
        return issueCount;
    }

    public void setIssueCount(Integer issueCount) {
        this.issueCount = issueCount;
    }
}
