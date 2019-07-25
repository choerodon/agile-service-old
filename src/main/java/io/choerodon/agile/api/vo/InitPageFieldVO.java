package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author shinan.chen
 * @since 2019/4/3
 */
public class InitPageFieldVO {
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
    @ApiModelProperty(value = "方案编码")
    private String schemeCode;
    @ApiModelProperty(value = "页面编码")
    private String pageCode;
    @ApiModelProperty(value = "字段编码")
    private String fieldCode;
    @ApiModelProperty(value = "项目id")
    private Long projectId;
    @ApiModelProperty(value = "组织id")
    private Long organizationId;

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

    public String getSchemeCode() {
        return schemeCode;
    }

    public void setSchemeCode(String schemeCode) {
        this.schemeCode = schemeCode;
    }

    public String getPageCode() {
        return pageCode;
    }

    public void setPageCode(String pageCode) {
        this.pageCode = pageCode;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
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
