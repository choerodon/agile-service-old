package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/5/14
 */
public class ProgramBoardInfoDTO {

    @ApiModelProperty(value = "piId")
    private Long piId;
    @ApiModelProperty(value = "pi编码")
    private String piCode;
    @ApiModelProperty(value = "冲刺信息")
    private List<ProgramBoardSprintInfoDTO> sprints;
    @ApiModelProperty(value = "团队信息")
    private List<ProgramBoardTeamInfoDTO> teamProjects;
    @ApiModelProperty(value = "依赖关系")
    private List<BoardDependInfoDTO> boardDepends;

    @ApiModelProperty(value = "冲刺筛选列表")
    private List<SprintVO> filterSprintList;
    @ApiModelProperty(value = "团队筛选列表")
    private List<TeamProjectDTO> filterTeamList;

    public ProgramBoardInfoDTO() {
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

    public List<ProgramBoardSprintInfoDTO> getSprints() {
        return sprints;
    }

    public void setSprints(List<ProgramBoardSprintInfoDTO> sprints) {
        this.sprints = sprints;
    }

    public List<ProgramBoardTeamInfoDTO> getTeamProjects() {
        return teamProjects;
    }

    public void setTeamProjects(List<ProgramBoardTeamInfoDTO> teamProjects) {
        this.teamProjects = teamProjects;
    }

    public List<BoardDependInfoDTO> getBoardDepends() {
        return boardDepends;
    }

    public void setBoardDepends(List<BoardDependInfoDTO> boardDepends) {
        this.boardDepends = boardDepends;
    }

    public List<SprintVO> getFilterSprintList() {
        return filterSprintList;
    }

    public void setFilterSprintList(List<SprintVO> filterSprintList) {
        this.filterSprintList = filterSprintList;
    }

    public List<TeamProjectDTO> getFilterTeamList() {
        return filterTeamList;
    }

    public void setFilterTeamList(List<TeamProjectDTO> filterTeamList) {
        this.filterTeamList = filterTeamList;
    }
}

