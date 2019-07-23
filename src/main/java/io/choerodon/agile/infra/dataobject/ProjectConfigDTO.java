package io.choerodon.agile.infra.dataobject;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author shinan.chen
 * @date 2018/9/4
 */
@Table(name = "fd_project_config")
public class ProjectConfigDTO extends BaseDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long projectId;
    private Long schemeId;
    private String schemeType;
    private String applyType;

    public ProjectConfigDTO() {
    }

    public ProjectConfigDTO(Long projectId, Long schemeId, String schemeType, String applyType) {
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
