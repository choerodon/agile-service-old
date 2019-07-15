package io.choerodon.agile.infra.mapper;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.agile.infra.dataobject.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 敏捷开发Issue评论
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:59:45
 */
public interface IssueCommentMapper extends Mapper<IssueCommentDTO> {

    /**
     * 根据issueId查询评论
     *
     * @param issueId   issueId
     * @param projectId projectId
     * @return IssueCommentDTO
     */
    List<IssueCommentDTO> queryIssueCommentList(@Param("projectId") Long projectId, @Param("issueId") Long issueId);

}
