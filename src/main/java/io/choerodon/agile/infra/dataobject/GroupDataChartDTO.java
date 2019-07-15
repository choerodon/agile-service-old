package io.choerodon.agile.infra.dataobject;


import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/7/28.
 * Email: fuqianghuang01@gmail.com
 */
public class GroupDataChartDTO implements Serializable {

    private String groupDay;

    private BigDecimal completedStoryPoints;

    private BigDecimal allStoryPoints;

    private BigDecimal allRemainTimes;

    private BigDecimal completedRemainTimes;

    private int issueCount;

    private int issueCompletedCount;

    private int unEstimateIssueCount;

    public String getGroupDay() {
        return groupDay;
    }

    public void setGroupDay(String groupDay) {
        this.groupDay = groupDay;
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

    public BigDecimal getCompletedStoryPoints() {
        return completedStoryPoints;
    }

    public void setCompletedStoryPoints(BigDecimal completedStoryPoints) {
        this.completedStoryPoints = completedStoryPoints;
    }

    public BigDecimal getAllStoryPoints() {
        return allStoryPoints;
    }

    public void setAllStoryPoints(BigDecimal allStoryPoints) {
        this.allStoryPoints = allStoryPoints;
    }

    public BigDecimal getAllRemainTimes() {
        return allRemainTimes;
    }

    public void setAllRemainTimes(BigDecimal allRemainTimes) {
        this.allRemainTimes = allRemainTimes;
    }

    public BigDecimal getCompletedRemainTimes() {
        return completedRemainTimes;
    }

    public void setCompletedRemainTimes(BigDecimal completedRemainTimes) {
        this.completedRemainTimes = completedRemainTimes;
    }
}
