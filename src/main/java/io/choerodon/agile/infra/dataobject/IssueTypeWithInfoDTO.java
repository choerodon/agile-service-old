package io.choerodon.agile.infra.dataobject;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/11/29.
 * Email: fuqianghuang01@gmail.com
 */
public class IssueTypeWithInfoDTO {

    private Long id;

    private String name;

    private String icon;

    private String description;

    private Long organizationId;

    private String colour;

    private String typeCode;

    private Boolean initialize;

    private Long objectVersionNumber;

    private List<IssueTypeSchemeRelationDTO> issueTypeSchemeRelationList;

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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public Boolean getInitialize() {
        return initialize;
    }

    public void setInitialize(Boolean initialize) {
        this.initialize = initialize;
    }

    public void setIssueTypeSchemeRelationList(List<IssueTypeSchemeRelationDTO> issueTypeSchemeRelationList) {
        this.issueTypeSchemeRelationList = issueTypeSchemeRelationList;
    }

    public List<IssueTypeSchemeRelationDTO> getIssueTypeSchemeRelationList() {
        return issueTypeSchemeRelationList;
    }
}
