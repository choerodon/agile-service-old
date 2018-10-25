package io.choerodon.agile.infra.dataobject;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/17.
 * Email: fuqianghuang01@gmail.com
 */
public class StatusAndIssuesDO {

    private Long id;

    private Long projectId;

    private String name;

    private Boolean enable;

    private String categoryCode;

    private Boolean completed;

    private Long objectVersionNumber;

    private Long statusId;

    private List<IssueForBoardDO> issues;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public void setIssues(List<IssueForBoardDO> issues) {
        this.issues = issues;
    }

    public List<IssueForBoardDO> getIssues() {
        return issues;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public Long getStatusId() {
        return statusId;
    }
}
