package io.choerodon.agile.infra.dataobject;


import io.choerodon.mybatis.entity.BaseDTO;
import io.choerodon.agile.infra.common.utils.StringUtil;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 敏捷开发Issue评论
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:59:45
 */
@Table(name = "agile_issue_comment")
public class IssueCommentDO extends BaseDTO {

    /***/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * issue的id
     */
    @NotNull(message = "error.issue_comment.issue_idNotNull")
    private Long issueId;

    /**
     * 评论内容
     */
    private String commentText;

    /**
     * 项目id
     */
    @NotNull(message = "error.IssueCommentDO.projectIdNotNull")
    private Long projectId;

    @Transient
    private List<IssueAttachmentDO> issueAttachmentDOList;

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public List<IssueAttachmentDO> getIssueAttachmentDOList() {
        return issueAttachmentDOList;
    }

    public void setIssueAttachmentDOList(List<IssueAttachmentDO> issueAttachmentDOList) {
        this.issueAttachmentDOList = issueAttachmentDOList;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

}