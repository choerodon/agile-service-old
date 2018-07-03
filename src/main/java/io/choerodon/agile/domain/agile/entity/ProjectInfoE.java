package io.choerodon.agile.domain.agile.entity;

import io.choerodon.agile.infra.common.utils.StringUtil;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/15
 */
public class ProjectInfoE {

    private Long infoId;

    private Long projectId;

    private String projectCode;

    private Long issueMaxNum;

    private Long objectVersionNumber;

    private Long lead;

    private String assigneeType;

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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
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
