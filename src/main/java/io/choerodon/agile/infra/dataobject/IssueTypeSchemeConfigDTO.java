package io.choerodon.agile.infra.dataobject;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * @author shinan.chen
 * @date 2018/8/8
 */
@Table(name = "fd_issue_type_scheme_config")
public class IssueTypeSchemeConfigDTO extends BaseDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long schemeId;
    private Long issueTypeId;
    private Long organizationId;
    private BigDecimal sequence;

    public IssueTypeSchemeConfigDTO(Long schemeId, Long issueTypeId, Long organizationId, BigDecimal sequence) {
        this.schemeId = schemeId;
        this.issueTypeId = issueTypeId;
        this.organizationId = organizationId;
        this.sequence = sequence;
    }

    public IssueTypeSchemeConfigDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public Long getIssueTypeId() {
        return issueTypeId;
    }

    public void setIssueTypeId(Long issueTypeId) {
        this.issueTypeId = issueTypeId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public BigDecimal getSequence() {
        return sequence;
    }

    public void setSequence(BigDecimal sequence) {
        this.sequence = sequence;
    }
}
