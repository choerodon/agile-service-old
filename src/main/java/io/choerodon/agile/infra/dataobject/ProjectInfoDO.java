package io.choerodon.agile.infra.dataobject;

import io.choerodon.agile.infra.common.utils.StringUtil;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/30
 */
@ModifyAudit
@VersionAudit
@Table(name = "agile_project_info")
public class ProjectInfoDO extends AuditDomain {

    @Id
    @GeneratedValue
    private Long infoId;

    private Long projectId;

    private String projectCode;

    private Long issueMaxNum;

    /**
     * 项目负责人
     */
    private Long lead;

    /**
     * 经办人策略
     */
    private String assigneeType;

    /**
     * 经办人策略
     */
    private String issueDefaultPriorityCode;

    public Long getInfoId() {
        return infoId;
    }

    public void setInfoId(Long infoId) {
        this.infoId = infoId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public Long getIssueMaxNum() {
        return issueMaxNum;
    }

    public void setIssueMaxNum(Long issueMaxNum) {
        this.issueMaxNum = issueMaxNum;
    }

    public Long getLead() {
        return lead;
    }

    public void setLead(Long lead) {
        this.lead = lead;
    }

    public String getAssigneeType() {
        return assigneeType;
    }

    public void setAssigneeType(String assigneeType) {
        this.assigneeType = assigneeType;
    }

    public String getIssueDefaultPriorityCode() {
        return issueDefaultPriorityCode;
    }

    public void setIssueDefaultPriorityCode(String issueDefaultPriorityCode) {
        this.issueDefaultPriorityCode = issueDefaultPriorityCode;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
