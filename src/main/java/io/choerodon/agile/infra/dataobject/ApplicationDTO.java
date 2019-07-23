package io.choerodon.agile.infra.dataobject;

public class ApplicationDTO {

    public ApplicationDTO() {}

    public ApplicationDTO(String applicationToken) {
        this.applicationToken = applicationToken;
    }

    private Long id;

    private Long organizationId;

    private Long projectId;

    private String applicationToken;

    private String name;

    private String code;

    private String projectName;

    private String projectCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public void setApplicationToken(String applicationToken) {
        this.applicationToken = applicationToken;
    }

    public String getApplicationToken() {
        return applicationToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }
}
