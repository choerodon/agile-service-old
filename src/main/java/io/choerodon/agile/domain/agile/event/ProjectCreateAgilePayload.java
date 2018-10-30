package io.choerodon.agile.domain.agile.event;

import java.util.List;

public class ProjectCreateAgilePayload {

    private ProjectEvent projectEvent;

    private List<StatusPayload> statusPayloads;

    public void setProjectEvent(ProjectEvent projectEvent) {
        this.projectEvent = projectEvent;
    }

    public List<StatusPayload> getStatusPayloads() {
        return statusPayloads;
    }

    public void setStatusPayloads(List<StatusPayload> statusPayloads) {
        this.statusPayloads = statusPayloads;
    }

    public ProjectEvent getProjectEvent() {
        return projectEvent;
    }
}
