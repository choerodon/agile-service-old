package io.choerodon.agile.infra.dataobject;

import io.choerodon.agile.infra.utils.StringUtil;

import java.util.Date;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/2
 */
public class ColumnChangeDTO {

    private Long issueId;

    private String issueNum;

    private String columnFrom;

    private String columnTo;

    private String statusTo;

    private String oldValue;

    private String newValue;

    private Date date;

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

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

    public String getColumnFrom() {
        return columnFrom;
    }

    public void setColumnFrom(String columnFrom) {
        this.columnFrom = columnFrom;
    }

    public String getColumnTo() {
        return columnTo;
    }

    public void setColumnTo(String columnTo) {
        this.columnTo = columnTo;
    }

    public String getStatusTo() {
        return statusTo;
    }

    public void setStatusTo(String statusTo) {
        this.statusTo = statusTo;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
