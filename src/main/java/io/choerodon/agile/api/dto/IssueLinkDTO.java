package io.choerodon.agile.api.dto;


import io.choerodon.agile.infra.common.utils.StringUtil;

/**
 * 敏捷开发Issue链接
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:50:34
 */
public class IssueLinkDTO {

    private Long issueId;

    private String issueLinkTypeCode;

    private Long linkedIssueId;

    private Long objectVersionNumber;

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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
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