package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.*;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by jian_zhang02@163.com on 2018/5/14.
 */
public interface ProductVersionMapper extends BaseMapper<ProductVersionDO> {

    List queryVersionIdsByProjectId(@Param("projectId") Long projectId,
                                    @Param("searchArgs") Map<String, Object> searchArgs,
                                    @Param("advancedSearchArgs") Map<String, Object> advancedSearchArgs);

    List<ProductVersionDO> queryVersionByIds(@Param("projectId") Long projectId, @Param("versionIds") List<Long> versionIds);

    Boolean isRepeatName(@Param("projectId") Long projectId, @Param("name") String name);

    Boolean isNotReName(@Param("projectId") Long projectId, @Param("versionId") Long versionId, @Param("name") String name);

    List<ProductVersionDataDO> queryVersionByprojectId(@Param("projectId") Long projectId);

    ProductVersionStatisticsDO queryVersionStatisticsByVersionId(@Param("projectId") Long projectId, @Param("versionId") Long versionId);

    List<IssueDO> queryIssueByVersionIdAndStatusCode(@Param("projectId") Long projectId, @Param("versionId") Long versionId, @Param("statusCode") String statusCode);

    /**
     * 根据版本名称和项目id返回版本id
     *
     * @param name      name
     * @param projectId projectId
     * @return Long
     */
    Long queryVersionIdByNameAndProjectId(@Param("name") String name, @Param("projectId") Long projectId);

    List<IssueCountDO> queryIssueCount(@Param("projectId") Long projectId, @Param("versionIds") List<Long> versionIds, @Param("categoryCode") String categoryCode);

    List<IssueCountDO> queryNotEstimate(@Param("projectId") Long projectId, @Param("versionIds") List<Long> productVersionIds);

    List<IssueCountDO> queryTotalEstimate(@Param("projectId") Long projectId, @Param("versionIds") List<Long> productVersionIds);

    List<IssueCountDO> queryIssueCountByVersionId(@Param("projectId") Long projectId, @Param("versionId") Long versionId, @Param("categoryCode") String categoryCode);

    int queryNotDoneIssueCount(@Param("projectId") Long projectId, @Param("versionId") Long versionId);

    List<ProductVersionNameDO> queryPlanVersionNames(@Param("projectId") Long projectId, @Param("versionId") Long versionId);

    int issueToDestination(@Param("projectId") Long projectId, @Param("targetVersionId") Long targetVersionId, @Param("versionIssues") List<VersionIssueDO> versionIssues);

    int releaseVersion(@Param("projectId") Long projectId, @Param("versionId") Long versionId, @Param("releaseDate") Date releaseDate);

    int queryIssueCountByRelationType(@Param("projectId") Long projectId, @Param("versionId") Long versionId, @Param("relationType") String relationType);

    List<ProductVersionNameDO> queryVersionNames(@Param("projectId") Long projectId, @Param("versionId") Long versionId);

    List<VersionIssueDO> queryIncompleteIssues(@Param("projectId") Long projectId, @Param("versionId") Long versionId);

    List<VersionIssueDO> queryIssuesByRelationType(@Param("projectId") Long projectId, @Param("versionId") Long versionId, @Param("relationType") String relationType);

    List<ProductVersionNameDO> queryNameByOptions(@Param("projectId") Long projectId, @Param("statusCodes") List<String> statusCodes);

    List<ProductVersionCommonDO> listByProjectId(@Param("projectId") Long projectId);

    List<VersionIssueDO> queryIssueByVersionIds(@Param("projectId") Long projectId, @Param("versionIds") List<Long> versionIds);

    int deleteByVersionIds(@Param("projectId") Long projectId, @Param("versionIds") List<Long> versionIds);
}
