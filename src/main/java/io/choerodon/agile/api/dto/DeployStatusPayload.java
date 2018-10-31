package io.choerodon.agile.api.dto;

import io.choerodon.agile.domain.agile.event.StatusPayload;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/10/31.
 * Email: fuqianghuang01@gmail.com
 */

public class DeployStatusPayload {

    private List<Long> projectIds;

    private List<StatusPayload> statusPayloads;

    public List<Long> getProjectIds() {
        return projectIds;
    }

    public void setProjectIds(List<Long> projectIds) {
        this.projectIds = projectIds;
    }

    public List<StatusPayload> getStatusPayloads() {
        return statusPayloads;
    }

    public void setStatusPayloads(List<StatusPayload> statusPayloads) {
        this.statusPayloads = statusPayloads;
    }
}
