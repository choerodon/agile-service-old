package io.choerodon.agile.api.vo;

import io.choerodon.agile.infra.feign.vo.ProjectCategoryDTO;
import io.choerodon.agile.infra.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/30
 */
public class ProjectVO {

    @ApiModelProperty(value = "项目主键id")
    private Long id;
    @ApiModelProperty(value = "项目名称")
    private String name;
    @ApiModelProperty(value = "组织id")
    private Long organizationId;
    @ApiModelProperty(value = "项目code")
    private String code;
    @ApiModelProperty(value = "项目是否启用")
    private Boolean enabled;
    @ApiModelProperty(value = "项目类型（遗留旧字段，一对一）:AGILE(敏捷项目),PROGRAM(普通项目组),ANALYTICAL(分析型项目群)")
    private String category;
    @ApiModelProperty(value = "项目类型(非开源，一对多)")
    private List<ProjectCategoryDTO> categories;
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

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<ProjectCategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(List<ProjectCategoryDTO> categories) {
        this.categories = categories;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

}