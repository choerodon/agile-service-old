package io.choerodon.agile.api.dto;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * Created by Zenger on 2019/3/27.
 */
public class IssueFeatureDTO {

    @ApiModelProperty(value = "问题主键id")
    private Long issueId;

    @ApiModelProperty(value = "问题概要")
    private String summary;

    @ApiModelProperty(value = "史诗id")
    private Long epicId;

    @ApiModelProperty(value = "故事数量")
    private Integer storyCount;

    @ApiModelProperty(value = "已完成故事数量")
    private Integer storyCompletedCount;

    @ApiModelProperty(value = "未预估故事数量")
    private Integer unEstimateStoryCount;

    @ApiModelProperty(value = "故事点和")
    private BigDecimal totalStoryPoints;

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

    public void setEpicId(Long epicId) {
        this.epicId = epicId;
    }

    public Long getEpicId() {
        return epicId;
    }

    public Integer getStoryCount() {
        return storyCount;
    }

    public void setStoryCount(Integer storyCount) {
        this.storyCount = storyCount;
    }

    public Integer getStoryCompletedCount() {
        return storyCompletedCount;
    }

    public void setStoryCompletedCount(Integer storyCompletedCount) {
        this.storyCompletedCount = storyCompletedCount;
    }

    public Integer getUnEstimateStoryCount() {
        return unEstimateStoryCount;
    }

    public void setUnEstimateStoryCount(Integer unEstimateStoryCount) {
        this.unEstimateStoryCount = unEstimateStoryCount;
    }

    public BigDecimal getTotalStoryPoints() {
        return totalStoryPoints;
    }

    public void setTotalStoryPoints(BigDecimal totalStoryPoints) {
        this.totalStoryPoints = totalStoryPoints;
    }
}
