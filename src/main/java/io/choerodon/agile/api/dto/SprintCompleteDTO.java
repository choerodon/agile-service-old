package io.choerodon.agile.api.dto;

import javax.validation.constraints.NotNull;

/**
 * Created by jian_zhang02@163.com on 2018/5/17.
 */
public class SprintCompleteDTO {
    private static final String SPRINT_ID_NULL_ERROR = "error.sprintId.NotNull";
    private static final String PROJECT_ID_NULL_ERROR = "error.projectId.NotNull";

    @NotNull(message = PROJECT_ID_NULL_ERROR)
    private Long projectId;
    @NotNull(message = SPRINT_ID_NULL_ERROR)
    private Long sprintId;
    private Long incompleteIssuesDestination;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public Long getIncompleteIssuesDestination() {
        return incompleteIssuesDestination;
    }

    public void setIncompleteIssuesDestination(Long incompleteIssuesDestination) {
        this.incompleteIssuesDestination = incompleteIssuesDestination;
    }
}
