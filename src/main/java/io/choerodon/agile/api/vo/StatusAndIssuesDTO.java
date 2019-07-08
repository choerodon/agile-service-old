package io.choerodon.agile.api.vo;

import io.choerodon.agile.infra.dataobject.IssueForBoardDO;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/17.
 * Email: fuqianghuang01@gmail.com
 */
public class StatusAndIssuesDTO {

    @ApiModelProperty(value = "状态主键id")
    private Long id;

    @ApiModelProperty(value = "状态所属项目id")
    private Long projectId;

    @ApiModelProperty(value = "状态名称")
    private String name;

    @ApiModelProperty(value = "enable")
    private Boolean enable;

    @ApiModelProperty(value = "状态类别")
    private String categoryCode;

    @ApiModelProperty(value = "状态是否已完成：true、false")
    private Boolean completed;

    @ApiModelProperty(value = "版本号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "真正的状态id")
    private Long statusId;

    @ApiModelProperty(value = "该状态下的问题列表")
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
