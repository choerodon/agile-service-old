package io.choerodon.agile.domain.agile.repository;

import io.choerodon.agile.domain.agile.entity.ProductVersionE;
import io.choerodon.agile.infra.dataobject.VersionIssueDO;

import java.util.Date;
import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/5/14.
 */
public interface ProductVersionRepository {

    ProductVersionE createVersion(ProductVersionE versionE);

    Boolean deleteVersion(ProductVersionE versionE);

    ProductVersionE updateVersion(ProductVersionE versionE, List<String> fieldList);

    Boolean issueToDestination(Long projectId, Long targetVersionId, List<VersionIssueDO> incompleteIssues);

    Boolean releaseVersion(Long projectId, Long versionId, Date releaseDate);

    ProductVersionE updateVersion(ProductVersionE versionE);

    int deleteByVersionIds(Long projectId, List<Long> versionIds);
}
