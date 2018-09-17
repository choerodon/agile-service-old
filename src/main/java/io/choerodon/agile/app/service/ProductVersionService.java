package io.choerodon.agile.app.service;

import io.choerodon.agile.api.dto.*;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;
import java.util.Map;

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

    Boolean deleteVersion(Long projectId, Long versionId, Long fixTargetVersionId, Long influenceTargetVersionId);

    ProductVersionDetailDTO updateVersion(Long projectId, Long versionId, ProductVersionUpdateDTO versionUpdateDTO, List<String> fieldList);

    Page<ProductVersionPageDTO> queryByProjectId(Long projectId, PageRequest pageRequest, Map<String, Object> searchParamMap);

    Boolean repeatName(Long projectId, String name);

    List<ProductVersionDataDTO> queryVersionByProjectId(Long projectId);

    ProductVersionStatisticsDTO queryVersionStatisticsByVersionId(Long projectId, Long versionId);

    List<IssueListDTO> queryIssueByVersionIdAndStatusCode(Long projectId, Long versionId, String statusCode);

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
