package io.choerodon.agile.domain.agile.event;

import java.util.Date;
import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/2/14.
 * Email: fuqianghuang01@gmail.com
 */
public class DemoPayload {

    private Long projectId;

    private Long versionId;

    private Long userId;

    private Date sprintEndDate;

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

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getSprintEndDate() {
        return sprintEndDate;
    }

    public void setSprintEndDate(Date sprintEndDate) {
        this.sprintEndDate = sprintEndDate;
    }
}
