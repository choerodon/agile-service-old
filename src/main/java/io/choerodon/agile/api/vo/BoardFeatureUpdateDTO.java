package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * @author shinan.chen
 * @since 2019/5/13
 */
public class BoardFeatureUpdateDTO {

    @ApiModelProperty(value = "是否拖动到第一个")
    @NotNull(message = "error.boardFeature.beforeNotNull")
    private Boolean before;
    @ApiModelProperty(value = "before：true，在当前移动的值之后，false，在当前移动的值之前，若为0L则为第一次创建")
    @NotNull(message = "error.boardFeature.outsetIdNotNull")
    private Long outsetId;
    @ApiModelProperty(value = "冲刺id")
    @NotNull(message = "error.boardFeature.sprintIdNotNull")
    private Long sprintId;
    @ApiModelProperty(value = "piId")
    @NotNull(message = "error.boardFeature.piIdNotNull")
    private Long piId;
    @ApiModelProperty(value = "团队项目id")
    @NotNull(message = "error.boardFeature.teamProjectIdNotNull")
    private Long teamProjectId;
    @ApiModelProperty(value = "乐观锁")
    @NotNull(message = "error.boardFeature.objectVersionNumberNotNull")
    private Long objectVersionNumber;

    public Long getPiId() {
        return piId;
    }

    public void setPiId(Long piId) {
        this.piId = piId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

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

    public Boolean getBefore() {
        return before;
    }

    public void setBefore(Boolean before) {
        this.before = before;
    }

    public Long getOutsetId() {
        return outsetId;
    }

    public void setOutsetId(Long outsetId) {
        this.outsetId = outsetId;
    }
}

