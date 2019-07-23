package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author peng.jiang@hand-china.com
 */
public class StateMachineSchemeVO {
    @ApiModelProperty(value = "状态机方案id")
    private Long id;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "组织id")
    private Long organizationId;
    @ApiModelProperty(value = "状态机的状态（draft/active/create）")
    private String status;
    @ApiModelProperty(value = "乐观锁")
    private Long objectVersionNumber;
    @ApiModelProperty(value = "发布的状态（doing/done）")
    private String deployStatus;
    @ApiModelProperty(value = "关联的项目列表")
    private List<ProjectVO> projectVOS;
    @ApiModelProperty(value = "方案配置列表")
    private List<StateMachineSchemeConfigVO> configVOS;
    @ApiModelProperty(value = "方案配置列表（用于列表）")
    private List<StateMachineSchemeConfigViewVO> viewVOS;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public List<ProjectVO> getProjectVOS() {
        return projectVOS;
    }

    public void setProjectVOS(List<ProjectVO> projectVOS) {
        this.projectVOS = projectVOS;
    }

    public String getDeployStatus() {
        return deployStatus;
    }

    public void setDeployStatus(String deployStatus) {
        this.deployStatus = deployStatus;
    }

    public List<StateMachineSchemeConfigVO> getConfigVOS() {
        return configVOS;
    }

    public void setConfigVOS(List<StateMachineSchemeConfigVO> configVOS) {
        this.configVOS = configVOS;
    }

    public List<StateMachineSchemeConfigViewVO> getViewVOS() {
        return viewVOS;
    }

    public void setViewVOS(List<StateMachineSchemeConfigViewVO> viewVOS) {
        this.viewVOS = viewVOS;
    }
}
