package io.choerodon.agile.api.dto;

public class IdWithNameDTO {

    public IdWithNameDTO(Long userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    private Long userId;

    private String name;

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
}
