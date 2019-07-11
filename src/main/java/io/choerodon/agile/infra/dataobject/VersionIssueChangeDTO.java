package io.choerodon.agile.infra.dataobject;

import java.util.Date;
import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/7/5.
 */
public class VersionIssueChangeDTO {
    private Date preDate;
    private Date changeDate;
    private List<Long> logIds;
    private List<Long> issueIds;
    private List<Long> addIssueIds;
    private List<Long> removeIssueIds;

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public List<Long> getAddIssueIds() {
        return addIssueIds;
    }

    public void setAddIssueIds(List<Long> addIssueIds) {
        this.addIssueIds = addIssueIds;
    }

    public List<Long> getRemoveIssueIds() {
        return removeIssueIds;
    }

    public void setRemoveIssueIds(List<Long> removeIssueIds) {
        this.removeIssueIds = removeIssueIds;
    }

    public List<Long> getIssueIds() {
        return issueIds;
    }

    public void setIssueIds(List<Long> issueIds) {
        this.issueIds = issueIds;
    }

    public Date getPreDate() {
        return preDate;
    }

    public void setPreDate(Date preDate) {
        this.preDate = preDate;
    }

    public List<Long> getLogIds() {
        return logIds;
    }

    public void setLogIds(List<Long> logIds) {
        this.logIds = logIds;
    }
}
