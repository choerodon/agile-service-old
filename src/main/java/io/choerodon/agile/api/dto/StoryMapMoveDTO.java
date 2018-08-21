package io.choerodon.agile.api.dto;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/8/20.
 * Email: fuqianghuang01@gmail.com
 */
public class StoryMapMoveDTO {

    private List<Long> issueIds;

    private Boolean isBefore;

    private Long outsetIssueId;

    private Boolean rankIndex;

    private Long epicId;

    private Long sprintId;

    private Long versionId;

    public void setIssueIds(List<Long> issueIds) {
        this.issueIds = issueIds;
    }

    public List<Long> getIssueIds() {
        return issueIds;
    }

    public void setEpicId(Long epicId) {
        this.epicId = epicId;
    }

    public Long getEpicId() {
        return epicId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public Long getVersionId() {
        return versionId;
    }

    public Boolean getRankIndex() {
        return rankIndex;
    }

    public void setRankIndex(Boolean rankIndex) {
        this.rankIndex = rankIndex;
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
