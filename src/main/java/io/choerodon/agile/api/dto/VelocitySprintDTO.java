package io.choerodon.agile.api.dto;

import java.io.Serializable;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/7/26.
 * Email: fuqianghuang01@gmail.com
 */
public class VelocitySprintDTO implements Serializable {

    private Long sprintId;

    private String sprintName;

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

    public void setCompletedRemainTime(int completedRemainTime) {
        this.completedRemainTime = completedRemainTime;
    }

    public int getCompletedRemainTime() {
        return completedRemainTime;
    }

    public void setCommittedRemainTime(int committedRemainTime) {
        this.committedRemainTime = committedRemainTime;
    }

    public int getCommittedRemainTime() {
        return committedRemainTime;
    }
}
