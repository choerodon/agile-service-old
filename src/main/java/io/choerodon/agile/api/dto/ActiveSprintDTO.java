package io.choerodon.agile.api.dto;

import java.util.Date;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/8/27.
 * Email: fuqianghuang01@gmail.com
 */
public class ActiveSprintDTO {

    private Long sprintId;

    private String sprintName;

    private Date startDate;

    private Date endDate;

    private Integer dayRemain;

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

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Integer getDayRemain() {
        return dayRemain;
    }

    public void setDayRemain(Integer dayRemain) {
        this.dayRemain = dayRemain;
    }
}
