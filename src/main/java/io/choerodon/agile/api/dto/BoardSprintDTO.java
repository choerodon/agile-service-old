package io.choerodon.agile.api.dto;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/31.
 * Email: fuqianghuang01@gmail.com
 */
public class BoardSprintDTO {

    private Long sprintId;

    private String sprintName;

    private Integer dayRemain;

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintName(String sprintName) {
        this.sprintName = sprintName;
    }

    public String getSprintName() {
        return sprintName;
    }

    public void setDayRemain(Integer dayRemain) {
        this.dayRemain = dayRemain;
    }

    public Integer getDayRemain() {
        return dayRemain;
    }
}
