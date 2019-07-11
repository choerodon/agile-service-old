package io.choerodon.agile.infra.dataobject;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/2/25.
 * Email: fuqianghuang01@gmail.com
 */
public class ColumnIssueNumDTO {

    private Long columnId;

    private Long issueCount;

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setIssueCount(Long issueCount) {
        this.issueCount = issueCount;
    }

    public Long getIssueCount() {
        return issueCount;
    }
}
