package io.choerodon.agile.infra.dataobject;

import java.util.Date;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/3/14.
 * Email: fuqianghuang01@gmail.com
 */
public class SprintCalendarDO {

    private Long sprintId;

    private String sprintName;

    private Date startDate;

    private Date endDate;

    private String statusCode;

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

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
