package io.choerodon.agile.infra.dataobject;

public class StoryMapVersionDO {

    private Long versionId;

    private String name;

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
