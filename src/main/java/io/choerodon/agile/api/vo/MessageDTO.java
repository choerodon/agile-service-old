package io.choerodon.agile.api.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Transient;
import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/10/9.
 * Email: fuqianghuang01@gmail.com
 */
public class MessageDTO {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "通知时间类型：issue_created、issue_assigneed、issue_solved")
    private String event;

    @ApiModelProperty(value = "通知对象：assigneer、reporter、project_owner、users")
    private String noticeType;

    @ApiModelProperty(value = "通知对象名称")
    private String noticeName;

    @ApiModelProperty(value = "是否启用")
    private Boolean enable;

    @ApiModelProperty(value = "如果通知对象是users，则设置该字段，以id隔开")
    private String user;

    @ApiModelProperty(value = "版本号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "用户列表详细信息列表")
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
