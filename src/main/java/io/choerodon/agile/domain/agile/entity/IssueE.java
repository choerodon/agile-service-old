package io.choerodon.agile.domain.agile.entity;


import io.choerodon.agile.infra.common.utils.StringUtil;
import io.choerodon.agile.infra.dataobject.LookupValueDO;
import io.choerodon.core.oauth.DetailsHelper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * 敏捷开发Issue
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 20:30:48
 */
public class IssueE {

    private static final String SUB_TASK = "sub_task";
    private static final String ISSUE_EPIC = "issue_epic";

    private Long issueId;

    private String issueNum;

    private String typeCode;

    private Long statusId;

    private String summary;

    private String priorityCode;

    private Long reporterId;

    private String description;

    private String rank;

    private Long assigneeId;

    private Long projectId;

    private Long epicId;

    private Long sprintId;

    private Long parentIssueId;

    private Integer storyPoints;

    private Long objectVersionNumber;

    private BigDecimal estimateTime;

    private BigDecimal remainingTime;

    private String colorCode;

    private String epicName;

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getIssueNum() {
        return issueNum;
    }

    public void setIssueNum(String issueNum) {
        this.issueNum = issueNum;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPriorityCode() {
        return priorityCode;
    }

    public void setPriorityCode(String priorityCode) {
        this.priorityCode = priorityCode;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getEpicId() {
        return epicId;
    }

    public void setEpicId(Long epicId) {
        this.epicId = epicId;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public Long getParentIssueId() {
        return parentIssueId;
    }

    public void setParentIssueId(Long parentIssueId) {
        this.parentIssueId = parentIssueId;
    }

    public Integer getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(Integer storyPoints) {
        this.storyPoints = storyPoints;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public BigDecimal getEstimateTime() {
        return estimateTime;
    }

    public void setEstimateTime(BigDecimal estimateTime) {
        this.estimateTime = estimateTime;
    }

    public BigDecimal getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(BigDecimal remainingTime) {
        this.remainingTime = remainingTime;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getEpicName() {
        return epicName;
    }

    public void setEpicName(String epicName) {
        this.epicName = epicName;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

    /**
     * 初始化创建子任务
     *
     * @param issueSub issueSub
     * @return IssueE
     */
    public IssueE initializationSubIssue(IssueE issueSub) {
        issueSub.setStatusId(this.statusId);
        issueSub.setReporterId(DetailsHelper.getUserDetails().getUserId());
        issueSub.setParentIssueId(this.issueId);
        issueSub.setSprintId(this.sprintId);
        issueSub.setTypeCode(SUB_TASK);
        issueSub.setEpicId(0L);
        return issueSub;
    }

    /**
     * 对epic类型的issue初始化颜色
     *
     * @param lookupValueDOList lookupValueDOList
     */
    public void initializationColor(List<LookupValueDO> lookupValueDOList) {
        if (ISSUE_EPIC.equals(this.typeCode)) {
            this.colorCode = lookupValueDOList.get(new Random().nextInt(lookupValueDOList.size())).getValueCode();
        }
    }

    /**
     * 初始化创建issue
     *
     * @param statusId statusId
     */
    public void initializationIssue(Long statusId) {
        this.statusId = statusId;
        this.parentIssueId = 0L;
        this.reporterId = DetailsHelper.getUserDetails().getUserId();
        if (this.epicId == null) {
            this.epicId = 0L;
        }
    }

    public Boolean isIssueRank() {
        return !Objects.equals(this.typeCode, SUB_TASK) && !Objects.equals(this.typeCode, ISSUE_EPIC);
    }
}