package io.choerodon.agile.domain.agile.entity;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
@Component
@Scope("prototype")
public class BoardE {

    private Long boardId;

    private String name;

    private Long projectId;

    private Long administratorId;

    private String columnConstraint;

    private Boolean dayInColumn;

    private String swimlaneBasedCode;

    private String estimationStatistic;

    private Long objectVersionNumber;

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
}
