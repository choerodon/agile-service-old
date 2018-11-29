package io.choerodon.agile.domain.agile.event;

import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/10/31
 */
public class DeployStateMachinePayload {
    private Long userId;
    private List<RemoveStatusWithProject> removeStatusWithProjects;
    private List<AddStatusWithProject> addStatusWithProjects;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<RemoveStatusWithProject> getRemoveStatusWithProjects() {
        return removeStatusWithProjects;
    }

    public void setRemoveStatusWithProjects(List<RemoveStatusWithProject> removeStatusWithProjects) {
        this.removeStatusWithProjects = removeStatusWithProjects;
    }

    public List<AddStatusWithProject> getAddStatusWithProjects() {
        return addStatusWithProjects;
    }

    public void setAddStatusWithProjects(List<AddStatusWithProject> addStatusWithProjects) {
        this.addStatusWithProjects = addStatusWithProjects;
    }
}
