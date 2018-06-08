package io.choerodon.agile.infra.dataobject;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
@VersionAudit
@ModifyAudit
@Table(name = "agile_issue_component")
public class IssueComponentDO extends AuditDomain {

    @Id
    @GeneratedValue
    private Long componentId;

    private Long projectId;

    private String name;

    private String description;

    private Long managerId;

    private String defaultAssigneeRole;

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
}
