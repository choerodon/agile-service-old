package io.choerodon.agile.infra.dataobject;

import java.math.BigDecimal;

/**
 * Created by hande on 2018/7/30.
 */
public class VelocitySingleDO {

    private Long sprintId;

    private Long issueId;

    private Integer storyPoint;

    private int remainTime;

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

    public Integer getStoryPoint() {
        return storyPoint;
    }

    public void setStoryPoint(Integer storyPoint) {
        this.storyPoint = storyPoint;
    }

    public void setRemainTime(int remainTime) {
        this.remainTime = remainTime;
    }

    public int getRemainTime() {
        return remainTime;
    }

    public void setIssueCount(Integer issueCount) {
        this.issueCount = issueCount;
    }

    public Integer getIssueCount() {
        return issueCount;
    }
}
