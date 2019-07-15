package io.choerodon.agile.api.vo;

import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/5/18.
 */
public class SprintCompleteMessageVO {
    private Integer incompleteIssues;
    private Integer partiallyCompleteIssues;
    private List<SprintNameVO> sprintNames;
    private List<IssueNumVO> parentsDoneUnfinishedSubtasks;

    public Integer getIncompleteIssues() {
        return incompleteIssues;
    }

    public void setIncompleteIssues(Integer incompleteIssues) {
        this.incompleteIssues = incompleteIssues;
    }

    public Integer getPartiallyCompleteIssues() {
        return partiallyCompleteIssues;
    }

    public void setPartiallyCompleteIssues(Integer partiallyCompleteIssues) {
        this.partiallyCompleteIssues = partiallyCompleteIssues;
    }

    public List<SprintNameVO> getSprintNames() {
        return sprintNames;
    }

    public void setSprintNames(List<SprintNameVO> sprintNames) {
        this.sprintNames = sprintNames;
    }

    public List<IssueNumVO> getParentsDoneUnfinishedSubtasks() {
        return parentsDoneUnfinishedSubtasks;
    }

    public void setParentsDoneUnfinishedSubtasks(List<IssueNumVO> parentsDoneUnfinishedSubtasks) {
        this.parentsDoneUnfinishedSubtasks = parentsDoneUnfinishedSubtasks;
    }
}
