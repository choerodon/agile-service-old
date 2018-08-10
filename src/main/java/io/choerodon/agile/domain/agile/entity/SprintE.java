package io.choerodon.agile.domain.agile.entity;

import io.choerodon.agile.infra.dataobject.ProjectInfoDO;
import io.choerodon.core.exception.CommonException;

import java.util.Date;

/**
 * Created by jian_zhang02@163.com on 2018/5/14.
 */

public class SprintE {
    private Long sprintId;
    private String sprintName;
    private String sprintGoal;
    private Date startDate;
    private Date endDate;
    private Date actualEndDate;
    private String statusCode;
    private Long projectId;
    private Long objectVersionNumber;

    private static final String STATUS_SPRINT_PLANNING_CODE = "sprint_planning";
    private static final String STATUS_STARTED_CODE = "started";
    private static final String STATUS_CLOSE_CODE = "closed";
    private static final String SPRINT_DATE_ERROR = "error.sprintDate.nullOrStartAfterEndDate";
    private static final String DELETE_ERROR = "error.sprint.isDoing";

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

    public void createSprint(ProjectInfoDO project) {
        this.projectId = project.getProjectId();
        this.sprintName = project.getProjectCode().trim() + " 1";
        this.statusCode = STATUS_SPRINT_PLANNING_CODE;
    }

    public void judgeDelete() {
        if (this.statusCode.equals(STATUS_STARTED_CODE)) {
            throw new CommonException(DELETE_ERROR);
        }
    }

    public void createSprint(SprintE sprintE) {
        this.projectId = sprintE.getProjectId();
        this.sprintName = assembleName(sprintE.getSprintName());
        this.statusCode = STATUS_SPRINT_PLANNING_CODE;
    }

    public String assembleName(String sprintName) {
        char[] chars = sprintName.trim().toCharArray();
        StringBuilder num = new StringBuilder();
        int index = 0;
        for (index = chars.length - 1; index >= 0; index--) {
            if (chars[index] >= 48 && chars[index] <= 57) {
                num.insert(0, chars[index]);
            } else {
                break;
            }
        }
        try {
            int number = Integer.parseInt(num.toString()) + 1;
            return sprintName.trim().substring(0, index + 1) + number;
        } catch (Exception e) {
            return sprintName.trim() + " 2";
        }
    }

    public void trimSprintName() {
        this.sprintName = this.sprintName != null ? this.sprintName.trim() : null;
    }

    public void completeSprint() {
        this.statusCode = STATUS_CLOSE_CODE;
        this.actualEndDate = new Date();
    }

    public void startSprint() {
        this.statusCode = STATUS_STARTED_CODE;
    }

    public void checkDate() {
        if (this.startDate == null || this.endDate == null || this.startDate.after(this.endDate)) {
            throw new CommonException(SPRINT_DATE_ERROR);
        }
    }

    public void initStartAndEndTime() {
        if (this.actualEndDate == null) {
            this.setActualEndDate(new Date());
        }
    }
}
