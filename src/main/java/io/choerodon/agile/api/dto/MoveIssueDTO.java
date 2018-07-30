package io.choerodon.agile.api.dto;

import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/5/28.
 */
public class MoveIssueDTO {
    private List<Long> issueIds;
    private Boolean isBefore;
    private Long outsetIssueId;
    private Boolean rankIndex;

    public Boolean getRankIndex() {
        return rankIndex;
    }

    public void setRankIndex(Boolean rankIndex) {
        this.rankIndex = rankIndex;
    }

    public List<Long> getIssueIds() {
        return issueIds;
    }

    public void setIssueIds(List<Long> issueIds) {
        this.issueIds = issueIds;
    }

    public Boolean getBefore() {
        return isBefore;
    }

    public void setBefore(Boolean before) {
        isBefore = before;
    }

    public Long getOutsetIssueId() {
        return outsetIssueId;
    }

    public void setOutsetIssueId(Long outsetIssueId) {
        this.outsetIssueId = outsetIssueId;
    }
}
