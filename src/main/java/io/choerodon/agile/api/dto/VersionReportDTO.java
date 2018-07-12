package io.choerodon.agile.api.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/7/5.
 */
public class VersionReportDTO {
    private Integer totalField;
    private Integer completedField;
    private double  unEstimatedPercentage;
    private Date changeDate;
    private List<IssueChangeDTO> fieldChangIssues;
    private List<IssueChangeDTO> completedIssues;
    private List<IssueChangeDTO> unCompletedIssues;
    private List<IssueChangeDTO> addIssues;
    private List<IssueChangeDTO> removeIssues;

    public Integer getTotalField() {
        return totalField;
    }

    public void setTotalField(Integer totalField) {
        this.totalField = totalField;
    }

    public Integer getCompletedField() {
        return completedField;
    }

    public void setCompletedField(Integer completedField) {
        this.completedField = completedField;
    }

    public double getUnEstimatedPercentage() {
        return unEstimatedPercentage;
    }

    public void setUnEstimatedPercentage(double unEstimatedPercentage) {
        this.unEstimatedPercentage = unEstimatedPercentage;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public List<IssueChangeDTO> getFieldChangIssues() {
        return fieldChangIssues;
    }

    public void setFieldChangIssues(List<IssueChangeDTO> fieldChangIssues) {
        this.fieldChangIssues = fieldChangIssues;
    }

    public List<IssueChangeDTO> getCompletedIssues() {
        return completedIssues;
    }

    public void setCompletedIssues(List<IssueChangeDTO> completedIssues) {
        this.completedIssues = completedIssues;
    }

    public List<IssueChangeDTO> getUnCompletedIssues() {
        return unCompletedIssues;
    }

    public void setUnCompletedIssues(List<IssueChangeDTO> unCompletedIssues) {
        this.unCompletedIssues = unCompletedIssues;
    }

    public List<IssueChangeDTO> getAddIssues() {
        return addIssues;
    }

    public void setAddIssues(List<IssueChangeDTO> addIssues) {
        this.addIssues = addIssues;
    }

    public List<IssueChangeDTO> getRemoveIssues() {
        return removeIssues;
    }

    public void setRemoveIssues(List<IssueChangeDTO> removeIssues) {
        this.removeIssues = removeIssues;
    }

}
