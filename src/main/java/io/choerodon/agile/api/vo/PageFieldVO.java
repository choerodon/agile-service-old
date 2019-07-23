package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author shinan.chen
 * @since 2019/4/1
 */
public class PageFieldVO {
    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "页面id")
    private Long pageId;
    @ApiModelProperty(value = "字段id")
    private Long fieldId;
    @ApiModelProperty(value = "是否显示")
    private Boolean display;
    @ApiModelProperty(value = "rank排序")
    private String rank;
    @ApiModelProperty(value = "字段名称")
    private String fieldName;
    @ApiModelProperty(value = "字段类型")
    private String fieldType;
    @ApiModelProperty(value = "是否系统")
    private Boolean system;
    @ApiModelProperty(value = "是否必填")
    private Boolean required;
    @ApiModelProperty(value = "上下文")
    private String context;
    @ApiModelProperty(value = "字段类型名称")
    private String fieldTypeName;
    @ApiModelProperty(value = "上下文名称")
    private String contextName;
    @ApiModelProperty(value = "项目id")
    private Long projectId;
    @ApiModelProperty(value = "组织id")
    private Long organizationId;
    @ApiModelProperty(value = "乐观锁")
    private Long objectVersionNumber;

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getFieldTypeName() {
        return fieldTypeName;
    }

    public void setFieldTypeName(String fieldTypeName) {
        this.fieldTypeName = fieldTypeName;
    }

    public String getContextName() {
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public Boolean getSystem() {
        return system;
    }

    public void setSystem(Boolean system) {
        this.system = system;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    public Boolean getDisplay() {
        return display;
    }

    public void setDisplay(Boolean display) {
        this.display = display;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}
