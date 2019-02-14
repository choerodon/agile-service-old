package io.choerodon.agile.domain.agile.event;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/2/14.
 * Email: fuqianghuang01@gmail.com
 */
public class DemoPayload {

    private List<Long> testIssueIds;

    public void setTestIssueIds(List<Long> testIssueIds) {
        this.testIssueIds = testIssueIds;
    }

    public List<Long> getTestIssueIds() {
        return testIssueIds;
    }
}
