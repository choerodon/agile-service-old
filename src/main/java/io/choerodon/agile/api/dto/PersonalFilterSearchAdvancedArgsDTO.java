package io.choerodon.agile.api.dto;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/2/25
 */
public class PersonalFilterSearchAdvancedArgsDTO {
    private List<Long> issueTypeId;
    private List<Long> statusId;
    private List<Long> priorityId;
    private List<Long> assigneeIds;
    private List<Long> reporterIds;

    public List<Long> getReporterIds() {
        return reporterIds;
    }

    public void setReporterIds(List<Long> reporterIds) {
        this.reporterIds = reporterIds;
    }

    public List<Long> getIssueTypeId() {
        return issueTypeId;
    }

    public void setIssueTypeId(List<Long> issueTypeId) {
        this.issueTypeId = issueTypeId;
    }

    public List<Long> getStatusId() {
        return statusId;
    }

    public void setStatusId(List<Long> statusId) {
        this.statusId = statusId;
    }

    public List<Long> getPriorityId() {
        return priorityId;
    }

    public void setPriorityId(List<Long> priorityId) {
        this.priorityId = priorityId;
    }

    public List<Long> getAssigneeIds() {
        return assigneeIds;
    }

    public void setAssigneeIds(List<Long> assigneeIds) {
        this.assigneeIds = assigneeIds;
    }
}
