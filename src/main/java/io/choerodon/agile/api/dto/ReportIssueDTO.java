package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/20
 */
public class ReportIssueDTO implements Serializable {

    private String issueNum;

    private Long issueId;

    private Date date;

    private BigDecimal oldValue;

    private String type;

    private BigDecimal newValue;

    private Boolean statistical;

    private String parentIssueId;

    private String parentIssueNum;

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getIssueNum() {
        return issueNum;
    }

    public void setIssueNum(String issueNum) {
        this.issueNum = issueNum;
    }

    public void setOldValue(BigDecimal oldValue) {
        this.oldValue = oldValue;
    }

    public BigDecimal getOldValue() {
        return oldValue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setNewValue(BigDecimal newValue) {
        this.newValue = newValue;
    }

    public BigDecimal getNewValue() {
        return newValue;
    }

    public Boolean getStatistical() {
        return statistical;
    }

    public void setStatistical(Boolean statistical) {
        this.statistical = statistical;
    }

    public String getParentIssueId() {
        return parentIssueId;
    }

    public void setParentIssueId(String parentIssueId) {
        this.parentIssueId = parentIssueId;
    }

    public String getParentIssueNum() {
        return parentIssueNum;
    }

    public void setParentIssueNum(String parentIssueNum) {
        this.parentIssueNum = parentIssueNum;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
