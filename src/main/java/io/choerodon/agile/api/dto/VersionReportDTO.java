package io.choerodon.agile.api.dto;

import java.util.Date;
import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/7/5.
 */
public class VersionReportDTO {
    private Integer totalStoryPoints;
    private Integer completedStoryPoints;
    private double  unEstimatedPercentage;
    private Date changeDate;
    private List<IssueChangeDTO> storyPointsChangIssues;
    private List<IssueChangeDTO> completedIssues;
    private List<IssueChangeDTO> unCompletedIssues;
    private List<IssueChangeDTO> addIssues;
    private List<IssueChangeDTO> removeIssues;

    public Integer getTotalStoryPoints() {
        return totalStoryPoints;
    }

    public void setTotalStoryPoints(Integer totalStoryPoints) {
        this.totalStoryPoints = totalStoryPoints;
    }

    public Integer getCompletedStoryPoints() {
        return completedStoryPoints;
    }

    public void setCompletedStoryPoints(Integer completedStoryPoints) {
        this.completedStoryPoints = completedStoryPoints;
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

    public List<IssueChangeDTO> getStoryPointsChangIssues() {
        return storyPointsChangIssues;
    }

    public void setStoryPointsChangIssues(List<IssueChangeDTO> storyPointsChangIssues) {
        this.storyPointsChangIssues = storyPointsChangIssues;
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
