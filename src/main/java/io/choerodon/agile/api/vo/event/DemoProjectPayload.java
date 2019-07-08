package io.choerodon.agile.api.vo.event;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/2/26.
 * Email: fuqianghuang01@gmail.com
 */
public class DemoProjectPayload {

    private Long projectId;

    private Long userId1;

    private Long userId2;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getUserId1() {
        return userId1;
    }

    public void setUserId1(Long userId1) {
        this.userId1 = userId1;
    }

    public Long getUserId2() {
        return userId2;
    }

    public void setUserId2(Long userId2) {
        this.userId2 = userId2;
    }
}
