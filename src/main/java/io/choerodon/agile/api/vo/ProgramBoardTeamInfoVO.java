package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/5/14
 */
public class ProgramBoardTeamInfoVO {

    @ApiModelProperty(value = "公告板团队id")
    private Long boardTeamId;
    @ApiModelProperty(value = "项目id")
    private Long projectId;
    @ApiModelProperty(value = "项目名称")
    private String projectName;
    @ApiModelProperty(value = "排序")
    private String rank;
    @ApiModelProperty(value = "乐观锁")
    private Long objectVersionNumber;
    @ApiModelProperty(value = "团队冲刺信息")
    private List<ProgramBoardTeamSprintInfoVO> teamSprints;

    public Long getBoardTeamId() {
        return boardTeamId;
    }

    public void setBoardTeamId(Long boardTeamId) {
        this.boardTeamId = boardTeamId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public List<ProgramBoardTeamSprintInfoVO> getTeamSprints() {
        return teamSprints;
    }

    public void setTeamSprints(List<ProgramBoardTeamSprintInfoVO> teamSprints) {
        this.teamSprints = teamSprints;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}

