package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.api.dto.BoardDependInfoDTO;
import io.choerodon.agile.infra.dataobject.BoardDependDO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/5/13
 */
public interface BoardDependMapper extends Mapper<BoardDependDO> {
    List<BoardDependInfoDTO> queryInfoByPiId(@Param("programId") Long programId, @Param("piId") Long piId, @Param("teanProjectIds") List<Long> teanProjectIds);

    void deleteByBoardFeatureId(@Param("programId") Long programId, @Param("boardFeatureId") Long boardFeatureId);

    void deleteByFeatureId(@Param("programId") Long programId, @Param("featureId") Long featureId);
}
