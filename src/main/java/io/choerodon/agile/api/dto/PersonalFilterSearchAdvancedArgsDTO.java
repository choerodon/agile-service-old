package io.choerodon.agile.api.dto;

import java.util.Date;
import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/2/25
 */
public class PersonalFilterSearchAdvancedArgsDTO {
    List<Long> issueTypeId;
    List<Long> statusId;
    List<Long> priorityId;
    List<Long> assigneeIds;
    Date createStartDate;
    Date createEndDate;

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

    public Date getCreateStartDate() {
        return createStartDate;
    }

    public void setCreateStartDate(Date createStartDate) {
        this.createStartDate = createStartDate;
    }

    public Date getCreateEndDate() {
        return createEndDate;
    }

    public void setCreateEndDate(Date createEndDate) {
        this.createEndDate = createEndDate;
    }
}
