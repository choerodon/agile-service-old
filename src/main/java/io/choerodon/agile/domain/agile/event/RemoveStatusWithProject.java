package io.choerodon.agile.domain.agile.event;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2018/11/28
 */
public class RemoveStatusWithProject {
    private Long projectId;
    private List<Long> deleteStatusIds;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public List<Long> getDeleteStatusIds() {
        return deleteStatusIds;
    }

    public void setDeleteStatusIds(List<Long> deleteStatusIds) {
        this.deleteStatusIds = deleteStatusIds;
    }
}
