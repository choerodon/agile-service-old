package io.choerodon.agile.domain.agile.event;

import io.choerodon.agile.api.dto.StatusDTO;

import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/11/23
 */
public class StateMachineSchemeDeployUpdateIssue {
    private List<StateMachineSchemeChangeItem> changeItems;
    private List<ProjectConfig> projectConfigs;
    private List<StatusDTO> addStatuses;
    private List<StatusDTO> deleteStatuses;

    public List<ProjectConfig> getProjectConfigs() {
        return projectConfigs;
    }

    public void setProjectConfigs(List<ProjectConfig> projectConfigs) {
        this.projectConfigs = projectConfigs;
    }

    public List<StateMachineSchemeChangeItem> getChangeItems() {
        return changeItems;
    }

    public void setChangeItems(List<StateMachineSchemeChangeItem> changeItems) {
        this.changeItems = changeItems;
    }

    public List<StatusDTO> getAddStatuses() {
        return addStatuses;
    }

    public void setAddStatuses(List<StatusDTO> addStatuses) {
        this.addStatuses = addStatuses;
    }

    public List<StatusDTO> getDeleteStatuses() {
        return deleteStatuses;
    }

    public void setDeleteStatuses(List<StatusDTO> deleteStatuses) {
        this.deleteStatuses = deleteStatuses;
    }
}
