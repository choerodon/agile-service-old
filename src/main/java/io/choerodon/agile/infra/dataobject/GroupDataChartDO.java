package io.choerodon.agile.infra.dataobject;


/**
 * Created by HuangFuqiang@choerodon.io on 2018/7/28.
 * Email: fuqianghuang01@gmail.com
 */
public class GroupDataChartDO {

    private String groupDay;

    private int completedStoryPoints;

    private int allStoryPoints;

    private int allRemainTimes;

    private int completedRemainTimes;

    private int issueCount;

    private int issueCompletedCount;

    private int unEstimateIssueCount;

    public String getGroupDay() {
        return groupDay;
    }

    public void setGroupDay(String groupDay) {
        this.groupDay = groupDay;
    }

    public int getCompletedStoryPoints() {
        return completedStoryPoints;
    }

    public void setCompletedStoryPoints(int completedStoryPoints) {
        this.completedStoryPoints = completedStoryPoints;
    }

    public int getAllStoryPoints() {
        return allStoryPoints;
    }

    public void setAllStoryPoints(int allStoryPoints) {
        this.allStoryPoints = allStoryPoints;
    }

    public int getIssueCount() {
        return issueCount;
    }

    public void setIssueCount(int issueCount) {
        this.issueCount = issueCount;
    }

    public int getUnEstimateIssueCount() {
        return unEstimateIssueCount;
    }

    public void setUnEstimateIssueCount(int unEstimateIssueCount) {
        this.unEstimateIssueCount = unEstimateIssueCount;
    }

    public void setIssueCompletedCount(int issueCompletedCount) {
        this.issueCompletedCount = issueCompletedCount;
    }

    public int getIssueCompletedCount() {
        return issueCompletedCount;
    }

    public void setCompletedRemainTimes(int completedRemainTimes) {
        this.completedRemainTimes = completedRemainTimes;
    }

    public int getAllRemainTimes() {
        return allRemainTimes;
    }

    public void setAllRemainTimes(int allRemainTimes) {
        this.allRemainTimes = allRemainTimes;
    }

    public int getCompletedRemainTimes() {
        return completedRemainTimes;
    }
}
