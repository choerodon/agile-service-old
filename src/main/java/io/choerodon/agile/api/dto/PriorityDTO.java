package io.choerodon.agile.api.dto;


import java.math.BigDecimal;


public class PriorityDTO {

    private Long id;
    private String name;
    private String description;
    private String colour;
    private Long organizationId;
    private Boolean isDefault;
    private Long objectVersionNumber;
    private BigDecimal sequence;


    public BigDecimal getSequence() {
        return sequence;
    }

    public void setSequence(BigDecimal sequence) {
        this.sequence = sequence;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
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

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public Boolean getDefault() {
        return isDefault;
    }
}
