package io.choerodon.agile.app.service;


import io.choerodon.agile.api.dto.IssueCommentCreateDTO;
import io.choerodon.agile.api.dto.IssueCommentDTO;
import io.choerodon.agile.api.dto.IssueCommentUpdateDTO;

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
     * @param issueCommentCreateDTO issueCommentCreateDTO
     * @return IssueCommentDTO
     */
    IssueCommentDTO createIssueComment(Long projectId, IssueCommentCreateDTO issueCommentCreateDTO);

    /**
     * 更新issue评论
     *
     * @param issueCommentUpdateDTO issueCommentUpdateDTO
     * @param fieldList             fieldList
     * @return IssueCommentDTO
     */
    IssueCommentDTO updateIssueComment(IssueCommentUpdateDTO issueCommentUpdateDTO, List<String> fieldList);

    /**
     * 根据issueId和项目id查询IssueComment列表
     *
     * @param projectId projectId
     * @param issueId   issueId
     * @return IssueCommentDTO
     */
    List<IssueCommentDTO> queryIssueCommentList(Long projectId, Long issueId);

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