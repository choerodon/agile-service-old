package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/5/18.
 */
public class BackLogIssueDTO {

    @ApiModelProperty(value = "待办问题数量")
    private Integer backlogIssueCount;

    @ApiModelProperty(value = "待办问题列表")
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
