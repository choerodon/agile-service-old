package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/8/20.
 * Email: fuqianghuang01@gmail.com
 */
public class StoryMapMoveDTO {

    @ApiModelProperty(value = "是否放在某个问题之前，true表示放前面，false表示放后面")
    private Boolean isBefore;

    @ApiModelProperty(value = "位置参照物")
    private Long outsetIssueId;

    @ApiModelProperty(value = "是否记录排序日志")
    private Boolean rankIndex;

    @ApiModelProperty(value = "要关联的史诗id")
    private Long epicId;

    @ApiModelProperty(value = "要关联的冲刺id")
    private Long sprintId;

    @ApiModelProperty(value = "要关联的版本id")
    private Long versionId;

    @ApiModelProperty(value = "要移动的问题id列表")
    private List<Long> issueIds;

    @ApiModelProperty(value = "问题id列表，移动到史诗，配合epicId使用")
    private List<Long> epicIssueIds;

    @ApiModelProperty(value = "问题id列表，移动到冲刺，配合sprintId使用")
    private List<Long> sprintIssueIds;

    @ApiModelProperty(value = "问题id列表，移动到版本，配合versionId使用")
    private List<Long> versionIssueIds;

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

    public void setIssueIds(List<Long> issueIds) {
        this.issueIds = issueIds;
    }

    public List<Long> getIssueIds() {
        return issueIds;
    }

    public List<Long> getEpicIssueIds() {
        return epicIssueIds;
    }

    public void setEpicIssueIds(List<Long> epicIssueIds) {
        this.epicIssueIds = epicIssueIds;
    }

    public List<Long> getSprintIssueIds() {
        return sprintIssueIds;
    }

    public void setSprintIssueIds(List<Long> sprintIssueIds) {
        this.sprintIssueIds = sprintIssueIds;
    }

    public List<Long> getVersionIssueIds() {
        return versionIssueIds;
    }

    public void setVersionIssueIds(List<Long> versionIssueIds) {
        this.versionIssueIds = versionIssueIds;
    }
}
