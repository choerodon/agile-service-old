package io.choerodon.agile.api.validator;

import io.choerodon.agile.api.vo.ProductVersionReleaseVO;
import io.choerodon.agile.infra.dataobject.ProductVersionDTO;
import io.choerodon.agile.infra.mapper.ProductVersionMapper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/8/9.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class ProductVersionValidator {

    @Autowired
    private ProductVersionMapper productVersionMapper;

    private static final String REPEAT_NAME_ERROR = "error.productVersion.repeatName";
    private static final String VERSION_NOT_FOUND = "error.version.notFound";
    private static final String VERSION_STATUS_PLAN_CODE = "version_planning";
    private static final String RELEASE_ERROR = "error.productVersion.release";
    private static final String ARCHIVED = "archived";
    private static final String RELEASED = "released";
    private static final String ERROR_VERSION_ISARCHIVED = "error.version.isArchived";
    private static final String VERSION_DATE_ERROR = "error.versionDate.startAfterReleaseDate";

    public void checkDate(ProductVersionDTO productVersionDTO) {
        if (productVersionDTO.getStartDate() != null && productVersionDTO.getExpectReleaseDate() != null && productVersionDTO.getStartDate().after(productVersionDTO.getExpectReleaseDate())) {
            throw new CommonException(VERSION_DATE_ERROR);
        }
    }

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
            ProductVersionDTO productVersionDTO = new ProductVersionDTO();
            productVersionDTO.setProjectId(projectId);
            productVersionDTO.setVersionId(versionId);
            if (productVersionMapper.selectByPrimaryKey(productVersionDTO) == null) {
                throw new CommonException(VERSION_NOT_FOUND);
            }
        }
    }

    public void judgeExistStoryMap(Long projectId, Long versionId) {
        if (versionId != null && !Objects.equals(versionId, 0L)) {
            ProductVersionDTO productVersionDTO = new ProductVersionDTO();
            productVersionDTO.setVersionId(versionId);
            productVersionDTO.setProjectId(projectId);
            ProductVersionDTO result = productVersionMapper.selectByPrimaryKey(productVersionDTO);
            if (result == null) {
                throw new CommonException(VERSION_NOT_FOUND);
            }
            if (ARCHIVED.equals(result.getStatusCode()) || RELEASED.equals(result.getStatusCode())) {
                throw new CommonException(ERROR_VERSION_ISARCHIVED);
            }
        }
    }

    public void isRelease(Long projectId, ProductVersionReleaseVO productVersionRelease) {
        judgeExist(projectId, productVersionRelease.getTargetVersionId());
        ProductVersionDTO productVersionDTO = new ProductVersionDTO();
        productVersionDTO.setProjectId(projectId);
        productVersionDTO.setVersionId(productVersionRelease.getVersionId());
        productVersionDTO = productVersionMapper.selectOne(productVersionDTO);
        if (productVersionDTO == null || !Objects.equals(productVersionDTO.getStatusCode(), VERSION_STATUS_PLAN_CODE)) {
            throw new CommonException(RELEASE_ERROR);
        }
    }
}
