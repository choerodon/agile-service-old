package io.choerodon.agile.api.vo.event;

import io.choerodon.mybatis.entity.BaseDTO;

/**
 * @author shinan.chen
 * @date 2018/11/22
 */
public class ProjectConfig extends BaseDTO {
    private Long id;
    private Long projectId;
    private Long schemeId;
    private String schemeType;
    private String applyType;

    public ProjectConfig() {
    }

    public ProjectConfig(Long projectId, Long schemeId, String schemeType, String applyType) {
        this.projectId = projectId;
        this.schemeId = schemeId;
        this.schemeType = schemeType;
        this.applyType = applyType;
    }

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

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
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
