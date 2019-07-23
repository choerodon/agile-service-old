package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author shinan.chen
 * @since 2018/11/25
 */
public class StateMachineSchemeConfigVO {
    @ApiModelProperty(value = "状态机方案配置id")
    private Long id;
    @ApiModelProperty(value = "状态机方案id")
    private Long schemeId;
    @ApiModelProperty(value = "问题类型id")
    private Long issueTypeId;
    @ApiModelProperty(value = "状态机id")
    private Long stateMachineId;
    @ApiModelProperty(value = "是否默认配置")
    private Boolean isDefault;
    @ApiModelProperty(value = "排序")
    private int sequence;
    @ApiModelProperty(value = "乐观锁")
    private Long objectVersionNumber;
    @ApiModelProperty(value = "组织id")
    private Long organizationId;
    @ApiModelProperty(value = "状态机名称")
    private String stateMachineName;
    @ApiModelProperty(value = "问题类型名称")
    private String issueTypeName;
    @ApiModelProperty(value = "问题类型图标")
    private String issueTypeIcon;
    @ApiModelProperty(value = "问题类型颜色")
    private String issueTypeColour;

    public Long getId() {
        return id;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public Long getIssueTypeId() {
        return issueTypeId;
    }

    public void setIssueTypeId(Long issueTypeId) {
        this.issueTypeId = issueTypeId;
    }

    public Long getStateMachineId() {
        return stateMachineId;
    }

    public void setStateMachineId(Long stateMachineId) {
        this.stateMachineId = stateMachineId;
    }

    public String getStateMachineName() {
        return stateMachineName;
    }

    public void setStateMachineName(String stateMachineName) {
        this.stateMachineName = stateMachineName;
    }

    public String getIssueTypeName() {
        return issueTypeName;
    }

    public void setIssueTypeName(String issueTypeName) {
        this.issueTypeName = issueTypeName;
    }

    public String getIssueTypeIcon() {
        return issueTypeIcon;
    }

    public void setIssueTypeIcon(String issueTypeIcon) {
        this.issueTypeIcon = issueTypeIcon;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getIssueTypeColour() {
        return issueTypeColour;
    }

    public void setIssueTypeColour(String issueTypeColour) {
        this.issueTypeColour = issueTypeColour;
    }
}
