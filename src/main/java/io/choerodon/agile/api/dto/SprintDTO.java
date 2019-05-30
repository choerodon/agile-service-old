package io.choerodon.agile.api.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author shinan.chen
 * @since 2019/5/30
 */
public class SprintDTO {

    @ApiModelProperty(value = "冲刺id")
    private Long sprintId;
    @ApiModelProperty(value = "冲刺名称")
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

