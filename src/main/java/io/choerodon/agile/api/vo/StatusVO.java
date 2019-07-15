package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/26.
 * Email: fuqianghuang01@gmail.com
 */
public class StatusVO {

    @ApiModelProperty(value = "状态主键id")
    private Long id;

    @ApiModelProperty(value = "状态名称")
    private String name;

    @ApiModelProperty(value = "项目名称")
    private Long projectId;

    @ApiModelProperty(value = "状态类别")
    private String categoryCode;

    @ApiModelProperty(value = "状态类别名称")
    private String categoryName;

    @ApiModelProperty(value = "问题数量统计")
    private Integer issueNumCount;

    @ApiModelProperty(value = "版本号")
    private Long objectVersionNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getIssueNumCount() {
        return issueNumCount;
    }

    public void setIssueNumCount(Integer issueNumCount) {
        this.issueNumCount = issueNumCount;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
