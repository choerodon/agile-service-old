package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author shinan.chen
 * @date 2018/10/24
 */
public class StatusVO {
    @ApiModelProperty(value = "状态id")
    private Long id;
    @ApiModelProperty(value = "名称")
    private String name;
    /**
     * code是用来识别是否是初始化状态
     */
    @ApiModelProperty(value = "编码")
    private String code;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "状态类型（todo/doing/done/none/prepare）")
    private String type;
    @ApiModelProperty(value = "组织id")
    private Long organizationId;
    @ApiModelProperty(value = "乐观锁")
    private Long objectVersionNumber;
    @ApiModelProperty(value = "是否能删除")
    private Boolean canDelete;
    @ApiModelProperty(value = "状态是否已完成")
    private Boolean completed;

    public StatusVO() {
    }

    public StatusVO(String name, String description, String type, Long organizationId) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.organizationId = organizationId;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Boolean getCanDelete() {
        return canDelete;
    }

    public void setCanDelete(Boolean canDelete) {
        this.canDelete = canDelete;
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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
