package io.choerodon.agile.domain.agile.rule;

import io.choerodon.agile.api.dto.ProductVersionReleaseDTO;
import io.choerodon.agile.infra.dataobject.ProductVersionDO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.agile.infra.mapper.ProductVersionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Created by jian_zhang02@163.com on 2018/5/15.
 */
@Component
public class ProductVersionRule {
    @Autowired
    private ProductVersionMapper productVersionMapper;

    private static final String REPEAT_NAME_ERROR = "error.productVersion.repeatName";
    private static final String VERSION_NOT_FOUND = "error.version.notFound";
    private static final String VERSION_STATUS_PLAN_CODE = "version_planning";
    private static final String RELEASE_ERROR = "error.productVersion.release";

    public void judgeName(Long projectId, Long versionId, String name) {
        if (versionId != null && !Objects.equals(versionId, 0L) && productVersionMapper.isNotReName(projectId, versionId, name)) {
            return;
        }
        if (productVersionMapper.isRepeatName(projectId, name)) {
            throw new CommonException(REPEAT_NAME_ERROR);
        }
    }

    public void judgeExist(Long projectId, Long versionId) {
        if (versionId != null && !Objects.equals(versionId, 0L)) {
            ProductVersionDO productVersionDO = new ProductVersionDO();
            productVersionDO.setProjectId(projectId);
            productVersionDO.setVersionId(versionId);
            if (productVersionMapper.selectByPrimaryKey(productVersionDO) == null) {
                throw new CommonException(VERSION_NOT_FOUND);
            }
        }
    }

    public void isRelease(Long projectId, ProductVersionReleaseDTO productVersionRelease) {
        judgeExist(projectId, productVersionRelease.getTargetVersionId());
        ProductVersionDO productVersionDO = new ProductVersionDO();
        productVersionDO.setProjectId(projectId);
        productVersionDO.setVersionId(productVersionRelease.getVersionId());
        productVersionDO = productVersionMapper.selectOne(productVersionDO);
        if (productVersionDO == null || !Objects.equals(productVersionDO.getStatusCode(), VERSION_STATUS_PLAN_CODE)
                || (productVersionDO.getStartDate() != null && productVersionRelease.getReleaseDate()
                != null )) {
            throw new CommonException(RELEASE_ERROR);
        }
    }
}
