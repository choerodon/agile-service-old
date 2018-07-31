package io.choerodon.agile.infra.dataobject;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/7/23.
 * Email: fuqianghuang01@gmail.com
 */
public class VelocitySprintDO {

    private Long sprintId;

    private String sprintName;

    private Long issueCount;

    private int storyPoints;

    private int remainTime;

    private int committedIssueCount;

    private int completedIssueCount;

    private int committedStoryPoints;

    private int completedStoryPoints;

    private int committedRemainTime;

    private int completedRemainTime;


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

    public void setRemainTime(int remainTime) {
        this.remainTime = remainTime;
    }

    public int getRemainTime() {
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

    public void setCommittedRemainTime(int committedRemainTime) {
        this.committedRemainTime = committedRemainTime;
    }

    public int getCommittedRemainTime() {
        return committedRemainTime;
    }

    public void setCompletedRemainTime(int completedRemainTime) {
        this.completedRemainTime = completedRemainTime;
    }

    public int getCompletedRemainTime() {
        return completedRemainTime;
    }
}
