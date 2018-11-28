package io.choerodon.agile.domain.agile.event;

import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @date 2018/10/31
 */
public class DeployStatusPayload {
    private Map<String, List<Long>> projectIdsMap;
    private List<StatusPayload> statusPayloads;
    private List<RemoveStatusWithProject> removeStatusWithProjects;

    public Map<String, List<Long>> getProjectIdsMap() {
        return projectIdsMap;
    }

    public void setProjectIdsMap(Map<String, List<Long>> projectIdsMap) {
        this.projectIdsMap = projectIdsMap;
    }

    public List<StatusPayload> getStatusPayloads() {
        return statusPayloads;
    }

    public void setStatusPayloads(List<StatusPayload> statusPayloads) {
        this.statusPayloads = statusPayloads;
    }

    public List<RemoveStatusWithProject> getRemoveStatusWithProjects() {
        return removeStatusWithProjects;
    }

    public void setRemoveStatusWithProjects(List<RemoveStatusWithProject> removeStatusWithProjects) {
        this.removeStatusWithProjects = removeStatusWithProjects;
    }
}