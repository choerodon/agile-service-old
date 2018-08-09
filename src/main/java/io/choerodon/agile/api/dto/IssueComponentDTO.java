package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
public class IssueComponentDTO {

    private Long componentId;

    private Long projectId;

    private String name;

    private String description;

    private Long managerId;

    private String defaultAssigneeRole;

    private Long objectVersionNumber;

    public void setComponentId(Long componentId) {
        this.componentId = componentId;
    }

    public Long getComponentId() {
        return componentId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setDefaultAssigneeRole(String defaultAssigneeRole) {
        this.defaultAssigneeRole = defaultAssigneeRole;
    }

    public String getDefaultAssigneeRole() {
        return defaultAssigneeRole;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
