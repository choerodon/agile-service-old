package io.choerodon.agile.infra.dataobject;

import java.math.BigDecimal;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/7/23.
 * Email: fuqianghuang01@gmail.com
 */
public class VelocitySprintDTO {

    private Long sprintId;

    private String sprintName;

    private Long issueCount;

    private BigDecimal storyPoints;

    private BigDecimal remainTime;

    private int committedIssueCount;

    private int completedIssueCount;

    private BigDecimal committedStoryPoints;

    private BigDecimal completedStoryPoints;

    private BigDecimal committedRemainTime;

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

    public Long getIssueCount() {
        return issueCount;
    }

    public void setIssueCount(Long issueCount) {
        this.issueCount = issueCount;
    }

    public void setStoryPoints(BigDecimal storyPoints) {
        this.storyPoints = storyPoints;
    }

    public BigDecimal getStoryPoints() {
        return storyPoints;
    }

    public void setRemainTime(BigDecimal remainTime) {
        this.remainTime = remainTime;
    }

    public BigDecimal getRemainTime() {
        return remainTime;
    }

    public void setCommittedIssueCount(int committedIssueCount) {
        this.committedIssueCount = committedIssueCount;
    }

    public int getCommittedIssueCount() {
        return committedIssueCount;
    }

    public void setCompletedIssueCount(int completedIssueCount) {
        this.completedIssueCount = completedIssueCount;
    }

    public int getCompletedIssueCount() {
        return completedIssueCount;
    }

    public BigDecimal getCommittedStoryPoints() {
        return committedStoryPoints;
    }

    public void setCommittedStoryPoints(BigDecimal committedStoryPoints) {
        this.committedStoryPoints = committedStoryPoints;
    }

    public BigDecimal getCompletedStoryPoints() {
        return completedStoryPoints;
    }

    public void setCompletedStoryPoints(BigDecimal completedStoryPoints) {
        this.completedStoryPoints = completedStoryPoints;
    }

    public BigDecimal getCommittedRemainTime() {
        return committedRemainTime;
    }

    public void setCommittedRemainTime(BigDecimal committedRemainTime) {
        this.committedRemainTime = committedRemainTime;
    }

    public BigDecimal getCompletedRemainTime() {
        return completedRemainTime;
    }

    public void setCompletedRemainTime(BigDecimal completedRemainTime) {
        this.completedRemainTime = completedRemainTime;
    }
}
