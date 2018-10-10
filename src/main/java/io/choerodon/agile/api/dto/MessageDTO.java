package io.choerodon.agile.api.dto;

import javax.persistence.Transient;
import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/10/9.
 * Email: fuqianghuang01@gmail.com
 */
public class MessageDTO {

    private Long id;

    private String event;

    private String noticeType;

    private String noticeName;

    private Boolean enable;

    private String user;

    private Long objectVersionNumber;

    @Transient
    private List<IdWithNameDTO> idWithNameDTOList;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setIdWithNameDTOList(List<IdWithNameDTO> idWithNameDTOList) {
        this.idWithNameDTOList = idWithNameDTOList;
    }

    public List<IdWithNameDTO> getIdWithNameDTOList() {
        return idWithNameDTOList;
    }
}
