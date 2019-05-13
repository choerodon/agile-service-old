package io.choerodon.agile.api.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * @author shinan.chen
 * @since 2019/5/13
 */
public class BoardFeatureUpdateDTO {

    @ApiModelProperty(value = "冲刺id")
    @NotNull(message = "error.boardFeature.sprintIdNotNull")
    private Long sprintId;
    @ApiModelProperty(value = "团队项目id")
    @NotNull(message = "error.boardFeature.teamProjectIdNotNull")
    private Long teamProjectId;

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public Long getTeamProjectId() {
        return teamProjectId;
    }

    public void setTeamProjectId(Long teamProjectId) {
        this.teamProjectId = teamProjectId;
    }
}

