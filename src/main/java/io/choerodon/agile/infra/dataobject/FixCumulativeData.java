package io.choerodon.agile.infra.dataobject;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/12/20
 */
public class FixCumulativeData {
    private Long logId;
    private Long issueId;
    private Long oldStatusId;
    private String oldString;
    private Long newStatusId;
    private String newString;
    private Date creationDate;

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getOldStatusId() {
        return oldStatusId;
    }

    public void setOldStatusId(Long oldStatusId) {
        this.oldStatusId = oldStatusId;
    }

    public String getOldString() {
        return oldString;
    }

    public void setOldString(String oldString) {
        this.oldString = oldString;
    }

    public Long getNewStatusId() {
        return newStatusId;
    }

    public void setNewStatusId(Long newStatusId) {
        this.newStatusId = newStatusId;
    }

    public String getNewString() {
        return newString;
    }

    public void setNewString(String newString) {
        this.newString = newString;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof FixCumulativeData)) {
            return false;
        }

        FixCumulativeData that = (FixCumulativeData) o;

        return new EqualsBuilder()
                .append(getLogId(), that.getLogId())
                .append(getIssueId(), that.getIssueId())
                .append(getOldStatusId(), that.getOldStatusId())
                .append(getOldString(), that.getOldString())
                .append(getNewStatusId(), that.getNewStatusId())
                .append(getNewString(), that.getNewString())
                .append(getCreationDate(), that.getCreationDate())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getLogId())
                .append(getIssueId())
                .append(getOldStatusId())
                .append(getOldString())
                .append(getNewStatusId())
                .append(getNewString())
                .append(getCreationDate())
                .toHashCode();
    }
}
