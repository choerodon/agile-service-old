package io.choerodon.agile.api.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/14.
 * Email: fuqianghuang01@gmail.com
 */

public class DataLogDTO {

    @ApiModelProperty(value = "日志主键id")
    private Long logId;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "问题字段")
    private String field;

    @ApiModelProperty(value = "日志旧值")
    private String oldValue;

    @ApiModelProperty(value = "日志旧值")
    private String oldString;

    @ApiModelProperty(value = "日志新值")
    private String newValue;

    @ApiModelProperty(value = "日志新值")
    private String newString;

    @ApiModelProperty(value = "问题主键id")
    private Long issueId;

    @ApiModelProperty(value = "最后更新用户id")
    private Long lastUpdatedBy;

    @ApiModelProperty(value = "最后更新日期")
    private Date lastUpdateDate;

    @ApiModelProperty(value = "用户名")
    private String name;

    @ApiModelProperty(value = "登录名")
    private String loginName;

    @ApiModelProperty(value = "真实名")
    private String realName;

    @ApiModelProperty(value = "用户头像url")
    private String imageUrl;

    @ApiModelProperty(value = "状态类别")
    private String categoryCode;

    @ApiModelProperty(value = "用户email地址")
    private String email;

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getOldString() {
        return oldString;
    }

    public void setOldString(String oldString) {
        this.oldString = oldString;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getNewString() {
        return newString;
    }

    public void setNewString(String newString) {
        this.newString = newString;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getRealName() {
        return realName;
    }
}
