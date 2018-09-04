package io.choerodon.agile.api.dto;

/**
 * Creator: ChangpingShi0213@gmail.com
 * Date:  11:16 2018/9/4
 * Description:
 */
public class AssigneeDistributeDTO {
    private String assigneeName;

    private Long assigneeId;

    private Integer issueNum;

    private Double percent;

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public Integer getIssueNum() {
        return issueNum;
    }

    public void setIssueNum(Integer issueNum) {
        this.issueNum = issueNum;
    }

    public Double getPercent() {
        return percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }
}