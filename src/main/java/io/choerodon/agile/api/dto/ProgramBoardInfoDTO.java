package io.choerodon.agile.api.dto;

import io.swagger.annotations.ApiModelProperty;

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
    private List<SprintDTO> filterSprintList;

    public List<SprintDTO> getFilterSprintList() {
        return filterSprintList;
    }

    public void setFilterSprintList(List<SprintDTO> filterSprintList) {
        this.filterSprintList = filterSprintList;
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

    public List<ProgramBoardTeamInfoDTO> getTeamProjects() {
        return teamProjects;
    }

    public void setTeamProjects(List<ProgramBoardTeamInfoDTO> teamProjects) {
        this.teamProjects = teamProjects;
    }

    public List<ProgramBoardSprintInfoDTO> getSprints() {
        return sprints;
    }

    public void setSprints(List<ProgramBoardSprintInfoDTO> sprints) {
        this.sprints = sprints;
    }

    public List<BoardDependInfoDTO> getBoardDepends() {
        return boardDepends;
    }

    public void setBoardDepends(List<BoardDependInfoDTO> boardDepends) {
        this.boardDepends = boardDepends;
    }
}

