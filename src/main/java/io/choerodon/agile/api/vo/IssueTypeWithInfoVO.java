package io.choerodon.agile.api.vo;

import io.choerodon.agile.infra.dataobject.IssueTypeSchemeRelationDTO;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/11/29.
 * Email: fuqianghuang01@gmail.com
 */
public class IssueTypeWithInfoVO {
    @ApiModelProperty(value = "问题类型id")
    private Long id;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "图标")
    private String icon;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "组织id")
    private Long organizationId;
    @ApiModelProperty(value = "颜色")
    private String colour;
    @ApiModelProperty(value = "类型编码")
    private String typeCode;
    @ApiModelProperty(value = "是否初始化")
    private Boolean initialize;
    @ApiModelProperty(value = "乐观锁")
    private Long objectVersionNumber;
    @ApiModelProperty(value = "关联的问题类型方案列表")
    private List<IssueTypeSchemeRelationDTO> issueTypeSchemeRelationList;

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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public Boolean getInitialize() {
        return initialize;
    }

    public void setInitialize(Boolean initialize) {
        this.initialize = initialize;
    }

    public void setIssueTypeSchemeRelationList(List<IssueTypeSchemeRelationDTO> issueTypeSchemeRelationList) {
        this.issueTypeSchemeRelationList = issueTypeSchemeRelationList;
    }

    public List<IssueTypeSchemeRelationDTO> getIssueTypeSchemeRelationList() {
        return issueTypeSchemeRelationList;
    }
}
