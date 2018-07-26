package io.choerodon.agile.infra.dataobject;

import java.math.BigDecimal;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/7/23.
 * Email: fuqianghuang01@gmail.com
 */
public class VelocitySprintDO {

    private Long sprintId;

    private String sprintName;

    private Long issueCount;

    private int storyPoints;

    private BigDecimal remainTime;

    private Long committedIssueCount;

    private Long completedIssueCount;

    private int committedStoryPoints;

    private int completedStoryPoints;

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

    public void setStoryPoints(int storyPoints) {
        this.storyPoints = storyPoints;
    }

    public int getStoryPoints() {
        return storyPoints;
    }

    public void setRemainTime(BigDecimal remainTime) {
        this.remainTime = remainTime;
    }

    public BigDecimal getRemainTime() {
        return remainTime;
    }

    public Long getCommittedIssueCount() {
        return committedIssueCount;
    }

    public void setCommittedIssueCount(Long committedIssueCount) {
        this.committedIssueCount = committedIssueCount;
    }

    public Long getCompletedIssueCount() {
        return completedIssueCount;
    }

    public void setCompletedIssueCount(Long completedIssueCount) {
        this.completedIssueCount = completedIssueCount;
    }

    public int getCommittedStoryPoints() {
        return committedStoryPoints;
    }

    public void setCommittedStoryPoints(int committedStoryPoints) {
        this.committedStoryPoints = committedStoryPoints;
    }

    public int getCompletedStoryPoints() {
        return completedStoryPoints;
    }

    public void setCompletedStoryPoints(int completedStoryPoints) {
        this.completedStoryPoints = completedStoryPoints;
    }

    public void setCommittedRemainTime(BigDecimal committedRemainTime) {
        this.committedRemainTime = committedRemainTime;
    }

    public BigDecimal getCommittedRemainTime() {
        return committedRemainTime;
    }

    public BigDecimal getCompletedRemainTime() {
        return completedRemainTime;
    }

    public void setCompletedRemainTime(BigDecimal completedRemainTime) {
        this.completedRemainTime = completedRemainTime;
    }
}
