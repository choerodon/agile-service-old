package io.choerodon.agile.infra.dataobject;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.*;

/**
 * @author shinan.chen
 * @since 2019/4/1
 */
@Table(name = "fd_page_field")
public class PageFieldDTO extends BaseDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long pageId;
    private Long fieldId;
    @Column(name = "is_display")
    private Boolean display;
    private String rank;
    private Long projectId;
    private Long organizationId;

    @Transient
    private String fieldName;
    @Transient
    private String fieldType;
    @Transient
    private String fieldCode;
    @Transient
    private Boolean system;
    @Transient
    private String context;
    @Transient
    private String defaultValue;
    @Transient
    private Boolean extraConfig;
    @Transient
    private Boolean required;
    @Transient
    private String fieldTypeName;

    public String getFieldCode() {
        return fieldCode;
    }

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getFieldTypeName() {
        return fieldTypeName;
    }

    public void setFieldTypeName(String fieldTypeName) {
        this.fieldTypeName = fieldTypeName;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Boolean getExtraConfig() {
        return extraConfig;
    }

    public void setExtraConfig(Boolean extraConfig) {
        this.extraConfig = extraConfig;
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
