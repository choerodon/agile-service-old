package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.BoardTeamDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author shinan.chen
 * @since 2019/5/20
 */
public interface BoardTeamMapper extends BaseMapper<BoardTeamDO> {
    String queryRightRank(@Param("boardTeam") BoardTeamDO boardTeamDO, @Param("rank") String rank);

    String queryMinRank(@Param("programId")Long programId);
}
