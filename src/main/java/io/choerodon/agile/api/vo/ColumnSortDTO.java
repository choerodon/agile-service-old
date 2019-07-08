package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/25.
 * Email: fuqianghuang01@gmail.com
 */
public class ColumnSortDTO {

    @ApiModelProperty(value = "项目id")
    @NotNull(message = "项目id不能为空")
    private Long projectId;

    @ApiModelProperty(value = "看板id")
    @NotNull(message = "board id不能为空")
    private Long boardId;

    @ApiModelProperty(value = "列id")
    @NotNull(message = "列id不能为空")
    private Long columnId;

    @ApiModelProperty(value = "列顺序")
    @NotNull(message = "列序号不能为空")
    private Integer sequence;

    @ApiModelProperty(value = "版本号")
    @NotNull(message = "数据版本序号不能为空")
    private Long objectVersionNumber;

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public Long getBoardId() {
        return boardId;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
