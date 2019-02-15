package io.choerodon.agile.domain.agile.event;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/2/14.
 * Email: fuqianghuang01@gmail.com
 */
public class DemoPayload {

    private Long projectId;

    private List<Long> testIssueIds;

    public void setTestIssueIds(List<Long> testIssueIds) {
        this.testIssueIds = testIssueIds;
    }

    public List<Long> getTestIssueIds() {
        return testIssueIds;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getProjectId() {
        return projectId;
    }
}
