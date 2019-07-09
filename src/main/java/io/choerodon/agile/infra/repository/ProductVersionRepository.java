package io.choerodon.agile.infra.repository;

import io.choerodon.agile.domain.agile.entity.ProductVersionE;
import io.choerodon.agile.infra.dataobject.VersionIssueDTO;

import java.util.Date;
import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/5/14.
 */
public interface ProductVersionRepository {

    ProductVersionE createVersion(ProductVersionE versionE);

    Boolean deleteVersion(ProductVersionE versionE);

    ProductVersionE updateVersion(ProductVersionE versionE, List<String> fieldList);

    Boolean batchIssueToDestination(Long projectId, Long targetVersionId, List<VersionIssueDTO> incompleteIssues, Date date, Long userId);

    Boolean releaseVersion(Long projectId, Long versionId, Date releaseDate);

    ProductVersionE updateVersion(ProductVersionE versionE);

    int deleteByVersionIds(Long projectId, List<Long> versionIds);

    /**
     * 批量更新排序
     *
     * @param sequence  sequence
     * @param projectId projectId
     * @param add       add
     * @param versionId versionId
     * @return int
     */
    int batchUpdateSequence(Integer sequence, Long projectId, Integer add, Long versionId);
}
