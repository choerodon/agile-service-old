package io.choerodon.agile.api.dto;

/**
 * @author shinan.chen
 * @since 2019/5/27
 */
public class IssueIdSprintIdDTO {

    private Long issueId;
    private Long sprintId;

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }
}

