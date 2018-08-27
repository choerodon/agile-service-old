package io.choerodon.agile.api.dto;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/8/27.
 * Email: fuqianghuang01@gmail.com
 */
public class VersionIssueCountDTO {

    private Long versionId;

    private String name;

    private Integer doneIssueCount;

    private Integer doingIssueCount;

    private Integer todoIssueCount;

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Integer getDoneIssueCount() {
        return doneIssueCount;
    }

    public void setDoneIssueCount(Integer doneIssueCount) {
        this.doneIssueCount = doneIssueCount;
    }

    public Integer getDoingIssueCount() {
        return doingIssueCount;
    }

    public void setDoingIssueCount(Integer doingIssueCount) {
        this.doingIssueCount = doingIssueCount;
    }

    public Integer getTodoIssueCount() {
        return todoIssueCount;
    }

    public void setTodoIssueCount(Integer todoIssueCount) {
        this.todoIssueCount = todoIssueCount;
    }
}
