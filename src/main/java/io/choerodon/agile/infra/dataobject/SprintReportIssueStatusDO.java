package io.choerodon.agile.infra.dataobject;

/**
 * Created by jian_zhang02@163.com on 2018/6/22.
 */
public class SprintReportIssueStatusDO {
    private Long issueId;
    private String categoryCode;
    private String statusName;
    private String storyPoints;

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(String storyPoints) {
        this.storyPoints = storyPoints;
    }
}
