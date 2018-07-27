package io.choerodon.agile.domain.agile.event;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/7/9.
 * Email: fuqianghuang01@gmail.com
 */
public class VersionPayload {

    private Long versionId;

    private Long projectId;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }
}
