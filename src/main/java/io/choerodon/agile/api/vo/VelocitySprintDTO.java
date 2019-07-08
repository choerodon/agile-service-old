package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/7/26.
 * Email: fuqianghuang01@gmail.com
 */
public class VelocitySprintDTO implements Serializable {

    @ApiModelProperty(value = "冲刺id")
    private Long sprintId;

    @ApiModelProperty(value = "冲刺名称")
    private String sprintName;

    @ApiModelProperty(value = "预估问题数")
    private int committedIssueCount;

    @ApiModelProperty(value = "完成问题数")
    private int completedIssueCount;

    @ApiModelProperty(value = "预估故事点")
    private BigDecimal committedStoryPoints;

    @ApiModelProperty(value = "完成故事点")
    private BigDecimal completedStoryPoints;

    @ApiModelProperty(value = "预估剩余时间")
    private BigDecimal committedRemainTime;

    @ApiModelProperty(value = "完成剩余时间")
    private BigDecimal completedRemainTime;


    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public String getSprintName() {
        return sprintName;
    }

    public void setSprintName(String sprintName) {
        this.sprintName = sprintName;
    }

    public void setCompletedIssueCount(int completedIssueCount) {
        this.completedIssueCount = completedIssueCount;
    }

    public int getCompletedIssueCount() {
        return completedIssueCount;
    }

    public void setCommittedIssueCount(int committedIssueCount) {
        this.committedIssueCount = committedIssueCount;
    }

    public int getCommittedIssueCount() {
        return committedIssueCount;
    }

    public void setCommittedStoryPoints(BigDecimal committedStoryPoints) {
        this.committedStoryPoints = committedStoryPoints;
    }

    public BigDecimal getCommittedStoryPoints() {
        return committedStoryPoints;
    }

    public void setCompletedStoryPoints(BigDecimal completedStoryPoints) {
        this.completedStoryPoints = completedStoryPoints;
    }

    public BigDecimal getCompletedStoryPoints() {
        return completedStoryPoints;
    }

    public void setCommittedRemainTime(BigDecimal committedRemainTime) {
        this.committedRemainTime = committedRemainTime;
    }

    public BigDecimal getCommittedRemainTime() {
        return committedRemainTime;
    }

    public void setCompletedRemainTime(BigDecimal completedRemainTime) {
        this.completedRemainTime = completedRemainTime;
    }

    public BigDecimal getCompletedRemainTime() {
        return completedRemainTime;
    }
}
