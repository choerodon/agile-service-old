package io.choerodon.agile.api.dto;

/**
 * Created by jian_zhang02@163.com on 2018/5/17.
 */
public class SprintNameDTO {
    private Long sprintId;
    private String sprintName;

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
}