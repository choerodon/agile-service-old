package io.choerodon.agile.api.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * @author shinan.chen
 * @since 2019/5/13
 */
public class BoardFeatureCreateDTO {

    @ApiModelProperty(value = "特性id")
    @NotNull(message = "error.boardFeature.featureIdNotNull")
    private Long featureId;
    @ApiModelProperty(value = "冲刺id")
    @NotNull(message = "error.boardFeature.sprintIdNotNull")
    private Long sprintId;
    @ApiModelProperty(value = "piid")
    @NotNull(message = "error.boardFeature.piIdNotNull")
    private Long piId;
    @ApiModelProperty(value = "团队项目id")
    @NotNull(message = "error.boardFeature.teamProjectIdNotNull")
    private Long teamProjectId;

    public Long getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Long featureId) {
        this.featureId = featureId;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public Long getPiId() {
        return piId;
    }

    public void setPiId(Long piId) {
        this.piId = piId;
    }

    public Long getTeamProjectId() {
        return teamProjectId;
    }

    public void setTeamProjectId(Long teamProjectId) {
        this.teamProjectId = teamProjectId;
    }
}

