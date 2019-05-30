package io.choerodon.agile.api.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author shinan.chen
 * @since 2019/5/14
 */
public class ProgramBoardFilterDTO {

    @ApiModelProperty(value = "只显示有依赖关系的公告板特性")
    private Boolean onlyDependFeature;
    @ApiModelProperty(value = "只筛选某个冲刺的公告板特性")
    private Long sprintId;
    @ApiModelProperty(value = "只筛选某个团队的公告板特性")
    private Long teamProjectId;
    @ApiModelProperty(value = "只显示其他团队与当前所选团队有依赖关系的公告板特性")
    private Boolean onlyOtherTeamDependFeature;

    public Boolean getOnlyOtherTeamDependFeature() {
        return onlyOtherTeamDependFeature;
    }

    public void setOnlyOtherTeamDependFeature(Boolean onlyOtherTeamDependFeature) {
        this.onlyOtherTeamDependFeature = onlyOtherTeamDependFeature;
    }

    public Long getTeamProjectId() {
        return teamProjectId;
    }

    public void setTeamProjectId(Long teamProjectId) {
        this.teamProjectId = teamProjectId;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public Boolean getOnlyDependFeature() {
        return onlyDependFeature;
    }

    public void setOnlyDependFeature(Boolean onlyDependFeature) {
        this.onlyDependFeature = onlyDependFeature;
    }
}

