package io.choerodon.agile.api.dto;

/**
 * @author shinan.chen
 * @since 2019/2/25
 */
public class PersonalFilterDTO {

    private Long filterId;
    private Long projectId;
    private Long userId;
    private String name;
    private String filterJson;

    private Long objectVersionNumber;

    private PersonalFilterSearchDTO personalFilterSearchDTO;

    public PersonalFilterSearchDTO getPersonalFilterSearchDTO() {
        return personalFilterSearchDTO;
    }

    public void setPersonalFilterSearchDTO(PersonalFilterSearchDTO personalFilterSearchDTO) {
        this.personalFilterSearchDTO = personalFilterSearchDTO;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getFilterId() {
        return filterId;
    }

    public void setFilterId(Long filterId) {
        this.filterId = filterId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilterJson() {
        return filterJson;
    }

    public void setFilterJson(String filterJson) {
        this.filterJson = filterJson;
    }
}

