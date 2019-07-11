package io.choerodon.agile.app.service;


import io.choerodon.agile.api.vo.IssueCommentCreateVO;
import io.choerodon.agile.api.vo.IssueCommentVO;
import io.choerodon.agile.api.vo.IssueCommentUpdateVO;

import java.util.List;

/**
 * 敏捷开发Issue评论
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:59:45
 */
public interface IssueCommentService {

    /**
     * 创建issue评论
     *
     * @param projectId             projectId
     * @param issueCommentCreateVO issueCommentCreateVO
     * @return IssueCommentVO
     */
    IssueCommentVO createIssueComment(Long projectId, IssueCommentCreateVO issueCommentCreateVO);

    /**
     * 更新issue评论
     *
     * @param issueCommentUpdateVO issueCommentUpdateVO
     * @param fieldList             fieldList
     * @param projectId             projectId
     * @return IssueCommentVO
     */
    IssueCommentVO updateIssueComment(IssueCommentUpdateVO issueCommentUpdateVO, List<String> fieldList, Long projectId);

    /**
     * 根据issueId和项目id查询IssueComment列表
     *
     * @param projectId projectId
     * @param issueId   issueId
     * @return IssueCommentVO
     */
    List<IssueCommentVO> queryIssueCommentList(Long projectId, Long issueId);

    /**
     * 删除issueComment
     *
     * @param projectId projectId
     * @param commentId commentId
     * @return int
     */
    int deleteIssueComment(Long projectId, Long commentId);

    /**
     * 根据issueId删除评论
     *
     * @param issueId issueId
     * @return int
     */
    int deleteByIssueId(Long issueId);
}