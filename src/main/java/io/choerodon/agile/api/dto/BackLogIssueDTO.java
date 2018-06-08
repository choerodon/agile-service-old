package io.choerodon.agile.api.dto;

import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/5/18.
 */
public class BackLogIssueDTO {
    private Integer backlogIssueCount;
    private List<IssueSearchDTO> backLogIssue;

    public BackLogIssueDTO(){}

    public BackLogIssueDTO(Integer backlogIssueCount, List<IssueSearchDTO> backLogIssue) {
        this.backlogIssueCount = backlogIssueCount;
        this.backLogIssue = backLogIssue;
    }

    public Integer getBacklogIssueCount() {
        return backlogIssueCount;
    }

    public void setBacklogIssueCount(Integer backlogIssueCount) {
        this.backlogIssueCount = backlogIssueCount;
    }

    public List<IssueSearchDTO> getBackLogIssue() {
        return backLogIssue;
    }

    public void setBackLogIssue(List<IssueSearchDTO> backLogIssue) {
        this.backLogIssue = backLogIssue;
    }
}
