package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/31.
 * Email: fuqianghuang01@gmail.com
 *
 * @author dinghuang123@gmail.com
 */
public class ComponentForListDTO {

    @ApiModelProperty(value = "模块id")
    private Long componentId;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "模块名称")
    private String name;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "负责人id")
    private Long managerId;

    @ApiModelProperty(value = "默认经办人")
    private String defaultAssigneeRole;

    @ApiModelProperty(value = "模块关联的问题数量")
    private Integer issueCount;

    @ApiModelProperty(value = "负责人名称")
    private String managerName;

    @ApiModelProperty(value = "负责人登录名称")
    private String managerLoginName;

    @ApiModelProperty(value = "负责人真实名称")
    private String managerRealName;

    @ApiModelProperty(value = "图标")
    private String imageUrl;

    public Long getComponentId() {
        return componentId;
    }

    public void setComponentId(Long componentId) {
        this.componentId = componentId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public String getDefaultAssigneeRole() {
        return defaultAssigneeRole;
    }

    public void setDefaultAssigneeRole(String defaultAssigneeRole) {
        this.defaultAssigneeRole = defaultAssigneeRole;
    }

    public Integer getIssueCount() {
        return issueCount;
    }

    public void setIssueCount(Integer issueCount) {
        this.issueCount = issueCount;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getManagerLoginName() {
        return managerLoginName;
    }

    public void setManagerLoginName(String managerLoginName) {
        this.managerLoginName = managerLoginName;
    }

    public String getManagerRealName() {
        return managerRealName;
    }

    public void setManagerRealName(String managerRealName) {
        this.managerRealName = managerRealName;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
