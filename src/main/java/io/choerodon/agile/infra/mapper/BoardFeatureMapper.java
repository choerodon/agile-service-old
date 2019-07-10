package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.api.vo.BoardFeatureInfoVO;
import io.choerodon.agile.infra.dataobject.BoardFeatureDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/5/13
 */
public interface BoardFeatureMapper extends Mapper<BoardFeatureDTO> {
    String queryRightRank(@Param("boardFeature") BoardFeatureDTO boardFeatureDTO, @Param("rank") String rank);

    List<BoardFeatureInfoVO> queryInfoByPiId(@Param("programId") Long programId, @Param("piId") Long piId);

    BoardFeatureInfoVO queryInfoById(@Param("programId") Long programId, @Param("boardFeatureId") Long boardFeatureId);

    void deleteByFeatureId(@Param("programId") Long programId, @Param("featureId") Long featureId);
}
