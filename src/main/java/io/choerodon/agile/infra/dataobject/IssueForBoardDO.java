package io.choerodon.agile.infra.dataobject;

import io.choerodon.agile.api.dto.IssueTypeDTO;
import io.choerodon.agile.api.dto.PriorityDTO;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/14.
 * Email: fuqianghuang01@gmail.com
 */
public class IssueForBoardDO {

    private Long issueId;

    private String issueNum;

    private String typeCode;

//    private String priorityName;

    private String summary;

    private Long parentIssueId;

    private Long objectVersionNumber;

    private Long assigneeId;

    private String assigneeName;

    private String imageUrl;

    private Long epicId;

    private String rank;

    private Long priorityId;

    private PriorityDTO priorityDTO;

    private Long issueTypeId;

    private IssueTypeDTO issueTypeDTO;

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public void setIssueNum(String issueNum) {
        this.issueNum = issueNum;
    }

    public String getIssueNum() {
        return issueNum;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setParentIssueId(Long parentIssueId) {
        this.parentIssueId = parentIssueId;
    }

    public Long getParentIssueId() {
        return parentIssueId;
    }

//    public void setPriorityName(String priorityName) {
//        this.priorityName = priorityName;
//    }
//
//    public String getPriorityName() {
//        return priorityName;
//    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setEpicId(Long epicId) {
        this.epicId = epicId;
    }

    public Long getEpicId() {
        return epicId;
    }

    public void setPriorityId(Long priorityId) {
        this.priorityId = priorityId;
    }

    public Long getPriorityId() {
        return priorityId;
    }

    public void setPriorityDTO(PriorityDTO priorityDTO) {
        this.priorityDTO = priorityDTO;
    }

    public PriorityDTO getPriorityDTO() {
        return priorityDTO;
    }

    public void setIssueTypeDTO(IssueTypeDTO issueTypeDTO) {
        this.issueTypeDTO = issueTypeDTO;
    }

    public IssueTypeDTO getIssueTypeDTO() {
        return issueTypeDTO;
    }

    public void setIssueTypeId(Long issueTypeId) {
        this.issueTypeId = issueTypeId;
    }

    public Long getIssueTypeId() {
        return issueTypeId;
    }
}
