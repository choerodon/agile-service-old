package io.choerodon.agile.api.vo;

import javax.validation.constraints.NotNull;

import io.choerodon.agile.infra.utils.StringUtil;

public class IssueUpdateParentIdVO {

    @NotNull(message = "issueId 不能为空")
    private Long issueId;

    @NotNull(message = "parentIssueId 不能为空")
    private Long parentIssueId;

    @NotNull(message = "objectVersionNumber 不能为空")
    private Long objectVersionNumber;

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getParentIssueId() {
        return parentIssueId;
    }

    public void setParentIssueId(Long parentIssueId) {
        this.parentIssueId = parentIssueId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
