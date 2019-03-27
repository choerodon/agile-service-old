package io.choerodon.agile.api.dto;


import io.choerodon.agile.infra.common.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/5/16.
 * Email: fuqianghuang01@gmail.com
 */
public class IssueAttachmentDTO {

    @ApiModelProperty(value = "附件主键id")
    private Long attachmentId;

    @ApiModelProperty(value = "问题id")
    private Long issueId;

    @ApiModelProperty(value = "评论id")
    private Long commentId;

    @ApiModelProperty(value = "附件url")
    private String url;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "版本号")
    private Long objectVersionNumber;

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

}