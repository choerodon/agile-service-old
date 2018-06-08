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
     * 批量创建issueLink
     *
     * @param issueLinkDOList issueLinkDOList
     * @param issueId         issueId
     */
    void batchCreateIssueLink(@Param("issueLinkDOList") List<IssueLinkDO> issueLinkDOList, @Param("issueId") Long issueId);
}