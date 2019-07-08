package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/2/25
 */
public class PersonalFilterSearchAdvancedArgsDTO {

    @ApiModelProperty(value = "问题类型id集合")
    private List<Long> issueTypeId;

    @ApiModelProperty(value = "状态id集合")
    private List<Long> statusId;

    @ApiModelProperty(value = "优先级id集合")
    private List<Long> priorityId;

    @ApiModelProperty(value = "经办人id集合")
    private List<Long> assigneeIds;

    @ApiModelProperty(value = "报告人id集合")
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
