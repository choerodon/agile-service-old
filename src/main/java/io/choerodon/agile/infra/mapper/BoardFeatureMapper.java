package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.api.vo.BoardFeatureInfoDTO;
import io.choerodon.agile.infra.dataobject.BoardFeatureDO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/5/13
 */
public interface BoardFeatureMapper extends Mapper<BoardFeatureDO> {
    String queryRightRank(@Param("boardFeature") BoardFeatureDO boardFeatureDO, @Param("rank") String rank);

    List<BoardFeatureInfoDTO> queryInfoByPiId(@Param("programId") Long programId, @Param("piId") Long piId);

    BoardFeatureInfoDTO queryInfoById(@Param("programId") Long programId, @Param("boardFeatureId") Long boardFeatureId);

    void deleteByFeatureId(@Param("programId") Long programId, @Param("featureId") Long featureId);
}
