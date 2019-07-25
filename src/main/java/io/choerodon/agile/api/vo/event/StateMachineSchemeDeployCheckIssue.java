package io.choerodon.agile.api.vo.event;

import io.choerodon.agile.infra.dataobject.ProjectConfigDTO;

import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/11/22
 */
public class StateMachineSchemeDeployCheckIssue {
    private List<Long> issueTypeIds;

    private List<ProjectConfigDTO> projectConfigs;

    public List<ProjectConfigDTO> getProjectConfigs() {
        return projectConfigs;
    }

    public void setProjectConfigs(List<ProjectConfigDTO> projectConfigs) {
        this.projectConfigs = projectConfigs;
    }

    public List<Long> getIssueTypeIds() {
        return issueTypeIds;
    }

    public void setIssueTypeIds(List<Long> issueTypeIds) {
        this.issueTypeIds = issueTypeIds;
    }
}
