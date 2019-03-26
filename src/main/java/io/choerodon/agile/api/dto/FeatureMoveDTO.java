package io.choerodon.agile.api.dto;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/25.
 * Email: fuqianghuang01@gmail.com
 */
public class FeatureMoveDTO {

    private Long issueId;

    private Long statusId;

    private Long piId;

    private Long boardId;

    private Long columnId;

    private Long originColumnId;

    private Long objectVersionNumber;

    private Boolean before;

    private Long outsetIssueId;

    private Boolean rank;

    private Boolean piChange;

    public Boolean getRank() {
        return rank;
    }

    public void setRank(Boolean rank) {
        this.rank = rank;
    }

    public Boolean getBefore() {
        return before;
    }

    public void setBefore(Boolean before) {
        this.before = before;
    }

    public void setPiId(Long piId) {
        this.piId = piId;
    }

    public Long getPiId() {
        return piId;
    }

    public Long getOutsetIssueId() {
        return outsetIssueId;
    }

    public void setOutsetIssueId(Long outsetIssueId) {
        this.outsetIssueId = outsetIssueId;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public Long getOriginColumnId() {
        return originColumnId;
    }

    public void setOriginColumnId(Long originColumnId) {
        this.originColumnId = originColumnId;
    }

    public void setPiChange(Boolean piChange) {
        this.piChange = piChange;
    }

    public Boolean getPiChange() {
        return piChange;
    }
}
