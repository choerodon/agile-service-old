package io.choerodon.agile.domain.agile.entity;


import java.util.Date;

import io.choerodon.agile.infra.common.utils.StringUtil;

/**
 * 敏捷开发Issue标签关联
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:31:22
 */
public class LabelIssueRelE {

    private Long issueId;

    private Long labelId;

    private Long objectVersionNumber;

    private Date lastUpdateDate;

    private String labelName;

    private Long projectId;

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getLabelId() {
        return labelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

    public IssueLabelE createIssueLabelE() {
        IssueLabelE issueLabelE = new IssueLabelE();
        issueLabelE.setLabelName(this.labelName);
        issueLabelE.setProjectId(this.projectId);
        return issueLabelE;
    }
}