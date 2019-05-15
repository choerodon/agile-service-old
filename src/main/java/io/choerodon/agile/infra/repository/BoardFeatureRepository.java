package io.choerodon.agile.infra.repository;


import io.choerodon.agile.api.dto.BoardFeatureInfoDTO;
import io.choerodon.agile.infra.dataobject.BoardFeatureDO;

/**
 * @author shinan.chen
 * @since 2019/5/13
 */
public interface BoardFeatureRepository {
    BoardFeatureDO create(BoardFeatureDO create);

    void delete(Long boardFeatureId);

    void update(BoardFeatureDO update);

    BoardFeatureDO queryById(Long projectId, Long boardFeatureId);

    BoardFeatureInfoDTO queryInfoById(Long projectId, Long boardFeatureId);

    void checkId(Long projectId, Long boardFeatureId);
}
