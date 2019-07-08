package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/5/14
 */
public class ProgramBoardFilterDTO {

    @ApiModelProperty(value = "只显示有依赖关系的公告板特性")
    private Boolean onlyDependFeature;
    @ApiModelProperty(value = "只筛选某些冲刺的公告板特性")
    private List<Long> sprintIds;
    @ApiModelProperty(value = "只筛选某些团队的公告板特性")
    private List<Long> teamProjectIds;
    @ApiModelProperty(value = "只显示其他团队与当前所选团队有依赖关系的公告板特性")
    private Boolean onlyOtherTeamDependFeature;

    public Boolean getOnlyOtherTeamDependFeature() {
        return onlyOtherTeamDependFeature;
    }

    public void setOnlyOtherTeamDependFeature(Boolean onlyOtherTeamDependFeature) {
        this.onlyOtherTeamDependFeature = onlyOtherTeamDependFeature;
    }

    public List<Long> getSprintIds() {
        return sprintIds;
    }

    public void setSprintIds(List<Long> sprintIds) {
        this.sprintIds = sprintIds;
    }

    public List<Long> getTeamProjectIds() {
        return teamProjectIds;
    }

    public void setTeamProjectIds(List<Long> teamProjectIds) {
        this.teamProjectIds = teamProjectIds;
    }

    public Boolean getOnlyDependFeature() {
        return onlyDependFeature;
    }

    public void setOnlyDependFeature(Boolean onlyDependFeature) {
        this.onlyDependFeature = onlyDependFeature;
    }
}

