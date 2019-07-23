package io.choerodon.agile.infra.dataobject;


import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Table(name = "feedback_comment")
public class FeedbackCommentDTO extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long feedbackId;

    private Long userId;

    private Long beRepliedId;

    private String content;

    private Long projectId;

    private Boolean isWithin;

    private Long parentId;

    @Transient
    private UserMessageDTO user;

    @Transient
    private UserMessageDTO beRepliedUser;

    @Transient
    private List<FeedbackAttachmentDTO> feedbackAttachmentDTOList;

    private Date lastUpdateDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Long feedbackId) {
        this.feedbackId = feedbackId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setBeRepliedId(Long beRepliedId) {
        this.beRepliedId = beRepliedId;
    }

    public Long getBeRepliedId() {
        return beRepliedId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public void setWithin(Boolean within) {
        isWithin = within;
    }

    public Boolean getWithin() {
        return isWithin;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setUser(UserMessageDTO user) {
        this.user = user;
    }

    public UserMessageDTO getUser() {
        return user;
    }

    public UserMessageDTO getBeRepliedUser() {
        return beRepliedUser;
    }

    public void setBeRepliedUser(UserMessageDTO beRepliedUser) {
        this.beRepliedUser = beRepliedUser;
    }

    public void setFeedbackAttachmentDTOList(List<FeedbackAttachmentDTO> feedbackAttachmentDTOList) {
        this.feedbackAttachmentDTOList = feedbackAttachmentDTOList;
    }

    public List<FeedbackAttachmentDTO> getFeedbackAttachmentDTOList() {
        return feedbackAttachmentDTOList;
    }

    @Override
    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    @Override
    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }
}
