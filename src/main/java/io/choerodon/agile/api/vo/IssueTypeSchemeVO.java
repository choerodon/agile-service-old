package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/8/10
 */
public class IssueTypeSchemeVO {
    @ApiModelProperty(value = "问题类型方案id")
    private Long id;
    @ApiModelProperty(value = "名称")
    @NotNull(message = "error.name.null")
    private String name;
    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * 若无默认问题类型，传0L
     */
    @NotNull(message = "error.defaultIssueTypeId.null")
    @ApiModelProperty(value = "默认问题类型id")
    private Long defaultIssueTypeId;
    @ApiModelProperty(value = "组织id")
    private Long organizationId;
    @ApiModelProperty(value = "应用类型（agile/test/program）")
    private String applyType;
    @ApiModelProperty(value = "乐观锁")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "问题类型列表")
    private List<IssueTypeVO> issueTypes;

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

    public Long getDefaultIssueTypeId() {
        return defaultIssueTypeId;
    }

    public void setDefaultIssueTypeId(Long defaultIssueTypeId) {
        this.defaultIssueTypeId = defaultIssueTypeId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public List<IssueTypeVO> getIssueTypes() {
        return issueTypes;
    }

    public void setIssueTypes(List<IssueTypeVO> issueTypes) {
        this.issueTypes = issueTypes;
    }

    public String getApplyType() {
        return applyType;
    }

    public void setApplyType(String applyType) {
        this.applyType = applyType;
    }
}
