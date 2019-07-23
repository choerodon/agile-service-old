package io.choerodon.agile.infra.dataobject;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.Table;
import java.util.Date;

@Table(name = "feedback_data_log")
public class FeedbackDataLogDTO extends BaseDTO {

    private Long id;

    private Long projectId;

    private String field;

    private String oldValue;

    private String oldString;

    private String newValue;

    private String newString;

    private Long feedbackId;

    private Date creationDate;

    private Long createdBy;

    private UserMessageDTO created;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getOldString() {
        return oldString;
    }

    public void setOldString(String oldString) {
        this.oldString = oldString;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getNewString() {
        return newString;
    }

    public void setNewString(String newString) {
        this.newString = newString;
    }

    public Long getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Long feedbackId) {
        this.feedbackId = feedbackId;
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
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreated(UserMessageDTO created) {
        this.created = created;
    }

    public UserMessageDTO getCreated() {
        return created;
    }
}
