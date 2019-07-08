package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author shinan.chen
 * @since 2019/2/25
 */
public class PersonalFilterDTO {

    @ApiModelProperty(value = "主键id")
    private Long filterId;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "过滤名称")
    private String name;

    @ApiModelProperty(value = "搜索条件")
    private String filterJson;

    @ApiModelProperty(value = "版本号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "搜索条件")
    private PersonalFilterSearchDTO personalFilterSearchDTO;

    public PersonalFilterSearchDTO getPersonalFilterSearchDTO() {
        return personalFilterSearchDTO;
    }

    public void setPersonalFilterSearchDTO(PersonalFilterSearchDTO personalFilterSearchDTO) {
        this.personalFilterSearchDTO = personalFilterSearchDTO;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getFilterId() {
        return filterId;
    }

    public void setFilterId(Long filterId) {
        this.filterId = filterId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilterJson() {
        return filterJson;
    }

    public void setFilterJson(String filterJson) {
        this.filterJson = filterJson;
    }
}

