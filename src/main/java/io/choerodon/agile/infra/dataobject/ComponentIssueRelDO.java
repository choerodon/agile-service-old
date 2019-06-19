package io.choerodon.agile.infra.dataobject;


import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
@Table(name = "agile_component_issue_rel")
public class ComponentIssueRelDO extends BaseDTO {

    private Long componentId;

    private Long issueId;

    private Long projectId;

    @Transient
    private String name;

    public Long getComponentId() {
        return componentId;
    }

    public void setComponentId(Long componentId) {
        this.componentId = componentId;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
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
}