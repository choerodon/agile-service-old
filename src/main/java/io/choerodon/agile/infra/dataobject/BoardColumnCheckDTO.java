package io.choerodon.agile.infra.dataobject;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/30.
 * Email: fuqianghuang01@gmail.com
 */
public class BoardColumnCheckDTO {

    private String name;

    private Long minNum;

    private Long maxNum;

    private Long issueCount;

    private Long issueCountOrigin;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Long getMinNum() {
        return minNum;
    }

    public void setMinNum(Long minNum) {
        this.minNum = minNum;
    }

    public Long getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(Long maxNum) {
        this.maxNum = maxNum;
    }

    public void setIssueCount(Long issueCount) {
        this.issueCount = issueCount;
    }

    public Long getIssueCount() {
        return issueCount;
    }

    public void setIssueCountOrigin(Long issueCountOrigin) {
        this.issueCountOrigin = issueCountOrigin;
    }

    public Long getIssueCountOrigin() {
        return issueCountOrigin;
    }
}
