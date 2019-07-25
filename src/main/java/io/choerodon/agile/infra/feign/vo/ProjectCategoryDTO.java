package io.choerodon.agile.infra.feign.vo;

/**
 * @author shinan.chen
 * @since 2019/6/28
 */
public class ProjectCategoryDTO {
    private Long id;
    private String name;
    private String description;
    private String code;
    private Long organizationId;
    private Boolean displayFlag;
    private Boolean builtInFlag;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public Boolean getDisplayFlag() {
        return displayFlag;
    }

    public void setDisplayFlag(Boolean displayFlag) {
        this.displayFlag = displayFlag;
    }

    public Boolean getBuiltInFlag() {
        return builtInFlag;
    }

    public void setBuiltInFlag(Boolean builtInFlag) {
        this.builtInFlag = builtInFlag;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
