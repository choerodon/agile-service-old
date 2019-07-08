package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/13.
 * Email: fuqianghuang01@gmail.com
 */
public class QuickFilterDTO {

    @ApiModelProperty(value = "主键id")
    private Long filterId;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "快速搜索名称")
    private String name;

    @ApiModelProperty(value = "快速搜索表达式")
    private String expressQuery;

    @ApiModelProperty(value = "快速搜索创建传值")
    private List<QuickFilterValueDTO> quickFilterValueDTOList;

    @ApiModelProperty(value = "多个表达式之间的关系")
    private List<String> relationOperations;

    @ApiModelProperty(value = "是否包含子任务")
    private Boolean childIncluded;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "版本号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "排序字段")
    private Integer sequence;

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpressQuery() {
        return expressQuery;
    }

    public void setExpressQuery(String expressQuery) {
        this.expressQuery = expressQuery;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setQuickFilterValueDTOList(List<QuickFilterValueDTO> quickFilterValueDTOList) {
        this.quickFilterValueDTOList = quickFilterValueDTOList;
    }

    public List<QuickFilterValueDTO> getQuickFilterValueDTOList() {
        return quickFilterValueDTOList;
    }

    public void setRelationOperations(List<String> relationOperations) {
        this.relationOperations = relationOperations;
    }

    public List<String> getRelationOperations() {
        return relationOperations;
    }

    public void setChildIncluded(Boolean childIncluded) {
        this.childIncluded = childIncluded;
    }

    public Boolean getChildIncluded() {
        return childIncluded;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
