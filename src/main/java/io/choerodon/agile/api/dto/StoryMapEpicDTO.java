package io.choerodon.agile.api.dto;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/8/10.
 * Email: fuqianghuang01@gmail.com
 */
public class StoryMapEpicDTO {
    private Long issueId;
    private String summary;
    private String description;
    private String epicName;
    private Long projectId;
    private Integer issueCount;
    private Integer doneIssueCount;
    private Integer notEstimate;
    private Integer totalEstimate;
    private String color;
    private Long objectVersionNumber;
    private Integer epicSequence;
//    private String statusColor;
//    private String statusName;
    private Long statusId;
    private StatusMapDTO statusMapDTO;
    private Long issueTypeId;
    private IssueTypeDTO issueTypeDTO;
    private String issueNum;

    public Integer getEpicSequence() {
        return epicSequence;
    }

    public void setEpicSequence(Integer epicSequence) {
        this.epicSequence = epicSequence;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getIssueCount() {
        return issueCount;
    }

    public void setIssueCount(Integer issueCount) {
        this.issueCount = issueCount;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getDoneIssueCount() {
        return doneIssueCount;
    }

    public void setDoneIssueCount(Integer doneIssueCount) {
        this.doneIssueCount = doneIssueCount;
    }

    public Integer getNotEstimate() {
        return notEstimate;
    }

    public void setNotEstimate(Integer notEstimate) {
        this.notEstimate = notEstimate;
    }

    public Integer getTotalEstimate() {
        return totalEstimate;
    }

    public void setTotalEstimate(Integer totalEstimate) {
        this.totalEstimate = totalEstimate;
    }

    public String getEpicName() {
        return epicName;
    }

    public void setEpicName(String epicName) {
        this.epicName = epicName;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

//    public String getStatusColor() {
//        return statusColor;
//    }
//
//    public void setStatusColor(String statusColor) {
//        this.statusColor = statusColor;
//    }
//
//    public String getStatusName() {
//        return statusName;
//    }
//
//    public void setStatusName(String statusName) {
//        this.statusName = statusName;
//    }

    public String getIssueNum() {
        return issueNum;
    }

    public void setIssueNum(String issueNum) {
        this.issueNum = issueNum;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusMapDTO(StatusMapDTO statusMapDTO) {
        this.statusMapDTO = statusMapDTO;
    }

    public StatusMapDTO getStatusMapDTO() {
        return statusMapDTO;
    }

    public void setIssueTypeId(Long issueTypeId) {
        this.issueTypeId = issueTypeId;
    }

    public Long getIssueTypeId() {
        return issueTypeId;
    }

    public void setIssueTypeDTO(IssueTypeDTO issueTypeDTO) {
        this.issueTypeDTO = issueTypeDTO;
    }

    public IssueTypeDTO getIssueTypeDTO() {
        return issueTypeDTO;
    }
}
