package io.choerodon.agile.infra.dataobject;


import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.agile.infra.common.utils.StringUtil;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 * 敏捷开发Issue标签关联
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:31:22
 */
@ModifyAudit
@VersionAudit
@Table(name = "agile_label_issue_rel")
public class LabelIssueRelDO extends AuditDomain {

    /***/
    @Id
    @GeneratedValue
    private Long issueId;

    /**
     * 标签id
     */
    @NotNull(message = "error.label_issue.label_idNotNull")
    private Long labelId;

    @Transient
    private String labelName;

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

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

}