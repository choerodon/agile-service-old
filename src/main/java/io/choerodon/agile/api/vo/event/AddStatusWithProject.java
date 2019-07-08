package io.choerodon.agile.api.vo.event;

import io.choerodon.agile.api.vo.StatusMapDTO;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2018/11/28
 */
public class AddStatusWithProject {
    private Long projectId;
    private List<StatusMapDTO> addStatuses;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public List<StatusMapDTO> getAddStatuses() {
        return addStatuses;
    }

    public void setAddStatuses(List<StatusMapDTO> addStatuses) {
        this.addStatuses = addStatuses;
    }
}
