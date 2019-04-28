package io.choerodon.agile.infra.dataobject;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/4/23.
 * Email: fuqianghuang01@gmail.com
 */
public class PiExportNameDO {

    private Long issueId;

    private String piCodeName;

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getPiCodeName() {
        return piCodeName;
    }

    public void setPiCodeName(String piCodeName) {
        this.piCodeName = piCodeName;
    }
}
