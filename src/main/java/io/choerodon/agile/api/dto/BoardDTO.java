package io.choerodon.agile.api.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
public class BoardDTO {

    @ApiModelProperty(value = "看板id")
    private Long boardId;

    @ApiModelProperty(value = "看板名称")
    private String name;

    @ApiModelProperty(value = "项目id")
    @NotNull(message = "项目id不能为空")
    private Long projectId;

    @ApiModelProperty(value = "管理员id")
    private Long administratorId;

    @ApiModelProperty(value = "列约束")
    private String columnConstraint;

    private Boolean dayInColumn;

    @ApiModelProperty(value = "泳道")
    private String swimlaneBasedCode;

    private String estimationStatistic;

    @ApiModelProperty(value = "版本号")
    private Long objectVersionNumber;

    private Boolean userDefault;

    @ApiModelProperty(value = "用户默认看板")
    private String userDefaultBoard;

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getAdministratorId() {
        return administratorId;
    }

    public void setAdministratorId(Long administratorId) {
        this.administratorId = administratorId;
    }

    public String getColumnConstraint() {
        return columnConstraint;
    }

    public void setColumnConstraint(String columnConstraint) {
        this.columnConstraint = columnConstraint;
    }

    public void setDayInColumn(Boolean dayInColumn) {
        this.dayInColumn = dayInColumn;
    }

    public Boolean getDayInColumn() {
        return dayInColumn;
    }

    public String getSwimlaneBasedCode() {
        return swimlaneBasedCode;
    }

    public void setSwimlaneBasedCode(String swimlaneBasedCode) {
        this.swimlaneBasedCode = swimlaneBasedCode;
    }

    public String getEstimationStatistic() {
        return estimationStatistic;
    }

    public void setEstimationStatistic(String estimationStatistic) {
        this.estimationStatistic = estimationStatistic;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public Boolean getUserDefault() {
        return userDefault;
    }

    public void setUserDefault(Boolean userDefault) {
        this.userDefault = userDefault;
    }

    public String getUserDefaultBoard() {
        return userDefaultBoard;
    }

    public void setUserDefaultBoard(String userDefaultBoard) {
        this.userDefaultBoard = userDefaultBoard;
    }
}
