package io.choerodon.agile.api.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by jian_zhang02@163.com on 2018/7/5.
 */
public class IssueChangeDTO {
    private Long issueId;
    private String issueNum;
    private String oldValue;
    private String newValue;
    private String status;
    private String changeStoryPoints;
    private Date changeDate;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public String getChangeStoryPoints() {
        return changeStoryPoints;
    }

    public void setChangeStoryPoints(String changeStoryPoints) {
        this.changeStoryPoints = changeStoryPoints;
    }
}
