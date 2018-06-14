package io.choerodon.agile.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.agile.infra.dataobject.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 敏捷开发Issue链接
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:50:34
 */
public interface IssueLinkMapper extends BaseMapper<IssueLinkDO> {

    /**
     * 根据issueId删除
     *
     * @param issueId issueId
     * @return int
     */
    int deleteByIssueId(@Param("issueId") Long issueId);

    /**
     * 根据IssueId查询issueLink
     *
     * @param issueId   issueId
     * @param projectId projectId
     * @return IssueLinkDO
     */
    List<IssueLinkDO> queryIssueLinkByIssueId(@Param("issueId") Long issueId, @Param("projectId") Long projectId);

}