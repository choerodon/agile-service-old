package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author shinan.chen
 * @since 2019/5/30
 */
public class TeamProjectVO {

    @ApiModelProperty(value = "团队项目id")
    private Long teamProjectId;
    @ApiModelProperty(value = "团队项目名称")
    private String name;

    public Long getTeamProjectId() {
        return teamProjectId;
    }

    public void setTeamProjectId(Long teamProjectId) {
        this.teamProjectId = teamProjectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

