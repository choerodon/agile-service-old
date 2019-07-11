package io.choerodon.agile.infra.dataobject;

/**
 * Creator: ChangpingShi0213@gmail.com
 * Date:  11:28 2018/9/4
 * Description:
 */
public class AssigneeDistributeDTO {

    private Long assigneeId;

    private Double percent;

    private Integer issueNum;

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public Double getPercent() {
        return percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }

    public Integer getIssueNum() {
        return issueNum;
    }

    public void setIssueNum(Integer issueNum) {
        this.issueNum = issueNum;
    }
}
