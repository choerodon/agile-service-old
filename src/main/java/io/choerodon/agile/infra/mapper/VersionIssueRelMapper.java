package io.choerodon.agile.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.agile.infra.dataobject.*;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018-05-15 16:21:18
 */
public interface VersionIssueRelMapper extends BaseMapper<VersionIssueRelDO> {

    int deleteIncompleteIssueByVersionId(@Param("projectId") Long projectId, @Param("versionId") Long versionId);

    int deleteByVersionIds(@Param("projectId") Long projectId, @Param("versionIds") List<Long> versionIds);

    /**
     * 通过issueId和Type批量删除版本关联（已归档的不删除）
     *
     * @param projectId   projectId
     * @param issueId     issueId
     * @param versionType versionType
     * @return int
     */
    int batchDeleteByIssueIdAndTypeArchivedExceptInfluence(@Param("projectId") Long projectId, @Param("issueId") Long issueId, @Param("versionType") String versionType);

    /**
     * 查询当前版本关联（不包括归档的）
     *
     * @param projectId   projectId
     * @param issueId     issueId
     * @param versionType versionType
     * @return Long
     */
    List<Long> queryByIssueIdAndProjectIdNoArchivedExceptInfluence(@Param("projectId") Long projectId, @Param("issueId") Long issueId, @Param("versionType") String versionType);

    void updateDemoVersionIssueTime(@Param("projectId") Long projectId, @Param("updateDate") Date updateDate);

    /**
     * 根据issueId和projectId查询versionIds
     *
     * @param issueId   issueId
     * @param projectId projectId
     * @return Long
     */
    List<Long> queryVersionIdsByIssueId(@Param("issueId") Long issueId, @Param("projectId") Long projectId);
}