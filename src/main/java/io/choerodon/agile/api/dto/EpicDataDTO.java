package io.choerodon.agile.api.dto;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * Created by jian_zhang02@163.com on 2018/5/16.
 */
public class EpicDataDTO {

    @ApiModelProperty(value = "问题id")
    private Long issueId;

    @ApiModelProperty(value = "问题概要")
    private String summary;

    @ApiModelProperty(value = "问题编号")
    private String issueNum;

    @ApiModelProperty(value = "问题描述")
    private String description;

    @ApiModelProperty(value = "史诗名称")
    private String epicName;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "关联该史诗的问题数量")
    private Integer issueCount;

    @ApiModelProperty(value = "关联该史诗的已完成问题数量")
    private Integer doneIssueCount;

    @ApiModelProperty(value = "关联该史诗的未预估问题数量")
    private Integer notEstimate;

    @ApiModelProperty(value = "关联该史诗的问题的故事点之和")
    private BigDecimal totalEstimate;

    @ApiModelProperty(value = "史诗颜色")
    private String color;

    @ApiModelProperty(value = "版本号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "史诗排序字段")
    private Integer epicSequence;

    private String epicRank;

    private Long epicRankObjectVersionNumber;

    public Integer getEpicSequence() {
        return epicSequence;
    }

    public void setEpicSequence(Integer epicSequence) {
        this.epicSequence = epicSequence;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getIssueCount() {
        return issueCount;
    }

    public void setIssueCount(Integer issueCount) {
        this.issueCount = issueCount;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getDoneIssueCount() {
        return doneIssueCount;
    }

    public void setDoneIssueCount(Integer doneIssueCount) {
        this.doneIssueCount = doneIssueCount;
    }

    public Integer getNotEstimate() {
        return notEstimate;
    }

    public void setNotEstimate(Integer notEstimate) {
        this.notEstimate = notEstimate;
    }

    public String getEpicName() {
        return epicName;
    }

    public void setEpicName(String epicName) {
        this.epicName = epicName;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getIssueNum() {
        return issueNum;
    }

    public void setIssueNum(String issueNum) {
        this.issueNum = issueNum;
    }

    public void setTotalEstimate(BigDecimal totalEstimate) {
        this.totalEstimate = totalEstimate;
    }

    public BigDecimal getTotalEstimate() {
        return totalEstimate;
    }

    public String getEpicRank() {
        return epicRank;
    }

    public void setEpicRank(String epicRank) {
        this.epicRank = epicRank;
    }

    public Long getEpicRankObjectVersionNumber() {
        return epicRankObjectVersionNumber;
    }

    public void setEpicRankObjectVersionNumber(Long epicRankObjectVersionNumber) {
        this.epicRankObjectVersionNumber = epicRankObjectVersionNumber;
    }
}
