package io.choerodon.agile.api.dto;

import javax.validation.constraints.NotNull;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/6/2.
 * Email: fuqianghuang01@gmail.com
 */
public class ColumnWithMaxMinNumDTO {

    @NotNull(message = "项目id不能为空")
    private Long projectId;

    private Long boardId;

    @NotNull(message = "列id不能为空")
    private Long columnId;

    private Long maxNum;

    private Long minNum;

    private Long objectVersionNumber;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public Long getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(Long maxNum) {
        this.maxNum = maxNum;
    }

    public Long getMinNum() {
        return minNum;
    }

    public void setMinNum(Long minNum) {
        this.minNum = minNum;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }
}
