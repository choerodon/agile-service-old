package io.choerodon.agile.api.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author shinan.chen
 * @since 2019/5/13
 */
public class BoardDependInfoDTO {

    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "公告板特性id")
    private Long boardFeatureId;
    @ApiModelProperty(value = "依赖的公告板特性id")
    private Long dependBoardFeatureId;
    @ApiModelProperty(value = "piId")
    private Long piId;
    @ApiModelProperty(value = "项目群id")
    private Long programId;
    @ApiModelProperty(value = "乐观锁")
    private Long objectVersionNumber;
    @ApiModelProperty(value = "公告板特性")
    private BoardFeatureInfoDTO boardFeature;
    @ApiModelProperty(value = "依赖的公告板特")
    private BoardFeatureInfoDTO dependBoardFeature;

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getPiId() {
        return piId;
    }

    public void setPiId(Long piId) {
        this.piId = piId;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public BoardFeatureInfoDTO getBoardFeature() {
        return boardFeature;
    }

    public void setBoardFeature(BoardFeatureInfoDTO boardFeature) {
        this.boardFeature = boardFeature;
    }

    public BoardFeatureInfoDTO getDependBoardFeature() {
        return dependBoardFeature;
    }

    public void setDependBoardFeature(BoardFeatureInfoDTO dependBoardFeature) {
        this.dependBoardFeature = dependBoardFeature;
    }
}
