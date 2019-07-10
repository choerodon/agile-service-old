package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/5/14
 */
public class ProgramBoardTeamSprintInfoDTO {

    @ApiModelProperty(value = "冲刺id")
    private Long sprintId;
    @ApiModelProperty(value = "公告板特性列表")
    private List<BoardFeatureInfoVO> boardFeatures;

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public List<BoardFeatureInfoVO> getBoardFeatures() {
        return boardFeatures;
    }

    public void setBoardFeatures(List<BoardFeatureInfoVO> boardFeatures) {
        this.boardFeatures = boardFeatures;
    }
}

