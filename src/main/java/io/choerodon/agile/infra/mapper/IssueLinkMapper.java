package io.choerodon.agile.infra.mapper;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.agile.infra.dataobject.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 敏捷开发Issue链接
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:50:34
 */
public interface IssueLinkMapper extends Mapper<IssueLinkDTO> {

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
     * @param noIssueTest noIssueTest
     * @return IssueLinkDTO
     */
    List<IssueLinkDTO> queryIssueLinkByIssueId(@Param("issueId") Long issueId, @Param("projectId") Long projectId, @Param("noIssueTest") Boolean noIssueTest);

    /**
     * 批量更新issue链接关系到别的issueLinkType
     *
     * @param issueLinkTypeId   issueLinkTypeId
     * @param toIssueLinkTypeId toIssueLinkTypeId
     * @return int
     */
    int batchUpdateRelToIssueLinkType(@Param("issueLinkTypeId") Long issueLinkTypeId, @Param("toIssueLinkTypeId") Long toIssueLinkTypeId);

    List<IssueLinkDTO> listIssueLinkByBatch(@Param("projectId") Long projectId, @Param("issueIds") List<Long> issueIds);
}