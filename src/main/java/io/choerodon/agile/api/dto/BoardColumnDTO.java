package io.choerodon.agile.api.dto;

import javax.validation.constraints.NotNull;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
public class BoardColumnDTO {

    private Long columnId;

    @NotNull(message = "列名称不能为空")
    private String name;

    private Long boardId;

    private Long minNum;

    private Long maxNum;

    @NotNull(message = "类别code不能为空")
    private String categoryCode;

    @NotNull(message = "项目id不能为空")
    private Long projectId;

    private Integer sequence;

    private String color;

    private String colorCode;

    private Long objectVersionNumber;

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public Long getMinNum() {
        return minNum;
    }

    public void setMinNum(Long minNum) {
        this.minNum = minNum;
    }

    public Long getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(Long maxNum) {
        this.maxNum = maxNum;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getColorCode() {
        return colorCode;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    @Override
    public String toString() {
        return "BoardColumnDTO{" +
                "columnId=" + columnId +
                ", name='" + name + '\'' +
                ", boardId=" + boardId +
                ", minNum=" + minNum +
                ", maxNum=" + maxNum +
                ", categoryCode='" + categoryCode + '\'' +
                ", projectId=" + projectId +
                ", sequence=" + sequence +
                ", color='" + color + '\'' +
                ", colorCode='" + colorCode + '\'' +
                ", objectVersionNumber=" + objectVersionNumber +
                '}';
    }
}
