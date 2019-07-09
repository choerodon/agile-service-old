package io.choerodon.agile.infra.dataobject;

import io.choerodon.agile.api.vo.StatusMapVO;

import java.math.BigDecimal;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/8/8.
 * Email: fuqianghuang01@gmail.com
 */
public class StoryMapIssueDO {

    private Long sprintId;

    private String sprintName;

    private Long versionId;

    private String versionName;

    private Long issueId;

    private Long assigneeId;

    private String priorityCode;

    private String summary;

    private String typeCode;

    private BigDecimal storyPoints;

    private String issueNum;

    private String statusCode;

    private String statusName;

    private Long epicId;

    private Long objectVersionNumber;

    private String mapRank;

    private Long priorityId;

    private Long statusId;

    private Long issueTypeId;

    private StatusMapVO statusMapVO;

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

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }


    public String getPriorityCode() {
        return priorityCode;
    }

    public void setPriorityCode(String priorityCode) {
        this.priorityCode = priorityCode;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public void setStoryPoints(BigDecimal storyPoints) {
        this.storyPoints = storyPoints;
    }

    public BigDecimal getStoryPoints() {
        return storyPoints;
    }

    public String getIssueNum() {
        return issueNum;
    }

    public void setIssueNum(String issueNum) {
        this.issueNum = issueNum;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setEpicId(Long epicId) {
        this.epicId = epicId;
    }

    public Long getEpicId() {
        return epicId;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setMapRank(String mapRank) {
        this.mapRank = mapRank;
    }

    public String getMapRank() {
        return mapRank;
    }

    public void setPriorityId(Long priorityId) {
        this.priorityId = priorityId;
    }

    public Long getPriorityId() {
        return priorityId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusMapVO(StatusMapVO statusMapVO) {
        this.statusMapVO = statusMapVO;
    }

    public StatusMapVO getStatusMapVO() {
        return statusMapVO;
    }

    public void setIssueTypeId(Long issueTypeId) {
        this.issueTypeId = issueTypeId;
    }

    public Long getIssueTypeId() {
        return issueTypeId;
    }
}
