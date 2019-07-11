package io.choerodon.agile.infra.dataobject;


import java.math.BigDecimal;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/7/30.
 * Email: fuqianghuang01@gmail.com
 */
public class VelocitySingleDTO {

    private Long sprintId;

    private Long issueId;

    private BigDecimal storyPoint;

    private BigDecimal remainTime;

    private Integer issueCount;

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public void setStoryPoint(BigDecimal storyPoint) {
        this.storyPoint = storyPoint;
    }

    public BigDecimal getStoryPoint() {
        return storyPoint;
    }

    public void setRemainTime(BigDecimal remainTime) {
        this.remainTime = remainTime;
    }

    public BigDecimal getRemainTime() {
        return remainTime;
    }

    public void setIssueCount(Integer issueCount) {
        this.issueCount = issueCount;
    }

    public Integer getIssueCount() {
        return issueCount;
    }
}
