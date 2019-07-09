package io.choerodon.agile.infra.dataobject;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/10/9.
 * Email: fuqianghuang01@gmail.com
 */
@Table(name = "agile_message")
public class MessageDTO extends BaseDTO {

    @Transient
    private Long id;

    private String event;

    private String noticeType;

    private String noticeName;

    @Column(name = "is_enable")
    private Boolean enable;

    private String user;

    @Transient
    private Long projectId;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    public String getNoticeName() {
        return noticeName;
    }

    public void setNoticeName(String noticeName) {
        this.noticeName = noticeName;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }
}
