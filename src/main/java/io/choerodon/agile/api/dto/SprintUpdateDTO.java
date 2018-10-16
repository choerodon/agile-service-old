package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/5/15.
 *
 * @author dinghuang123@gmail.com
 */
public class SprintUpdateDTO {
    private static final String OBJECT_VERSION_NUMBER_NULL_ERROR = "error.objectVersionNumber.NotNull";

    private Long sprintId;
    private String sprintName;
    private String sprintGoal;
    private Date startDate;
    private Date endDate;
    private Long projectId;

    private List<SprintWorkCalendarRefDTO> workDates;

    @NotNull(message = OBJECT_VERSION_NUMBER_NULL_ERROR)
    private Long objectVersionNumber;

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public String getSprintName() {
        return sprintName;
    }

    public void setSprintName(String sprintName) {
        this.sprintName = sprintName;
    }

    public String getSprintGoal() {
        return sprintGoal;
    }

    public void setSprintGoal(String sprintGoal) {
        this.sprintGoal = sprintGoal;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public List<SprintWorkCalendarRefDTO> getWorkDates() {
        return workDates;
    }

    public void setWorkDates(List<SprintWorkCalendarRefDTO> workDates) {
        this.workDates = workDates;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

}
