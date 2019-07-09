package io.choerodon.agile.infra.dataobject;

import io.choerodon.agile.infra.common.utils.StringUtil;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/9/27
 */
public class IssueProjectDTO {

    private Long projectId;

    private List<Long> issueIdList;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public List<Long> getIssueIdList() {
        return issueIdList;
    }

    public void setIssueIdList(List<Long> issueIdList) {
        this.issueIdList = issueIdList;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
