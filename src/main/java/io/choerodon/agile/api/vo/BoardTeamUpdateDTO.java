package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * @author shinan.chen
 * @since 2019/5/20
 */
public class BoardTeamUpdateDTO {

    @ApiModelProperty(value = "是否拖动到第一个")
    @NotNull(message = "error.boardTeam.beforeNotNull")
    private Boolean before;
    @ApiModelProperty(value = "before：true，在当前移动的值之后，false，在当前移动的值之前，若为0L则为第一次创建")
    @NotNull(message = "error.boardTeam.outsetIdNotNull")
    private Long outsetId;
    @ApiModelProperty(value = "乐观锁")
    @NotNull(message = "error.boardTeam.objectVersionNumberNotNull")
    private Long objectVersionNumber;

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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}

