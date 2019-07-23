package io.choerodon.agile.api.vo;

import io.choerodon.agile.infra.dataobject.IssueTypeWithInfoDTO;
import io.choerodon.agile.infra.dataobject.ProjectWithInfoDTO;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/11/29.
 * Email: fuqianghuang01@gmail.com
 */
public class IssueTypeSchemeWithInfoVO {
    @ApiModelProperty(value = "问题类型方案id")
    private Long id;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "默认问题类型id")
    private Long defaultIssueTypeId;
    @ApiModelProperty(value = "组织id")
    private Long organizationId;
    @ApiModelProperty(value = "应用类型（agile/test/program）")
    private String applyType;
    @ApiModelProperty(value = "乐观锁")
    private Long objectVersionNumber;
    @ApiModelProperty(value = "问题类型列表")
    private List<IssueTypeWithInfoDTO> issueTypeWithInfoList;
    @ApiModelProperty(value = "关联的项目列表")
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
