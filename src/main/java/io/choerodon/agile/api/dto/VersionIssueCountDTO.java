package io.choerodon.agile.api.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/8/27.
 * Email: fuqianghuang01@gmail.com
 */
public class VersionIssueCountDTO {

    @ApiModelProperty(value = "版本id")
    private Long versionId;

    @ApiModelProperty(value = "版本名称")
    private String name;

    @ApiModelProperty(value = "版本下已完成的问题数量")
    private Integer doneIssueCount;

    @ApiModelProperty(value = "版本下进行中的问题数量")
    private Integer doingIssueCount;

    @ApiModelProperty(value = "版本下待处理的问题数量")
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
