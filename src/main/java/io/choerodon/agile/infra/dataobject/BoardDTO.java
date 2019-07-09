package io.choerodon.agile.infra.dataobject;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.*;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
@Table(name = "agile_board")
public class BoardDTO extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    private String name;

    private Long projectId;

    private Long administratorId;

    private String columnConstraint;

    @Column(name = "is_day_in_column")
    private Boolean dayInColumn;

    private String swimlaneBasedCode;

    private String estimationStatistic;

    @Transient
    private Boolean userDefault;

    @Transient
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
