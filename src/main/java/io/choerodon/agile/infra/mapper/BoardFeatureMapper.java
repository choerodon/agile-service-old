package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.BoardFeatureDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author shinan.chen
 * @since 2019/5/13
 */
public interface BoardFeatureMapper extends BaseMapper<BoardFeatureDO> {
    String queryRightRank(@Param("boardFeature") BoardFeatureDO boardFeatureDO, @Param("rank") String rank);
}
