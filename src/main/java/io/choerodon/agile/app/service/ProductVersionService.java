package io.choerodon.agile.app.service;

import io.choerodon.agile.api.vo.*;
import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;

import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/5/14.
 */
public interface ProductVersionService {
    /**
     * 版本创建
     *
     * @param projectId        projectId
     * @param versionCreateDTO versionCreateDTO
     * @return ProductVersionDetailDTO
     */
    ProductVersionDetailDTO createVersion(Long projectId, ProductVersionCreateDTO versionCreateDTO);

    Boolean deleteVersion(Long projectId, Long versionId, Long targetVersionId);

    ProductVersionDetailDTO updateVersion(Long projectId, Long versionId, ProductVersionUpdateDTO versionUpdateDTO, List<String> fieldList);

    PageInfo<ProductVersionPageDTO> queryByProjectId(Long projectId, PageRequest pageRequest, SearchVO searchVO);

    Boolean repeatName(Long projectId, String name);

    List<ProductVersionDataDTO> queryVersionByProjectId(Long projectId);

    ProductVersionStatisticsDTO queryVersionStatisticsByVersionId(Long projectId, Long versionId);

    List<IssueListVO> queryIssueByVersionIdAndStatusCode(Long projectId, Long versionId, String statusCode, Long organizationId, SearchVO searchVO);

    VersionMessageDTO queryReleaseMessageByVersionId(Long projectId, Long versionId);

    ProductVersionDetailDTO releaseVersion(Long projectId, ProductVersionReleaseDTO productVersionRelease);

    ProductVersionDetailDTO revokeReleaseVersion(Long projectId, Long versionId);

    VersionMessageDTO queryDeleteMessageByVersionId(Long projectId, Long versionId);

    List<ProductVersionNameDTO> queryNameByOptions(Long projectId, List<String> statusCodes);

    List<ProductVersionDTO> listByProjectId(Long projectId);

    ProductVersionDetailDTO archivedVersion(Long projectId, Long versionId);

    ProductVersionDetailDTO revokeArchivedVersion(Long projectId, Long versionId);

    Boolean mergeVersion(Long projectId, ProductVersionMergeDTO productVersionMergeDTO);

    ProductVersionDetailDTO queryVersionByVersionId(Long projectId, Long versionId);

    List<Long> listIds(Long projectId);

    /**
     * 拖动版本排序
     *
     * @param projectId          projectId
     * @param versionSequenceDTO versionSequenceDTO
     * @return ProductVersionPageDTO
     */
    ProductVersionPageDTO dragVersion(Long projectId, VersionSequenceDTO versionSequenceDTO);

    VersionIssueCountDTO queryByCategoryCode(Long projectId, Long versionId);

    Long queryProjectIdByVersionId(Long projectId, Long versionId);
}
