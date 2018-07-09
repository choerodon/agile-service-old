package io.choerodon.agile.infra.dataobject;

import io.choerodon.agile.infra.common.utils.StringUtil;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.Table;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/6
 */
@ModifyAudit
@VersionAudit
@Table(name = "agile_issue_sprint_rel")
public class IssueSprintRelDO extends AuditDomain {

    private Long issueId;

    private Long sprintId;

    private Long projectId;

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
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
}
