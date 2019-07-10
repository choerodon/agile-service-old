package io.choerodon.agile.infra.mapper;

import io.choerodon.agile.infra.dataobject.BoardTeamDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author shinan.chen
 * @since 2019/5/20
 */
public interface BoardTeamMapper extends Mapper<BoardTeamDTO> {
    String queryRightRank(@Param("boardTeam") BoardTeamDTO boardTeamDTO, @Param("rank") String rank);

    String queryMinRank(@Param("programId")Long programId);
}
