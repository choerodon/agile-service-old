package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * @author shinan.chen
 * @since 2019/5/13
 */
public class BoardDependUpdateDTO {

    @ApiModelProperty(value = "公告板特性id")
    @NotNull(message = "error.boardDepend.boardFeatureIdNotNull")
    private Long boardFeatureId;
    @ApiModelProperty(value = "依赖的公告板特性id")
    @NotNull(message = "error.boardDepend.dependBoardFeatureIdNotNull")
    private Long dependBoardFeatureId;
    @ApiModelProperty(value = "乐观锁")
    @NotNull(message = "error.boardDepend.objectVersionNumberNotNull")
    private Long objectVersionNumber;

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

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
}

