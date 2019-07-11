package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/5/14
 */
public class ProgramBoardInfoVO {

    @ApiModelProperty(value = "piId")
    private Long piId;
    @ApiModelProperty(value = "pi编码")
    private String piCode;
    @ApiModelProperty(value = "冲刺信息")
    private List<ProgramBoardSprintInfoVO> sprints;
    @ApiModelProperty(value = "团队信息")
    private List<ProgramBoardTeamInfoVO> teamProjects;
    @ApiModelProperty(value = "依赖关系")
    private List<BoardDependInfoVO> boardDepends;

    @ApiModelProperty(value = "冲刺筛选列表")
    private List<SprintVO> filterSprintList;
    @ApiModelProperty(value = "团队筛选列表")
    private List<TeamProjectVO> filterTeamList;

    public ProgramBoardInfoVO() {
        sprints = new ArrayList<>();
        teamProjects = new ArrayList<>();
        boardDepends = new ArrayList<>();
        filterSprintList = new ArrayList<>();
        filterTeamList = new ArrayList<>();
    }

    public Long getPiId() {
        return piId;
    }

    public void setPiId(Long piId) {
        this.piId = piId;
    }

    public String getPiCode() {
        return piCode;
    }

    public void setPiCode(String piCode) {
        this.piCode = piCode;
    }

    public List<ProgramBoardSprintInfoVO> getSprints() {
        return sprints;
    }

    public void setSprints(List<ProgramBoardSprintInfoVO> sprints) {
        this.sprints = sprints;
    }

    public List<ProgramBoardTeamInfoVO> getTeamProjects() {
        return teamProjects;
    }

    public void setTeamProjects(List<ProgramBoardTeamInfoVO> teamProjects) {
        this.teamProjects = teamProjects;
    }

    public List<BoardDependInfoVO> getBoardDepends() {
        return boardDepends;
    }

    public void setBoardDepends(List<BoardDependInfoVO> boardDepends) {
        this.boardDepends = boardDepends;
    }

    public List<SprintVO> getFilterSprintList() {
        return filterSprintList;
    }

    public void setFilterSprintList(List<SprintVO> filterSprintList) {
        this.filterSprintList = filterSprintList;
    }

    public List<TeamProjectVO> getFilterTeamList() {
        return filterTeamList;
    }

    public void setFilterTeamList(List<TeamProjectVO> filterTeamList) {
        this.filterTeamList = filterTeamList;
    }
}

