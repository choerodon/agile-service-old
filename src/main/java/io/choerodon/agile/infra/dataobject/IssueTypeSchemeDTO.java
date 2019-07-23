package io.choerodon.agile.infra.dataobject;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author shinan.chen
 * @date 2018/8/10
 */
@Table(name = "fd_issue_type_scheme")
public class IssueTypeSchemeDTO extends BaseDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String applyType;
    private String description;
    private Long defaultIssueTypeId;
    private Long organizationId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApplyType() {
        return applyType;
    }

    public void setApplyType(String applyType) {
        this.applyType = applyType;
    }
}
