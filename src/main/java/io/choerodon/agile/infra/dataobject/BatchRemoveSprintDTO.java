package io.choerodon.agile.infra.dataobject;

import io.choerodon.agile.infra.utils.StringUtil;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/25
 */
public class BatchRemoveSprintDTO {

    private Long projectId;

    private Long sprintId;

    private List<Long> issueIds;

    public BatchRemoveSprintDTO(Long projectId, Long sprintId, List<Long> issueIds) {
        this.projectId = projectId;
        this.sprintId = sprintId;
        this.issueIds = issueIds;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public List<Long> getIssueIds() {
        return issueIds;
    }

    public void setIssueIds(List<Long> issueIds) {
        this.issueIds = issueIds;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
