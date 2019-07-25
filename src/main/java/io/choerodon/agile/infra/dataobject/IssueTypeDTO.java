package io.choerodon.agile.infra.dataobject;

import com.google.common.base.MoreObjects;
import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author shinan.chen
 * @date 2018/8/8
 */
@Table(name = "fd_issue_type")
public class IssueTypeDTO extends BaseDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String icon;
    private String name;
    private String description;
    private Long organizationId;
    private String colour;
    private String typeCode;
    @Column(name = "is_initialize")
    private Boolean initialize;

    public IssueTypeDTO(String icon, String name, String description, Long organizationId, String colour, String typeCode, Boolean initialize) {
        this.icon = icon;
        this.name = name;
        this.description = description;
        this.organizationId = organizationId;
        this.colour = colour;
        this.typeCode = typeCode;
        this.initialize = initialize;
    }

    public IssueTypeDTO() {
    }

    @Transient
    private BigDecimal sequence;

    @Transient
    private StateMachineSchemeConfigDTO stateMachineSchemeConfig;

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

    public BigDecimal getSequence() {
        return sequence;
    }

    public void setSequence(BigDecimal sequence) {
        this.sequence = sequence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public StateMachineSchemeConfigDTO getStateMachineSchemeConfig() {
        return stateMachineSchemeConfig;
    }

    public void setStateMachineSchemeConfig(StateMachineSchemeConfigDTO stateMachineSchemeConfig) {
        this.stateMachineSchemeConfig = stateMachineSchemeConfig;
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("icon", icon)
                .add("name", name)
                .add("description", description)
                .add("organizationId", organizationId)
                .add("colour", colour)
                .add("typeCode", typeCode)
                .add("initialize", initialize)
                .add("sequence", sequence)
                .add("stateMachineSchemeConfig", stateMachineSchemeConfig)
                .toString();
    }
}
