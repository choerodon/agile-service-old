package io.choerodon.agile.infra.dataobject;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/15.
 * Email: fuqianghuang01@gmail.com
 */
public class SubStatus {

    private Long id;

    private String name;

    private Boolean completed;

    private String categoryCode;

    private Long objectVersionNumber;

    private List<IssueForBoardDO> issues;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setIssues(List<IssueForBoardDO> issues) {
        this.issues = issues;
    }

    public List<IssueForBoardDO> getIssues() {
        return issues;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryCode() {
        return categoryCode;
    }
}
