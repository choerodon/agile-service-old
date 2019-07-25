package io.choerodon.agile.infra.dataobject;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.*;
import java.util.List;

/**
 * @author peng.jiang@hand-china.com
 */
@Table(name = "fd_state_machine_scheme")
public class StateMachineSchemeDTO extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String status;
    private Long organizationId;
    private Integer deployProgress;
    private String deployStatus;

    @Transient
    private List<StateMachineSchemeConfigDTO> schemeConfigs;
    @Transient
    private List<ProjectConfigDTO> projectConfigs;

    public Integer getDeployProgress() {
        return deployProgress;
    }

    public void setDeployProgress(Integer deployProgress) {
        this.deployProgress = deployProgress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public List<StateMachineSchemeConfigDTO> getSchemeConfigs() {
        return schemeConfigs;
    }

    public void setSchemeConfigs(List<StateMachineSchemeConfigDTO> schemeConfigs) {
        this.schemeConfigs = schemeConfigs;
    }

    public List<ProjectConfigDTO> getProjectConfigs() {
        return projectConfigs;
    }

    public void setProjectConfigs(List<ProjectConfigDTO> projectConfigs) {
        this.projectConfigs = projectConfigs;
    }

    public String getDeployStatus() {
        return deployStatus;
    }

    public void setDeployStatus(String deployStatus) {
        this.deployStatus = deployStatus;
    }
}
