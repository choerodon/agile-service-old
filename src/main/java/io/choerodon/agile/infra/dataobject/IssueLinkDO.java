package io.choerodon.agile.infra.dataobject;


import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.agile.infra.common.utils.StringUtil;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 敏捷开发Issue链接
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:50:34
 */
@ModifyAudit
@VersionAudit
@Table(name = "agile_issue_link")
public class IssueLinkDO extends AuditDomain {

    /***/
    @Id
    private Long issueId;

    /**
     * 链接code
     */
    private String issueLinkTypeCode;

    /**
     * 链接的issue的id
     */
    private Long linkedIssueId;

    @Transient
    private String summary;

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getIssueLinkTypeCode() {
        return issueLinkTypeCode;
    }

    public void setIssueLinkTypeCode(String issueLinkTypeCode) {
        this.issueLinkTypeCode = issueLinkTypeCode;
    }

    public Long getLinkedIssueId() {
        return linkedIssueId;
    }

    public void setLinkedIssueId(Long linkedIssueId) {
        this.linkedIssueId = linkedIssueId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

}