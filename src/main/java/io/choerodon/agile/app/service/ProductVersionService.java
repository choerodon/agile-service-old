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

    /**
     * 删除指定version
     *
     * @param projectId            projectId
     * @param productVersionDelete productVersionDelete
     * @return Boolean
     */
    Boolean deleteVersion(Long projectId, ProductVersionDeleteDTO productVersionDelete);

    ProductVersionDetailDTO updateVersion(Long projectId, Long versionId, ProductVersionUpdateDTO versionUpdateDTO, List<String> fieldList);

    Page<ProductVersionPageDTO> queryByProjectId(Long projectId, PageRequest pageRequest, Map<String, Object> searchParamMap);

    Boolean repeatName(Long projectId, String name);

    List<ProductVersionDataDTO> queryVersionByprojectId(Long projectId);

    ProductVersionStatisticsDTO queryVersionStatisticsByVersionId(Long projectId, Long versionId);

    List<IssueListDTO> queryIssueByVersionIdAndStatusCode(Long projectId, Long versionId, String statusCode);

    VersionMessageDTO queryReleaseMessageByVersionId(Long projectId, Long versionId);

    ProductVersionDetailDTO releaseVersion(Long projectId, ProductVersionReleaseDTO productVersionRelease);

    ProductVersionDetailDTO revokeReleaseVersion(Long projectId, Long versionId);

    VersionMessageDTO queryDeleteMessageByVersionId(Long projectId, Long versionId);

    List<ProductVersionNameDTO> queryNameByProjectId(Long projectId);

    List<ProductVersionDTO> listByProjectId(Long projectId);
}
