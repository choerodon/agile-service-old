package io.choerodon.agile.infra.dataobject;


/**
 * Created by HuangFuqiang@choerodon.io on 2018/7/28.
 * Email: fuqianghuang01@gmail.com
 */
public class DateIssueIdsDO {

    private String groupDay;

    private Long issueId;

    private Integer storyPoint;

    private int completed;

    private Integer remainTime;

    public String getGroupDay() {
        return groupDay;
    }

    public void setGroupDay(String groupDay) {
        this.groupDay = groupDay;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public void setStoryPoint(Integer storyPoint) {
        this.storyPoint = storyPoint;
    }

    public Integer getStoryPoint() {
        return storyPoint;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }

    public int getCompleted() {
        return completed;
    }

    public void setRemainTime(Integer remainTime) {
        this.remainTime = remainTime;
    }

    public Integer getRemainTime() {
        return remainTime;
    }
}
