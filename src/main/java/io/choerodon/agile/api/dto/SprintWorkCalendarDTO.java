package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;

import java.util.Date;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/18
 */
public class SprintWorkCalendarDTO {

    private List<SprintWorkCalendarRefDTO> sprintWorkCalendarRefDTOS;

    private Long sprintId;

    private Date startDate;

    private String sprintName;

    private String sprintGoal;

    private Date endDate;

    private Date actualEndDate;

    private String statusCode;

    public List<SprintWorkCalendarRefDTO> getSprintWorkCalendarRefDTOS() {
        return sprintWorkCalendarRefDTOS;
    }

    public void setSprintWorkCalendarRefDTOS(List<SprintWorkCalendarRefDTO> sprintWorkCalendarRefDTOS) {
        this.sprintWorkCalendarRefDTOS = sprintWorkCalendarRefDTOS;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
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

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getActualEndDate() {
        return actualEndDate;
    }

    public void setActualEndDate(Date actualEndDate) {
        this.actualEndDate = actualEndDate;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
