package io.choerodon.agile.api.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * @author shinan.chen
 * @since 2019/5/13
 */
public class BoardDependCreateDTO {

    @ApiModelProperty(value = "公告板特性id")
    @NotNull(message = "error.boardDepend.boardFeatureIdNotNull")
    private Long boardFeatureId;
    @ApiModelProperty(value = "依赖的公告板特性id")
    @NotNull(message = "error.boardDepend.dependBoardFeatureIdNotNull")
    private Long dependBoardFeatureId;
    @ApiModelProperty(value = "piid")
    @NotNull(message = "error.boardDepend.piIdNotNull")
    private Long piId;

    public Long getBoardFeatureId() {
        return boardFeatureId;
    }

    public void setBoardFeatureId(Long boardFeatureId) {
        this.boardFeatureId = boardFeatureId;
    }

    public Long getDependBoardFeatureId() {
        return dependBoardFeatureId;
    }

    public void setDependBoardFeatureId(Long dependBoardFeatureId) {
        this.dependBoardFeatureId = dependBoardFeatureId;
    }

    public Long getPiId() {
        return piId;
    }

    public void setPiId(Long piId) {
        this.piId = piId;
    }
}

