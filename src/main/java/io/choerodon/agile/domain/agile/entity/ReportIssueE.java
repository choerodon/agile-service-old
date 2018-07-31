package io.choerodon.agile.domain.agile.entity;

import io.choerodon.agile.infra.common.utils.StringUtil;

import java.util.Date;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/19
 */
public class ReportIssueE {

    private String issueNum;

    private Long issueId;

    private Date date;

    private Integer oldValue;

    private String type;

    private Integer newValue;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getNewValue() {
        return newValue;
    }

    public void setNewValue(Integer newValue) {
        this.newValue = newValue;
    }

    public Integer getOldValue() {

        return oldValue;
    }

    public void setOldValue(Integer oldValue) {
        this.oldValue = oldValue;
    }

    public String getIssueNum() {
        return issueNum;
    }

    public void setIssueNum(String issueNum) {
        this.issueNum = issueNum;
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

    public void initStartSprint(Date startDate) {
        this.date = startDate;
        this.type = "startSprint";
        this.oldValue = 0;
        this.newValue = 0;
        this.statistical = true;
    }

    public void initEndSprint(Date actualEndDate) {
        this.date = actualEndDate;
        this.type = "endSprint";
        this.oldValue = 0;
        this.newValue = 0;
        this.statistical = true;
    }
}
