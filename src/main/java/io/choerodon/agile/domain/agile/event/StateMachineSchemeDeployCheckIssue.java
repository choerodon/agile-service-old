package io.choerodon.agile.domain.agile.event;

import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/11/22
 */
public class StateMachineSchemeDeployCheckIssue {
    private List<Long> issueTypeIds;

    private List<ProjectConfig> projectConfigs;

    public List<ProjectConfig> getProjectConfigs() {
        return projectConfigs;
    }

    public void setProjectConfigs(List<ProjectConfig> projectConfigs) {
        this.projectConfigs = projectConfigs;
    }

    public List<Long> getIssueTypeIds() {
        return issueTypeIds;
    }

    public void setIssueTypeIds(List<Long> issueTypeIds) {
        this.issueTypeIds = issueTypeIds;
    }
}
