package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author shinan.chen
 * @date 2018/11/1
 */
public class ProjectConfigVO {
    @ApiModelProperty(value = "项目配置id")
    private Long id;
    @ApiModelProperty(value = "项目id")
    private Long projectId;
    @ApiModelProperty(value = "方案类型（scheme_state_machine/scheme_issue_type）")
    private String schemeType;
    @ApiModelProperty(value = "应用类型（agile/test/program）")
    private String applyType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getSchemeType() {
        return schemeType;
    }

    public void setSchemeType(String schemeType) {
        this.schemeType = schemeType;
    }

    public String getApplyType() {
        return applyType;
    }

    public void setApplyType(String applyType) {
        this.applyType = applyType;
    }
}
