package io.choerodon.agile.api.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/5/28.
 */
public class MoveIssueDTO {

    @ApiModelProperty(value = "移动的问题集合")
    private List<Long> issueIds;

    @ApiModelProperty(value = "是否在前面")
    private Boolean isBefore;

    @ApiModelProperty(value = "移动的参照问题id")
    private Long outsetIssueId;

    @ApiModelProperty(value = "是否生成移动日志")
    private Boolean rankIndex;

    private Long updateStatusId;

    private String statusCategoryCode;

    public void setUpdateStatusId(Long updateStatusId) {
        this.updateStatusId = updateStatusId;
    }

    public Long getUpdateStatusId() {
        return updateStatusId;
    }

    public void setStatusCategoryCode(String statusCategoryCode) {
        this.statusCategoryCode = statusCategoryCode;
    }

    public String getStatusCategoryCode() {
        return statusCategoryCode;
    }

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
