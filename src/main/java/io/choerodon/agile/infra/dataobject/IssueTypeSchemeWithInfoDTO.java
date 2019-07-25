package io.choerodon.agile.infra.dataobject;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/11/29.
 * Email: fuqianghuang01@gmail.com
 */
public class IssueTypeSchemeWithInfoDTO {

    private Long id;
    private String name;
    private String description;
    private Long defaultIssueTypeId;
    private Long organizationId;
    private String applyType;
    private Long objectVersionNumber;
    private List<IssueTypeWithInfoDTO> issueTypeWithInfoList;
    private List<ProjectWithInfoDTO> projectWithInfoList;

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

    public Long getDefaultIssueTypeId() {
        return defaultIssueTypeId;
    }

    public void setDefaultIssueTypeId(Long defaultIssueTypeId) {
        this.defaultIssueTypeId = defaultIssueTypeId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public void setIssueTypeWithInfoList(List<IssueTypeWithInfoDTO> issueTypeWithInfoList) {
        this.issueTypeWithInfoList = issueTypeWithInfoList;
    }

    public List<IssueTypeWithInfoDTO> getIssueTypeWithInfoList() {
        return issueTypeWithInfoList;
    }

    public String getApplyType() {
        return applyType;
    }

    public void setApplyType(String applyType) {
        this.applyType = applyType;
    }

    public void setProjectWithInfoList(List<ProjectWithInfoDTO> projectWithInfoList) {
        this.projectWithInfoList = projectWithInfoList;
    }

    public List<ProjectWithInfoDTO> getProjectWithInfoList() {
        return projectWithInfoList;
    }
}
