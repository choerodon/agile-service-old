package io.choerodon.agile.infra.dataobject;


import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Table(name = "feedback")
public class FeedbackDTO extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String feedbackNum;

    private Long organizationId;

    private Long projectId;

    private String type;

    private String summary;

    private String description;

    private String reporter;

    private Long assigneeId;

    private String status;

    private String email;

    private Long applicationId;

    private String screenSize;

    private String browser;

    private Date creationDate;

    private Date lastUpdateDate;

    @Transient
    private String token;

    @Transient
    private List<FeedbackAttachmentDTO> feedbackAttachmentDTOList;

    @Transient
    private UserMessageDTO assignee;

    @Transient
    private ApplicationDTO applicationDTO;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFeedbackNum(String feedbackNum) {
        this.feedbackNum = feedbackNum;
    }

    public String getFeedbackNum() {
        return feedbackNum;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(String screenSize) {
        this.screenSize = screenSize;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    @Override
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    @Override
    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setFeedbackAttachmentDTOList(List<FeedbackAttachmentDTO> feedbackAttachmentDTOList) {
        this.feedbackAttachmentDTOList = feedbackAttachmentDTOList;
    }

    public List<FeedbackAttachmentDTO> getFeedbackAttachmentDTOList() {
        return feedbackAttachmentDTOList;
    }

    public void setAssignee(UserMessageDTO assignee) {
        this.assignee = assignee;
    }

    public UserMessageDTO getAssignee() {
        return assignee;
    }

    public ApplicationDTO getApplicationDTO() {
        return applicationDTO;
    }

    public void setApplicationDTO(ApplicationDTO applicationDTO) {
        this.applicationDTO = applicationDTO;
    }
}
