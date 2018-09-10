package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;

import java.util.Date;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/9/10
 */
public class SprintBurnDownReportDTO {

    private Long sprintId;

    private String sprintName;

    private Date startDate;

    private Date endDate;

    private List<IssueBurnDownReportDTO> completeIssues;

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public String getSprintName() {
        return sprintName;
    }

    public void setSprintName(String sprintName) {
        this.sprintName = sprintName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<IssueBurnDownReportDTO> getCompleteIssues() {
        return completeIssues;
    }

    public void setCompleteIssues(List<IssueBurnDownReportDTO> completeIssues) {
        this.completeIssues = completeIssues;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
